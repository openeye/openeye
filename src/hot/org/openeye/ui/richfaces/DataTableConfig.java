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

import org.ajax4jsf.model.DataComponentState;
import org.richfaces.model.selection.Selection;

/**
 * Backing bean for richfaces data table
 * 
 * @author Patrick Öberg
 * 
 */
public class DataTableConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	private String groupingColumn = "";
	private DataComponentState componentState = null;
	private Selection selection = null;
	private String tableState = "";

	/**
	 * @param groupingColumn
	 *            the groupingColumn to set
	 */
	public void setGroupingColumn(String groupingColumn) {
		this.groupingColumn = groupingColumn;
	}

	/**
	 * @return the groupingColumn
	 */
	public String getGroupingColumn() {
		return groupingColumn;
	}

	/**
	 * @param selection the selection to set
	 */
	public void setSelection(Selection selection) {
		this.selection = selection;
	}

	/**
	 * @return the selection
	 */
	public Selection getSelection() {
		return selection;
	}

	/**
	 * @param componentState the componentState to set
	 */
	public void setComponentState(DataComponentState componentState) {
		this.componentState = componentState;
	}

	/**
	 * @return the componentState
	 */
	public DataComponentState getComponentState() {
		return componentState;
	}

	/**
	 * @param tableState the tableState to set
	 */
	public void setTableState(String tableState) {
		this.tableState = tableState;
	}

	/**
	 * @return the tableState
	 */
	public String getTableState() {
		return tableState;
	}

}