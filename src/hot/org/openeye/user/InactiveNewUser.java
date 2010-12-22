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

package org.openeye.user;

/**
 * Inactive user
 * 
 * @author Patrick Öberg
 * 
 */
public class InactiveNewUser {

	private String displayName;
	private String userName;
	private String email;
	private String activationLink;

	/**
	 * Default constructor.
	 */
	public InactiveNewUser() {
	}

	/**
	 * Default constructor with init params.
	 * 
	 * @param displayName
	 * @param userName
	 * @param email
	 * @param activationLink
	 */
	public InactiveNewUser(String displayName, String userName, String email,
			String activationLink) {
		this.displayName = displayName;
		this.userName = userName;
		this.email = email;
		this.activationLink = activationLink;
	}

	// Getters and setters

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getActivationLink() {
		return activationLink;
	}

	public void setActivationLink(String activationLink) {
		this.activationLink = activationLink;
	}
}
