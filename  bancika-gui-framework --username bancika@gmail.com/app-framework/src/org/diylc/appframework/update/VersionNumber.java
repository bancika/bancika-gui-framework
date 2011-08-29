package org.diylc.appframework.update;

import java.io.Serializable;

public class VersionNumber implements Serializable, Comparable<VersionNumber> {

	private static final long serialVersionUID = 1L;

	private int major;
	private int minor;
	private int build;

	public VersionNumber(int major, int minor, int build) {
		super();
		this.major = major;
		this.minor = minor;
		this.build = build;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getBuild() {
		return build;
	}

	public void setBuild(int build) {
		this.build = build;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + build;
		result = prime * result + major;
		result = prime * result + minor;
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
		VersionNumber other = (VersionNumber) obj;
		if (build != other.build)
			return false;
		if (major != other.major)
			return false;
		if (minor != other.minor)
			return false;
		return true;
	}

	@Override
	public int compareTo(VersionNumber o) {
		int result = new Integer(major).compareTo(o.major);
		if (result == 0) {
			result = new Integer(minor).compareTo(o.minor);
			if (result == 0) {
				result = new Integer(build).compareTo(o.build);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return major + "." + minor + "." + build;
	}
}
