package com.keroz.notes.model;

import java.io.File;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import com.keroz.notes.views.Notes;

/**
 *
 * @author z21542
 * @Date 2019年10月21日下午5:21:53
 */
public class RenameNoteContributionItem extends ContributionItem {

	private MenuItem menuItem;
	private Notes notes;

	public RenameNoteContributionItem(Notes notes) {
		this.notes = notes;
		notes.getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				if (menuItem != null) {
					menuItem.setEnabled(!notes.getTreeViewer().getSelection().isEmpty());
				}
			}
		});
	}

	@Override
	public void fill(Menu menu, int index) {
		menuItem = new MenuItem(menu, SWT.NONE, index);
		menuItem.setText("&Rename...");
		menuItem.setAccelerator(SWT.ALT + SWT.SHIFT + 'R');
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Note note = (Note) notes.getTreeViewer().getStructuredSelection().getFirstElement();
				InputDialog inputDialog = new InputDialog(notes.getShell(), "Rename", "New Name:", note.getName(),
						null);
				if (inputDialog.open() == InputDialog.OK) {
					String newFileName = inputDialog.getValue().trim();
					String oldFilePath = note.getPath();
					String newFilePath = oldFilePath.substring(0, oldFilePath.lastIndexOf(File.separator))
							+ File.separator + newFileName + ".md";
					File newFile = new File(newFilePath);
					note.getFile().renameTo(newFile);
					note.setFile(newFile);
					updateNameInTree(note);
					CTabItem tab = isNoteOpened(note);
					if (tab != null) {
						tab.setText(newFileName);
						tab.setToolTipText(note.getPath());
					}
				}
			}
		});
		menuItem.setEnabled(!notes.getTreeViewer().getSelection().isEmpty());
	}

	private void updateNameInTree(Note note) {
		NotesManager.getManager().updateNote(note);
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
