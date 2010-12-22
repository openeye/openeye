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

package org.openeye.admin;

import static org.jboss.seam.international.StatusMessage.Severity.ERROR;
import javax.persistence.EntityManager;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.PasswordHash;
import org.jboss.seam.security.management.action.UserAction;
import org.openeye.model.user.Preferences;
import org.openeye.model.user.User;

/**
 * Provides access to custom user implementation.
 * 
 * @author Patrick Öberg
 * 
 */
@Name("customUserAction")
public class CustomUserAction extends UserAction {

	private static final long serialVersionUID = 1L;

	@In
	private FacesMessages facesMessages;

	@Logger
	private Log log;

	@In
	IdentityManager identityManager;

	@In(create = true, value = "entityManager")
	private EntityManager em;

	@In(required = false, create = true)
	private StatusMessages statusMessages;

	private String email;

	/**
	 * Call to super class to edit user.
	 */
	public void editUser(String user) {
		super.editUser(user);
	}

	/**
	 * Call to super class and and activiti integration to save user and raise
	 * refresh task list event.
	 * 
	 * @return userid
	 */
	@RaiseEvent("refreshTaskList")
	public String save() {

		// Set message
		facesMessages.add("Saving user: " + this.getUsername());

		return super.save();
	}

	/**
	 * Call to super class and and activiti integration to create user.
	 */
	@Transactional
	public void createUser() {

		try {
			// Create user
			User newUser = new User();
			newUser.setUserName(getUsername());
			newUser.setPasswordHash(createPasswordHash(this.getPassword()));
			newUser.setFirstName(getFirstname());
			newUser.setLastName(getLastname());
			newUser.setEmail(email);
			newUser.setLanguage("se");
			newUser.setActive(true);
			newUser.setActivationKey(null);
			newUser.setCreatedOn(System.currentTimeMillis());

			// Save new user
			em.persist(newUser);

			// Create new preferences
			Preferences prefs = new Preferences();
			prefs.setUserId(newUser.getUserId());
			prefs.setStartPage("Assigned Tasks");
			em.persist(prefs);
			newUser.setPreferences(prefs);

			// Clear all fields
			clearFields();

		} catch (Exception e) {
			log.error("Registration failed for {0}", e, String.valueOf(this
					.getUsername()));
			// NOTE: we don't return the Exception message back to the newUser
			// because it may reveal too much information to a hacker
			String message = "Failed to create new user";
			statusMessages.addToControlFromResourceBundle("registerButton",
					ERROR, message);
		}

		// Set message
		facesMessages.add("Created user: " + this.getFirstname() + " "
				+ this.getLastname());

	}

	/**
	 * End conversation and clear fields.
	 */
	@End
	public void cancel() {
		statusMessages.clear();
		clearFields();
	}

	/**
	 * Clear fields.
	 */
	private void clearFields() {
		this.setUsername("");
		this.setFirstname("");
		this.setLastname("");
		this.setEmail("");
		this.setPassword("");
		this.setConfirm("");
	}

	/**
	 * Create encrypted password.
	 */
	@SuppressWarnings( { "deprecation" })
	private String createPasswordHash(String saltPhrase) {
		return PasswordHash.instance().generateSaltedHash(this.getPassword(),
				saltPhrase, "SHA");
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
}
