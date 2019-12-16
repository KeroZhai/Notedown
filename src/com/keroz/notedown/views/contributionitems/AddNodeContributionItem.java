package com.keroz.notedown.views.contributionitems;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.keroz.notedown.model.Category;
import com.keroz.notedown.model.Note;
import com.keroz.notedown.model.NotesElement;
import com.keroz.notedown.model.NotesManager;
import com.keroz.notedown.views.Notedown;

public class AddNodeContributionItem extends ContributionItem {
    
    private Notedown notes;
    private MenuItem menuItem;

    public AddNodeContributionItem(Notedown notes) {
        this.notes = notes;
    }
    
    @Override
    public void fill(Menu menu, int index) {
        NotesElement notesElement = (NotesElement) notes.getTreeViewer().getStructuredSelection().getFirstElement();
        if (notesElement instanceof Note) {
            return;
        }
        menuItem = new MenuItem(menu, SWT.NONE, index);
        if (notesElement == null) {
            menuItem.setText("New Category");
            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    InputDialog inputDialog = new InputDialog(notes.getShell(), "New Category", "Category Name:", "Unnamed Category",
                            null);
                    if (inputDialog.open() == InputDialog.OK) {
                        String categoryName = inputDialog.getValue();
                        if (categoryName.isEmpty()) {
                            return;
                        }
                        Category category = new Category(NotesManager.getManager(), inputDialog.getValue());
                        NotesManager.getManager().addCategory(category);
                    }
                }
            });
        } else {
            Category parent = (Category) notesElement;
            menuItem.setText("New Note");
            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    Note note = Note.newNote(parent);
                    notes.openNote(note);
                }
            });
        }
       
    }
    
}
