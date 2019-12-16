package com.keroz.notedown.views.contributionitems;

import java.io.File;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.keroz.notedown.model.Category;
import com.keroz.notedown.model.ElementChangeEvent;
import com.keroz.notedown.model.Note;
import com.keroz.notedown.model.NotesElement;
import com.keroz.notedown.model.NotesManager;
import com.keroz.notedown.views.Notedown;

/**
 *
 * @author z21542
 * @Date 2019年10月21日下午5:21:53
 */
public class RenameNodeContributionItem extends ContributionItem {

    private MenuItem menuItem;
    private Notedown notes;

    public RenameNodeContributionItem(Notedown notes) {
        this.notes = notes;
    }

    @Override
    public void fill(Menu menu, int index) {
        ITreeSelection selection = notes.getTreeViewer().getStructuredSelection();
        if (selection.isEmpty() || Category.UNCATEGORIED.equals(selection.getFirstElement())) {
            return;
        }
        menuItem = new MenuItem(menu, SWT.NONE, index);
        menuItem.setText("&Rename...\tAlt+Shift+R");
        menuItem.setAccelerator(SWT.ALT + SWT.SHIFT + 'R');
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                NotesElement selection = (NotesElement) notes.getTreeViewer().getStructuredSelection()
                        .getFirstElement();
                String oldName = selection.getDisplayName();
                InputDialog inputDialog = new InputDialog(notes.getShell(), "Rename", "New Name:", oldName, null);
                if (inputDialog.open() == InputDialog.OK) {
                    String newName = inputDialog.getValue().trim();
                    if (oldName.equals(newName)) {
                        return;
                    }
                    if (checkIsDuplicated(selection, newName)) {
                        MessageDialog.openError(notes.getShell(), "Name Duplicated",
                                "\"" + newName + "\" already exists.");
                        return;
                    }
                    if (selection instanceof Category) {
                        Category category = (Category) selection;
                        category.setDisplayName(newName);
                        updateNameInTree(category);
                    } else if (selection instanceof Note) {
                        Note note = (Note) selection;
                        String oldFilePath = note.getPath();
                        String newFilePath = oldFilePath.substring(0, oldFilePath.lastIndexOf(File.separator))
                                + File.separator + newName + ".md";
                        File newFile = new File(newFilePath);
                        note.getFile().renameTo(newFile);
                        note.setFile(newFile);
                        updateNameInTree(note);
                        CTabItem tab = isNoteOpened(note);
                        if (tab != null) {
                            tab.setText(newName);
                            tab.setToolTipText(note.getPath());
                        }
                    }
                }
            }
        });
    }

    private boolean checkIsDuplicated(NotesElement selection, String newName) {
        return NotesManager.getManager().getCategories().stream().anyMatch(category -> {
            if (category != selection && category.getDisplayName().equals(newName)) {
                return true;
            }
            return category.getNotes().stream()
                    .anyMatch(note -> note != selection && note.getDisplayName().equals(newName));
        });
    }

    private void updateNameInTree(NotesElement node) {
        node.fireNotesElementChanged(new ElementChangeEvent(ElementChangeEvent.CHANGE, node));
    }

    private CTabItem isNoteOpened(Note note) {
        for (CTabItem tab : notes.getTabFolder().getItems()) {
            if (note.equals(tab.getData())) {
                return tab;
            }
        }
        return null;
    }

}
