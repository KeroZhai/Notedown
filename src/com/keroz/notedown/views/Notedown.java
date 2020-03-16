package com.keroz.notedown.views;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerDropAdapter;
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
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import org.eclipse.swt.widgets.TreeItem;

import com.keroz.notedown.Resources;
import com.keroz.notedown.Settings;
import com.keroz.notedown.model.Category;
import com.keroz.notedown.model.ElementChangeEvent;
import com.keroz.notedown.model.Note;
import com.keroz.notedown.model.NotesElement;
import com.keroz.notedown.model.NotesManager;
import com.keroz.notedown.model.UndoRedoEnhancement;
import com.keroz.notedown.util.Markdown2HTML;
import com.keroz.notedown.util.MarkdownHelper;
import com.keroz.notedown.util.ThemeUtils;
import com.keroz.notedown.views.contributionitems.AddNodeContributionItem;
import com.keroz.notedown.views.contributionitems.DeleteNodeContributionItem;
import com.keroz.notedown.views.contributionitems.RenameNodeContributionItem;

public class Notedown {

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
    
    private MenuItem save;
    private MenuItem saveAs;
//    private MenuItem export;

    public Notedown(int width, int height) {
        this.width = width;
        this.height = height;
        init();
        enableDropDown();
        hookMenu();
        hookSashForm();
        enableTreeDragNDrop();
        enableTabDragNDrop();
        hookContextMenuForTree();
        initTabFolder();
    }

    public Notedown() {
        this(DEFAULT_WIDTH, DEFALUT_HEIGHT);
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
        // Sleak sleak = new Sleak();
        // sleak.open();
        treeForegroundColor = new Color(display, ThemeUtils.getTreeForegroundRgb());
        treeBackgroundColor = new Color(display, ThemeUtils.getTreeBackgroundRgb());
        sourceForegroundColor = new Color(display, ThemeUtils.getSourceForegroundRgb());
        sourceBackgroundColor = new Color(display, ThemeUtils.getSourceBackgroundRgb());
        globalForegroundColor = new Color(display, ThemeUtils.getGlobalForegroundRGB());
        globalBackgroundColor = new Color(display, ThemeUtils.getGlobalBackgroundRGB());
        font = new Font(this.display, Settings.getProperty(Settings.FONT_TYPE),
                Settings.getProperty(Settings.FONT_SIZE), Settings.getProperty(Settings.FONT_STYLE));
        shell = new Shell(display);
        shell.setText("Notedown");
        shell.setImage(new Image(display, Resources.ICON_PATH));
        shell.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent arg0) {

            }

            @Override
            public void mouseDown(MouseEvent arg0) {

            }

