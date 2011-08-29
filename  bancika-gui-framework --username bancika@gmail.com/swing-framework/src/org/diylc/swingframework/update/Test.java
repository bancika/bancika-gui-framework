package org.diylc.swingframework.update;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.diylc.appframework.update.Change;
import org.diylc.appframework.update.ChangeType;
import org.diylc.appframework.update.Version;
import org.diylc.appframework.update.VersionNumber;

import com.thoughtworks.xstream.XStream;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		org.diylc.appframework.update.
		Version v = new Version(new VersionNumber(0, 0, 1), new Date(), "first release",
				"http://some-site.com/file.zip");
		v.getChanges().add(new Change(ChangeType.BUG_FIX, "Fixed NPE bug"));
		Version v2 = new Version(new VersionNumber(0, 0, 2), new Date(), "second release",
				"http://some-site.com/file.zip");
		v2.getChanges().add(new Change(ChangeType.NEW_FEATURE, "added new feature"));
		List<Version> l = new ArrayList<Version>();
		l.add(v);
		l.add(v2);

		try {
			FileOutputStream os = new FileOutputStream("c:\\update.xml");
			new XStream().toXML(l, os);
			os.close();
		} catch (Exception e) {

		}
	}
}
