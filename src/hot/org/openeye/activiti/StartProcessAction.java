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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.openeye.task.TaskForm;
import org.openeye.util.VariableConverter;

/**
 * Starts a new process instance
 * 
 * @author Patrick Öberg
 * 
 */
@Name("startProcessAction")
@Scope(ScopeType.CONVERSATION)
public class StartProcessAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@In
	private FacesMessages facesMessages;

	@Logger
	private Log log;

	@In
	Identity identity;

	@In(create = true, required = false)
	ProcessEngineService processEngineService;

	private String processDefinitionId;

	@In(required = false)
	@Out(required = false)
	TaskForm startProcessForm;

	/**
	 * @return the processDefinitionId
	 */
	public String getProcessDefinitionId() {
		return processDefinitionId;
	}

	/**
	 * @param processDefinitionId
	 *            the processDefinitionId to set
	 */
	@Begin(join = true)
	public void setProcessDefinitionId(String processDefinitionId) {
		this.processDefinitionId = processDefinitionId;
	}

	/**
	 * @return the startProcessForm
	 */
	@Begin(join = true)
	public TaskForm getStartProcessForm() {

		if ((processDefinitionId != null) && !processDefinitionId.isEmpty()) {

			// Get the process definition
			ProcessDefinition processDefinition = processEngineService
					.getRepositoryService().createProcessDefinitionQuery()
					.processDefinitionId(processDefinitionId).singleResult();

			// Create start form
			startProcessForm = new TaskForm();

			// Set task id
			startProcessForm.setId(processDefinition.getId());

			// Set task name
			startProcessForm.setName(processDefinition.getName());

			// Set task description
			startProcessForm.setDescription(processDefinition.getKey());

			// Set task form variables
			startProcessForm.setVariables(new HashMap<String, Object>());

			// Set deployment id
			startProcessForm.setDeploymentId(processDefinition
					.getDeploymentId());

			// Set form resource key
			startProcessForm.setFormResourceKey(processEngineService
					.getFormService().getStartFormData(
							processDefinition.getId()).getFormKey());

			// Get start form as string
			startProcessForm.setForm(getTaskFormAsString(startProcessForm
					.getDeploymentId(), startProcessForm.getFormResourceKey()));

			// Return start form
			return startProcessForm;

		}

		// Return null if start form was not found
		return null;
	}

	/**
	 * Start a new process and raise event to refresh assigned and unassigned
	 * task lists
	 */
	@RaiseEvent("refreshTaskList")
	@End
	public void confirm() {

		if ((processDefinitionId != null) && !processDefinitionId.isEmpty()
				&& (startProcessForm != null)) {

			// Create variable converter
			VariableConverter converter = new VariableConverter();

			// Get the process variables
			Map<String, Object> var = converter.convert(startProcessForm
					.getVariables());

			ProcessInstance processInstance = null;

			// Start new process instance
			if (var == null || var.isEmpty()) {

				log.debug("Request parameter map is null");

				// Start process
				processInstance = processEngineService.getRuntimeService()
						.startProcessInstanceById(processDefinitionId);
			} else {

				log.debug("Found request parameter map");

				processInstance = processEngineService.getRuntimeService()
						.startProcessInstanceById(processDefinitionId, var);
			}

			if (processInstance != null) {

				log.debug("New process started, Id = "
						+ processInstance.getId());

				facesMessages.add("New process started, Id = "
						+ processInstance.getId());

			}
		}

		// Clear the selected process definition id
		processDefinitionId = "";

		// Clear the start process form
		startProcessForm = null;

		log.debug("Exiting StartProcessAction:confirm()");

	}

	/**
	 * Cancel starting a new process
	 */
	@End
	public void cancel() {

		// Clear the selected process definition id
		processDefinitionId = "";

		// Clear the start process form
		startProcessForm = null;

	}

	/**
	 * @return the task form as String
	 */
	private String getTaskFormAsString(String deploymentId,
			String formResourceKey) {

		if ((deploymentId != null) && !deploymentId.isEmpty()
				&& (formResourceKey != null) && !formResourceKey.isEmpty()) {

			// Get the input stream for the task form
			InputStream is = processEngineService.getRepositoryService()
					.getResourceAsStream(deploymentId, formResourceKey);

			if (is != null) {

				String taskForm = "";

				try {
					taskForm = convertStreamToString(is);
				} catch (Exception e) {
					// TODO
				}

				// Return the task form as String
				return taskForm;
			}
		}

		// Return empty string if no task form was found
		return "";
	}

	/**
	 * @return the converted stream as a String.
	 */
	private String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];

			// Read input stream
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}

			// Convert to String
			return writer.toString();
		} else {

			// Return empty String
			return "";
		}
	}
}
