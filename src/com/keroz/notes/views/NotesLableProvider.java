package com.keroz.notes.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.keroz.notes.model.Note;

public class NotesLableProvider extends LabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Note) {
			return ((Note) element).getName();
		}
		return "";
	}

	

}
