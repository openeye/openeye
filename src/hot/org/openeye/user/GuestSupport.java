/*
 * Open Eye Development Team, Open Eye Community and individual 
 * contributors as indicated by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.openeye.user;

import java.security.MessageDigest;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.Pattern;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Events;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.international.StatusMessages;
import static org.jboss.seam.international.StatusMessage.Severity.ERROR;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.util.Hex;
import org.openeye.model.user.Preferences;
import org.openeye.model.user.Role;
import org.openeye.model.user.User;

/**
 * Provide support for both manual and automatic user account creation
 * 
 * @author Patrick Öberg
 * 
 */
@Name("guest")
@Scope(ScopeType.EVENT)
public class GuestSupport {

	@Logger
	private Log log;

	@In
	private EntityManager entityManager;

	@In
	private Credentials credentials;

	@In(required = false)
	private PasswordSupport passwordSupport;

	@In(create = true)
	private StatusMessages statusMessages;

	@Out(required = false, scope = ScopeType.SESSION)
	private User currentUser;

	private User newUser;
	private String lostPasswordUserId;
	private String lostPasswordEmail;
	private String loginUserId;
	private String recoveredPasswordEmail;
	private String resetPassword;
	private boolean sendEmailConfirmation = false;

	@NotEmpty
	@Length(min = 4, max = 12)
	@Pattern(regex = "^[a-zA-Z\\d_]{4,12}$", message = "{invalid_screen_name}")
	private String registrationScreenName;

	/**
	 * Get user.
	 * 
	 * @return User
	 */
	public User getUser() {
		if (newUser == null) {
			newUser = new User();
			newUser.setPreferences(null);
			newUser.setUserName(null);
			newUser.setTemporaryPassword(false);
			newUser.setActive(false);
		}
		return newUser;
	}

	/**
	 * Activate user account
	 * 
	 * @param activationKey
	 */
	@Transactional
	public void doActivate(String activationKey) {
		Query q = entityManager
				.createQuery("from User u where u.active=0 AND u.activationKey=:activationKey");
		q.setParameter("activationKey", activationKey);
		User activatedUser = null;
		try {
			activatedUser = (User) q.getSingleResult();
		} catch (javax.persistence.NoResultException nre) {
			// safe to ignore ...
		}
		if (activatedUser != null) {
			activatedUser.setActive(true);
			activatedUser.setActivationKey(null);
			loginUserId = activatedUser.getUserName();
			credentials.setUsername(loginUserId);

			log.info("User {0} activated successfully.", loginUserId);
		}
	}

	/**
	 * Puts the current user name in session scope.
	 * 
	 * @param identity
	 */
	@Observer(Identity.EVENT_POST_AUTHENTICATE)
	public void postAuthenticate(Identity identity) {

		if (identity != null && identity.isLoggedIn()
				&& this.currentUser == null) {

			// Get user name from credentials
			String userName = identity.getCredentials().getUsername();

			if (userName == null)
				// Try get user name from pricipal
				userName = identity.getPrincipal().getName();

			if (userName != null) {

				// Set current user name
				this.currentUser = byUserName(userName);
			}
		}
	}

	/**
	 * Log the logged in user.
	 */
	@Observer(Identity.EVENT_LOGIN_SUCCESSFUL)
	public void onLogin() {

		// Get the user name
		this.currentUser = byUserName(credentials.getUsername());

		log.info("User " + this.currentUser.getUserName()
				+ " logged in successfully.");
	}

	/**
	 * Log authentication failure.
	 */
	@Observer(Identity.EVENT_LOGIN_FAILED)
	public void onLoginFailed() {
		this.currentUser = null;
		statusMessages.addToControlFromResourceBundle("loginBtn", ERROR,
				"login_error");
	}

