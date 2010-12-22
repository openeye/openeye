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

import java.io.Serializable;

import org.activiti.engine.task.Task;

/**
 * Backing bean for richfaces data table filter
 * 
 * @author Patrick Öberg
 * 
 */
public class DataTableFilter implements Serializable {

	private static final long serialVersionUID = 1L;

	private String filterValue = "";

	/**
	 * @return the filterValue
	 */
	public String getFilterValue() {
		return filterValue;
	}

	/**
	 * @param filterValue the filterValue to set
	 */
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}

	/**
	 * Hold filter description for richface data table filter
	 * @param current
	 * @return
	 */
	public boolean filterDescription(Object current) {

		// Cast to activiti task
		Task task = (Task) current;

		// Filter is empty
		if ((filterValue == null) || (filterValue.length() == 0)) {
			return true;
		}

		// Filter on task description
		if (task.getDescription().toLowerCase().startsWith(
				filterValue.toLowerCase())) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Hold definition id for richface data table filter
	 * @param current
	 * @return
	 */
	public boolean filterDefinitionId(Object current) {

		// Cast to activiti task
		Task task = (Task) current;

		// Filter is empty
		if ((filterValue == null) || (filterValue.length() == 0)) {
			return true;
		}

		// Filter on task description
		if (task.getProcessDefinitionId().toLowerCase().startsWith(
				filterValue.toLowerCase())) {
			return true;
		} else {
			return false;
		}

	}
	
}