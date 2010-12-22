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

package org.openeye.activiti.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.identity.GroupEntity;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.identity.UserEntity;
import org.activiti.engine.impl.db.DbIdentitySession;
import org.jboss.seam.Component;

/**
 * Extension of activiti DbIdentitySession which is the default implementation
 * of the IdentitySession and Session interfaces. This class is used to replace
 * the built in identity store with the one specified by Seam component.xml.
 * 
 * @author Patrick Öberg
 * 
 */
public class IdentitySessionImpl extends DbIdentitySession {

	/**
	 * Find all groups of which the user is a member
	 */
	@Override
	public List<Group> findGroupsByUser(String userName) {

		// Get entity manager
		EntityManager entityManager = (EntityManager) Component.getInstance(
				"entityManager", true);

		// Create query
		Query q = entityManager
				.createQuery("from User u where u.userName = :userName");

		q.setParameter("userName", userName);

		org.openeye.model.user.User user = null;

		try {
			user = (org.openeye.model.user.User) q.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("User not found");
			return null;
		}

		List<Group> activitiRoles = null;

		if (user != null) {

			// Get user roles
			Set<org.openeye.model.user.Role> seamRoles = user.getUserRoles();

			if (seamRoles != null) {
				activitiRoles = new ArrayList<Group>();

				// Convert to seam roles to activiti groups
				for (org.openeye.model.user.Role role : seamRoles) {
					activitiRoles.add(new GroupImpl(role));
				}
			}
		}

		return activitiRoles;
	}

	/**
	 * Find user by user name. TODO: Investigate why id can't be used instead of
	 * user name.
	 */
	@Override
	public UserEntity findUserById(String userName) {

		// Get entity manager
		EntityManager entityManager = (EntityManager) Component.getInstance(
				"entityManager", true);

		// Create query
		Query q = entityManager
				.createQuery("from User u where u.userName = :userName");

		q.setParameter("userName", userName);

		org.openeye.model.user.User user = null;

		// Get seam user
		try {
			user = (org.openeye.model.user.User) q.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("User not found");
			return null;
		}

		// Return activiti user
		return new UserImpl(user);
	}

	/**
	 * Find users by group/role name. TODO: Investigate why id can't be used
	 * instead of group/role name.
	 */
	@Override
	public List<User> findUsersByGroupId(String roleName) {

		// Get entity manager
		EntityManager entityManager = (EntityManager) Component.getInstance(
				"entityManager", true);

		// Create query
		Query q = entityManager
				.createQuery("from Role r where r.name = :roleName");

		q.setParameter("roleName", roleName);

		org.openeye.model.user.Role role = null;

		// Get seam role
		try {
			role = (org.openeye.model.user.Role) q.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("Role not found");
			return null;
		}

		List<User> activitiUsers = null;

		if (role != null) {

			// Get role users
			Set<org.openeye.model.user.User> seamUsers = role.getRoleUsers();

			activitiUsers = new ArrayList<User>();

			if (activitiUsers != null) {
				// Convert from seam user to activiti user
				for (org.openeye.model.user.User user : seamUsers) {
					activitiUsers.add(new UserImpl(user));
				}
			}
		}

		return activitiUsers;

	}

	/**
	 * Check if the user exists
	 */
	@Override
	public boolean isValidUser(String userName) {

		// Get entity manager
		EntityManager entityManager = (EntityManager) Component.getInstance(
				"entityManager", true);

		// Create query
		Query q = entityManager
				.createQuery("from User u where u.userName = :userName");

		q.setParameter("userName", userName);

		org.openeye.model.user.User user = null;

		try {
			user = (org.openeye.model.user.User) q.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("User not found");
		}

		if (user != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Find group by group/role name. TODO: Investigate why id can't be used
	 * instead of group/role name.
	 */
	@Override
	public GroupEntity findGroupById(String roleName) {

		// Get entity manager
		EntityManager entityManager = (EntityManager) Component.getInstance(
				"entityManager", true);

		// Create query
		Query q = entityManager
				.createQuery("from Role r where r.name = :roleName");

		q.setParameter("roleName", roleName);

		org.openeye.model.user.Role role = null;

		// Get seam role
		try {
			role = (org.openeye.model.user.Role) q.getSingleResult();
		} catch (NoResultException e) {
			System.out.println("Role not found");
			return null;
		}

		return new GroupImpl(role);

	}

}
