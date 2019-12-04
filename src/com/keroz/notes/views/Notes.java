package com.keroz.notes.views;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolder2Adapter;
import org.eclipse.swt.custom.CTabFolderEvent;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.keroz.notes.model.DeleteNoteContributionItem;
import com.keroz.notes.model.Note;
import com.keroz.notes.model.NotesManager;
import com.keroz.notes.model.RenameNoteContributionItem;
import com.keroz.notes.model.UndoRedoEnhancement;
import com.keroz.notes.util.Markdown2HTML;
import com.keroz.notes.util.MarkdownHelper;
import com.keroz.notes.util.Resources;
import com.keroz.notes.util.Settings;
import com.keroz.notes.util.ThemeUtils;

public class Notes {
    
    public static final int RUNNING = 0;
    public static final int CLOSED = 1;
    public static final int RESTART = 2;
    
    private int status = CLOSED;
    
    private static final int DEFAULT_WIDTH = 1440;
    private static final int DEFALUT_HEIGHT = 900;

    private int width;
    private int height;
    private Color treeForegroundColor;
    private Color treeBackgroundColor;
    private Color sourceForegroundColor;
    private Color sourceBackgroundColor;
    private Color globalForegroundColor;
    private Color globalBackgroundColor;
   
    private Font font;

    private Display display;
    private Shell shell;
    private TreeViewer treeViewer;
    private CTabFolder tabFolder;
    private String theme;

    public Notes(int width, int height) {
        this.width = width;
        this.height = height;
        init();
        enableDropDown();
        hookMenu();
        hookSashForm();
        enableDragNDrop();
        hookContextMenuForTree();
        initTabFolder();
    }

    public Notes() {
        this(DEFAULT_WIDTH, DEFALUT_HEIGHT);
    }

    private MenuItem save;
    private MenuItem saveAs;
    private MenuItem export;

    /**
     * 挂载主菜单栏
     */
    public void hookMenu() {

        Menu menuBar = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menuBar);
        MenuItem fileMenu = new MenuItem(menuBar, SWT.CASCADE);
        /*
         * &表示紧跟其后的字符是助记符 即当按下F时 相当于选中了File
         */
        fileMenu.setText("&File");
        Menu fileSubMenu = new Menu(shell, SWT.DROP_DOWN);
        fileMenu.setMenu(fileSubMenu);
        MenuItem newFile = new MenuItem(fileSubMenu, SWT.NONE);
        newFile.setText("&New\tCtrl+N");
        newFile.setAccelerator(SWT.CTRL + 'N');
        newFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openNewNote();
            }
        });
        MenuItem openFile = new MenuItem(fileSubMenu, SWT.NONE);
        openFile.setText("&Open File...\tCtrl+O");
        openFile.setAccelerator(SWT.CTRL + 'O');
        openFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openFile();
            }
        });
        save = new MenuItem(fileSubMenu, SWT.NONE);
        save.setText("&Save\tCtrl+S");
        save.setAccelerator(SWT.CTRL + 'S');
        save.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doSave();
            }
        });
        save.setEnabled(false);
        saveAs = new MenuItem(fileSubMenu, SWT.NONE);
        saveAs.setText("Save As...");
        saveAs.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doSaveAs();
            }
        });
        saveAs.setEnabled(false);
        /**
         * 编码有问题 暂时不启用
         */
