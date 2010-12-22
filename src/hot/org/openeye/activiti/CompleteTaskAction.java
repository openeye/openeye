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
import org.activiti.engine.task.Task;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.openeye.task.TaskForm;
import org.openeye.util.VariableConverter;

/**
 * Complete task
 * 
 * @author Patrick Öberg
 * 
 */
@Name("completeTaskAction")
@Scope(ScopeType.CONVERSATION)
@Restrict("#{identity.loggedIn}")
public class CompleteTaskAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@In
	private FacesMessages facesMessages;

	@Logger
	private Log log;

	@In
	Identity identity;

	@In(create = true, required = false)
	ProcessEngineService processEngineService;

	@Out(required = false)
	private String taskId = "";

	@In(required = false)
	@Out(required = false, scope = ScopeType.SESSION)
	TaskForm taskForm = null;

	/**
	 * @param taskId
	 *            the taskId to set
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @return the taskId
	 */
	@Begin(join = true)
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the taskForm
	 */
	public TaskForm getTaskForm() {

		if ((taskId != null) && !taskId.isEmpty()) {

			// check if task form is opened
			if ((taskForm) != null && taskForm.getId().equals(taskId)) {
				return taskForm;
			} else {

				// Create task form
				taskForm = new TaskForm();

				// Get the task
				Task task = getTask(taskId);

				if (task != null) {

					// Set task id
					taskForm.setId(task.getId());

					// Set task name
					taskForm.setName(task.getName());

					// Set task description
					taskForm.setDescription(task.getDescription());

					// Set task form variables
					taskForm.setVariables(getFormVariables(task));

					// Set deployment id
					taskForm.setDeploymentId(getDeploymentId(task
							.getProcessDefinitionId()));

					taskForm.setFormResourceKey(getFormKey(task.getId()));

					taskForm.setForm(getTaskFormAsString(taskForm
							.getDeploymentId(), taskForm.getFormResourceKey()));

					return taskForm;

				}
			}
		}

		// Return null if no task form was found
		return null;
	}

	/**
	 * Save the task, e.g. update process variables
	 */
	public void save() {

		if (taskForm != null) {

			// Get the task
			Task task = getTask(taskForm.getId());

			if (task != null) {

				// Save task variables
				setFormVariables(task.getExecutionId());
			}

			facesMessages.add("Saving task: " + taskForm.getId());
		}
	}

	/**
	 * Complete the task
	 */
	@RaiseEvent("refreshTaskList")
	@End
	public void complete() {

		if (taskForm != null) {

			// Get the task
			Task task = getTask(taskForm.getId());

			if (task != null) {
				// Save task variables
				setFormVariables(task.getExecutionId());
			}

			// Complete the task
			processEngineService.getTaskService().complete(taskForm.getId());

			facesMessages.add("Completed task: " + taskForm.getId());

			// Clear the completed task id
			taskId = "";

			// Clear the completed task form
			taskForm = null;
		}
	}

	/**
	 * Cancel completing the task
	 */
	public void cancel() {

		// Clear the completed task id
		taskId = "";

		// Clear the completed task form
		taskForm = null;
	}

	/**
	 * @return task form variables
	 */
	private Map<String, Object> getFormVariables(Task task) {

		// Get the process variables
		Map<String, Object> var = processEngineService.getRuntimeService()
				.getVariables(task.getExecutionId());

		// Create a a new variable map if none is found for the current task
		if (var == null) {

			log.debug("No variables found for task: " + taskId
					+ ". Creating new HashMap.");

			var = new HashMap<String, Object>();

		} else {
			log.debug("Variables found for task: " + taskId);
		}

		return var;

	}

	/**
	 * @param executionId
	 *            the executionId to set
	 */
	private void setFormVariables(String executionId) {

		// Get variable map
		Map<String, Object> map = taskForm.getVariables();

		if (map != null) {

			// Create variable converter
			VariableConverter converter = new VariableConverter();

			// Convert the process variables
			map = converter.convert(map);

			// Update variables
			processEngineService.getRuntimeService().setVariables(executionId,
					map);

			// Set task variables
			taskForm.setVariables(map);

		}
	}

	/**
	 * @return the selected task
	 */
	private Task getTask(String taskId) {

		// Get the task
		return processEngineService.getTaskService().createTaskQuery().taskId(
				taskId).singleResult();
	}

	/**
	 * @return the deploymentId
	 */
	private String getDeploymentId(String processDefinitionId) {

		return processEngineService.getRepositoryService()
				.createProcessDefinitionQuery().processDefinitionId(
						processDefinitionId).singleResult().getDeploymentId();
	}

	/**
	 * @return the formKey
	 */
	private String getFormKey(String taskId) {
		return processEngineService.getFormService().getTaskFormData(taskId)
				.getFormKey();
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
