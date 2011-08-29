package org.diylc.appframework.update;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Version implements Serializable, Comparable<Version> {

	private static final long serialVersionUID = 1L;

	private VersionNumber versionNumber;
	private Date releaseDate;
	private String name;
	private List<Change> changes;
	private String url;

	public Version(VersionNumber versionNumber, Date releaseDate, String name, String url) {
		super();
		this.versionNumber = versionNumber;
		this.releaseDate = releaseDate;
		this.name = name;
		this.url = url;
		this.changes = new ArrayList<Change>();
	}

	public VersionNumber getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(VersionNumber versionNumber) {
		this.versionNumber = versionNumber;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Change> getChanges() {
		return changes;
	}

	public void setChanges(List<Change> changes) {
		this.changes = changes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changes == null) ? 0 : changes.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((releaseDate == null) ? 0 : releaseDate.hashCode());
		result = prime * result + ((versionNumber == null) ? 0 : versionNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Version other = (Version) obj;
		if (changes == null) {
			if (other.changes != null)
				return false;
		} else if (!changes.equals(other.changes))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (releaseDate == null) {
			if (other.releaseDate != null)
				return false;
		} else if (!releaseDate.equals(other.releaseDate))
			return false;
		if (versionNumber == null) {
			if (other.versionNumber != null)
				return false;
		} else if (!versionNumber.equals(other.versionNumber))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(Version o) {
		return -versionNumber.compareTo(o.versionNumber);
	}
}
