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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
//import org.jboss.seam.annotations.RaiseEvent;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.core.Events;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.security.Identity;

/**
 * Refresh all user task lists.
 * 
 * @author Patrick Öberg
 * 
 */
@Name("refreshTaskListAction")
@Scope(ScopeType.EVENT)
@Restrict("#{identity.loggedIn}")
public class RefreshTaskListAction {

	@In
	Identity identity;
	
	@In FacesMessages facesMessages;
	
	/**
	 * Raise event to refresh all user task lists
	 */
	//@RaiseEvent("refreshTaskList")
     public void refreshAll() {
		Events.instance().raiseEvent("refreshTaskList"); 
     	facesMessages.add("Refresh all task lists");
     }	

}
