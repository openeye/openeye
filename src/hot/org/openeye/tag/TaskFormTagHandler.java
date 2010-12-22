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

package org.openeye.tag;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.el.VariableMapperWrapper;
import com.sun.facelets.tag.*;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.UIComponent;

/**
 * JSF Tag handler for activiti task form integration
 * 
 * @author Patrick Öberg
 * 
 */
public class TaskFormTagHandler extends TagHandler {

	private final TagAttribute formIdAttribute = getRequiredAttribute("formId");
	private final TagAttribute formAttribute = getRequiredAttribute("form");

	/**
	 * Constructor for tag handler
	 * 
	 * @param config
	 */
	public TaskFormTagHandler(TagConfig config) {
		super(config);
	}

	/**
	 * Include task form in faclet
	 */
	public void apply(FaceletContext context, UIComponent parent)
			throws IOException, FacesException, FaceletException, ELException {

		// if ((formIdAttribute != null) && (formAttribute != null)) {

		// Get ValueExpression(s)
		ValueExpression idExpression = formIdAttribute.getValueExpression(
				context, String.class);

		ValueExpression formExpression = formAttribute.getValueExpression(
				context, String.class);

		// Get id string
		String id = (String) idExpression.getValue(context);

		// Get the task form string
		String form = (String) formExpression.getValue(context);

		// Create a unique file path to form to avoid server-side caching of
		// the url stream
		String filePath = "/form" + id + "/form.xhtml";

		if (!form.isEmpty()) {

			// Convert String to InputStream
			InputStream is = convertStringToStream(form);

			if (is != null) {

				// Set variable mapper
				javax.el.VariableMapper orig = context.getVariableMapper();
				VariableMapperWrapper newVarMapper = new VariableMapperWrapper(
						orig);
				context.setVariableMapper(newVarMapper);

				try {

					nextHandler.apply(context, parent);

					// Create url and stream handler to include form in
					// facelet
					URL url = new URL("xhtml", "", 0, filePath,
							new CustomURLStreamHandler(is, "taskForm.xhtml"));

					context.includeFacelet(parent, url);

				} finally {

					context.setVariableMapper(orig);

				}
			}
		}
		// }
	}

	/**
	 * Custom URL stream handler
	 * 
	 * @author Patrick Öberg
	 * 
	 */
	private static final class CustomURLStreamHandler extends URLStreamHandler {
		private final InputStream is;

		private final String src;

		public CustomURLStreamHandler(InputStream is, String src) {
			this.src = src;
			this.is = is;
		}

		protected URLConnection openConnection(URL url) {
			return new CustomURLConnection(url, is, src);
		}
	}

	/**
	 * Custom URL connection
	 * 
	 * @author Patrick Öberg
	 * 
	 */
	private static final class CustomURLConnection extends URLConnection {
		private final InputStream is;
		private final String src;

		protected CustomURLConnection(URL url, InputStream is, String src) {
			super(url);
			this.src = src;
			this.is = is;
		}

		public void connect() {
		}

		public InputStream getInputStream() throws FileNotFoundException {
			if (is == null) {
				throw new FileNotFoundException("File '" + src
						+ "' not found in process file definition");
			} else {
				return is;
			}
		}
	}

	/*
	 * Convert String to InputStream using ByteArrayInputStream class. This
	 * class constructor takes the string byte array which can be done by
	 * calling the getBytes() method.
	 */
	private InputStream convertStringToStream(String text) {
		InputStream is = null;
		try {
			is = new ByteArrayInputStream(text.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return is;
	}
}
