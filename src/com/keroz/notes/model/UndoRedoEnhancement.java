package com.keroz.notes.model;

import java.util.Stack;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 *
 * @author z21542
 * @Date 2019年10月22日下午5:52:13
 */
public class UndoRedoEnhancement implements KeyListener, ExtendedModifyListener {
	/**
     * Encapsulation of the Undo and Redo stack(s).
     */
    private static class UndoRedoStack<T> {

        private Stack<T> undo;
        private Stack<T> redo;

        public UndoRedoStack() {
            undo = new Stack<T>();
            redo = new Stack<T>();
        }

        public void pushUndo(T delta) {
            undo.add(delta);
        }

        public void pushRedo(T delta) {
            redo.add(delta);
        }

        public T popUndo() {
            T res = undo.pop();
            return res;
        }

        public T popRedo() {
            T res = redo.pop();
            return res;
        }

        public void clearRedo() {
            redo.clear();
        }

        public boolean hasUndo() {
            return !undo.isEmpty();
        }

        public boolean hasRedo() {
            return !redo.isEmpty();
        }

    }

    private StyledText editor;
    
    private MenuItem popupUndoItem;
    
    private MenuItem menubarUndoItem;
    
    private MenuItem menubarRedoItem;

    private UndoRedoStack<ExtendedModifyEvent> stack;

    private boolean isUndo;

    private boolean isRedo;
    
    /**
     * Creates a new instance of this class. Automatically starts listening to
     * corresponding key and modify events coming from the given
     * <var>editor</var>.
     * 
     * @param editor
     *            the text field to which the Undo-Redo functionality should be
     *            added
     */
    public UndoRedoEnhancement(StyledText editor) {
        editor.addExtendedModifyListener(this);
        editor.addKeyListener(this);

        this.editor = editor;
        popupUndoItem = editor.getMenu().getItem(0);
        menubarUndoItem = editor.getShell().getMenuBar().getItem(1).getMenu().getItem(0);
        menubarRedoItem = editor.getShell().getMenuBar().getItem(1).getMenu().getItem(1);
        stack = new UndoRedoStack<ExtendedModifyEvent>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.
     * KeyEvent)
     */
    public void keyPressed(KeyEvent e) {
        // Listen to CTRL+Z for Undo, to CTRL+Y or CTRL+SHIFT+Z for Redo
        boolean isCtrl = (e.stateMask & SWT.CTRL) > 0;
        boolean isAlt = (e.stateMask & SWT.ALT) > 0;
        if (isCtrl && !isAlt) {
            boolean isShift = (e.stateMask & SWT.SHIFT) > 0;
            if (!isShift && e.keyCode == 'z') {
                undo();
            } else if (!isShift && e.keyCode == 'y' || isShift
                    && e.keyCode == 'z') {
                redo();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events
     * .KeyEvent)
     */
    public void keyReleased(KeyEvent e) {
        // ignore
    }

    /**
     * Creates a corresponding Undo or Redo step from the given event and pushes
     * it to the stack. The Redo stack is, logically, emptied if the event comes
     * from a normal user action.
     * 
     * @param event
     * @see org.eclipse.swt.custom.ExtendedModifyListener#modifyText(org.eclipse.
     *      swt.custom.ExtendedModifyEvent)
     */
    public void modifyText(ExtendedModifyEvent event) {
        if (isUndo) {
            stack.pushRedo(event);
        } else { // is Redo or a normal user action
            stack.pushUndo(event);
            if (!isRedo) {
                stack.clearRedo();
                // TODO Switch to treat consecutive characters as one event?
            }
        }
    }

    /**
     * Performs the Undo action. A new corresponding Redo step is automatically
     * pushed to the stack.
     */
    public void undo() {
        if (stack.hasUndo()) {
            isUndo = true;
            revertEvent(stack.popUndo());
            isUndo = false;
            menubarRedoItem.setEnabled(true);
            if (!stack.hasUndo()) {
                popupUndoItem.setEnabled(false);
                menubarUndoItem.setEnabled(false);
            }
        }
    }

    /**
     * Performs the Redo action. A new corresponding Undo step is automatically
     * pushed to the stack.
     */
    public void redo() {
        if (stack.hasRedo()) {
            isRedo = true;
            revertEvent(stack.popRedo());
            isRedo = false;
            popupUndoItem.setEnabled(true);
            menubarUndoItem.setEnabled(true);
            if (!stack.hasRedo()) {
                menubarRedoItem.setEnabled(false);
            }
        }
    }

    /**
     * Reverts the given modify event, in the way as the Eclipse text editor
     * does it.
     * 
     * @param event
     */
    private void revertEvent(ExtendedModifyEvent event) {
        editor.replaceTextRange(event.start, event.length, event.replacedText);
        // (causes the modifyText() listener method to be called)

        editor.setSelectionRange(event.start, event.replacedText.length());
    }

}