            @Override
            public void mouseDoubleClick(MouseEvent arg0) {

            }
        });
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
            }
        });
    }

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
        new MenuItem(fileSubMenu, SWT.SEPARATOR);
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
        about.setText("About Notedown");
        about.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                MessageDialog messageDialog = new MessageDialog(shell, "About Notedown", null,
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
        treeViewer.setComparator(new ViewerComparator() {
            @Override
            public int compare(Viewer viewer, Object e1, Object e2) {
                if (e1 == Category.UNCATEGORIED) {
                    return -1;
                } else if (e2 == Category.UNCATEGORIED) {
                    return 1;
                }
                return ((NotesElement) e1).getDisplayName().compareTo(((NotesElement) e2).getDisplayName());
            }
        });
        treeViewer.getTree().setForeground(treeForegroundColor);
        treeViewer.getTree().setBackground(treeBackgroundColor);
        treeViewer.setContentProvider(new NotesContentProvider());
        treeViewer.setLabelProvider(new NotesLabelProvider());
        ColumnViewerToolTipSupport.enableFor(treeViewer);
        treeViewer.getTree().addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(MouseEvent arg0) {
            }

            @Override
            public void mouseDown(MouseEvent arg0) {
                Point point = new Point(arg0.x, arg0.y);
                if (treeViewer.getTree().getItem(point) == null) {
                    treeViewer.setSelection(StructuredSelection.EMPTY);
                }
            }

            @Override
            public void mouseDoubleClick(MouseEvent arg0) {
            }
        });
        treeViewer.addDoubleClickListener(new IDoubleClickListener() {

            @Override
            public void doubleClick(DoubleClickEvent arg0) {
                Object object = treeViewer.getStructuredSelection().getFirstElement();
                if (object instanceof Note) {
                    Note note = (Note) object;
                    openNote(note);
                } else if (object instanceof Category) {
                    treeViewer.setExpandedState(object, !treeViewer.getExpandedState(object));
                }

            }
        });
        treeViewer.setInput(NotesManager.getManager());
        treeViewer.expandToLevel(Category.UNCATEGORIED, TreeViewer.ALL_LEVELS, true);
        Composite editorComposite = new Composite(form, SWT.NONE);
        form.setWeights(new int[] { 20, 80 });
        editorComposite.setLayout(new FillLayout());
        editorComposite.setRedraw(false);
        tabFolder = new CTabFolder(editorComposite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI);
        tabFolder.setForeground(globalForegroundColor);
        tabFolder.setBackground(globalBackgroundColor);
        tabFolder.setSelectionForeground(globalForegroundColor);
        tabFolder.setSelectionBackground(sourceBackgroundColor);
        CTabItem newTab = new CTabItem(tabFolder, SWT.NONE);
        editorComposite.setRedraw(true);
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
                boolean isSourcePageActive = ((CTabFolder) tabFolder.getSelection().getControl())
                        .getSelectionIndex() == 1;
                shell.getMenuBar().getItem(1).setEnabled(isSourcePageActive);

            }
        });
        tabFolder.addCTabFolder2Listener(new CTabFolder2Adapter() {
            @Override
            public void close(CTabFolderEvent event) {
                CTabItem tabToClose = (CTabItem) event.item;
                event.doit = isReadyToCloseTab(tabToClose);
                if (event.doit) {
                    Note note = (Note) tabToClose.getData();
                    note.setOpened(false);
                    NotesManager.getManager().updateNotesPathFile();
                }
            }
        });
        if (Settings.getProperty(Settings.SHOW_WELCOME)) {
            openNote(Note.WELCOME);
            Settings.setProperty(Settings.SHOW_WELCOME, Boolean.FALSE);
        }
    }

    void enableTreeDragNDrop() {
        int ops = DND.DROP_COPY | DND.DROP_MOVE;
        Transfer[] dragTrasfers = new Transfer[] { LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance() };
        Transfer[] dropTransfers = new Transfer[] { LocalSelectionTransfer.getTransfer(), FileTransfer.getInstance() };
        treeViewer.addDragSupport(ops, dragTrasfers, new DragSourceListener() {

            StructuredSelection selection = null;

            @Override
            public void dragStart(DragSourceEvent arg0) {
                selection = (StructuredSelection) treeViewer.getSelection();
                Object dragSource = selection.getFirstElement();
                if (!(dragSource instanceof Note)) {
                    arg0.doit = false;
                }
            }

            @Override
            public void dragSetData(DragSourceEvent event) {
                LocalSelectionTransfer.getTransfer().setSelection(selection);
                /*
                 * Set the data for FileTransfer.
                 */
                event.data = new String[] {
                        ((Note) selection.getFirstElement()).getPath()
                };
            }

            @Override
            public void dragFinished(DragSourceEvent event) {
            }
        });
        ViewerDropAdapter viewerDropAdapter = new ViewerDropAdapter(treeViewer) {

            NotesElement dropTarget = null;

            @Override
            public boolean validateDrop(Object target, int operation, TransferData transferType) {
                this.dropTarget = target == null ? Category.UNCATEGORIED : (NotesElement) target;
                boolean isValid = (LocalSelectionTransfer.getTransfer()).isSupportedType(transferType)
                        || FileTransfer.getInstance().isSupportedType(transferType);
                if (!isValid) {
                    System.out.println(6);
                }
                return isValid;
            }

            @Override
            public boolean performDrop(Object data) {
                if (data instanceof TreeSelection) {
                    Note source = (Note) ((TreeSelection) data).getFirstElement();
                    Category targetParent = null;
                    if (dropTarget instanceof Category) {
                        targetParent = (Category) dropTarget;
                    } else {
                        targetParent = (Category) dropTarget.getParent();
                    }
                    if (source.getParent() != targetParent) {
                        treeViewer.remove(source);
                        source.removeFromParent(false);
                        source.setParent(targetParent);
                        targetParent.addNote(source);
                        source.fireNotesElementChanged(new ElementChangeEvent(ElementChangeEvent.CHANGE, source));
                        treeViewer.add(dropTarget, source);
                    }
                } else if (data instanceof String[]) {
                    handleDropFile(getCurrentEvent());
                }

                return true;
            }
        };
        viewerDropAdapter.setScrollEnabled(true);
        viewerDropAdapter.setExpandEnabled(true);
        viewerDropAdapter.setSelectionFeedbackEnabled(true);
        viewerDropAdapter.setFeedbackEnabled(false);
        treeViewer.addDropSupport(ops, dropTransfers, viewerDropAdapter);
    }

    void initTabFolder() {
        NotesManager.getManager().getCategories().forEach(category -> {
            for (int i = 0; i < category.getNotes().size(); i++) {
                Note note = category.getNotes().get(i);
                if (note.isOpened()) {
                    openNote(note);
                }
            }
        });
    }

    void enableTabDragNDrop() {
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
                    if (current != dragItem) {
                        tabFolder.setInsertMark(null, false);
                        CTabItem first = tabFolder.getItem(0);
                        CTabItem last = tabFolder.getItem(tabFolder.getItemCount() - 2);
                        int indexToInsert;
                        int dragTabIndex = tabFolder.indexOf(dragItem);
                        int currentTabIndex = current == null ? -1 : tabFolder.indexOf(current);
                        
                        if (p.x > last.getBounds().x + last.getBounds().width) {
                            indexToInsert = tabFolder.getItemCount() - 1;
                        } else if (p.x < first.getBounds().x) {
                            indexToInsert = 0;
                        } else {
                            if (p.x >= current.getBounds().x && (p.x <= current.getBounds().x + current.getBounds().width / 2)) {
                                indexToInsert = currentTabIndex;
                            } else {
                                indexToInsert = currentTabIndex + 1;
                            }

                        }
                        
                        if (indexToInsert != dragTabIndex && (indexToInsert - 1) != dragTabIndex) {
                            CTabItem newItem = new CTabItem(tabFolder, SWT.CLOSE, indexToInsert);
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
        treeMenuManager.setRemoveAllWhenShown(true);
        treeMenuManager.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager menuManager) {
                Object selection = treeViewer.getStructuredSelection().getFirstElement();
                menuManager.add(new AddNodeContributionItem(Notedown.this));
                if (selection != null && !(selection instanceof Note) && !(selection.equals(Category.UNCATEGORIED))) {
                    menuManager.add(new Separator("edit"));
                }
                menuManager.add(new DeleteNodeContributionItem(Notedown.this));
                menuManager.add(new RenameNodeContributionItem(Notedown.this));
            }
        });
        Tree tree = treeViewer.getTree();
        Menu contextMenu = treeMenuManager.createContextMenu(tree);
        tree.setMenu(contextMenu);
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
                    if (MessageDialog.openConfirm(shell, "Theme Change",
                            "A restart is required for the theme change to take full effect.\nWould you like to restart now?")) {
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
                        Settings.setProperty(Settings.DIRECTLY_EXIT, !Settings.getProperty(Settings.DIRECTLY_EXIT));
                    }
                });
                return button;
            }
        };
        return messageDialog.open() == 0 ? true : false;
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
                    "\"" + note.getDisplayName() + "\" has been modified. Do you want to save changes?", SWT.NONE,
                    "Save", "Don't save", "Cancel");
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

    private void enableDropDown() {
        DropTarget target = new DropTarget(getShell(),
                DND.DROP_DEFAULT | DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
        target.setTransfer(new Transfer[] { FileTransfer.getInstance() });
        target.addDropListener(new DropTargetAdapter() {

            public void drop(DropTargetEvent event) {
                if (!(event.data instanceof String[])) {
                    event.detail = DND.DROP_NONE;
                }
                handleDropFile(event);
            }
        });
    }

    private void handleDropFile(DropTargetEvent e) {
        String[] paths = (String[]) e.data;
        /**
         * 转换坐标
         */
        Point location = display.map(null, treeViewer.getControl(), e.x, e.y);
        TreeItem treeItem = treeViewer.getTree().getItem(location);
        String filePath = null;
        if (paths.length == 1 && (filePath = paths[0]).endsWith(".md")) {
            Category category = Category.UNCATEGORIED;
            if (treeItem != null) {
                NotesElement dropTarget = (NotesElement) treeItem.getData();
                if (dropTarget instanceof Category) {
                    category = (Category) dropTarget;
                } else {
                    category = (Category) dropTarget.getParent();
                }
            }
            openFile(filePath, category);
        }
    }

    void openNewNote() {
        Note note = Note.newNote();
        openNote(note);
    }

    void openFile() {
        FileDialog fileDialog = new FileDialog(shell, SWT.OPEN);
        fileDialog.setFilterExtensions(new String[] { "*.md", "*.txt" });
        String filePath = fileDialog.open();
        openFile(filePath, Category.UNCATEGORIED);
    }

    private void openFile(String filePath, Category category) {
        if (filePath != null) {
            File file = new File(filePath);
            Note note = new Note(category, file);
            category.addNote(note);
            openNote(note);
        }
    }

    CTabItem tryGetTabForNote(Note note) {
        for (CTabItem tab : tabFolder.getItems()) {
            if (note.equals(tab.getData())) {
                return tab;
            }
        }
        return null;
    }

    public void openNote(Note note) {
        try {
            note.loadFile();
        } catch (FileNotFoundException e1) {
            MessageDialog.openInformation(shell, "Open Note Failed", e1.getMessage());
            note.removeFromParent(false);
            return;
        }
        CTabItem tab;
        if ((tab = tryGetTabForNote(note)) != null) {
            tabFolder.setSelection(tab);
            return;
        }
        createTabForNote(note);
        note.setOpened(true);
        NotesManager.getManager().updateNotesPathFile();
    }

    private void createTabForNote(Note note) {
        tabFolder.setRedraw(false);
        CTabItem tab = new CTabItem(tabFolder, SWT.CLOSE, tabFolder.getItemCount() - 1);
        CTabFolder noteTabFolder = new CTabFolder(tabFolder, SWT.MULTI | SWT.BOTTOM);
        noteTabFolder.setRedraw(false);
        noteTabFolder.setForeground(globalForegroundColor);
        noteTabFolder.setBackground(globalBackgroundColor);
        noteTabFolder.setSelectionBackground(sourceBackgroundColor);
        noteTabFolder.setSelectionForeground(globalForegroundColor);

        tab.setText(note.getShortenDisplayName());
        if (note.getFile() != null) {
            tab.setToolTipText(note.getFile().getAbsolutePath());
        }
        tab.setData(note);
        CTabItem display = createDisplayPage(noteTabFolder, note);
        CTabItem source = createSourcePage(noteTabFolder, note);
        syncPages(display, source, noteTabFolder, note);
        
        tab.setControl(noteTabFolder);
        if (note.getFile() == null) {
            noteTabFolder.setSelection(1);
        } else {
            noteTabFolder.setSelection(0);
        }
        
        noteTabFolder.setRedraw(true);
        tabFolder.setSelection(tabFolder.getItemCount() - 2);
        tabFolder.setFocus();

        if (note != null && note.isSaveAllowed()) {
            save.setEnabled(true);
            saveAs.setEnabled(true);
//            export.setEnabled(true);
        } else {
            save.setEnabled(false);
            saveAs.setEnabled(false);
//            export.setEnabled(false);
        }

        tabFolder.setRedraw(true);
    }

    private CTabItem createDisplayPage(CTabFolder noteTabFolder, Note note) {
        CTabItem display = new CTabItem(noteTabFolder, SWT.NONE);
        display.setText("Display");
        Browser browser = new Browser(noteTabFolder, SWT.NONE);
        browser.addMenuDetectListener(new MenuDetectListener() {
            @Override
            public void menuDetected(MenuDetectEvent arg0) {
                arg0.doit = false;
            }
        });

        browser.setText(Markdown2HTML.toStyled(note.getContent()));
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
        display.setControl(browser);
        browser.setFocus();
        return display;
    }

    private CTabItem createSourcePage(CTabFolder noteTabFolder, Note note) {
        CTabItem source = new CTabItem(noteTabFolder, SWT.NONE);
        source.setText("Source");
        StyledText content = new StyledText(noteTabFolder, SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
        content.setEditable(note.canEdit());
        content.setTabs(4);
        content.addVerifyKeyListener(new VerifyKeyListener() {

            @Override
            public void verifyKey(VerifyEvent event) {
                boolean isCtrl = (event.stateMask & SWT.CTRL) == SWT.CTRL;
                if (isCtrl) {
                    if (event.keyCode == 'a') {
                        content.selectAll();
                    } else if (event.keyCode == 'd') {
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
                                saver.save(path2Save, SWT.IMAGE_PNG);
                                String toMarkdown = "![Alt Text](" + path2Save + " \"Clipboard Image\")";
                                clipboard.setContents(new String[] { toMarkdown },
                                        new Transfer[] { TextTransfer.getInstance() });
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
                        content.setCaretOffset(content.getCaretOffset() + spaces2Add.length());
                    }
                }
                completePairs(event.character, content);
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
        Listener scrollBarListener = new Listener () {
            @Override
            public void handleEvent(Event event) {
              StyledText t = (StyledText) event.widget;
              Rectangle r1 = t.getClientArea();
              Rectangle r2 = t.computeTrim(r1.x, r1.y, r1.width, r1.height);
              Point p = t.computeSize(SWT.DEFAULT,  SWT.DEFAULT,  true);
//              t.getHorizontalBar().setVisible(r2.width <= p.x);
              t.getVerticalBar().setVisible(r2.height <= p.y);
              if (event.type == SWT.Modify) {
                t.getParent().layout(true);
                t.showSelection();
              }
            }
          };
        content.addListener(SWT.Resize, scrollBarListener);
        content.addListener(SWT.Modify, scrollBarListener);
        content.setFont(font);
        content.setForeground(sourceForegroundColor);
        content.setBackground(sourceBackgroundColor);
        content.setText(note.getContent());
        hookContextMenuForEditor(content);
        UndoRedoEnhancement undoRedoEnhancement = new UndoRedoEnhancement(content);
        content.setData(undoRedoEnhancement);
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
        source.setControl(content);
        return source;
    }
    
    
    private char getNextChar(StyledText styledText) {
        
        try {
            return styledText.getTextRange(styledText.getCaretOffset(), 1).charAt(0);
        } catch (IllegalArgumentException e) {
            return '\0';
        }
    }
    
    private void completePairs(char character, StyledText styledText) {
        char opposite = 0;
        switch (character) {
            case '\'':
                opposite = '\'';
                break;
            case '"':
                opposite = '"';
                break;
            case '(':
                opposite = ')';
                break;
            case '[':
                opposite = ']';
                break;
            case '{':
                opposite = '}';
                break;
            case '<':
                opposite = '>';
                break;
            //Chinese
            case '‘':
                opposite = '’';
                break;
            case '“':
                opposite = '”';
                break;
            case '（':
                opposite = '）';
                break;
            case '【':
                opposite = '】';
                break;
            case '《':
                opposite = '》';
                break;
            default:
                break;
        }
        if (opposite != 0) {
            styledText.insert(String.valueOf(opposite));
        }
        
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
        new MenuItem(editorPopupMenu, SWT.SEPARATOR);
        MenuItem cut = new MenuItem(editorPopupMenu, SWT.PUSH);
        cut.setText("Cut\tCtrl+X");
        cut.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                styledText.cut();
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
        new MenuItem(editorPopupMenu, SWT.SEPARATOR);
        MenuItem fontMenuItem = new MenuItem(editorPopupMenu, SWT.CASCADE);
        Menu fontMenu = new Menu(shell, SWT.DROP_DOWN);
        fontMenuItem.setText("Font");
        fontMenuItem.setMenu(fontMenu);
        addMenuItemForFontMenu(fontMenu, "Larger", 2);
        addMenuItemForFontMenu(fontMenu, "Smaller", -2);
        new MenuItem(editorPopupMenu, SWT.SEPARATOR);
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
        fileDialog.setFilterExtensions(
                new String[] { "Images (*.jpg;*.jpeg;*.png;*.bmp)", "*.jpg", "*.jpeg", "*.png", "*.bmp" });
        fileDialog.setText("Insert Image");
        String imagePath = null;
        if ((imagePath = fileDialog.open()) != null) {
            String insertText = "![Alt Text](" + imagePath + " \"\")";
            styledText.insert(insertText);
            styledText.setSelectionRange(styledText.getSelectionRange().x + (insertText.length() - 2), 0);
        }
    }

    private void insertTable(StyledText styledText) {
        InputDialog inputDialog = new InputDialog(getShell(), "Insert Table", "Rows and cols ", "1,1",
                new IInputValidator() {

                    @Override
                    public String isValid(String rowsAndCols) {
                        if (rowsAndCols.matches("([0-9]+,( *)[0-9]+){1}")) {
                            String[] split = rowsAndCols.split(",");
                            int rowNum = Integer.valueOf(split[0]);
                            int colNum = Integer.valueOf(split[1]);
                            if (rowNum > 0 && rowNum <= 20 && colNum > 0 && colNum <= 10) {
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
                tableContent.append("| " + colContent + " ");
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

    private void syncPages(CTabItem display, CTabItem source, CTabFolder noteTabFolder, Note note) {
        Browser browser = (Browser) display.getControl();
        StyledText content = (StyledText) source.getControl();
        noteTabFolder.addSelectionListener(new SelectionAdapter() {

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
                }
                shell.getMenuBar().getItem(1).setEnabled(!isDisplayPage);
            }
        });
        ScrollBar contentScrollBar = content.getVerticalBar();
        content.addMouseWheelListener(new MouseWheelListener() {

            @Override
            public void mouseScrolled(MouseEvent event) {
                double percent = (double) contentScrollBar.getSelection()
                        / (double) (contentScrollBar.getMaximum() - contentScrollBar.getSize().y);
                browser.setData(percent);
            }
        });
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
        ((NotesManager) treeViewer.getInput()).updateNotesPathFile();
    }

    void doSave() {
        CTabItem tab = tabFolder.getSelection();
        Note note = (Note) tab.getData();
        CTabFolder cTabFolder = (CTabFolder) tab.getControl();
        CTabItem source = cTabFolder.getItem(1);
        StyledText content = (StyledText) source.getControl();
        /*
         * 如果在source页面保存 即未同步到display
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
        fileDialog.setFilterExtensions(new String[] { "*.md" });
        fileDialog.setOverwrite(true);
        String savePath = fileDialog.open();
        if (savePath == null || savePath.endsWith(".md") && savePath.replace(".md", "").isEmpty()) {
            return;
        }
        note.setEdited(false);
        note.setSaved(true);
        File file = new File(savePath);
        note.setFile(file);
        note.save();
        update(note);
        ((NotesManager) treeViewer.getInput()).updateNotesPathFile();
    }

    public void update(Note note) {
        /*
         * 保存的时候再refresh
         */
        Category category = (Category) note.getParent();
        category.addNote(note);
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
        Note saveAsNote = Note.newNote((Category) note.getParent());
        saveAsNote.setContent(note.getContent());
        saveAsNote.setDisplayName(note.getShortenDisplayName());
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