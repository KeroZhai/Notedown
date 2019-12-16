package com.keroz.notedown.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import com.keroz.notedown.model.ElementChangeEvent;
import com.keroz.notedown.model.ElementChangedListener;
import com.keroz.notedown.model.NotesElement;
import com.keroz.notedown.model.NotesManager;

public class NotesContentProvider implements ITreeContentProvider, ElementChangedListener {

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
		return getChildren(arg0);
	}



	@Override
	public Object[] getChildren(Object arg0) {
	    if (arg0 instanceof NotesElement) {
            return ((NotesElement) arg0).getChildren();
        }
		return null;
	}



	@Override
	public Object getParent(Object arg0) {
	    if (arg0 instanceof NotesElement) {
            return ((NotesElement) arg0).getParent();
        }
		return null;
	}



	@Override
	public boolean hasChildren(Object arg0) {
	    if (arg0 instanceof NotesElement) {
            return ((NotesElement) arg0).getChildren().length > 0;
        }
		return false;
	}

	@Override
	public void elementChanged(ElementChangeEvent event) {
		treeViewer.getTree().setRedraw(false);
	    NotesElement element = event.item;
	    switch (event.eventType) {
        case ElementChangeEvent.ADD:
            treeViewer.add(element.getParent(), element);
            TreePath treePath = new TreePath(new NotesElement[] {element.getParent(), element});
            TreeSelection treeSelection = new TreeSelection(treePath);
            treeViewer.setSelection(treeSelection, true);
            break;
        case ElementChangeEvent.REMOVE:
            treeViewer.remove(element);
            break;
        case ElementChangeEvent.CHANGE:
            treeViewer.refresh(element.getParent(), true);
            break;
        default:
            break;
        }
	    treeViewer.getTree().setRedraw(true);
	}


}
