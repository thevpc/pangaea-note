/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.rich.editor;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.BorderPane;
import net.thevpc.echo.ContextMenu;
import net.thevpc.echo.RichHtmlEditor;
import net.thevpc.echo.api.AppColor;
import net.thevpc.echo.api.components.AppContextMenu;
import net.thevpc.pnote.api.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.api.model.HighlightType;
import net.thevpc.pnote.gui.PangaeaNoteFrame;
import net.thevpc.pnote.gui.SelectableElement;
import net.thevpc.pnote.gui.editor.editorcomponents.source.RichHtmlToolBarHelper;
import net.thevpc.echo.util.ClipboardHelper;
import net.thevpc.pnote.gui.search.SearchDialog;
import net.thevpc.pnote.service.search.SearchQuery;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.service.search.strsearch.StringQuerySearch;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;

import java.util.stream.Stream;
import net.thevpc.pnote.api.model.PangaeaNote;

/**
 * @author vpc
 */
public class PangaeaNoteRichEditorTypeComponent extends BorderPane implements PangaeaNoteEditorTypeComponent {

    private PangaeaNoteFrame frame;
    private RichHtmlEditor textArea;
    private PangaeaNote currentNote;

    private boolean compactMode;
    private SelectableElement selectableElement = new SelectableElement() {
        @Override
        public String getSelectedText() {
            String s = textArea.textSelection().get();
            return (s != null && s.length() > 0) ? s : null;
        }
    };

    @Override
    public void requestFocus() {
        textArea.requestFocus();
    }

//    private SourceEditorPaneExtension textExtension = new SourceEditorPanePanelTextExtension();
//    private SourceEditorPaneExtension htmlExtension = new SourceEditorPanePanelHtmlExtension();
    public PangaeaNoteRichEditorTypeComponent(boolean compactMode, PangaeaNoteFrame frame) {
        super(frame.app());
        this.frame = frame;
        this.compactMode = compactMode;
        textArea = new RichHtmlEditor(app());
        textArea.installDefaults();
        textArea.registerAccelerator("search-text", "control F", () -> showSearchDialog());

        textArea.text().onChange(t -> {
            if (currentNote != null) {
                frame.onDocumentChanged();
                currentNote.setContent(frame.app().stringToElement(
                        textArea.text().get().value()
                ));
            }
        });
//        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, KeyEvent.CTRL_MASK), "select-color");
//        editor.getActionMap().put("select-color",
//                new AbstractAction() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        try {
//                            String st = editor.getSelectedText();
//                            if (st != null && st.length() > 0) {
//                                Rectangle r = editor.modelToView(editor.getSelectionStart());
//                                JPopupMenu popup = new JPopupMenu();
//                                Application app = win.app();
//                                JToolBar bar = null;
//                                AbstractSourceEditorPaneExtension.addActionList("insert-bloc", SourceEditorPanePanelHtmlExtension.createBlocTypeActions(editor), bar, popup, app);
//
//                                AbstractSourceEditorPaneExtension.addActionList("font-styles", SourceEditorPanePanelHtmlExtension.createfontStyleActions(editor), bar, popup, app);
//
//                                AbstractSourceEditorPaneExtension.addActionList("align-text", SourceEditorPanePanelHtmlExtension.createAlignActions(editor), bar, popup, app);
//
//                                AbstractSourceEditorPaneExtension.addSeparator(bar, popup, app);
//
//                                AbstractSourceEditorPaneExtension.addActionList("font-type", SourceEditorPanePanelHtmlExtension.createFontFamilyActions(editor, win), bar, popup, app);
//
//                                AbstractSourceEditorPaneExtension.addActionList("font-size", SourceEditorPanePanelHtmlExtension.createFontActions(), bar, popup, app);
//
//                                AbstractSourceEditorPaneExtension.addActionList("colors", SourceEditorPanePanelHtmlExtension.createColorActions(app, editor), bar, popup, app);
//
//                                FontMetrics fm = editor.getFontMetrics(editor.getFont());
//                                Point pt = new Point(r.x, (int) (r.y + r.height/*+fm.getHeight()+fm.getDescent()*/));
////                        SwingUtilities.convertPointToScreen(pt, editor);
//                                popup.show(editor, pt.x, pt.y);
//                            }
//                        } catch (Exception ex) {
//                            Logger.getLogger(SourceEditorTypeComponent.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                    }
//                }
//        );
        textArea.zoomOnMouseWheel().set(true);
        ClipboardHelper.prepareToolBar(frame);
        RichHtmlToolBarHelper.prepare(frame);
        AppContextMenu popup = textArea.contextMenu().getOrCompute(() -> new ContextMenu(app()));
//        textExtension.prepareEditor(editorBuilder, compactMode, win);
//        htmlExtension.prepareEditor(editorBuilder, compactMode, win);

//        this.editorBuilder.editor().addPropertyChangeListener("document", new PropertyChangeListener() {
//            @Override
//            public void propertyChange(PropertyChangeEvent evt) {
//                GuiHelper.installUndoRedoManager(editorBuilder.editor());
//            }
//        });
//        GuiHelper.installUndoRedoManager(editorBuilder.editor());
        children().add(textArea);
        textArea.focused()
                .onChange(e -> {
                    if (e.newValue()) {
                        frame.selectableElement().set(selectableElement);
                    } else {
                        if (selectableElement == frame.selectableElement().get()) {
                            frame.selectableElement().set(null);
                        }
                    }
                });
    }

//    public static String getCopiedString(JEditorPane editor) {
//        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
//        try {
//            Transferable content = clip.getContents(editor);
//            return content.getTransferData(
//                    new DataFlavor(String.class, "String")).toString();
//        } catch (Exception ex) {
//            //ex.printStackTrace();
//        }
//        return "";
//    }
    private void showSearchDialog() {
        SearchDialog dialog = new SearchDialog(frame);
        dialog.setTitle(Str.i18n("Message.search.searchInDocument"));
        dialog.setSearchTextElseClipboard(textArea.textSelection().get());
        SearchQuery query = dialog.showDialog();
        if (query != null) {
            StringQuerySearch<String> fi = new StringQuerySearch(query);
            String txt = textArea.getText(0, textArea.getTextLength());
            Stream<StringSearchResult<String>> found = fi.search(StringDocumentTextNavigator.of(txt), SearchProgressMonitor.NONE);
            found.forEach(x -> {
                int from = x.getStart();
                int to = x.getEnd();
                highlight(from, to, HighlightType.SEARCH_MAIN);
            });
        }

    }

    @Override
    public void uninstall() {
        //
    }

    @Override
    public void setNote(PangaeaNote note) {
        this.currentNote = note;
        textArea.text().set(Str.of(frame.app().elementToString(note.getContent())));
        setEditable(!note.isReadOnly());
//        UndoManager um = GuiHelper.getUndoRedoManager(editorBuilder.editor());
//        um.discardAllEdits();
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    @Override
    public void setEditable(boolean b) {
        if (currentNote != null && currentNote.isReadOnly()) {
            b = false;
        }
        textArea.editable().set(b);
    }

    @Override
    public boolean isEditable() {
        return textArea.editable().get();
    }

    @Override
    public void removeHighlights(HighlightType highlightType) {
        textArea.removeAllHighlights(highlightType);
    }

    @Override
    public void highlight(int from, int to, HighlightType highlightType) {
        AppColor c = frame.colorForHighlightType(highlightType);
        textArea.highlight(from, to, c, highlightType);
    }

    @Override
    public void moveTo(int pos) {
        textArea.caretPosition().set(pos);
    }
}
