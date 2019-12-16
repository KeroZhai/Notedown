package com.keroz.notedown.model;

import java.io.File;
import java.io.FileNotFoundException;

import com.keroz.notedown.util.FileUtils;

public class Note extends NotesElement {

    private File file;
    private String content;
    private String path;
    private boolean opened = false;
    private boolean saved = false;
    private boolean edited = false;
    private boolean newlyEdited = false;
    
    public final static Note WELCOME = new Note(null, new File(System.getProperty("user.dir") + "\\welcome.md")) {
        @Override
        public boolean isSaveAllowed() {
            return false;
        }
        
        @Override
        public boolean canEdit() {
            return false;
        }
    };

    public static Note newNote() {
        return newNote(Category.UNCATEGORIED);
    }
    
    public static Note newNote(Category parent) {
        return new Note(parent, null);
    }

    public Note(Category category, File file) {
        super(category);
        this.file = file;
        if (file != null) {
            setDisplayName(getSimpleName(file.getName()));
            path = file.getAbsolutePath();
        } else {
            setDisplayName("Untitled");
            path = "";
        }
    }

    public void loadFile() throws FileNotFoundException {
        if (file != null) {
            if (file.exists()) {
                content = file2Content();
            } else {
                throw new FileNotFoundException("File doesn't exist or has been deleted.");
            }
        } else {
            content = "";
        }
    }

    public void setFile(File file) {
        this.file = file;
        setDisplayName(getSimpleName(file.getName()));
        this.path = file.getAbsolutePath();
    }

    private String getSimpleName(String fileName) {
        int index = fileName.indexOf('.');
        return fileName.substring(0, index);
    }
    
    public File getFile() {
        return file;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getPath() {
        return path;
    }

    public void setEdited(boolean isEdited) {
        this.edited = isEdited;
    }

    public boolean isEdited() {
        return edited;
    }

    public boolean isNewlyEdited() {
        return newlyEdited;
    }

    public void setNewlyEdited(boolean newlyEdited) {
        this.newlyEdited = newlyEdited;
    }

    public void setSaved(boolean isSaved) {
        this.saved = isSaved;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setOpened(boolean isOpened) {
        this.opened = isOpened;
    }

    public boolean isOpened() {
        return opened;
    }

    public void save() {
        content2File();
    }

    private String file2Content() {
        if (file == null) {
            return "";
        }
       return FileUtils.readContentFromFile(file);
    }

    private void content2File() {
       FileUtils.writeContentToFile(file, content);
    }

    public void delete() {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    @Override
    public boolean canEdit() {
        return true;
    }

    @Override
    public boolean isSaveAllowed() {
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Note other = (Note) obj;
        if (file == null) {
            if (other.file != null)
                return false;
        } else if (!file.equals(other.file))
            return false;
        return true;
    }

    @Override
    public void removeFromParent(boolean deleteFile) {
        ((Category) getParent()).removeNote(this);
        if (deleteFile) {
            delete();
        }
    }
    
    @Override
    public NotesElement[] getChildren() {
        return NO_CHILDREN;
    }

    @Override
    public String appendText(StringBuilder stringBuilder) {
        return null;
    }

    @Override
    public void elementChanged(ElementChangeEvent event) {
        fireNotesElementChanged(event);
    }
    
    
}
