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
import org.openeye.model.user.User;

/**
 * Change password
 * 
 * @author Patrick Öberg
 * 
 */
@Name("changePassword")
@Scope(ScopeType.EVENT)
@Restrict("#{identity.loggedIn}")
public class ChangePasswordAction {

	@In
	private EntityManager entityManager;
	@In(create = true)
	private PasswordSupport passwordSupport;
	@In(create = true)
	private StatusMessages statusMessages;

	@Out(required = false, scope = ScopeType.SESSION)
	private User currentUser;

	/**
	 * Change user password
	 * 
	 * @param userId
	 */
	@Transactional
	public void doChangePassword(Long userId) {
		
		// Get user
		currentUser = entityManager.find(User.class, userId);
		
		if (passwordSupport.checkConfirmed(statusMessages, "chngPswd")) {
			
			// Set password
			currentUser.setPasswordHash(passwordSupport
					.getPasswordHash(currentUser.getUserName()));
			
			// Invalidate temporary password
			currentUser.setTemporaryPassword(false);
		}
	}
}
