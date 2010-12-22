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
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.international.StatusMessages;
import static org.jboss.seam.international.StatusMessage.Severity.ERROR;
import org.jboss.seam.security.management.PasswordHash;

/**
 * Provides support for managing password.
 * 
 * @author Patrick Öberg
 *
 */
@Name("passwordSupport")
@Scope(ScopeType.EVENT)
@BypassInterceptors
public class PasswordSupport implements java.io.Serializable {

	private static final long serialVersionUID = 6014077444383293120L;
	
	private String password = null;

	private String confirm = null;

	/**
	 * Create temp password
	 * 
	 * @return tempPassword
	 */
	public static final String tempPassword() {
		java.util.Random rand = new java.util.Random();
		int[] aNums = new int[8];
		for (int n = 0; n < aNums.length; n++)
			aNums[n] = rand.nextInt(9) + 1;
		char[] ach1 = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
				'j' };
		char[] ach2 = new char[] { 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T',
				'U' };
		char[] ach3 = new char[] { 'v', 'w', 'x', 'y', 'z', 'V', 'W', 'X', 'Y',
				'Z' };
		char[] ach4 = new char[] { 'k', '$', 'm', 'n', 'p', 'q', 'r', 's', 't',
				'u' };
		char[] ach5 = new char[] { '$', '%', '!', '#', '$', '%', '!', '#', '$',
				'%' };
		return (ach4[aNums[7]] + String.valueOf(aNums[2]) + ach1[aNums[3]]
				+ String.valueOf(aNums[0]) + ach3[aNums[5]] + ach2[aNums[4]]
				+ ach4[aNums[6]] + ach5[aNums[1]]);
	}

	/**
	 * Create password hash
	 * 
	 * @param saltPhrase
	 * @param plainTextPassword
	 * @return passwordHash
	 */
	@SuppressWarnings( { "deprecation" })
	public static final String hash(String saltPhrase, String plainTextPassword) {
		return PasswordHash.instance().generateSaltedHash(plainTextPassword,
				saltPhrase, "SHA");
	}

	/**
	 * Get hashed password
	 * @param saltPhrase
	 * @return hashedPassword
	 */
	public String getPasswordHash(String saltPhrase) {
		return PasswordSupport.hash(saltPhrase, getPassword());
	}

	/**
	 * Is password confirmed.
	 * 
	 * @param statusMessages
	 * @param controlId
	 * @return passwordConfirmed
	 */
	public boolean checkConfirmed(StatusMessages statusMessages,
			String controlId) {

		boolean isConfirmed = isConfirmed();
		
		if (!isConfirmed) {
			
			// Add error message
			statusMessages.addToControlFromResourceBundle(controlId, ERROR,
					"password_not_confirmed");
		}
		
		// Return password confirmed
		return isConfirmed;
	}
	
	// Getters and setters
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String value) {
		this.password = (value != null && value.trim().length() > 0) ? value
				.trim() : null;
	}

	public String getPasswordConfirm() {
		return confirm;
	}

	public void setPasswordConfirm(String confirm) {
		this.confirm = confirm;
	}

	public boolean isConfirmed() {
		return (confirm != null && confirm.equals(password));
	}

	/**
	 * Convert to string
	 */
	@Override
	public String toString() {
		return new StringBuilder("PasswordSupport[").append("password=")
				.append(password).append(", confirm=").append(confirm).append(
						"]").toString();
	}
}
