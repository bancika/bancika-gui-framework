package org.diylc.appframework.update;

import java.io.Serializable;

public class Change implements Serializable {

	private static final long serialVersionUID = 1L;

	private ChangeType changeType;
	private String description;

	public Change(ChangeType changeType, String description) {
		super();
		this.changeType = changeType;
		this.description = description;
	}

	public ChangeType getChangeType() {
		return changeType;
	}

	public void setChangeType(ChangeType changeType) {
		this.changeType = changeType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changeType == null) ? 0 : changeType.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
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
		Change other = (Change) obj;
		if (changeType == null) {
			if (other.changeType != null)
				return false;
		} else if (!changeType.equals(other.changeType))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + changeType + "] " + description;
	}
}
