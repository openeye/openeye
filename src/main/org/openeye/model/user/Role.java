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

package org.openeye.model.user;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hibernate.validator.Length;
import org.jboss.seam.annotations.security.management.RoleGroups;
import org.jboss.seam.annotations.security.management.RoleName;

/**
 * Role model
 * 
 * @author Patrick Öberg
 *
 */
@Entity
@Table(name = "oe_role")
public class Role implements java.io.Serializable {

	static final long serialVersionUID = -2631452282682699312L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "role_id", unique = true)
	private Long roleId;

	@Column(name = "name", unique = true, nullable = false)
	@Length(min = 4, max = 30)
	@RoleName
	private String name;

	@Column(name = "description", nullable = true, length = 90)
	private String description;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, mappedBy = "userRoles", targetEntity = User.class)
	private Set<User> roleUsers;

	@ManyToMany
	@JoinTable(name = "oe_role_role", joinColumns = @JoinColumn(name = "role"), inverseJoinColumns = @JoinColumn(name = "referenced_role"))
	@RoleGroups
	private Set<Role> groups;

	public Long getRoleId() {
		return this.roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<User> getRoleUsers() {
		if (roleUsers == null) {
			roleUsers = new HashSet<User>();
		}
		return roleUsers;
	}

	public void setRoleUsers(Set<User> roleUsers) {
		this.roleUsers = roleUsers;
	}

	public Set<Role> getGroups() {
		return groups;
	}

	public void setGroups(Set<Role> groups) {
		this.groups = groups;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;

		if (other.getClass() != this.getClass())
			return false;

		Role otherRole = (Role) other;
		if (otherRole.getRoleId().equals(this.getRoleId()))
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		return roleId.intValue();
	}
}
