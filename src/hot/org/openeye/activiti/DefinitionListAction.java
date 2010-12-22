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

import java.util.List;
import org.activiti.engine.repository.ProcessDefinition;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;

/**
 * Return the list of process definitions and put the result in the session
 * scope when the refresh definitions event is raised.
 * 
 * @author Patrick Öberg
 * 
 */
@Name("definitionListAction")
@Scope(ScopeType.EVENT)
@Restrict("#{identity.loggedIn}")
public class DefinitionListAction {

	@In(create = true, required = false)
	ProcessEngineService processEngineService;

	@Out(required = false, scope = ScopeType.SESSION)
	private List<ProcessDefinition> processDefinitions;

	/**
	 * @return the process definitions
	 */
	@Observer("refreshDefinitionsList")
	@Factory("processDefinitions")
	public List<ProcessDefinition> getProcessDefinitions() {
		processDefinitions = processEngineService.getRepositoryService()
				.createProcessDefinitionQuery().list();

		return processDefinitions;
	}
}
