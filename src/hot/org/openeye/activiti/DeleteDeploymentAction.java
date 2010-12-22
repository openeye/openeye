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
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.Identity;

/**
 * Delete the selected process definition and raise the refresh process
 * definitions event.
 * 
 * @author Patrick Öberg
 * 
 */
@Name("deleteDeploymentAction")
@Scope(ScopeType.CONVERSATION)
public class DeleteDeploymentAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@In
	private FacesMessages facesMessages;

	@In
	Identity identity;

	@In(create = true, required = false)
	ProcessEngineService processEngineService;

	private String deploymentId;

	/**
	 * Delete the selected definition
	 */
	@RaiseEvent({"refreshTaskList", "refreshDefinitionsList"})
	@End
	public void confirm() {

		// Delete the selected process definition
		if ((deploymentId != null) && !deploymentId.isEmpty()) {
			processEngineService.getRepositoryService()
					.deleteDeploymentCascade(deploymentId);
		}

		facesMessages.add("Deleted deployment: " + deploymentId);

		// Clear the selected definition id
		deploymentId = "";
	}

	/**
	 * @return the deploymentId
	 */
	public String getDeploymentId() {
		return deploymentId;
	}

	/**
	 * @param deploymentId
	 *            the deploymentId to set
	 */
	@Begin(join = true)
	public void setDeploymentId(String deploymentId) {
		this.deploymentId = deploymentId;
	}

	@End
	public void cancel() {

	}

}
