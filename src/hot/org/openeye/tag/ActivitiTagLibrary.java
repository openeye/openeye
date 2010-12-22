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

package org.openeye.tag;

import com.sun.facelets.tag.AbstractTagLibrary;

/**
 * JSF Tag library for activiti integration
 * 
 * @author Patrick Öberg
 * 
 */
public class ActivitiTagLibrary extends AbstractTagLibrary {

	public final static String NAMESPACE = "http://org.openeye/activiti";
	public static final ActivitiTagLibrary INSTANCE = new ActivitiTagLibrary();

	/**
	 * Constructor for tag library
	 */
	public ActivitiTagLibrary() {

		// Call to super class
		super(NAMESPACE);

		// Add tag handler for task forms
		addTagHandler("includeTaskForm", TaskFormTagHandler.class);
	}
}
