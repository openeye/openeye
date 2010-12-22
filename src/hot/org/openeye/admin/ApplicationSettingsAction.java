package org.openeye.admin;

import java.io.Serializable;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.management.PasswordHash;
import org.openeye.model.admin.Settings;
import org.openeye.model.user.Preferences;
import org.openeye.model.user.Role;
import org.openeye.model.user.User;

/**
 * Provides access to application settings
 */
@Name("applicationSettingsAction")
@Scope(ScopeType.EVENT)
public class ApplicationSettingsAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Logger
	private static Log log;

	@In
	private FacesMessages facesMessages;

	@In(create = true)
	CustomUserAction customUserAction;

	@In(create = true)
	CustomRoleAction customRoleAction;

	@In(required = false)
	@Out(required = true, scope = ScopeType.APPLICATION)
	Settings settings;

	@In(required = false, create = true, value = "entityManager")
	private EntityManager em;

	/**
	 * @return settings for the application
	 */
	@Factory("settings")
	public void getSettings() {

		// Do nothing if settings already loaded from db
		if (settings == null) {

			// log.info("Systems settings = null");

			// Load from db if settings is null
			settings = em.find(Settings.class, new Long(1));

			// Create settings if not found in db
			if (settings == null) {

				// Create application settings
				settings = new Settings();
				settings.setUserLogin(true);
				settings.setUserRegistration(true);
				settings.setMailConfirmation(true);

				// Save settings
				em.persist(settings);

				log.info("Created default application settings");

			}
		}

		if (!isUserCreated("admin")) {

			User admin = createAdminUser();

			// Set default welcome message
			settings
					.setWelcomeMessage("You can login with username: admin and password: admin");

			log.info("Created admin user.");

			if (!isRoleCreated("admin")) {

				// Create role
				Role role = createRole("admin", "Application Administrator");

				// Add user to role
				admin.addRole(role);

				log.info("Created Admin role.");
			}

			if (!isRoleCreated("User")) {

				// Create role
				createRole("user", "Default User Role");

				log.info("Created User role.");
			}

		} else {

			// Reset login setting after reboot to allow user login
			settings.setUserLogin(true);

		}

		em.flush();
	}

	/**
	 * Save application settings
	 */
	@Transactional
	public void saveSettings() {

		if (settings != null) {

			// Get current settings
			Settings tmp = em.find(Settings.class, new Long(1));

			// Update with new settings
			tmp.setUserRegistration(settings.getUserRegistration());
			tmp.setMailConfirmation(settings.getMailConfirmation());
			tmp.setWelcomeMessage(settings.getWelcomeMessage());

			// Save temporary login settings. Login setting must not be
			// persisted in db only for the application session
			Boolean userLogin = settings.getUserLogin();

			// Persist new settings in db
			em.flush();

			// Refresh application settings from db
			settings = em.find(Settings.class, new Long(1));

			// Save new login setting only in application scope. In case admin
			// is logged out, the app can be restarted and users can login again
			settings.setUserLogin(userLogin);

			facesMessages.add("Settings saved!");
		}
	}

	/**
	 * 
	 * @param user
	 * @return true if user is created false otherwise
	 */
	private boolean isUserCreated(String user) {

		// Create query to find admin user
		Query q = em.createQuery("from User u where u.userName=:userName");
		q.setParameter("userName", user);

		User admin = null;

		try {
			admin = (User) q.getSingleResult();
		} catch (javax.persistence.NoResultException nre) {
			// safe to ignore ...
		}

		// Check if admin user was found
		if (admin != null)
			return true;
		else
			return false;
	}

	/**
	 * 
	 * @param roleName
	 * @return true if role is created false otherwise
	 */
	private boolean isRoleCreated(String roleName) {

		// Create query to find admin role
		Query q = em.createQuery("from Role r where r.name=:roleName");
		q.setParameter("roleName", "roleName");

		Role role = null;

		try {
			role = (Role) q.getSingleResult();
		} catch (javax.persistence.NoResultException nre) {
			// safe to ignore ...
		}

		// Check if admin role was found
		if (role != null)
			return true;
		else
			return false;
	}

	/**
	 * Helper method to create admin user
	 * 
	 * @return User
	 */
	@SuppressWarnings("deprecation")
	public User createAdminUser() {

		User newUser = null;

		// Default admin user name
		String userName = "admin";

		try {

			// Create user
			newUser = new User();

			// Configure admin user settings
			newUser.setUserName(userName);
			newUser.setPasswordHash(PasswordHash.instance().generateSaltedHash(
					userName, userName, "SHA"));
			newUser.setFirstName(userName);
			newUser.setLastName(userName);
			newUser.setEmail("admin@yourcompany.org");
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

			// Save preferences
			em.persist(prefs);

			// Add preferences to admin
			newUser.setPreferences(prefs);

		} catch (Exception e) {
			log.error("Registration failed for {0}", e, String
					.valueOf(userName));
			// NOTE: we don't return the Exception message back to the newUser
			// because it may reveal too much information to a hacker
		}

		log.debug("Created admin user");

		// Return created user
		return newUser;

	}

	/**
	 * Create role
	 */
	private Role createRole(String roleName, String roleDescription) {

		// Create role
		Role role = new Role();
		role.setName(roleName);
		role.setDescription(roleDescription);

		// Save role
		em.persist(role);

		// Return created role
		return role;
	}

}