//        export = new MenuItem(fileSubMenu, SWT.NONE);
//        export.setText("Export...");
//        export.addSelectionListener(new SelectionAdapter() {
//            @Override
//            public void widgetSelected(SelectionEvent e) {
//                export();
//            }
//        });
//        export.setEnabled(false);
        MenuItem separator1 = new MenuItem(fileSubMenu, SWT.SEPARATOR);
        MenuItem restart = new MenuItem(fileSubMenu, SWT.NONE);
        restart.setText("&Restart");
        restart.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                restart();
            }
        });
        MenuItem exit = new MenuItem(fileSubMenu, SWT.NONE);
        exit.setText("&Exit");
        exit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                close();
            }
        });
        
        MenuItem editMenuItem = new MenuItem(menuBar, SWT.CASCADE);
        editMenuItem.setText("&Edit");
        editMenuItem.setEnabled(false);
        Menu editSubMenu = new Menu(shell, SWT.DROP_DOWN);
        editMenuItem.setMenu(editSubMenu);
        MenuItem undo = new MenuItem(editSubMenu, SWT.PUSH);
        undo.setText("Undo Typing\tCtrl+Z");
        undo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StyledText styledText = getCurrentAcitveTextEditor();
                UndoRedoEnhancement undoRedoEnhancement = (UndoRedoEnhancement) styledText.getData();
                undoRedoEnhancement.undo();
            }
        });
        undo.setEnabled(false);
        MenuItem redo = new MenuItem(editSubMenu, SWT.PUSH);
        redo.setText("Redo\tCtrl+Y");
        redo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                StyledText styledText = getCurrentAcitveTextEditor();
                UndoRedoEnhancement undoRedoEnhancement = (UndoRedoEnhancement) styledText.getData();
                undoRedoEnhancement.redo();
            }
        });
        redo.setEnabled(false);
        MenuItem preferencesMenuItemMenuItem = new MenuItem(menuBar, SWT.CASCADE);
        preferencesMenuItemMenuItem.setText("&Preferences");
        Menu preferencesSubMenu = new Menu(shell, SWT.DROP_DOWN);
        preferencesMenuItemMenuItem.setMenu(preferencesSubMenu);
        MenuItem fontMenuItem = new MenuItem(preferencesSubMenu, SWT.CASCADE);
        fontMenuItem.setText("Source - Font");
        Menu fontSubMenu = new Menu(shell, SWT.DROP_DOWN);
        fontMenuItem.setMenu(fontSubMenu);
        addMenuItemForFontMenu(fontSubMenu, "Larger", 2);
        addMenuItemForFontMenu(fontSubMenu, "Smaller", -2);
        MenuItem advanceFontSettingItem = new MenuItem(fontSubMenu, SWT.PUSH);
        advanceFontSettingItem.setText("Advanced Setting");
        advanceFontSettingItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FontDialog fontDialog = new FontDialog(shell);
                fontDialog.setText("Select Font");
                fontDialog.setRGB(sourceForegroundColor.getRGB());
                fontDialog.setFontList(font.getFontData());
                FontData fontData = fontDialog.open();
                if (fontData != null) {
                    font = new Font(display, fontData);
                    Settings.setProperty(Settings.FONT_TYPE, fontData.getName());
                    Settings.setProperty(Settings.FONT_SIZE, fontData.getHeight());
                    Settings.setProperty(Settings.FONT_STYLE, fontData.getStyle());
                    refreshFont(font, new Color(display, fontDialog.getRGB()));
                }
            }
        });
        MenuItem themeMenuItem = new MenuItem(preferencesSubMenu, SWT.CASCADE);
        themeMenuItem.setText("Theme");
        Menu themeSubMenu = new Menu(shell, SWT.DROP_DOWN);
        themeMenuItem.setMenu(themeSubMenu);
        fillThemeMenu(themeSubMenu);
        MenuItem helpMenu = new MenuItem(menuBar, SWT.CASCADE);
        helpMenu.setText("&Help");
        Menu helpSubMenu = new Menu(shell, SWT.DROP_DOWN);
        helpMenu.setMenu(helpSubMenu);
        MenuItem welcome = new MenuItem(helpSubMenu, SWT.NONE);
        welcome.setText("Welcome");
        welcome.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openNote(Note.WELCOME);
            }
        });
        MenuItem about = new MenuItem(helpSubMenu, SWT.NONE);
        about.setText("About Notes");
        about.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                MessageDialog messageDialog = new MessageDialog(shell, "About Notes", null,
                        "Notes is a simple editor which enables you to write notes using Markdown.\n\n"
                                + "ATTENTION: For study and PERSONAL USE ONLY",
                        MessageDialog.INFORMATION, 0, "OK") {
                    @Override
                    protected Control createCustomArea(Composite parent) {
                        Link link = new Link(parent, SWT.WRAP);
                        link.setText(
                                "This product includes software developed by other open source projects.\nThe core part which parses Markdown into HTML is based on vsch/flexmark-java.\n"
                                        + "Please visit <a>here</a> to get more infomation.");
                        link.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                Program.launch("https://github.com/vsch/flexmark-java");
                            }
                        });
                        return link;
                    }
                };
                if (messageDialog.open() == 0) {
                    messageDialog.close();
                }
            }
        });
    }

    void hookSashForm() {
        shell.setLayout(new FillLayout());

        SashForm form = new SashForm(shell, SWT.HORIZONTAL);
        form.setBackground(globalBackgroundColor);
        form.setLayout(new FillLayout());

        Composite fileListComposite = new Composite(form, SWT.WRAP);
        TreeColumnLayout layout = new TreeColumnLayout();
        fileListComposite.setLayout(layout);
        treeViewer = new TreeViewer(fileListComposite);
        treeViewer.getTree().setForeground(treeForegroundColor);
        treeViewer.getTree().setBackground(treeBackgroundColor);
        treeViewer.setContentProvider(new NotesContentProvider());
        treeViewer.setLabelProvider(new NotesLableProvider());
        ColumnViewerToolTipSupport.enableFor(treeViewer);
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent arg0) {
                Note note = (Note) treeViewer.getStructuredSelection().getFirstElement();
                openNote(note);
            }
        });
        treeViewer.setInput(NotesManager.getManager());
        Composite editorComposite = new Composite(form, SWT.NONE);
        form.setWeights(new int[] { 20, 80 });
        editorComposite.setLayout(new FillLayout());
        tabFolder = new CTabFolder(editorComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
        tabFolder.setForeground(globalForegroundColor);
        tabFolder.setBackground(globalBackgroundColor);
        tabFolder.setSelectionForeground(globalForegroundColor);
        tabFolder.setSelectionBackground(sourceBackgroundColor);
        CTabItem newTab = new CTabItem(tabFolder, SWT.NONE);
        newTab.setText("+");
        newTab.setToolTipText("Create a new note");
        tabFolder.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (tabFolder.getSelection().equals(newTab)) {
                    openNewNote();
                }
                Note note = (Note) tabFolder.getSelection().getData();
                if (note != null && note.isSaveAllowed()) {
                    save.setEnabled(true);
                    saveAs.setEnabled(true);
//                    export.setEnabled(true);
                } else {
                    save.setEnabled(false);
                    saveAs.setEnabled(false);
//                    export.setEnabled(false);
                }
                boolean isSourcePageActive = ((CTabFolder) tabFolder.getSelection().getControl()).getSelectionIndex() == 1;
                shell.getMenuBar().getItem(1).setEnabled(isSourcePageActive);
                
            }
        });
        tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
            @Override
            public void close(CTabFolderEvent event) {
                CTabItem tabToClose = (CTabItem) event.item;
                event.doit = isReadyToCloseTab(tabToClose);
                if (event.doit) {
                    Note note= (Note) tabToClose.getData();
                    note.setOpened(false);
                    NotesManager.getManager().update();
                }
            }
        });
        if (Settings.getProperty(Settings.SHOW_WELCOME)) {
            openNote(Note.WELCOME);
            Settings.setProperty(Settings.SHOW_WELCOME, Boolean.FALSE);
        }
    }

    void initTabFolder() {
        for (Note note : NotesManager.getManager().getNotes()) {
            if (note.isOpened()) {
                openNote(note);
            }
        }
    }

    void enableDragNDrop() {
        Listener listener = new Listener() {
            boolean drag = false;
            boolean exitDrag = false;
            CTabItem dragItem;
            Cursor cursorSizeAll = new Cursor(null, SWT.CURSOR_HAND);
            Cursor cursorIbeam = new Cursor(null, SWT.CURSOR_NO);
            Cursor cursorArrow = new Cursor(null, SWT.CURSOR_ARROW);

            public void handleEvent(Event e) {
                Point p = new Point(e.x, e.y);
                CTabItem current = tabFolder.getItem(new Point(p.x, 1));
                if (e.type == SWT.DragDetect) {
                    p = tabFolder.toControl(display.getCursorLocation()); // see eclipse bug 43251
                }
                switch (e.type) {
                // 拖拉Tab
                case SWT.DragDetect: {
                    CTabItem item = tabFolder.getItem(p);
                    if (item == null) {
                        return;
                    }

                    drag = true;
                    exitDrag = false;
                    dragItem = item;

                    // 换鼠标形状
                    tabFolder.getShell().setCursor(cursorIbeam);
                    break;
                }
                // 鼠标进入区域
                case SWT.MouseEnter:
                    if (exitDrag) {
                        exitDrag = false;
                        drag = e.button != 0;
                    }
                    break;
                // 鼠标离开区域
                case SWT.MouseExit:
                    if (drag) {
                        tabFolder.setInsertMark(null, false);
                        exitDrag = true;
                        drag = false;

                        // 换鼠标形状
                        tabFolder.getShell().setCursor(cursorArrow);
                    }
                    break;
                // 松开左键
                case SWT.MouseUp: {
                    if (!drag) {
                        return;
                    }
                    tabFolder.setInsertMark(null, false);
                    CTabItem item = null;
                    CTabItem first = tabFolder.getItem(0);
                    CTabItem last = tabFolder.getItem(tabFolder.getItemCount() - 2);
                    if (first == last) {
                        return;
                    }
                    if (p.x > last.getBounds().x + last.getBounds().width) {
                        item = last;
                    } else if (p.x < first.getBounds().x) {
                        item = first;
                    } else {
                        if ((p.x >= current.getBounds().x + current.getBounds().width / 2)) {
                            
                            item = current;
                        } else {
                            item = tabFolder
                                    .getItem(new Point(current.getBounds().x - 1, 1));
                        }

                    }

                    if (item != null && (dragItem != item)) {

                        int index = tabFolder.indexOf(item);
                        int newIndex = tabFolder.indexOf(item);
                        int oldIndex = tabFolder.indexOf(dragItem);
                        if (newIndex != oldIndex) {
                            boolean after = newIndex > oldIndex;
                            index = after ? index + 1 : index/* - 1 */;
                            index = Math.max(0, index);

                            CTabItem newItem = new CTabItem(tabFolder, SWT.CLOSE, index);
                            newItem.setText(dragItem.getText());
                            newItem.setToolTipText(dragItem.getToolTipText());
                            Control c = dragItem.getControl();
                            newItem.setControl(c);
                            newItem.setData(dragItem.getData());
                            dragItem.dispose();

                            tabFolder.setSelection(newItem);

                        }
                    }
                    drag = false;
                    exitDrag = false;
                    dragItem = null;

                    // 换鼠标形状
                    tabFolder.getShell().setCursor(cursorArrow);
                    break;
                }
                // 鼠标移动
                case SWT.MouseMove: {
                    if (!drag) {
                        return;
                    }
                    CTabItem item = tabFolder.getItem(new Point(p.x, 2));
                    if (item == null) {
                        tabFolder.setInsertMark(null, false);
                        return;
                    }
                    Rectangle rect = item.getBounds();
                    boolean after = p.x > rect.x + rect.width / 2;
                    tabFolder.setInsertMark(item, after);

                    // 换鼠标形状
                    tabFolder.getShell().setCursor(cursorSizeAll);
                    break;
                }
                }
            }
        };
        tabFolder.addListener(SWT.DragDetect, listener);
        tabFolder.addListener(SWT.MouseUp, listener);
        tabFolder.addListener(SWT.MouseMove, listener);
        tabFolder.addListener(SWT.MouseExit, listener);
        tabFolder.addListener(SWT.MouseEnter, listener);
    }

    void hookContextMenuForTree() {
        MenuManager treeMenuManager = new MenuManager("#PopupMenu");
//        DeleteNoteContributionItem deleteNoteContributionItem = new DeleteNoteContributionItem(this);
//        RenameNoteContributionItem renameNoteContributionItem = new RenameNoteContributionItem(this);
        treeMenuManager.setRemoveAllWhenShown(true);
        treeMenuManager.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager menuManager) {
                menuManager.add(new DeleteNoteContributionItem(Notes.this));
                menuManager.add(new RenameNoteContributionItem(Notes.this));
//				Action delete = new Action("&Delete") {
//					@Override
//					public void run() {
//						Note note = (Note) treeViewer.getStructuredSelection().getFirstElement();
//						((NotesManager) treeViewer.getInput()).removeNote(note);
//					}
//				};
//				delete.setAccelerator(SWT.DEL);
//				treeMenuManager.add(delete);
//				delete.setEnabled(false);
            }
        });
        Tree tree = treeViewer.getTree();
        Menu contextMenu = treeMenuManager.createContextMenu(tree);
        tree.setMenu(contextMenu);
    }
    
    void hookContextMenuForEditor(StyledText styledText) {
        Menu editorPopupMenu = new Menu(shell, SWT.POP_UP);
        MenuItem undo = new MenuItem(editorPopupMenu, SWT.PUSH);
        undo.setText("Undo Typing\tCtrl+Z");
        undo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                UndoRedoEnhancement undoRedoEnhancement = (UndoRedoEnhancement) styledText.getData();
                undoRedoEnhancement.undo();
            }
        });
        undo.setEnabled(false);
        MenuItem separator1 = new MenuItem(editorPopupMenu, SWT.SEPARATOR);
        MenuItem cut = new MenuItem(editorPopupMenu, SWT.PUSH);
        cut.setText("Cut\tCtrl+X");
        cut.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                styledText.cut();;
            }
        });
        MenuItem copy = new MenuItem(editorPopupMenu, SWT.PUSH);
        copy.setText("Copy\tCtrl+C");
        copy.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                styledText.copy();
            }
        });
        MenuItem paste = new MenuItem(editorPopupMenu, SWT.PUSH);
        paste.setText("Paste\tCtrl+V");
        paste.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                styledText.paste();
            }
        });
        MenuItem separator2 = new MenuItem(editorPopupMenu, SWT.SEPARATOR);
        MenuItem fontMenuItem = new MenuItem(editorPopupMenu, SWT.CASCADE);
        Menu fontMenu = new Menu(shell, SWT.DROP_DOWN);
        fontMenuItem.setText("Font");
        fontMenuItem.setMenu(fontMenu);
        addMenuItemForFontMenu(fontMenu, "Larger", 2);
        addMenuItemForFontMenu(fontMenu, "Smaller", -2);
        MenuItem separator3 = new MenuItem(editorPopupMenu, SWT.SEPARATOR);
        MenuItem insertMenuItem = new MenuItem(editorPopupMenu, SWT.CASCADE);
        insertMenuItem.setText("Insert...");
        Menu insertMenu = new Menu(shell, SWT.DROP_DOWN);
        insertMenuItem.setMenu(insertMenu);
        MenuItem link = new MenuItem(insertMenu, SWT.PUSH);
        link.setText("Link");
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                styledText.insert("[Alt Text](enter URL here)");
                styledText.setSelectionRange(styledText.getSelectionRange().x + 11, 14);
            }
        });
        MenuItem image = new MenuItem(insertMenu, SWT.PUSH);
        image.setText("Image");
        image.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                insertImage(styledText);
            }
        });
        MenuItem table = new MenuItem(insertMenu, SWT.PUSH);
        table.setText("Table");
        table.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                insertTable(styledText);
            }
        });
        MenuItem wrapWithMenuItem = new MenuItem(editorPopupMenu, SWT.CASCADE);
        Menu wrapWithMenu = new Menu(shell, SWT.DROP_DOWN);
        wrapWithMenuItem.setText("Wrap/Unwrap With");
        wrapWithMenuItem.setMenu(wrapWithMenu);
        addMenuItemForWrapWithMenu(wrapWithMenu, styledText, MarkdownHelper.BOLD);
        addMenuItemForWrapWithMenu(wrapWithMenu, styledText, MarkdownHelper.ITALIC);
        addMenuItemForWrapWithMenu(wrapWithMenu, styledText, MarkdownHelper.STRIKE_THROUGH);
        addMenuItemForWrapWithMenu(wrapWithMenu, styledText, MarkdownHelper.CODE);
        addMenuItemForWrapWithMenu(wrapWithMenu, styledText, MarkdownHelper.CODE_BLOCK);
        addMenuItemForWrapWithMenu(wrapWithMenu, styledText, MarkdownHelper.OTHER);
        styledText.setMenu(editorPopupMenu);
        styledText.addMenuDetectListener(new MenuDetectListener() {
            @Override
            public void menuDetected(MenuDetectEvent arg0) {
                boolean isTextSelected = styledText.isTextSelected();
                cut.setEnabled(isTextSelected);
                copy.setEnabled(isTextSelected);
                wrapWithMenuItem.setEnabled(isTextSelected);
            }
        });
        
    }
    
    private void insertImage(StyledText styledText) {
        FileDialog fileDialog = new FileDialog(getShell());
        fileDialog.setFilterExtensions(new String[] { "Images (*.jpg;*.jpeg;*.png;*.bmp)", "*.jpg", "*.jpeg", "*.png", "*.bmp"});
        fileDialog.setText("Insert Image");
        String imagePath = null;
        if ((imagePath = fileDialog.open()) != null) {
            String insertText = "![Alt Text](" + imagePath + " \"\")";
            styledText.insert(insertText);
            styledText.setSelectionRange(styledText.getSelectionRange().x + (insertText.length() - 2), 0);
        }
    }
    
    private void insertTable(StyledText styledText) {
        InputDialog inputDialog = new InputDialog(getShell(), "Insert Table", "Rows and cols ", "1,1", new IInputValidator() {
            
            @Override
            public String isValid(String rowsAndCols) {
                if (rowsAndCols.matches("([0-9]+,( *)[0-9]+){1}")) {
                    String[] split = rowsAndCols.split(",");
                    int rowNum = Integer.valueOf(split[0]);
                    int colNum = Integer.valueOf(split[1]);
                    if (rowNum > 0 && rowNum <= 20 && colNum > 0 && colNum <=10) {
                        return null;
                    } else {
                        return "Range limit : (1~20, 1~10)";
                    }
                } else if (rowsAndCols.isEmpty()) {
                    return null;
                }
                return "Invalid input";
            }
        });
        if (inputDialog.open() == InputDialog.OK) {
            String rowsAndCols = inputDialog.getValue();
            String[] split = rowsAndCols.split(",");
            int rowNum = Integer.valueOf(split[0]);
            int colNum = Integer.valueOf(split[1]);
            StringBuilder tableContent = new StringBuilder("\n\n");
            addTableContent(tableContent, "head", 1, colNum);
            addTableContent(tableContent, "---", 1, colNum);
            addTableContent(tableContent, "   ", rowNum, colNum);
            styledText.insert(tableContent.toString() + "\n");
            styledText.setSelectionRange(styledText.getSelectionRange().x + 2, 4);
        }
    }
    
    private void addTableContent(StringBuilder tableContent, String colContent, int rowNum, int colNum) {
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                tableContent.append("| " + colContent+ " ");
            }
            tableContent.append("|\n");
        }
    }
    
    void addMenuItemForFontMenu(Menu parentMenu, String name, int sizeChange) {
        MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
        menuItem.setText(name);
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int fontSize2Set = Settings.getProperty(Settings.FONT_SIZE) + sizeChange;
                if (fontSize2Set > 72 || fontSize2Set <= 0) {
                    return;
                }
                Settings.setProperty(Settings.FONT_SIZE, fontSize2Set);
                FontData fontData = font.getFontData()[0];
                fontData.setHeight(Settings.getProperty(Settings.FONT_SIZE));
                font = new Font(display, fontData);
                refreshFont(font, null);
            }
        });
    }
    
    void refreshFont(Font font, Color fontColor) {
        if (fontColor != null) {
            sourceForegroundColor.dispose();
            sourceForegroundColor = fontColor;
        }
        for (CTabItem tab : tabFolder.getItems()) {
            if (tab.getControl() != null) {
                StyledText styledText = (StyledText) ((CTabFolder) tab.getControl()).getItem(1).getControl();
                styledText.setFont(font);
                if (fontColor != null) {
                    styledText.setForeground(sourceForegroundColor);
                }
            }
        }
    }
    
    void addMenuItemForWrapWithMenu(Menu parentMenu, StyledText styledText, MarkdownHelper helper) {
        MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
        menuItem.setText(helper.getType());
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (helper.equals(MarkdownHelper.OTHER)) {
                    InputDialog inputDialog = new InputDialog(shell, "Wrap/Unwrap with", "Mark:", helper.getMark(),
                            null);
                    if (inputDialog.open() == InputDialog.OK) {
                        helper.setMark(inputDialog.getValue().trim());
                    } else {
                        return;
                    }
                }
                Point selection = styledText.getSelectionRange();
                String wrappedSelectionText = helper.wrap(styledText.getSelectionText());
                styledText.replaceTextRange(selection.x, selection.y, wrappedSelectionText);
                styledText.setSelectionRange(selection.x, wrappedSelectionText.length());
            }
        });
    }
    
    void fillThemeMenu(Menu parentMenu) {
        addThemeMemuItem(parentMenu, ThemeUtils.WARM_THEME);
        addThemeMemuItem(parentMenu, ThemeUtils.LIGHT_THEME);
        addThemeMemuItem(parentMenu, ThemeUtils.DARK_THEME);
        for (MenuItem theme : parentMenu.getItems()) {
            theme.setSelection(Settings.getProperty(Settings.THEME).equals(theme.getText()));
        }
    }
    
    void addThemeMemuItem(Menu parentMenu, String theme) {
        MenuItem menuItem = new MenuItem(parentMenu, SWT.RADIO);
        menuItem.setText(theme);
        menuItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (menuItem.getSelection()) {
                    Settings.setProperty(Settings.THEME, theme);
                    if (MessageDialog.openConfirm(shell, "Theme Change", "A restart is required for the theme change to take full effect.\nWould you like to restart now?")) {
                        restart();
                    }
                }
            }
        });
    }

    public int open() {
        shell.open();
        status = RUNNING;
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
        return status;
    }

    private boolean confirmClose() {
        if (Settings.getProperty(Settings.DIRECTLY_EXIT)) {
            return true;
        }
        MessageDialog messageDialog = new MessageDialog(shell, "Confirm Exit", null, "Do you want to exit the Notes?",
                MessageDialog.QUESTION, 0, "Exit", "Cancel") {
            @Override
            protected Control createCustomArea(Composite parent) {
                Button button = new Button(parent, SWT.CHECK | SWT.LEFT);
                button.setText("&Remember my decision");
                button.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Settings.setProperty(Settings.DIRECTLY_EXIT,
                                !Settings.getProperty(Settings.DIRECTLY_EXIT));
                    }
                });
                return button;
            }
        };
        return messageDialog.open() == 0 ? true : false;
