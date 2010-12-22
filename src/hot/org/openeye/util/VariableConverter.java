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

package org.openeye.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * Supports conversion from String to Date, Long and Boolean.
 * 
 * @author Patrick
 * 
 */
public final class VariableConverter {

	private SimpleDateFormat _date;
	private Long _long;
	private Boolean _boolean;

	public Map<String, Object> convert(Map<String, Object> map) {

		if (map != null) {
			// Check the variables map
			for (Map.Entry<String, Object> entry : map.entrySet()) {

				// Test if conversion from String to Long/Boolean/Date is needed
				if (entry.getValue() != null) {

					if (isValidNumber(entry.getValue().toString())) {

						// Convert String to Long
						entry.setValue(_long);
					} else if (isValidBoolean(entry.getValue().toString())) {

						// Convert String to Boolean
						entry.setValue(_boolean);
					} else if (isValidDate(entry.getValue().toString())) {

						// Convert String to Date
						entry.setValue(_date);
					}
				}
			}
		}

		// Return map with converted values
		return map;
	}

	/*
	 * Validates if input String is a number
	 */
	private boolean isValidNumber(String in) {

		try {
			Long.parseLong(in);
		} catch (NumberFormatException ex) {
			return false;
		}

		_long = new Long(in);

		return true;
	}

	/*
	 * Validates if input String is a Boolean
	 */
	private boolean isValidBoolean(String in) {

		if ("true".equalsIgnoreCase(in) || "false".equalsIgnoreCase(in)) {
			_boolean = new Boolean(in);
			return true;
		} else {
			return false;
		}
	}

	/*
	 * Validates if input String is a Date
	 */
	public boolean isValidDate(String inDate) {

		// set the format to use as a constructor argument
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		if (inDate.trim().length() != dateFormat.toPattern().length())
			return false;

		dateFormat.setLenient(false);

		try {
			// parse the inDate parameter
			dateFormat.parse(inDate.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}
}
