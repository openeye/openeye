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

package org.openeye.activiti;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.security.Identity;

/**
 * Returns the number of assigned tasks for the current user and puts it in the
 * session scope when the refresh task event is raised.
 * 
 * @author Patrick Öberg
 * 
 */
@Name("unassignedTaskCountAction")
@Scope(ScopeType.EVENT)
@Restrict("#{identity.loggedIn}")
public class UnassignedTaskCountAction {

	@In
	Identity identity;

	@In(create = true, required = false)
	ProcessEngineService processEngineService;

	@Out(required = false, scope = ScopeType.EVENT)
	private int unassignedTasksCount;

	/**
	 * @param unassignedTasksCount
	 *            the unassignedTasksCount to set
	 */
	private void setUnassignedTasksCount(int unassignedTasksCount) {
		this.unassignedTasksCount = unassignedTasksCount;
	}

	/**
	 * @return the unassignedTasksCount and raise event to refresh the
	 *         unassigned task list
	 */
	@Observer("refreshTaskList")
	@Factory("unassignedTasksCount")
	public int getUnassignedTasksCount() {

		setUnassignedTasksCount(processEngineService.getTaskService()
				.createTaskQuery().taskCandidateUser(
						identity.getCredentials().getUsername()).list().size());

		return unassignedTasksCount;
	}
}
