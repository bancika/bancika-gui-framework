package org.diylc.swingframework.update;

import java.io.BufferedInputStream;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.diylc.appframework.update.Change;
import org.diylc.appframework.update.ChangeType;
import org.diylc.appframework.update.Version;
import org.diylc.appframework.update.VersionNumber;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

class UpdateChecker {

	private static final Logger LOG = Logger.getLogger(UpdateChecker.class);

	private static final String VERSION_HTML = "<p><b>v%d.%d.%d (released on %s)</b><br>\n%s</p>\n";
	private static final String CHANGE_HTML = "&nbsp;&nbsp;&nbsp;<b>&rsaquo;</b>&nbsp;[%s] %s<br>\n";
	private static final String MAIN_HTML = "<html><font face=\"Tahoma\" size=\"2\">\n%s\n</font></html>";
	private static final Format dateFormat = new SimpleDateFormat();

	private VersionNumber currentVersion;
	private String updateFileURL;

	public UpdateChecker(VersionNumber currentVersion, String updateFileURL) {
		super();
		this.currentVersion = currentVersion;
		this.updateFileURL = updateFileURL;
	}

	@SuppressWarnings("unchecked")
	public List<Version> findNewVersions() throws Exception {
		LOG.info("Trying to download file: " + updateFileURL);
		BufferedInputStream in = new BufferedInputStream(new URL(updateFileURL).openStream());
		XStream xStream = new XStream(new DomDriver());
		List<Version> allVersions = (List<Version>) xStream.fromXML(in);
		in.close();
		List<Version> filteredVersions = new ArrayList<Version>();
		for (Version version : allVersions) {
			if (currentVersion.compareTo(version.getVersionNumber()) < 0) {
				filteredVersions.add(version);
			}
		}
		Collections.sort(filteredVersions);
		LOG.info(filteredVersions.size() + " updates found");
		return filteredVersions;
	}

	public String createUpdateHTML(List<Version> versions) {
		if (versions == null) {
			return "Could not obtain update information.";
		}

		String bodyHtml = "";
		for (Version version : versions) {
			String changeStr = "";
			for (Change change : version.getChanges()) {
				changeStr += String.format(CHANGE_HTML, convertChangeTypeToHTML(change
						.getChangeType()), change.getDescription());
			}
			bodyHtml += String.format(VERSION_HTML, version.getVersionNumber().getMajor(), version
					.getVersionNumber().getMinor(), version.getVersionNumber().getBuild(),
					dateFormat.format(version.getReleaseDate()), changeStr);
		}
		return String.format(MAIN_HTML, bodyHtml);
	}

	private String convertChangeTypeToHTML(ChangeType changeType) {
		String color;
		switch (changeType) {
		case BUG_FIX:
			color = "red";
			break;
		case NEW_FEATURE:
			color = "blue";
			break;
		case IMPROVEMENT:
			color = "green";
			break;
		default:
			color = "black";
		}
		return "<font color=\"" + color + "\">" + changeType + "</font>";
	}
}