//		int result = MessageDialog.open(SWT.ICON_QUESTION, shell, "Confirm Exit", "Do you want to exit the Notes?", SWT.NONE,
//				"Exit", "Exit Without Asking", "Cancel");
//		switch (result) {
//		case 1:
//			Settings.setProperty(Settings.DIRECTLY_EXIT, "true");
//		case 0:
//			return true;
//		default:
//			return false;
//		}
    }

    public void close() {
        if (confirmClose()) {
            closeWithoutConfirm();
        }
    }
    
    public void closeWithoutConfirm() {
        if (isReadyToCloseShell()) {
            Settings.updateSettings();
            shell.dispose();
            status = CLOSED;
        }
    }
    
    public void restart() {
        closeWithoutConfirm();
        if (status == CLOSED) {
            status = RESTART;
        }
    }
    
    private boolean isReadyToCloseShell() {
        boolean isReady = true;
        for (CTabItem tab : tabFolder.getItems()) {
            if (!isReadyToCloseTab(tab)) {
                isReady = false;
                break;
            }
        }
        return isReady;
    }

    private boolean isReadyToCloseTab(CTabItem tab) {
        boolean isReady = true;
        Note note = (Note) tab.getData();
        if (note != null && note.isEdited()) {
            int result = MessageDialog.open(SWT.ICON_QUESTION, shell, "Save Note",
                    "\"" + note.getDisplayName() + "\" has been modified. Do you want to save changes?", SWT.NONE, "Save",
                    "Don't save", "Cancel");
            switch (result) {
            case 0:
                doSave();
            case 1:
                isReady = true;
                break;
            default:
                isReady = false;
                break;
            }
        }
        return isReady;
    }

    void init() {
        /*
         * For s-leak
         */
        DeviceData data = new DeviceData();
        data.tracking = true;
        display = new Display(data);
        /*
         * Uncomment to enable s-leak
         */
//        Sleak sleak = new Sleak();
//        sleak.open();
        theme = Settings.getProperty(Settings.THEME);
        treeForegroundColor = new Color(display, ThemeUtils.getTreeForegroundRgb());
        treeBackgroundColor = new Color(display, ThemeUtils.getTreeBackgroundRgb());
        sourceForegroundColor = new Color(display, ThemeUtils.getSourceForegroundRgb());
        sourceBackgroundColor = new Color(display, ThemeUtils.getSourceBackgroundRgb());
        globalForegroundColor = new Color(display, ThemeUtils.getGlobalForegroundRGB());
        globalBackgroundColor = new Color(display, ThemeUtils.getGlobalBackgroundRGB());
        font = new Font(this.display, Settings.getProperty(Settings.FONT_TYPE), Settings.getProperty(Settings.FONT_SIZE), Settings.getProperty(Settings.FONT_STYLE));
        shell = new Shell(display);
        shell.setText("Notedown");
        shell.setImage(new Image(display, Resources.ICON_PATH));
        Rectangle area = display.getClientArea();
        int x = (area.width - width) / 2;
        int y = (area.height - height) / 2;
        shell.setBounds(x, y, width, height);
        /**
         * 注意这里要用SWT.Close而不是SWT.CLOSE
         */
        shell.addListener(SWT.Close, new Listener() {

            @Override
            public void handleEvent(Event event) {
                event.doit = confirmClose();
                if (!event.doit) {
                    return;
                }
                event.doit = isReadyToCloseShell();
                if (event.doit) {
                    Settings.updateSettings();
                    status = CLOSED;
                }
                
//				for (CTabItem tab : tabFolder.getItems()) {
//					Note note = (Note) tab.getData();
//					if (note != null && note.getIsEdited()) {
//						boolean doSave = false;
//						doSave = MessageDialog.openConfirm(shell, "Save Note", "\"" + note.getName().substring(1) + "\" has been modified. Do you want to save changes?");
//						if (doSave) {
//							event.doit = false;
//							StyledText content = (StyledText) tab.getControl();
//							doSave(note, content);
//						}
//					}
//				}
            }
        });
    }
    
    private void enableDropDown() {
        DropTarget target = new DropTarget(getShell(), DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY
                | DND.DROP_LINK);
        target.setTransfer(new Transfer[] { FileTransfer.getInstance() });
        target.addDropListener(new DropTargetAdapter() {

            public void drop(DropTargetEvent e) {
                String[] paths=(String[]) e.data;
                String filePath = null;
                if (paths.length == 1 && (filePath = paths[0]).endsWith(".md")){
                    openFile(filePath);
                } else{
                }
            }
        });
    }

    void openNewNote() {
        Note note = Note.newNote();
//		((NotesManager) treeViewer.getInput()).addNote(note);
        openNote(note);
    }

    void openFile() {
        FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
        fileDialog.setFilterExtensions(new String[] { "*.md", "*.txt" });
        String filePath = fileDialog.open();
        openFile(filePath);
    }
    
    private void openFile(String filePath) {
        if (filePath != null) {
            File file = new File(filePath);
            Note note = new Note(file);
            NotesManager.getManager().addNote(note);
            openNote(note);
        }
    }

    CTabItem isNoteOpened(Note note) {
        for (CTabItem tab : tabFolder.getItems()) {
            if (note.equals(tab.getData())) {
                return tab;
            }
        }
        return null;
    }

    void openNote(Note note) {
        try {
            note.loadFile();
        } catch (FileNotFoundException e1) {
            MessageDialog.openInformation(shell, "Open Note Failed", e1.getMessage());
            NotesManager.getManager().removeNote(note, false);
            return;
        }
        CTabItem tab;
        if ((tab = isNoteOpened(note)) != null) {
            tabFolder.setSelection(tab);
            return;
        }
        note.setOpened(true);
        NotesManager.getManager().update();
        tab = new CTabItem(tabFolder, SWT.CLOSE, tabFolder.getItemCount() - 1);
        CTabFolder cTabFolder = new CTabFolder(tabFolder, SWT.MULTI | SWT.BOTTOM);
        cTabFolder.setRedraw(false);
        cTabFolder.setForeground(globalForegroundColor);
        cTabFolder.setBackground(globalBackgroundColor);
        cTabFolder.setSelectionBackground(sourceBackgroundColor);
        cTabFolder.setSelectionForeground(globalForegroundColor);
        CTabItem display = new CTabItem(cTabFolder, SWT.NONE);
        display.setText("Display");
        CTabItem source = new CTabItem(cTabFolder, SWT.NONE);
        source.setText("Source");
        StyledText content = new StyledText(cTabFolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
        if (!note.canEdit()) {
            content.setEditable(false);
        }
        content.setAlwaysShowScrollBars(false);
        content.setTabs(4);
        content.addVerifyKeyListener(new VerifyKeyListener() {
            
            @Override
            public void verifyKey(VerifyEvent event) {
                boolean isCtrl = (event.stateMask & SWT.CTRL) > 0;
                if (isCtrl) {
                    if (event.keyCode == 'a') {
                        content.selectAll();
                    } else if (event.keyCode == 'd'){
                        int currentLineIndex = content.getLineAtOffset(content.getCaretOffset());
                        int x = content.getOffsetAtLine(currentLineIndex);
                        int y = content.getLine(currentLineIndex).length();
                        content.replaceTextRange(x, y, "");
                        content.setSelection(x);
                    } else if (event.keyCode == 'v') {
                        Clipboard clipboard = new Clipboard(getDisplay());
                        ImageTransfer imageTransfer = ImageTransfer.getInstance();
                        ImageData imageData = (ImageData) clipboard.getContents(imageTransfer);
                        if (imageData != null) {
                            ImageLoader saver = new ImageLoader();
                            saver.data = new ImageData[] { imageData };
                            FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
                            fileDialog.setText("Save Copied Image");
                            fileDialog.setFileName("image.png");
                            fileDialog.setFilterExtensions(new String[] { "*.png" });
                            fileDialog.setOverwrite(true);
                            String path2Save = fileDialog.open();
                            if (path2Save != null) {
//                                if (!path2Save.endsWith(".png")) {
//                                    path2Save += ".png";
//                                }
                                saver.save(path2Save, SWT.IMAGE_PNG);
                                String toMarkdown = "![Alt Text](" + path2Save + " \"Screenshot\")";
                                clipboard.setContents(new String[] { toMarkdown }, new Transfer[] { TextTransfer.getInstance() });
                            }
                        }
                    }
                } else {
                    if (event.keyCode == SWT.CR) {
                        event.doit = false;
                        int currentLineIndex = content.getLineAtOffset(content.getCaretOffset());
                        String textAtLine = content.getLine(currentLineIndex);
                        int spaces = getLeadingSpaces(textAtLine);
                        StringBuilder spaces2Add = new StringBuilder("\n");
                        for (int i = 0; i < spaces; i++) {
                            spaces2Add.append(" ");
                        }
                        content.insert(spaces2Add.toString());
                        content.setSelection(content.getCaretOffset() + spaces2Add.length());
                    }
                }
            }
            
            private int getLeadingSpaces(String line) {
                int counter = 0;

                char[] chars = line.toCharArray();
                for (char c : chars) {
                    if (c == '\t')
                        counter += 4;
                    else if (c == ' ')
                        counter++;
                    else
                        break;
                }

                return counter;
            }
        });
        content.setFont(font);
        content.setForeground(sourceForegroundColor);
        content.setBackground(sourceBackgroundColor);
        ScrollBar contentScrollBar = content.getVerticalBar();
        // 添加行号
//		content.addLineStyleListener(new LineStyleListener() {
//			public void lineGetStyle(LineStyleEvent e) {
//				// Set the line number
//				e.bulletIndex = content.getLineAtOffset(e.lineOffset);
//
//				// Set the style, 12 pixles wide for each digit
//				StyleRange style = new StyleRange();
//				style.metrics = new GlyphMetrics(0, 0, Integer.toString(content.getLineCount() + 1).length() * 12);
//
//				// Create and set the bullet
//				e.bullet = new Bullet(ST.BULLET_NUMBER, style);
//			}
//		});
        tab.setText(note.getShortenDisplayName());
        if (note.getFile() != null) {
            tab.setToolTipText(note.getFile().getAbsolutePath());
        }
        tab.setData(note);
        content.setText(note.getContent());
        hookContextMenuForEditor(content);
        UndoRedoEnhancement undoRedoEnhancement = new UndoRedoEnhancement(content);
        content.setData(undoRedoEnhancement);
        Browser browser = new Browser(cTabFolder, SWT.NONE);
        browser.addMenuDetectListener(new MenuDetectListener() {
            
            @Override
            public void menuDetected(MenuDetectEvent arg0) {
                arg0.doit = false;
            }
        });
        if (note.isSaveAllowed()) {
            content.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent modifyEvent) {
                    note.setNewlyEdited(true);
                    if (!note.isEdited()) {
                        note.setEdited(true);
                        note.setSaved(false);
                        updateTitle("*" + note.getShortenDisplayName());
                    }
                    /*
                     * pop-up undo menu
                     */
                    content.getMenu().getItem(0).setEnabled(true);
                    /*
                     * menubar undo menu
                     */
                    shell.getMenuBar().getItem(1).getMenu().getItem(0).setEnabled(true);
                    /*
                     * menubar redo menu
                     */
                    shell.getMenuBar().getItem(1).getMenu().getItem(1).setEnabled(false);
                }
            });
        }
        browser.setText(Markdown2HTML.toStyled(note.getContent()));
