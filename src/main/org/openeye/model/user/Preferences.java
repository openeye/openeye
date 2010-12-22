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

import javax.persistence.*;
import org.hibernate.validator.Length;

/**
 * Preferences model
 * 
 * @author Patrick Öberg
 * 
 */
@Entity
@Table(name = "oe_user_preferences")
public class Preferences implements java.io.Serializable {

	static final long serialVersionUID = 8838022288031104510L;

	protected Long userId;
	private String prefix;
	private String suffix;
	private Integer birthYear;
	private String startPage;

	@Id
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long value) {
		this.userId = value;
	}

	@Column(name = "prefix", length = 20)
	@Length(max = 20)
	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Column(name = "suffix", length = 20)
	@Length(max = 20)
	public String getSuffix() {
		return this.suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Column(name = "birth_year")
	public Integer getBirthYear() {
		return this.birthYear;
	}

	public void setBirthYear(Integer birthYear) {
		this.birthYear = birthYear;
	}

	@Override
	public int hashCode() {
		return (userId != null) ? userId.hashCode() : super.hashCode();
	}

	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}

	@Column(name = "start_page")
	public String getStartPage() {
		return startPage;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!getClass().equals(obj.getClass()))
			return false;
		Preferences x = (Preferences) obj;
		return equals(userId, x.userId);
	}

	protected final boolean equals(final Object left, final Object right) {
		return (left != null) ? left.equals(right) : (right == null);
	}

}