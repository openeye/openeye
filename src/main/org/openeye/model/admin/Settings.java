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

package org.openeye.model.admin;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Settings for the application.
 * 
 * @author Patrick Öberg
 * 
 */
@Entity
@Table(name = "oe_settings", uniqueConstraints = @UniqueConstraint(columnNames = "id"))
public class Settings implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", insertable = false, updatable = false)
	private Long id;

	@Column(name = "user_login", nullable = false)
	private Boolean userLogin = true;

	@Column(name = "user_reg", nullable = false)
	private Boolean userRegistration = true;

	@Column(name = "mail_confirmation")
	private Boolean mailConfirmation = true;

	@Column(name = "welcom_message")
	private String welcomeMessage;

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param userLogin
	 *            the userLogin to set
	 */
	public void setUserLogin(Boolean userLogin) {
		this.userLogin = userLogin;
	}

	/**
	 * @return the userLogin
	 */
	public Boolean getUserLogin() {
		return userLogin;
	}

	/**
	 * @param userRegistration
	 *            the userRegistration to set
	 */
	public void setUserRegistration(Boolean userRegistration) {
		this.userRegistration = userRegistration;
	}

	/**
	 * @return the userRegistration
	 */
	public Boolean getUserRegistration() {
		return userRegistration;
	}

	/**
	 * @param mailConfirmation
	 *            the mailConfirmation to set
	 */
	public void setMailConfirmation(Boolean mailConfirmation) {
		this.mailConfirmation = mailConfirmation;
	}

	/**
	 * @return the mailConfirmation
	 */
	public Boolean getMailConfirmation() {
		return mailConfirmation;
	}

	/**
	 * @param welcomeMessage
	 *            the welcomeMessage to set
	 */
	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}

	/**
	 * @return the welcomeMessage
	 */
	public String getWelcomeMessage() {
		return welcomeMessage;
	}

}