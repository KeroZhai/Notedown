package com.keroz.notes.model;

/**
 *
 * @author z21542
 * @Date 2019年10月17日上午9:15:47
 */
public interface INotesManagerListener {
	void notesChanged(NotesManagerEvent event);
}
