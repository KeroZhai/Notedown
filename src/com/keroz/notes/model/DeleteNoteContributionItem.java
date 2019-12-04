package com.keroz.notes.model;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.keroz.notes.util.Settings;
import com.keroz.notes.views.Notes;

/**
 *
 * @author z21542
 * @Date 2019年10月17日下午3:47:53
 */
public class DeleteNoteContributionItem extends ContributionItem {

	private Notes notes;
	private MenuItem menuItem;
	private boolean deleteFile = false;
	private ISelectionChangedListener selectionChangedListener;

	public DeleteNoteContributionItem(Notes notes) {
		this.notes = notes;
		this.selectionChangedListener = new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent arg0) {
                if (menuItem != null && !menuItem.isDisposed()) {
                    menuItem.setEnabled(!notes.getTreeViewer().getSelection().isEmpty());
                } else {
                    notes.getTreeViewer().removeSelectionChangedListener(this);
                }
            }
        };
		notes.getTreeViewer().addSelectionChangedListener(selectionChangedListener);
	}

	@Override
	public void fill(Menu menu, int index) {
		menuItem = new MenuItem(menu, SWT.NONE, index);
		menuItem.setText("&Delete");
		menuItem.setAccelerator(SWT.DEL);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Note note = (Note) notes.getTreeViewer().getStructuredSelection().getFirstElement();
				 MessageDialog messageDialog = new MessageDialog(notes.getShell(), "Confirm Delete", null,  "Are you sure you want to delete note \"" + note.getDisplayName() + "\"?",
			                MessageDialog.QUESTION, 0, "Yes", "Cancel") {
			            @Override
			            protected Control createCustomArea(Composite parent) {
			                Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
			                button.setText("&Delete actual file on disk(cannot be undone)");
			                button.addSelectionListener(new SelectionAdapter() {
			                    @Override
			                    public void widgetSelected(SelectionEvent e) {
			                        toggleDeleteFile();
			                    }
			                });
			                return button;
			            }
			        };
			        if (messageDialog.open() == 0) {
                        NotesManager.getManager().removeNote(note, deleteFile);
                        CTabItem tab = isNoteOpened(note);
                        if (tab != null) {
                            tab.dispose();
                        }
                    }
			   
			        
			}
		});
		menuItem.setEnabled(!notes.getTreeViewer().getSelection().isEmpty());
	}
	
	private void toggleDeleteFile() {
	    deleteFile = !deleteFile;
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