	/**
	 * Recover lost password.
	 * 
	 * @throws Exception
	 */
	@Transactional
	public void doRecoverLostPassword() throws Exception {

		if (lostPasswordUserId == null || lostPasswordEmail == null)
			return;

		lostPasswordEmail = lostPasswordEmail.trim();
		this.recoveredPasswordEmail = null;
		this.resetPassword = null;

		User theUser = byUserName(lostPasswordUserId);

		if (theUser == null) {
			statusMessages.addToControlFromResourceBundle("resetLostPassword",
					ERROR, "user_not_exist", lostPasswordUserId);
			return;
		}

		if (!theUser.getEmail().equalsIgnoreCase(lostPasswordEmail)) {
			statusMessages.addToControlFromResourceBundle("resetLostPassword",
					ERROR, "email_not_recognized");
			return;
		}

		try {
			// Create temp password
			this.resetPassword = PasswordSupport.tempPassword();

			// Set temp password
			theUser.setPasswordHash(PasswordSupport.hash(theUser.getUserName(),
					this.resetPassword));

			// Set temp password flag
			theUser.setTemporaryPassword(true);

			// Save temp password
			entityManager.flush();

			if (credentials != null) {
				// Set credentials
				credentials.setUsername(theUser.getUserName());
			}

			// Set email
			this.recoveredPasswordEmail = lostPasswordEmail;

			// Reset temp field email field
			this.lostPasswordEmail = null;

			// send the temporary password email ...
			Events.instance().raiseTransactionSuccessEvent("passwordReset");

		} catch (Exception exc) {
			// note: don't send the exception message back to the client as it
			// may contain
			// too much information that a hacker could use to break into the
			// site
			statusMessages.addToControlFromResourceBundle("resetLostPassword",
					ERROR, "reset_failed_unknown");
		}
	}

	/**
	 * Register user
	 */
	@Transactional
	public void doRegister() {

		// Is password confirmed
		if (!passwordSupport.isConfirmed()) {
			statusMessages.addToControlFromResourceBundle("password", ERROR,
					"Password not confirmed");
			return;
		}

		try {
			// Create new user
			newUser.setUserName(this.registrationScreenName);
			newUser.setActive(false);
			newUser.setPasswordHash(passwordSupport
					.getPasswordHash(this.registrationScreenName));
			newUser.setActivationKey(getMD5Hash(newUser.getLastName()
					+ newUser.getUserName() + newUser.getEmail()
					+ newUser.getFirstName())
					+ System.currentTimeMillis());
			newUser.setLanguage("en");
			newUser.setCreatedOn(System.currentTimeMillis());

			// Save new user
			entityManager.persist(newUser);

			// Create preferences
			Preferences prefs = new Preferences();
			prefs.setUserId(newUser.getUserId());
			prefs.setStartPage("Assigned Tasks");

			// Save preferences
			entityManager.persist(prefs);

			// Add preferences to user
			newUser.setPreferences(prefs);

			// Ok to sen confirmation mail?
			if (isSendEmailConfirmation()) {

				// Get external context
				ExternalContext extCtxt = FacesContext.getCurrentInstance()
						.getExternalContext();

				// Get url
				String activationLink = ((javax.servlet.http.HttpServletRequest) extCtxt
						.getRequest()).getRequestURL().toString();

				// Create activation link
				String newUserLink = activationLink
						+ ((activationLink.indexOf("?") != -1) ? "&act="
								: "?act=") + newUser.getActivationKey();

				// Send the activation email ... as part of this transaction
				Contexts.getEventContext()
						.set(
								"inactiveNewUser",
								new InactiveNewUser(newUser.toString(), newUser
										.getUserName(), newUser.getEmail(),
										newUserLink));

				// Raise event to send mail
				Events.instance().raiseEvent("userRegistered");

			} else {
				// Activate the user if send confirmation is disabled
				doActivate(newUser.getActivationKey());
			}

			// Reset form fields
			this.registrationScreenName = null;
			this.newUser = null;

		} catch (Exception exc) {
			log.error("Registration failed for {0}", exc, String
					.valueOf(this.registrationScreenName));
			// NOTE: we don't return the Exception message back to the newUser
			// because it may reveal too much information to a hacker
			statusMessages.addToControlFromResourceBundle("registerButton",
					ERROR, "general_reg_error");
		}
	}