//        browser.addMouseWheelListener(new MouseWheelListener() {
//
//            @Override
//            public void mouseScrolled(MouseEvent arg0) {
//                Double percent = Double.valueOf((String) browser
//                        .evaluate("var totalH = document.body.scrollHeight || document.documentElement.scrollHeight;" +
//                                "var clientH = window.innerHeight || document.documentElement.clientHeight;"
//                                + "    var validH = totalH - clientH;"
//                                + "    var scrollH = document.body.scrollTop || document.documentElement.scrollTop;"
//                                + "    var result = (scrollH/validH).toFixed(2);" + "return result;"));
//                System.out.println(percent);
//                content.setTopIndex((int) (content.getLineCount() * percent));
//            }
//        });
        browser.addLocationListener(new LocationListener() {

            @Override
            public void changing(LocationEvent event) {
                String location = event.location;
                if (!location.contains("about:blank")) {
                    event.doit = false;
                    Program.launch(location);
                }
            }

            @Override
            public void changed(LocationEvent event) {
            }
        });
        cTabFolder.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent event) {
                CTabItem selectedTab = (CTabItem) event.item;
                boolean isDisplayPage = selectedTab.equals(display);
                if (isDisplayPage) {
                    if (note.isNewlyEdited()) {
                        note.setNewlyEdited(false);
                        browser.setText(Markdown2HTML.toStyled(content.getText()));
                    } else {

                        Double percent = (Double) browser.getData();
                        if (percent != null) {
                            double y2Scroll = (Double) browser.evaluate(
                                    "var totalH = document.body.scrollHeight || document.documentElement.scrollHeight;"
                                            +

                                            "var clientH = window.innerHeight || document.documentElement.clientHeight;"
                                            + "    var validH = totalH - clientH;" + "return totalH * " + percent
                                            + ";");
                            browser.execute("window.scrollTo(0, " + y2Scroll + ");");
                            browser.setData(null);
                        }
                    }

//                    browser.setData(browser.evaluate(
//                            "var totalH = document.body.scrollHeight || document.documentElement.scrollHeight;" +
//
//                                    "var clientH = window.innerHeight || document.documentElement.clientHeight;"
//                                    + "    var validH = totalH - clientH;"
//                                    + "    var scrollH = document.body.scrollTop || document.documentElement.scrollTop;"
//                                    + "    var result = (scrollH/validH*100).toFixed(2);" + "return result;"));
                }
                shell.getMenuBar().getItem(1).setEnabled(!isDisplayPage);
            }
        });
        tabFolder.setSelection(tabFolder.getItemCount() - 2);
        tabFolder.setFocus();
        cTabFolder.setSelection(0);
        browser.setFocus();
        if (note != null && note.isSaveAllowed()) {
            save.setEnabled(true);
            saveAs.setEnabled(true);
//            export.setEnabled(true);
        } else {
            save.setEnabled(false);
            saveAs.setEnabled(false);
//            export.setEnabled(false);
        }
        content.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseScrolled(MouseEvent event) {
//                System.out.println("scrollbar:" + contentScrollBar.getSelection());
//                System.out.println("count" + event.count);
//                System.out.println("MAX" +contentScrollBar.getMaximum());
                double percent = (double) contentScrollBar.getSelection() / (double) (contentScrollBar.getMaximum() - contentScrollBar.getSize().y);
//                System.out.println(percent);
                browser.setData(percent);
            }
        });
        display.setControl(browser);
        source.setControl(content);
        tab.setControl(cTabFolder);
        cTabFolder.setRedraw(true);
        if (note.getFile() == null) {
            cTabFolder.setSelection(1);
        }
    }
    
    CTabItem getCurrentSelectedTab() {
        return tabFolder.getSelection();
    }
    
    StyledText getCurrentAcitveTextEditor() {
        return (StyledText) ((CTabFolder) getCurrentSelectedTab().getControl()).getItem(1).getControl();
    }
    
    
    void doSave(Note note, StyledText content) {
        note.setContent(content.getText());
        if (note.getFile() == null) {
            doSaveAs(note);
            return;
        }
        if (!note.isEdited()) {
            return;
        }
        note.save();
        note.setEdited(false);
        note.setNewlyEdited(false);
        note.setSaved(true);
        updateTitle(note.getShortenDisplayName());
        ((NotesManager) treeViewer.getInput()).update();
    }

    void doSave() {
        CTabItem tab = tabFolder.getSelection();
        Note note = (Note) tab.getData();
        CTabFolder cTabFolder = (CTabFolder) tab.getControl();
        CTabItem source = cTabFolder.getItem(1);
        StyledText content = (StyledText) source.getControl();
        /*
         * 如果在source页面保存
         * 即未同步到display
         */
        if (cTabFolder.getSelectionIndex() == 1) {
            CTabItem display = cTabFolder.getItem(0);
            Browser browser = (Browser) display.getControl();
            browser.setText(Markdown2HTML.toStyled(content.getText()));
        }
        doSave(note, content);
    }

    void doSaveAs(Note note) {
        FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
        fileDialog.setFileName(note.getDisplayName());
        fileDialog.setFilterExtensions(new String[] { "*.md"});
        fileDialog.setOverwrite(true);
        String savePath = fileDialog.open();
        if (savePath == null) {
            return;
        }
        if (savePath.endsWith(".md") && savePath.replace(".md", "").isEmpty()) {
            return;
        }
        note.setEdited(false);
        note.setSaved(true);
        File file = new File(savePath);
        note.setFile(file);
        note.save();
        update(note);
        ((NotesManager) treeViewer.getInput()).addNote(note);
        ((NotesManager) treeViewer.getInput()).update();
    }

    public void update(Note note) {
        treeViewer.getTree().setRedraw(false);
        treeViewer.refresh(note);
        treeViewer.getTree().setRedraw(true);
        CTabItem tab = tabFolder.getSelection();
        tab.setData(note);
        tab.setToolTipText(note.getFile().getAbsolutePath());
        updateTitle(note.getShortenDisplayName());
    }

    void updateTitle(String newTitle) {
        tabFolder.getSelection().setText(newTitle);
    }

    void doSaveAs() {
        Note note = (Note) tabFolder.getSelection().getData();
        Note saveAsNote = Note.newNote();
        saveAsNote.setContent(note.getContent());
        saveAsNote.setDisplayName(note.getDisplayName());
        doSaveAs(saveAsNote);
    }

    void export() {
        CTabItem tab = tabFolder.getSelection();
        if (isReadyToCloseTab(tab)) {
            FileDialog fileDialog = new FileDialog(shell, SWT.SAVE);
            fileDialog.setText("Export to HTML or PDF");
            fileDialog.setFileName(tab.getText() + ".html");
            fileDialog.setFilterExtensions(new String[] { "*.html", "*.pdf" });
            fileDialog.setOverwrite(true);
            String savePath = fileDialog.open();
            if (savePath == null) {
                return;
            }
            CTabFolder cTabFolder = (CTabFolder) tab.getControl();
            CTabItem source = cTabFolder.getItem(1);
            StyledText content = (StyledText) source.getControl();
            Markdown2HTML.export(content.getText(), savePath);
        }
    }

    public Display getDisplay() {
        return display;
    }

    public Shell getShell() {
        return shell;
    }

    public TreeViewer getTreeViewer() {
        return treeViewer;
    }

    public CTabFolder getTabFolder() {
        return tabFolder;
    }
}