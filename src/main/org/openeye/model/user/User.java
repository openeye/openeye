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
import javax.persistence.*;
import org.hibernate.validator.Email;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.jboss.seam.annotations.security.management.*;
import org.jboss.seam.security.Identity;

/**
 * User model
 * 
 * @author Patrick Öberg
 *
 */
@Entity
@Table(name = "oe_user", uniqueConstraints = @UniqueConstraint(columnNames = "user_name"))
public class User implements java.io.Serializable {

	static final long serialVersionUID = 5617334495706397826L;

	private Long userId;
	private Preferences preferences;
	private String userName;
	private String email;
	private String activationKey;
	private String firstName;
	private String middleName;
	private String lastName;
	private String language;
	private boolean active;
	private long createdOn;
	private String passwordHash;
	private boolean temporaryPassword;
	private Set<Role> userRoles = new HashSet<Role>(0);

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id", unique = true, nullable = false)
	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Column(name = "user_name", unique = true, nullable = false, length = 16)
	@NotNull
	@Length(min = 4, max = 16)
	@Pattern(regex = "^[a-zA-Z\\d_]{4,12}$", message = "{invalid_screen_name}")
	@UserPrincipal
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "email", nullable = false, length = 60)
	@NotNull
	@Length(max = 60)
	@Email
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "activation_key", length = 60)
	@Length(max = 60)
	public String getActivationKey() {
		return this.activationKey;
	}

	public void setActivationKey(String activationKey) {
		this.activationKey = activationKey;
	}

	@Column(name = "first_name", nullable = false, length = 60)
	@NotNull
	@Length(max = 60)
	@UserFirstName
	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(name = "middle_name", length = 60)
	@Length(max = 60)
	public String getMiddleName() {
		return this.middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@Column(name = "last_name", nullable = false, length = 60)
	@NotNull
	@Length(max = 60)
	@UserLastName
	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "language", nullable = false, length = 3)
	@Length(max = 3)
	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Column(name = "active", nullable = false)
	@NotNull
	@UserEnabled
	public boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "created_on", nullable = false)
	@NotNull
	public long getCreatedOn() {
		return this.createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	@Column(name = "password", nullable = false, length = 128)
	@NotNull
	@Length(max = 128)
	@UserPassword(hash = "SHA")
	public String getPasswordHash() {
		return this.passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	@Column(name = "temporary_password", nullable = false)
	@NotNull
	public boolean isTemporaryPassword() {
		return this.temporaryPassword;
	}

	public void setTemporaryPassword(boolean temporaryPassword) {
		this.temporaryPassword = temporaryPassword;
	}

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	public Preferences getPreferences() {
		return this.preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}

	public boolean isUserInRole(String roleName) {
		return Identity.instance().hasRole(roleName);
	}

	public void removeRole(Role role) {
		getUserRoles().remove(role);
		role.getRoleUsers().remove(this);
	}

	public void addRole(Role role) {
		getUserRoles().add(role);
		role.getRoleUsers().add(this);
	}

	@UserRoles
	@ManyToMany(targetEntity = Role.class, cascade = { CascadeType.PERSIST,
			CascadeType.MERGE })
	@JoinTable(name = "oe_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	public Set<Role> getUserRoles() {
		if (userRoles == null) {
			userRoles = new HashSet<Role>();
		}
		return userRoles;
	}

	public void setUserRoles(Set<Role> userRoles) {
		this.userRoles = userRoles;
	}

	public String toString() {
		String myFullName = null;
		if (firstName != null) {
			if (lastName != null) {
				if (middleName != null && middleName.length() > 0) {
					myFullName = firstName + " " + middleName + " " + lastName;
				} else {
					myFullName = firstName + " " + lastName;
				}
			}
		} else {
			if (lastName != null) {
				myFullName = lastName;
			} else {
				myFullName = userName;
			}
		}
		return myFullName;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (other == this)
			return true;

		if (other.getClass() != this.getClass())
			return false;

		User otherRole = (User) other;
		if (otherRole.getUserId().equals(this.getUserId()))
			return true;

		return false;
	}

	@Override
	public int hashCode() {
		return userId.intValue();
	}

}