package com.keroz.notedown;

import com.keroz.notedown.views.Notedown;

public class Launch {
    
    private static Notedown notedown = null;

	public static void main(String[] args) {
	    initNotes();
		while (notedown.open() == Notedown.RESTART) {
		    initNotes();
		}
	}
	
	private static void initNotes() {
	    Settings.init();
        notedown = new Notedown();
	}
}