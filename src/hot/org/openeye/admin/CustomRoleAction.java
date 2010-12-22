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

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.action.RoleAction;

/**
 * Provides access to custom role implementation and wrapper for integration
 * with activiti. Most of this components methods will be removed with the
 * activiti identity store is replaced with seam identity store.
 * 
 * @author Patrick Öberg
 * 
 */
@Name("customRoleAction")
public class CustomRoleAction extends RoleAction {

	private static final long serialVersionUID = 1L;

	@In
	private FacesMessages facesMessages;

	@In
	IdentityManager identityManager;

	/**
	 * Init conversation and call to super class to create role
	 * 
	 */
	@Begin(join = true)
	public void createRole() {

		// Call super class to create role
		super.createRole();

		facesMessages.add("Creating role: " + this.getRole());

	}

	/**
	 * Call super class to edit role and edit activiti role
	 */
	public void editRole(String role) {

		// Call super class to edit role
		super.editRole(role);

	}

	/**
	 * Call super class to delete role and delete activiti role
	 */
	public void deleteRole(String role) {

		facesMessages.add("Deleting role: " + this.getRole());

		// Delete Seam role
		identityManager.deleteRole(role);

	}

	/**
	 * Create seam role and corresponding activiti group
	 * 
	 * @return group
	 */
	public String save() {

		facesMessages.add("Saving role: " + this.getRole());

		// Return by calling super class to save role
		return super.save();

	}
}
