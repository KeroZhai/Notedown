package com.keroz.notes.model;

import java.util.List;

public class Category extends NotesElement {
	
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


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public List<Note> getNotes() {
        return notes;
    }


    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }
	
}
