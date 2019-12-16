package com.keroz.notedown.model;

public class ElementChangeEvent {
	
	public static final int ADD = 0;
	public static final int REMOVE = 1;
	public static final int CHANGE = 2;
	
	public final int eventType;
	public final NotesElement item;
	
	public ElementChangeEvent(int eventType, NotesElement item) {
	    this.eventType = eventType;
	    this.item = item;
	}
	
}
