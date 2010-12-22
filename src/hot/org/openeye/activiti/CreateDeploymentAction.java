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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.zip.ZipInputStream;
import org.activiti.engine.repository.DeploymentBuilder;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;

/**
 * Deploy business process archive and raise event to refresh definition list.
 * 
 * @author Patrick Öberg
 * 
 */
@Scope(ScopeType.CONVERSATION)
@Name("createDeploymentAction")
public class CreateDeploymentAction implements Serializable {

	private static final long serialVersionUID = 1L;

	@In
	private FacesMessages facesMessages;

	@Logger
	private Log log;

	@In
	Identity identity;

	@In(create = true, required = false)
	ProcessEngineService processEngineService;

	private String fileName = "";

	private long size = 0;

	private String contentType = "";

	private InputStream data = null;

	/**
	 * Upload business archive and create new deployment
	 */
	@RaiseEvent("refreshDefinitionsList")
	@End
	public void confirm() {

		if ((data != null) && (fileName != null) && !fileName.isEmpty()) {

			log.debug("Found file: " + fileName + ", size: " + size
					+ ", contentType: " + contentType);

			ZipInputStream zipInputStream = new ZipInputStream(
					new BufferedInputStream(data));

			try {

				if ((zipInputStream != null)
						&& (zipInputStream.available() > 0)) {

					log.debug("Uploading and deploying: " + fileName);

					DeploymentBuilder builder = processEngineService
							.getRepositoryService().createDeployment();

					builder.name(fileName);
					builder.addZipInputStream(zipInputStream);
					builder.deploy();

					facesMessages
							.add("Created new deployment for business archive: "
									+ fileName);

				}
			} catch (IOException e) {
				log.error("Failed to deploy business archive: " + fileName);
			}
		}
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	@Begin(join = true)
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param size
	 *            the size to set
	 */
	public void setSize(long size) {
		this.size = size;
	}

	/**
	 * @return the size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(InputStream data) {
		this.data = data;
	}

	/**
	 * @return the data
	 */
	public InputStream getData() {
		return data;
	}

	/**
	 * Cancel deployment
	 */
	@End
	public void cancel() {
	}

}
