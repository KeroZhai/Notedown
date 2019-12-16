package com.keroz.notedown.views;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Point;

import com.keroz.notedown.model.Note;
import com.keroz.notedown.model.NotesElement;

public class NotesLableProvider extends CellLabelProvider {
    
    
    @Override
    public void update(ViewerCell cell) {
        Object element = cell.getElement();
        String text = "";
        if (element instanceof NotesElement) {
            text = ((NotesElement) element).getDisplayName();
        }
        cell.setText(text);
    }

	@Override
	public String getToolTipText(Object element) {
	    if (element instanceof Note) {
             return ((Note) element).getPath();
        }
	    return null;
	}
	
	@Override
	public Point getToolTipShift(Object object) {
	    return new Point(5, 5);
	}

	@Override
	public int getToolTipTimeDisplayed(Object object) {
	    return 5000;
	}
	
	@Override
	public int getToolTipDisplayDelayTime(Object object) {
	    return 0;
	}
	
}
