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

import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.international.StatusMessages;
import static org.jboss.seam.international.StatusMessage.Severity.INFO;
import org.jboss.seam.log.Log;
import org.openeye.model.user.User;

/**
 * Provides support for managing user preferences.
 * 
 * @author patobe
 * 
 */
@Name("updatePreferences")
@Scope(ScopeType.CONVERSATION)
@Restrict("#{identity.loggedIn}")
@Transactional
public class UpdatePreferencesAction implements java.io.Serializable {

	private static final long serialVersionUID = -142320483654983571L;
	@Logger
	private Log log;
	@In
	private EntityManager entityManager;
	@In(create = true)
	private PasswordSupport passwordSupport;
	@In(create = true)
	private StatusMessages statusMessages;
	@Out(required = false, scope = ScopeType.SESSION)
	private User currentUser;

	/**
	 * Start conversation for editing preferences
	 * 
	 * @param userId
	 */
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void beginEditPreferences(Long userId) {

		// Get current user
		currentUser = entityManager.find(User.class, userId);

		log.debug("User {0} has started editing preferences.", currentUser
				.getUserName());
	}

	/**
	 * Save user preferences.
	 */
	public void savePreferences() {

		// Get password
		String password = (passwordSupport != null) ? passwordSupport
				.getPassword() : null;

		if ((password != null) && !password.isEmpty()) {

			// password change is not mandatory to update preferences ...
			// but if they do change it, then they must confirm the value ...
			if (passwordSupport.checkConfirmed(statusMessages,
					"updatePrefButton")) {

				// Create password hash
				currentUser.setPasswordHash(passwordSupport
						.getPasswordHash(currentUser.getUserName()));

				// Remove temp password flag
				currentUser.setTemporaryPassword(false);
			}
		}

		log.debug("User {0} saved changes to preferences.", currentUser
				.getUserName());

		statusMessages.addToControlFromResourceBundle("updatePrefButton", INFO,
				"preferences_saved");

		// Save password
		entityManager.flush();
	}

	/**
	 * End conversation
	 */
	@End
	public void endEditPreferences() {

		// Save preferences
		entityManager.flush();

		log.debug("User {0} finished editing preferences.", currentUser
				.getUserName());
	}
}
