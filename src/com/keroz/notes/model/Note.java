package com.keroz.notes.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import com.keroz.notes.util.FileUtils;

public class Note implements INotesElement {

    private File file;
    private String content;
    private String name;
    private String path;
    private Category category;
    private boolean isOpened = false;
    private boolean isSaved = false;
    private boolean isEdited = false;
    public final static Note WELCOME = new Note(new File(System.getProperty("user.dir") + "\\welcome.md")) {
        @Override
        public boolean isSaveAllowed() {
            return false;
        }
    };

    public static Note newNote() {
        return new Note(null);
    }

    public Note(File file) {
        this.file = file;
        if (file != null) {
            name = getSimpleName(file.getName());
            path = file.getAbsolutePath();
        } else {
            name = "Untitled";
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
        this.name = getSimpleName(file.getName());
        this.path = file.getAbsolutePath();
    }

    private String getSimpleName(String fileName) {
        int index = fileName.indexOf('.');
        return fileName.substring(0, index);
    }

    public File getFile() {
        return file;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    public void setIsEdited(boolean isEdited) {
        this.isEdited = isEdited;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public void setIsSaved(boolean isSaved) {
        this.isSaved = isSaved;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setIsOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

    public boolean isOpened() {
        return isOpened;
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
}
