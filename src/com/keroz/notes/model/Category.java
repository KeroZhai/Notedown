package com.keroz.notes.model;

import java.util.List;

public class Category implements INotesElement {
	
	private String name;
	private List<Note> notes;
	public static final Category UNCATEGORIED = new Category("Uncategoried");
	
	
	public Category(String name) {
		this.name = name;
	}


	@Override
	public boolean canEdit() {
		return false;
	}


	@Override
	public boolean isSaveAllowed() {
		return false;
	}
	
}
