package com.keroz.notedown.model;

import java.util.ArrayList;
import java.util.List;

public abstract class NotesElement implements ElementChangedListener {
    
    protected static final NotesElement[] NO_CHILDREN = {}; 
    
    private NotesElement parent;
    private String displayName;
    private List<ElementChangedListener> listeners = new ArrayList<ElementChangedListener>();

    
    public NotesElement(NotesElement parent, String displayName) {
        this.parent = parent;
        if (parent != null) {
            addListener(parent);
        }
        this.displayName= displayName;
    }
    
    public NotesElement(NotesElement parent) {
        this(parent, null);
    }
    
    public NotesElement getParent() {
        return parent;
    }
    
    public void setParent(NotesElement parent) {
        this.parent = parent;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getShortenDisplayName() {
        return displayName.length() > 12 ? displayName.substring(0, 12) + "..." : displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public void addListener(ElementChangedListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(ElementChangedListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }
    
    public void fireNotesElementChanged(ElementChangeEvent event) {
        listeners.forEach(listener -> listener.elementChanged(event));
    }

    public abstract boolean canEdit();
    public abstract boolean isSaveAllowed();
    
    public abstract void removeFromParent(boolean deleteFile);
    
    public abstract NotesElement[] getChildren();
    
    public abstract String appendText(StringBuilder stringBuilder);
    
}
