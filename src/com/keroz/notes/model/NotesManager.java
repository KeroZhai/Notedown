package com.keroz.notes.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.keroz.notes.util.FileUtils;

public class NotesManager {

	private static final String NOTES_PATH = System.getProperty("user.dir") + File.separator + "notes.txt";
	private static File notesPathFile;
	private static NotesManager manager;
	private List<Category> categories;
	private List<Note> notes;
	private List<INotesManagerListener> listeners = new ArrayList<INotesManagerListener>();
	private final Note[] NONE = {};
	
	public static NotesManager getManager() {
		if (manager == null) {
			manager = new NotesManager();
			notesPathFile = new File(NOTES_PATH);
			if (!notesPathFile.exists()) {
                try {
                    notesPathFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
		}
		return manager;
	}
	
	public void updateNote(Note note) {
		if (notes.contains(note)) {
			update();
			fireNotesChanged(new Note[] {note}, new Note[] {note});
		}
	}

	public void addNote(Note note) {
		if (!notes.contains(note)) {
			notes.add(note);
			update();
			fireNotesChanged(new Note[] {note}, NONE);
		}
	}

//	public void removeNote(Note note) {
//		if (notes.contains(note)) {
//			notes.remove(note);
//			note.delete();
//			update();
//			fireNotesChanged(NONE, new Note[] {note});
//		}
//	}
	
	public void removeNote(Note note, boolean deleteFile) {
	    if (notes.contains(note)) {
            notes.remove(note);
            if (deleteFile) {
                note.delete();
            }
            update();
            fireNotesChanged(NONE, new Note[] {note});
        }
	}

	public Note[] getNotes() {
		if (notes == null) {
			loadNotes();
		}
		return notes.toArray(new Note[notes.size()]);
	}

	public void loadNotes() {
	    notes = new ArrayList<Note>();
	    for (String line : FileUtils.readLinesFromFile(notesPathFile)) {
	        if ("".equals(line)) {
                return;
            }
	        String[] split = line.split(",");
            Note note = new Note(new File(split[1]));
            note.setOpened(Boolean.valueOf(split[0]));
            notes.add(note);
	    }
	}

	public void update() {
	    StringBuilder stringBuilder = new StringBuilder();
        notes.forEach(note -> stringBuilder.append(note.isOpened() + "," + note.getPath() + "\n"));
        FileUtils.writeContentToFile(notesPathFile, stringBuilder.toString());
	}

	public void addListener(INotesManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeListener(INotesManagerListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	void fireNotesChanged(Note[] added, Note[] removed) {
		listeners.forEach(listener -> listener.notesChanged(new NotesManagerEvent(this, added, removed)));
	}
}
