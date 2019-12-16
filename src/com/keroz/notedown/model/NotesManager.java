package com.keroz.notedown.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.keroz.notedown.util.FileUtils;

public class NotesManager extends NotesElement {

    private static final String NOTES_PATH = System.getProperty("user.dir") + File.separator + "notes.txt";
	private static File notesPathFile;
	private static NotesManager manager;
	private List<Category> categories;
	
	private NotesManager(NotesElement parent) {
        super(parent);
    }
	
	public static NotesManager getManager() {
		if (manager == null) {
			manager = new NotesManager(null);
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
	
	public void loadNotes() {
	    categories = new ArrayList<Category>();
	    boolean isCategory = false;
	    Category currentCategory = Category.UNCATEGORIED;
	    categories.add(currentCategory);
	    for (String line : FileUtils.readLinesFromFile(notesPathFile)) {
	        if ("".equals(line)) {
                return;
            }
	        isCategory = false;
	        if (line.startsWith("#")) {
	            isCategory = true;
	            currentCategory = new Category(this, line.replace("#", "").trim());
	            addCategoryWithoutNotify(currentCategory);
	        }
	        if (!isCategory) {
	            String[] split = line.split(",");
	            Note note = new Note(currentCategory, new File(split[1]));
	            note.setOpened(Boolean.valueOf(split[0]));
	            currentCategory.addNoteWithoutNotify(note);
            }
	        
	    }
	}
	public boolean addCategoryWithoutNotify(Category category) {
	    if (!categories.contains(category)) {
            return categories.add(category);
        }
	    return false;
	}
	
	public void addCategory(Category category) {
	    if (addCategoryWithoutNotify(category)) {
	        fireNotesElementChanged(new ElementChangeEvent(ElementChangeEvent.ADD, category));
        }
	}
	
	public void removeCategory(Category category) {
        if (categories.contains(category)) {
            categories.remove(category);
            fireNotesElementChanged(new ElementChangeEvent(ElementChangeEvent.REMOVE, category));
        }
    }

	//有必要每次调用? 是否只需要退出时调? 但万一没有正常退出? 还是每次都掉用吧
	public void updateNotesPathFile() {
	    StringBuilder stringBuilder = new StringBuilder();
        FileUtils.writeContentToFile(notesPathFile, appendText(stringBuilder));
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
    }

    @Override
    public NotesElement[] getChildren() {
        if (categories == null) {
            loadNotes();
        }
        return categories.toArray(new NotesElement[categories.size()]);
    }
    
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public String appendText(StringBuilder stringBuilder) {
        categories.forEach(category -> category.appendText(stringBuilder));
        return stringBuilder.toString();
    }

    @Override
    public void elementChanged(ElementChangeEvent event) {
        updateNotesPathFile();
        fireNotesElementChanged(event);
    }
}
