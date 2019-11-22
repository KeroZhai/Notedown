package com.keroz.notes;

import com.keroz.notes.util.Settings;
import com.keroz.notes.views.Notes;

public class Launch {
    
    private static Notes notes = null;

	public static void main(String[] args) {
	    initNotes();
		while (notes.open() == Notes.RESTART) {
		    initNotes();
		}
	}
	
	private static void initNotes() {
	    Settings.init();
        notes = new Notes();
	}
}