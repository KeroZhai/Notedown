package com.keroz.notes.model;

import java.util.EventObject;

public class NotesManagerEvent extends EventObject {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -6201434139201168522L;

	private final Note[] added;
	private final Note[] removed;
	
	public NotesManagerEvent(NotesManager source, Note[] added, Note[] removed) {
		super(source);
		this.added = added;
		this.removed = removed;
	}

	public Note[] getNotesAdded() {
		return added;
	}
	
	public Note[] getNotesRemoved() {
		return removed;
	}
}
