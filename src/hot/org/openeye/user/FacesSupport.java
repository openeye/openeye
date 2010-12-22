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

import java.io.*;
import java.util.*;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.component.UIViewRoot;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ui.graphicImage.Image;

//import org.richfaces.component.html.HtmlModalPanel;
import org.richfaces.model.UploadItem;

/**
 * Additional faces support for managing user preferences. Currently contains
 * unnecessary method to be removed.
 * 
 * @author Patrick Öberg
 * 
 */
@Name("facesSupport")
@AutoCreate
@Scope(ScopeType.APPLICATION)
public class FacesSupport {

	public static final boolean hasErrorMessage(FacesContext jsf, String msgPfx) {
		boolean hasErrorMessage = false;
		FacesMessage.Severity maxSeverity = jsf.getMaximumSeverity();
		if (maxSeverity != null
				&& maxSeverity.getOrdinal() >= FacesMessage.SEVERITY_ERROR
						.getOrdinal()) {
			java.util.Iterator<String> clientIdIter = jsf
					.getClientIdsWithMessages();
			if (clientIdIter != null) {
				while (clientIdIter.hasNext()) {
					String nextClientId = clientIdIter.next();
					if (nextClientId != null
							&& (nextClientId.startsWith(msgPfx) || nextClientId
									.equals(msgPfx))) {
						hasErrorMessage = true;
						break;
					}
				}
			}
		}
		return hasErrorMessage;
	}

	public static final void showWhenRendered(FacesContext jsf,
			String modalPanelId, boolean showWhenRendered) {
		UIViewRoot viewRoot = jsf.getViewRoot();
		if (viewRoot != null) {
			Object modalPanel = viewRoot.findComponent(modalPanelId);
			if (modalPanel != null) {
				// ((HtmlModalPanel)modalPanel).setShowWhenRendered(showWhenRendered);
			}
		}
	}

	public static final byte[] getImageBytes(UploadItem item) {
		byte[] aby = null;
		if (item != null) {
			if (item.isTempFile()) {
				try {
					aby = toBytes(item.getFile());
				} catch (java.io.IOException ioExc) {
					ioExc.printStackTrace(); // todo: log it
				}
			} else {
				aby = item.getData();
			}
		}
		return (aby != null && aby.length > 0) ? scaleImage(aby) : null;
	}

	public static final byte[] scaleImage(byte[] aby) {
		byte[] imageBytes = null;
		try {
			Image image = new Image();
			image.setInput(aby);
			if (image.getBufferedImage() != null) {
				if (image.getHeight() > 48 || image.getWidth() > 48) {
					if (image.getHeight() > image.getWidth()) {
						image.scaleToHeight(48);
					} else {
						image.scaleToWidth(48);
					}
				}
				imageBytes = image.getImage();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return imageBytes;
	}

	public static final byte[] toBytes(File file) throws IOException {
		byte[] aby = null;
		if (file.isFile()) {
			aby = toBytes(new FileInputStream(file));
		}
		return (aby != null) ? aby : new byte[0];
	}

	public static final byte[] toBytes(InputStream input) throws IOException {
		byte[] aby = new byte[512];
		int r = -1;
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		try {
			while ((r = input.read(aby)) != -1)
				bs.write(aby, 0, r);
			return bs.toByteArray();
		} finally {
			try {
				input.close();
			} catch (Exception ign) {
			}
		}
	}

	@SuppressWarnings("unused")
	private static final String UTF8 = "UTF-8";
	private static final SelectItem[] SICAST = new SelectItem[0];

	private Map<String, String> countries;
	private Map<String, Set<String>> regions;

	public FacesSupport() throws Exception {
		setCountryListSource(getClass().getClassLoader().getResourceAsStream(
				"openeye/jsf/countries.txt"));
		setRegionListSource(getClass().getClassLoader().getResourceAsStream(
				"openeye/jsf/regions.txt"));
	}

	public SelectItem[] getRegionOptions(String country) {
		if (country != null && country.trim().length() > 0) {
			Collection<String> regions = getRegions(country);
			if (regions != null && !regions.isEmpty()) {
				ArrayList<SelectItem> regionList = new ArrayList<SelectItem>(
						regions.size());
				regionList.add(new SelectItem("", "Select State/Region"));
				for (String region : regions) {
					regionList.add(new SelectItem(region));
				}
				return (SelectItem[]) regionList.toArray(SICAST);
			}
		}
		return SICAST;
	}

	public SelectItem[] getCountryOptions() {
		ArrayList<SelectItem> countryList = new ArrayList<SelectItem>(countries
				.size());
		for (String countryName : countries.keySet())
			countryList.add(new SelectItem(countries.get(countryName),
					countryName));
		return (SelectItem[]) countryList.toArray(SICAST);
	}

	public Collection<String> getRegions(String countryCode) {
		if (countryCode == null || countryCode.length() == 0)
			return Collections.emptyList();
		Set<String> regionSet = regions.get(countryCode);
		if (regionSet == null)
			regionSet = Collections.emptySet();
		return regionSet;
	}

	public void setCountryListSource(InputStream countryList) throws Exception {
		countries = new TreeMap<String, String>();
		if (countryList != null) {
			String ln = null;
			BufferedReader r = null;
			try {
				r = new BufferedReader(new InputStreamReader(countryList,
						"UTF8"));
				while ((ln = r.readLine()) != null) {
					ln = ln.trim();
					int eqx = ln.indexOf("=");
					if (eqx != -1) {
						countries.put(ln.substring(eqx + 1).trim(), ln
								.substring(0, eqx).trim().toUpperCase());
					}
				}
			} finally {
				if (r != null) {
					try {
						r.close();
					} catch (Exception ignore) {
					}
				}
			}
		}
	}

	public void setRegionListSource(InputStream regionList) throws Exception {
		regions = new HashMap<String, Set<String>>();
		if (regionList != null) {
			String ln = null;
			BufferedReader r = null;
			try {
				r = new BufferedReader(
						new InputStreamReader(regionList, "UTF8"));
				while ((ln = r.readLine()) != null) {
					ln = ln.trim();
					int eqx = ln.indexOf("=");
					if (eqx != -1) {
						String cc = ln.substring(0, eqx).trim().toUpperCase();
						Set<String> set = regions.get(cc);
						if (set == null) {
							set = new TreeSet<String>();
							regions.put(cc, set);
						}
						set.add(ln.substring(eqx + 1).trim());
					}
				}
			} finally {
				if (r != null) {
					try {
						r.close();
					} catch (Exception ignore) {
					}
				}
			}
		}
	}
}