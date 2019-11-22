package com.keroz.notes.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.keroz.notes.model.NotesManager;
import com.keroz.notes.model.NotesManagerEvent;
import com.keroz.notes.model.INotesManagerListener;

public class NotesContentProvider implements ITreeContentProvider, INotesManagerListener {

	private TreeViewer treeViewer;
	private NotesManager notesManager;
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.treeViewer = (TreeViewer) viewer;
		if (notesManager != null) {
			notesManager.removeListener(this);
		}
		notesManager = (NotesManager) newInput;
		if (notesManager != null) {
			notesManager.addListener(this);
		}
	}
	
	@Override
	public Object[] getElements(Object arg0) {
		return notesManager.getNotes();
	}



	@Override
	public Object[] getChildren(Object arg0) {
		return null;
	}



	@Override
	public Object getParent(Object arg0) {
		return null;
	}



	@Override
	public boolean hasChildren(Object arg0) {
		return false;
	}

	@Override
	public void notesChanged(NotesManagerEvent event) {
//		treeViewer.getTree().setRedraw(false);
		treeViewer.remove(event.getNotesRemoved());
		treeViewer.add(treeViewer.getInput(), event.getNotesAdded());
//		treeViewer.getTree().setRedraw(true);
	}


}
