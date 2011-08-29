package com.diyfever.gui.update;

enum ChangeType {

	BUG_FIX("Bug Fix"), NEW_FEATURE("New Feature"), IMPROVEMENT("Improvement");

	String name;

	private ChangeType(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
