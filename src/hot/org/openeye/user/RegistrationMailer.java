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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.Renderer;

/**
 * Adds support for sending mail confirmation of user registration
 * 
 * @author Patrick Öberg
 * 
 */
@Name("registrationMailer")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class RegistrationMailer {

	@In(create = true)
	private Renderer renderer;

	/**
	 * Send mail confirmation on registration event.
	 */
	@Observer("userRegistered")
	public void sendActivationEmail() {
		renderer.render("/preferences/email/activation.xhtml");
	}

	/**
	 * Send mail confirmation on password reset event.
	 */
	@Observer("passwordReset")
	public void sendPasswordResetEmail() {
		renderer.render("/preferences/email/password_reset.xhtml");
	}

}
