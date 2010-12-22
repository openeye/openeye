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

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

/**
 * Assigns the selected task to the current user
 * 
 * @author Patrick Öberg
 * 
 */
@Name("claimTaskAction")
@Scope(ScopeType.CONVERSATION)
@Restrict("#{identity.loggedIn}")
public class ClaimTaskAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@In
	private FacesMessages facesMessages;

	@Logger
	private Log log;

	@In
	Identity identity;

	@In(create = true, required = false)
	ProcessEngineService processEngineService;

	private String taskId;

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId
	 *            the task id to set
	 */
	@Begin(join = true)
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * Confirm claim task
	 */
	@RaiseEvent("refreshTaskList")
	@End
	public void confirm() {

		if ((taskId != null) && !taskId.isEmpty()) {

			// Claim the selected task
			processEngineService.getTaskService().claim(taskId,
					identity.getCredentials().getUsername());

			log.debug(identity.getCredentials().getUsername()
					+ " claimed task: " + taskId);

			facesMessages.add(identity.getCredentials().getUsername()
					+ " claimed task: " + taskId);

			// Clear task id
			taskId = "";

		}
	}
}
