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

import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ManagementService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.TaskService;
import org.activiti.engine.FormService;

import org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.activiti.engine.impl.interceptor.SessionFactory;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.openeye.activiti.identity.IdentitySessionFactoryImpl;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds the process engine and sets up the activiti services
 */
@Name("processEngineService")
@Scope(ScopeType.APPLICATION)
@Startup
@BypassInterceptors
@Install(precedence = Install.APPLICATION)
public class ProcessEngineService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Logger
	private static Log log;
	private boolean isInitialized = false;
	private ProcessEngine processEngine;
	private RuntimeService runtimeService;
	private IdentityService identityService;
	private HistoryService historyService;
	private ManagementService managementService;
	private RepositoryService repositoryService;
	private TaskService taskService;
	private FormService formService;

	/**
	 * Constructor for process engine service
	 */
	public static ProcessEngineService instance() {
		if (!Contexts.isApplicationContextActive()) {
			throw new IllegalStateException("No application context active");
		}
		return (ProcessEngineService) Component.getInstance(
				ProcessEngineService.class, ScopeType.APPLICATION);
	}

	/**
	 * Init the process engine service based on XML configuration file
	 */
	@Create
	public void init() {

		log.info("Init process engine!");

		if (!isInitialized()) {

			log.info("Process engine not initialized!");

			InputStream stream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("activiti.cfg.xml");

			if (stream != null) {

				log.info("Created stream for config file!");

				try {

					// Read configuration from file
					StandaloneProcessEngineConfiguration standaloneProcessEngineConfiguration = (StandaloneProcessEngineConfiguration) StandaloneProcessEngineConfiguration
							.createProcessEngineConfigurationFromInputStream(stream);

					// Create custom factories list
					List<SessionFactory> sessionFactories = new ArrayList<SessionFactory>();

					log.info("Creating custom session factories list");

					// Add custom identity session to custom factories list
					sessionFactories.add(new IdentitySessionFactoryImpl());

					log
							.info("Adding custom identity session factory to custom session factories list");

					// Set session factories
					standaloneProcessEngineConfiguration
							.setCustomSessionFactories(sessionFactories);

					log
							.info("Adding custom identity session factory list to process engine configuration");

					log.info("Trying to build process engine...");

					// Build process engine from processEngineConfiguration
					processEngine = standaloneProcessEngineConfiguration
							.buildProcessEngine();

					log.info("Completed building process engine!");

				} catch (Exception e) {
					log.error("Failed to build process engine");
				}

				// Init all activiti services
				if (processEngine != null) {
					setRuntimeService(processEngine.getRuntimeService());
					setIdentityService(processEngine.getIdentityService());
					setHistoryService(processEngine.getHistoryService());
					setManagementService(processEngine.getManagementService());
					setRepositoryService(processEngine.getRepositoryService());
					setTaskService(processEngine.getTaskService());
					setFormService(processEngine.getFormService());
					log.debug("RuntimeService = " + runtimeService.toString());
					log
							.debug("IdentityService = "
									+ identityService.toString());
					log.debug("HistoryService = " + historyService.toString());
					log.debug("ManagementService = "
							+ managementService.toString());
					log.debug("RepositoryService = "
							+ repositoryService.toString());
					log.debug("TaskService = " + taskService.toString());
					log.debug("FormService = " + formService.toString());
				}

			}

			isInitialized = true;
		}
	}

	/**
	 * Close the process engine service
	 */
	@Destroy
	public void destroy() {
		log.debug("Entering: ProcessEngineService:destroy");
		if (isInitialized()) {
			log.info("Shutting down ProcessEngineService");
			if (processEngine != null) {
				processEngine.close();
			}
			setInitialized(false);
			log.info("Completed shutting down ProcessEngineService");
		}
		log.debug("Exiting: ProcessEngineService:destroy");
	}

	/**
	 * @param Set
	 *            isInitialized, e.g. the process engine service status
	 */
	private void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	/**
	 * @return isInitialized, e.g. the process engine service status
	 */
	public boolean isInitialized() {
		return isInitialized;
	}

	/**
	 * @param runtimeService
	 *            the runtimeService to set
	 */
	private void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	/**
	 * @return the runtimeService
	 */
	public RuntimeService getRuntimeService() {
		return runtimeService;
	}

	/**
	 * @param identityService
	 *            the identityService to set
	 */
	private void setIdentityService(IdentityService identityService) {
		this.identityService = identityService;
	}

	/**
	 * @return the identityService
	 */
	public IdentityService getIdentityService() {
		return identityService;
	}

	/**
	 * @param historyService
	 *            the historyService to set
	 */
	private void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	/**
	 * @return the historyService
	 */
	public HistoryService getHistoryService() {
		return historyService;
	}

	/**
	 * @param managementService
	 *            the managementService to set
	 */
	private void setManagementService(ManagementService managementService) {
		this.managementService = managementService;
	}

	/**
	 * @return the managementService
	 */
	public ManagementService getManagementService() {
		return managementService;
	}

	/**
	 * @param repositoryService
	 *            the repositoryService to set
	 */
	private void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	/**
	 * @return the repositoryService
	 */
	public RepositoryService getRepositoryService() {
		return repositoryService;
	}

	/**
	 * @param taskService
	 *            the taskService to set
	 */
	private void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	/**
	 * @return the taskService
	 */
	public TaskService getTaskService() {
		return taskService;
	}

	/**
	 * @param formService
	 *            the formService to set
	 */
	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	/**
	 * @return the formService
	 */
	public FormService getFormService() {
		return formService;
	}

}
