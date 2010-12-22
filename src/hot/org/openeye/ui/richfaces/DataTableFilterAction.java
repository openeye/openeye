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

package org.openeye.ui.richfaces;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.security.Identity;

/**
 * Return the user's assigned tasks and puts the result in the session scope
 * when the refresh task event is raised.
 * 
 * @author Patrick Öberg
 * 
 */
@Name("dataTableFilterAction")
@Scope(ScopeType.EVENT)
@Restrict("#{identity.loggedIn}")
public class DataTableFilterAction {

	@In
	Identity identity;

	@Out(required = false, scope = ScopeType.SESSION)
	private DataTableFilter assignedTasksFilter;

	@Out(required = false, scope = ScopeType.SESSION)
	private DataTableFilter unassignedTasksFilter;	
	
	/**
	 * @return the assigned tasks filter
	 */
	@Factory("assignedTasksFilter")
	public DataTableFilter getAssignedTasksFilter() {

		// Get the assigned tasks filter
		if (assignedTasksFilter == null) {
			assignedTasksFilter = new DataTableFilter();
		}

		// Return the data table filter
		return assignedTasksFilter;
	}

	/**
	 * @param assignedTasksFilter
	 *            the assignedTasksFilter to set
	 */
	public void setAssignedTasksFilter(DataTableFilter assignedTasksFilter) {
		this.assignedTasksFilter = assignedTasksFilter;
	}

	/**
	 * @param unassignedTasksFilter the unassignedTasksFilter to set
	 */
	public void setUnassignedTasksFilter(DataTableFilter unassignedTasksFilter) {
		this.unassignedTasksFilter = unassignedTasksFilter;
	}

	/**
	 * @return the unassignedTasksFilter
	 */
	@Factory("unassignedTasksFilter")
	public DataTableFilter getUnassignedTasksFilter() {
		
		// Get the unassigned tasks filter
		if (unassignedTasksFilter == null) {
			unassignedTasksFilter = new DataTableFilter();
		}

		// Return the data table filter		
		return unassignedTasksFilter;
	}

}
