package com.keroz.notedown.model;

import java.util.ArrayList;
import java.util.List;

public class Category extends NotesElement {
    
    private boolean expanded = false;
    
    private List<Note> notes = new ArrayList<Note>();
    public static final Category UNCATEGORIED = new Category(NotesManager.getManager(), "Uncategoried");
    
    public Category(NotesManager notesManager, String displayName) {
        super(notesManager, displayName);
    }


    @Override
    public boolean canEdit() {
        return false;
    }


    @Override
    public boolean isSaveAllowed() {
        return false;
    }


    @Override
    public void removeFromParent(boolean deleteFile) {
        ((NotesManager) getParent()).removeCategory(this);
        if (deleteFile) {
            deleteAll();
        }
    }


    @Override
    public NotesElement[] getChildren() {
        return notes.toArray(new Note[notes.size()]);
    }
    
    public boolean addNoteWithoutNotify(Note note) {
        if (!notes.contains(note)) {
            return notes.add(note);
        }
        return false;
    }

    public void addNote(Note note) {
        if (addNoteWithoutNotify(note)) {
            fireNotesElementChanged(new ElementChangeEvent(ElementChangeEvent.ADD, note));
        }
    }
    
    public void removeNote(Note note) {
        if (notes.contains(note)) {
            notes.remove(note);
            fireNotesElementChanged(new ElementChangeEvent(ElementChangeEvent.REMOVE, note));
        }
    }

    public List<Note> getNotes() {
        return notes;
    }


    public void setNotes(List<Note> notes) {
        this.notes = notes;
    }

    @Override
    public String appendText(StringBuilder stringBuilder) {
        if (this != UNCATEGORIED) {
            stringBuilder.append("# " + getDisplayName() + "\n");
        }
        notes.forEach(note -> stringBuilder.append(note.isOpened() + "," + note.getPath() + "\n"));
        return stringBuilder.toString();
    }
    
    public void deleteAll() {
        notes.forEach(note -> note.delete());
        notes.clear();
    }


    @Override
    public void elementChanged(ElementChangeEvent event) {
        fireNotesElementChanged(event);
    }


    public boolean isExpanded() {
        return expanded;
    }


    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

}