	/**
	 * Show login form
	 * 
	 * @return
	 */
	public boolean getShowLogin() {

		// If there is a register error ... don't show login ...
		if (getRegisterError())
			return false;

		// If password is missing
		if (getForgotPasswordError())
			return false;

		// Set default login status
		boolean showLogin = false;

		if (loginUserId != null) {
			showLogin = true;
		} else {
			if (FacesSupport.hasErrorMessage(FacesContext.getCurrentInstance(),
					"loginForm:")) {
				showLogin = true;
			}
		}

		// Return login status
		return showLogin;
	}

	/**
	 * Is there any error messages for reset password
	 * 
	 * @return forgotPasswordError
	 */
	public boolean getForgotPasswordError() {
		return FacesSupport.hasErrorMessage(FacesContext.getCurrentInstance(),
				"forgotForm:");
	}

	/**
	 * Is there any error messages for registration
	 * 
	 * @return registerError
	 */
	public boolean getRegisterError() {
		return FacesSupport.hasErrorMessage(FacesContext.getCurrentInstance(),
				"registerForm:");
	}

	/**
	 * Set user name
	 * 
	 * @param screenName
	 */
	public void setRegistrationScreenName(String screenName) {
		if (hasUser(screenName)) {

			// User name already taken
			statusMessages.addToControlFromResourceBundle("userName", ERROR,
					"user_id_is_taken", screenName);
		} else {

			// Set user name
			this.registrationScreenName = screenName;
		}
	}

	/**
	 * Get hash of String
	 * 
	 * @param msg
	 * 
	 * @return md5Hash
	 */
	protected String getMD5Hash(final String msg) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			return new String(Hex.encodeHex(md5.digest(msg.getBytes("UTF-8"))));
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	/**
	 * Get role
	 * 
	 * @param roleName
	 * 
	 * @return role
	 */
	protected Role getRole(String roleName) {
		try {
			Query q = entityManager
					.createQuery("from Role r where r.name = :roleName");
			q.setParameter("roleName", roleName);
			return (Role) q.getSingleResult();
		} catch (javax.persistence.NoResultException nre) {
			return null;
		}
	}

	/**
	 * Check if user exists.
	 * 
	 * @param userName
	 * 
	 * @return hasUser
	 */
	protected boolean hasUser(String userName) {

		boolean hasUser = false;

		if (userName != null) {

			// Create user query
			Query q = entityManager
					.createQuery("select u.userName from User u where u.userName = :userName");
			q.setParameter("userName", userName);

			// User exists?
			hasUser = (q.getResultList().size() > 0);
		}
		return hasUser;
	}

	/**
	 * get user by user name
	 * 
	 * @param userName
	 * 
	 * @return user
	 */
	protected User byUserName(String userName) {

		try {

			// Create user query
			Query q = entityManager
					.createQuery("from User u where u.userName = :userName");

			q.setParameter("userName", userName);

			// Return user
			return (User) q.getSingleResult();

		} catch (javax.persistence.NoResultException nre) {
			return null;
		}
	}

	// Getters and setters for form fields

	public void setSendEmailConfirmation(boolean sendEmailConfirmation) {
		this.sendEmailConfirmation = sendEmailConfirmation;
	}

	public boolean isSendEmailConfirmation() {
		return sendEmailConfirmation;
	}

	public String getRegistrationScreenName() {
		return registrationScreenName;
	}

	public String getLostPasswordUserId() {
		return lostPasswordUserId;
	}

	public void setLostPasswordUserId(String value) {
		this.lostPasswordUserId = value;
	}

	public String getLostPasswordEmail() {
		return lostPasswordEmail;
	}

	public void setLostPasswordEmail(String value) {
		this.lostPasswordEmail = value;
	}

	public String getLoginUserId() {
		return loginUserId;
	}

	public void setLoginUserId(String loginUserId) {
		this.loginUserId = loginUserId;
	}

	public String getRecoveredPasswordEmail() {
		return this.recoveredPasswordEmail;
	}

	public void setRecoveredPasswordEmail(String value) {
		this.recoveredPasswordEmail = value;
	}

	public String getResetPassword() {
		return this.resetPassword;
	}

	public void setResetPassword(String value) {
		this.resetPassword = value;
	}

}