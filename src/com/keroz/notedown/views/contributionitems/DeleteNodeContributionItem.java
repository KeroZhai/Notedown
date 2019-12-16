package com.keroz.notedown.views.contributionitems;

import java.util.LinkedHashMap;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.keroz.notedown.model.Category;
import com.keroz.notedown.model.Note;
import com.keroz.notedown.model.NotesElement;
import com.keroz.notedown.views.Notedown;

/**
 *
 * @author z21542
 * @Date 2019年10月17日下午3:47:53
 */
public class DeleteNodeContributionItem extends ContributionItem {

    private Notedown notes;
    private MenuItem menuItem;
    private LinkedHashMap<String, Integer> labelId = new LinkedHashMap<String, Integer>();

    public DeleteNodeContributionItem(Notedown notes) {
        this.notes = notes;
        labelId.put("Yes", 0);
        labelId.put("Cancel", 1);
    }

    @Override
    public void fill(Menu menu, int index) {
        ITreeSelection selection = notes.getTreeViewer().getStructuredSelection();
        if (selection.isEmpty() || Category.UNCATEGORIED.equals(selection.getFirstElement())) {
            return;
        }
        menuItem = new MenuItem(menu, SWT.NONE, index);
        menuItem.setText("&Delete\tDelete");
        menuItem.setAccelerator(SWT.DEL);
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Object selection = notes.getTreeViewer().getStructuredSelection().getFirstElement();
                if (selection instanceof Category) {
                    Category category = (Category) selection;
                    MessageDialogWithToggle messageDialog = MessageDialogWithToggle.open(MessageDialogWithToggle.CONFIRM, notes.getShell(), "Confirm Delete",
                            "Are you sure you want to delete category \"" + category.getDisplayName()
                                    + "\" and nested notes?",
                            "&Delete actual file on disk(cannot be undone)", false, null, null, SWT.NONE,
                            labelId);
//                    MessageDialog messageDialog = new MessageDialog(
//                            notes.getShell(), "Confirm Delete", null, "Are you sure you want to delete category \""
//                                    + category.getDisplayName() + "\" and nested notes?",
//                            MessageDialog.CONFIRM, 0, "Yes", "Cancel") {
//
//                        @Override
//                        protected Control createCustomArea(Composite parent) {
//                            Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
//                            button.setText("&Delete actual file on disk(cannot be undone)");
//                            button.addSelectionListener(new SelectionAdapter() {
//                                @Override
//                                public void widgetSelected(SelectionEvent e) {
//                                    button.setSelection(!button.getSelection());
//                                }
//                            });
//                            return button;
//                        }
//                    };
                    if (messageDialog.getReturnCode() == MessageDialogWithToggle.OK) {
                        category.removeFromParent(messageDialog.getToggleState());
                        for (NotesElement notesElement : category.getChildren()) {
                            Note note = (Note) notesElement;
                            CTabItem tab = isNoteOpened(note);
                            if (tab != null) {
                                tab.dispose();
                            }
                        }
                    }
                } else if (selection instanceof Note) {
                    Note note = (Note) selection;
                    MessageDialogWithToggle messageDialog = MessageDialogWithToggle.open(MessageDialogWithToggle.CONFIRM, notes.getShell(), "Confirm Delete",
                            "Are you sure you want to delete note \"" + note.getDisplayName()
                                    + "\"?",
                            "&Delete actual file on disk(cannot be undone)", false, null, null, SWT.NONE,
                            labelId);
//                    MessageDialog messageDialog = new MessageDialog(notes.getShell(), "Confirm Delete", null,
//                            "Are you sure you want to delete note \"" + note.getDisplayName() + "\"?",
//                            MessageDialog.QUESTION, 0, "Yes", "Cancel") {
//                        @Override
//                        protected Control createCustomArea(Composite parent) {
//                            Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
//                            button.setText("&Delete actual file on disk(cannot be undone)");
//                            button.addSelectionListener(new SelectionAdapter() {
//                                @Override
//                                public void widgetSelected(SelectionEvent e) {
//                                    deleteActualFile = !deleteActualFile;
//                                }
//                            });
//                            return button;
//                        }
//                    };
                    if (messageDialog.getReturnCode() == MessageDialogWithToggle.OK) {
                        note.removeFromParent(messageDialog.getToggleState());
                        CTabItem tab = isNoteOpened(note);
                        if (tab != null) {
                            tab.dispose();
                        }
                    }
                }

            }
        });
        menuItem.setEnabled(!notes.getTreeViewer().getSelection().isEmpty());
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
