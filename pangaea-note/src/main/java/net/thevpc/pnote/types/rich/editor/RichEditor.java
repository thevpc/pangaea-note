/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.types.rich.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.font.LineMetrics;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.undo.UndoManager;
import net.thevpc.common.swing.SwingComponentUtils;
import net.thevpc.echo.Application;
import net.thevpc.echo.swing.core.swing.SwingApplicationsUtils;
import net.thevpc.jeep.editor.JEditorPaneBuilder;
import net.thevpc.more.shef.ShefHelper;
import net.thevpc.pnote.model.HighlightType;
import net.thevpc.pnote.gui.PangaeaNoteWindow;
import net.thevpc.pnote.gui.SelectableElement;
import net.thevpc.pnote.gui.editor.editorcomponents.source.SourceEditorPaneExtension;
import net.thevpc.pnote.gui.editor.editorcomponents.source.SourceEditorPanePanelTextExtension;
import net.thevpc.pnote.gui.util.AnyDocumentListener;
import net.thevpc.pnote.gui.util.GuiHelper;
import net.thevpc.pnote.model.PangaeaNoteExt;
import net.thevpc.pnote.gui.editor.PangaeaNoteEditorTypeComponent;
import net.thevpc.pnote.gui.editor.editorcomponents.source.AbstractSourceEditorPaneExtension;
import net.thevpc.pnote.gui.editor.editorcomponents.source.SourceEditorPanePanel;
import net.thevpc.pnote.gui.search.SearchDialog;
import net.thevpc.pnote.service.search.SearchQuery;
import net.thevpc.pnote.service.search.strsearch.SearchProgressMonitor;
import net.thevpc.pnote.service.search.strsearch.StringQuerySearch;
import net.thevpc.pnote.service.search.strsearch.StringDocumentTextNavigator;
import net.thevpc.pnote.service.search.strsearch.StringSearchResult;

/**
 *
 * @author vpc
 */
public class RichEditor extends JPanel implements PangaeaNoteEditorTypeComponent {

    private PangaeaNoteWindow sapp;
    private JEditorPaneBuilder editorBuilder;
    private PangaeaNoteExt currentNote;
    private boolean compactMode;
    private SelectableElement selectableElement=new SelectableElement() {
        @Override
        public String getSelectedText() {
            String s = editorBuilder.editor().getSelectedText();
            return (s!=null && s.length()>0)?s:null;
        }
    };
    DocumentListener documentListener = new AnyDocumentListener() {
        public void anyChange(DocumentEvent e) {
            if (currentNote != null) {
//                System.out.println("update note:" + editorBuilder.editor().getText());
                sapp.onDocumentChanged();
                currentNote.setContent(sapp.service().stringToElement(editorBuilder.editor().getText()));
            }
        }
    };

    private SourceEditorPaneExtension textExtension = new SourceEditorPanePanelTextExtension();
    private SourceEditorPaneExtension htmlExtension = new SourceEditorPanePanelHtmlExtension();

    public RichEditor(boolean compactMode, PangaeaNoteWindow sapp) {
        super(new BorderLayout());
        this.sapp = sapp;
        this.compactMode = compactMode;
        editorBuilder = new JEditorPaneBuilder().setEditor(ShefHelper.installMin(new JEditorPane("text/html", "")));

        JEditorPane editor = editorBuilder.editor();
        editor.getInputMap().put(KeyStroke.getKeyStroke("control B"), "font-bold");
        editor.getInputMap().put(KeyStroke.getKeyStroke("control I"), "font-italic");
        editor.getInputMap().put(KeyStroke.getKeyStroke("control U"), "font-underline");
        editor.getInputMap().put(KeyStroke.getKeyStroke("control L"), "left-justify");
        editor.getInputMap().put(KeyStroke.getKeyStroke("control R"), "left-justify");
        editor.getInputMap().put(KeyStroke.getKeyStroke("control E"), "center-justify");
        editor.getInputMap().put(KeyStroke.getKeyStroke("control F"), "search-text");
        editor.getInputMap().put(KeyStroke.getKeyStroke("ESCAPE"), "reset-highlights");
        editor.getActionMap().put("reset-highlights",
                SwingApplicationsUtils.registerAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        editor.getHighlighter().removeAllHighlights();
                    }

                }, "reset-highlights", "reset-highlights", sapp.app())
        );
        editor.getActionMap().put("search-text",
                SwingApplicationsUtils.registerAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        SearchDialog dialog = new SearchDialog(sapp);
                        dialog.setTitle(sapp.app().i18n().getString("Message.search.searchInDocument"));
                        dialog.setSearchText(editor.getSelectedText());
                        SearchQuery query = dialog.showDialog();
                        if (query != null) {
                            try {
                                StringQuerySearch<String> fi = new StringQuerySearch(query);
                                String txt = editor.getDocument().getText(0, editor.getDocument().getLength());
                                Stream<StringSearchResult<String>> found = fi.search(StringDocumentTextNavigator.of(txt), SearchProgressMonitor.NONE);
                                found.forEach(x -> {
                                    int from = x.getStart();
                                    int to = x.getEnd();
                                    highlight(from, to, HighlightType.SEARCH_MAIN);
                                });
                            } catch (BadLocationException ex) {
                                Logger.getLogger(RichEditor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                }, "search", "search", sapp.app())
        );
        editor.getInputMap().put(KeyStroke.getKeyStroke("control D"), "duplicate-text");
        editor.getActionMap().put("duplicate-text",
                SwingApplicationsUtils.registerAction(new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            int ss = editor.getSelectionStart();
                            int se = editor.getSelectionEnd();
                            if (se > ss) {
                                ShefHelper.runCopy(editor);
                                editor.select(se, se);
                                ShefHelper.runPaste(editor);
                            } else {
                                Action a = editor.getActionMap().get("select-line");
                                if (a != null) {
                                    a.actionPerformed(e);
                                    ShefHelper.runCopy(editor);
                                    editor.select(se, se);
                                    ShefHelper.runPaste(editor);
                                }
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(SourceEditorPanePanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }, "search", "search", sapp.app())
        );
        editor.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,KeyEvent.CTRL_MASK), "select-color");
        editor.getActionMap().put("select-color",
                new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String st = editor.getSelectedText();
                    if (st!=null && st.length() > 0) {
                        Rectangle r = editor.modelToView(editor.getSelectionStart());
                        JPopupMenu popup = new JPopupMenu();
                        Application app = sapp.app();
                        JToolBar bar = null;
                        AbstractSourceEditorPaneExtension.addActionList("insert-bloc", SourceEditorPanePanelHtmlExtension.createBlocTypeActions(editor), bar, popup, app);

                        AbstractSourceEditorPaneExtension.addActionList("font-styles", SourceEditorPanePanelHtmlExtension.createfontStyleActions(editor), bar, popup, app);

                        AbstractSourceEditorPaneExtension.addActionList("align-text", SourceEditorPanePanelHtmlExtension.createAlignActions(editor), bar, popup, app);

                        AbstractSourceEditorPaneExtension.addSeparator(bar, popup, app);

                        AbstractSourceEditorPaneExtension.addActionList("font-type", SourceEditorPanePanelHtmlExtension.createFontFamilyActions(editor, sapp), bar, popup, app);

                        AbstractSourceEditorPaneExtension.addActionList("font-size", SourceEditorPanePanelHtmlExtension.createFontActions(), bar, popup, app);

                        AbstractSourceEditorPaneExtension.addActionList("colors", SourceEditorPanePanelHtmlExtension.createColorActions(app, editor), bar, popup, app);
                        
                        FontMetrics fm = editor.getFontMetrics(editor.getFont());
                        Point pt = new Point(r.x,(int)(r.y+r.height/*+fm.getHeight()+fm.getDescent()*/));
//                        SwingUtilities.convertPointToScreen(pt, editor);
                        popup.show(editor, pt.x, pt.y);
                        popup.addPropertyChangeListener("visible", new PropertyChangeListener() {
                            @Override
                            public void propertyChange(PropertyChangeEvent evt) {
                                System.out.println("jpp visible " + popup.isVisible());
                            }
                        });
                    }
                } catch (Exception ex) {
                    Logger.getLogger(SourceEditorPanePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        );

        editor.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent evt) {
                if (evt.getDot() == evt.getMark()) {
                    return;
                }

                JEditorPane txtPane = (JEditorPane) evt.getSource();
                Highlighter highlighter = txtPane.getHighlighter();
                String selText = txtPane.getSelectedText();
                String contText = "";// = jTextPane1.getText();
                Document document = txtPane.getDocument();
                try {
                    contText = document.getText(0, document.getLength());
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
                if (contText.length() > 0) {
                    int index = 0;
                    List<int[]> all = new ArrayList<>();
                    while ((index = contText.indexOf(selText, index)) > -1) {
                        all.add(new int[]{index, selText.length() + index});
                        index = index + selText.length();
                    }
                    if (all.size() > 1) {
                        highlighter.removeAllHighlights();
                        for (int[] is : all) {
                            highlight(is[0], is[1], HighlightType.CARET);
                        }
                    }
                }
            }
        });

        SwingComponentUtils.addZoomTextOnMouseWheel(editorBuilder.editor());
        if (!compactMode) {
//            editorBuilder.footer()
//                    //                .add(new JLabel("example..."))
//                    //                .add(new JSyntaxPosLabel(e, completion))
//                    .addGlue()
//                    .addCaret()
//                    .end();
        } else {
            setBorder(BorderFactory.createEmptyBorder());
        }
        this.editorBuilder.editor().getDocument().addDocumentListener(documentListener);
        this.editorBuilder.editor().addPropertyChangeListener("document", e -> {
            Document o = (Document) e.getOldValue();
            Document n = (Document) e.getNewValue();
            if (o != null) {
                o.removeDocumentListener(documentListener);
            }
            if (n != null) {
                n.addDocumentListener(documentListener);
            }
        });
        JPopupMenu popup = editorBuilder.editor().getComponentPopupMenu();
        if (popup == null) {
            popup = new JPopupMenu();
            editorBuilder.editor().setComponentPopupMenu(popup);
        }
        textExtension.prepareEditor(editorBuilder, compactMode, sapp);
        htmlExtension.prepareEditor(editorBuilder, compactMode, sapp);

        if (!compactMode) {
            this.editorBuilder.header().addGlue();
        }
        this.editorBuilder.editor().addPropertyChangeListener("document", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                GuiHelper.installUndoRedoManager(editorBuilder.editor());
            }
        });
        GuiHelper.installUndoRedoManager(editorBuilder.editor());

        if (compactMode) {
            add(editorBuilder.component());
        } else {
            add(editorBuilder.component());
        }
        editor.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                sapp.selectableElement().set(selectableElement);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(selectableElement==sapp.selectableElement().get()){
                    sapp.selectableElement().set(null);
                }
            }
        });
    }

    public boolean isCompactMode() {
        return compactMode;
    }

    @Override
    public JComponent component() {
        return this;
    }

    @Override
    public void uninstall() {
        //
    }

    @Override
    public void setNote(PangaeaNoteExt note, PangaeaNoteWindow sapp) {
        this.currentNote = note;
        editorBuilder.editor().setText(sapp.service().elementToString(note.getContent()));
        setEditable(!note.isReadOnly());
        UndoManager um = GuiHelper.getUndoRedoManager(editorBuilder.editor());
        um.discardAllEdits();
    }

    @Override
    public void setEditable(boolean b) {
        editorBuilder.editor().setEditable(b);
    }

    @Override
    public boolean isEditable() {
        return editorBuilder.editor().isEditable();
    }

    public static String getCopiedString(JEditorPane editor) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        try {
            Transferable content = clip.getContents(editor);
            return content.getTransferData(
                    new DataFlavor(String.class, "String")).toString();
        } catch (Exception ex) {
            //ex.printStackTrace();
        }
        return "";
    }

    @Override
    public void removeHighlights(HighlightType hightlightType) {
        editorBuilder.editor().getHighlighter().removeAllHighlights();
    }

    @Override
    public void highlight(int from, int to, HighlightType hightlightType) {
        try {
            Color c = sapp.colorForHighlightType(hightlightType);
            javax.swing.text.DefaultHighlighter.DefaultHighlightPainter highlightPainter
                    = new javax.swing.text.DefaultHighlighter.DefaultHighlightPainter(c);
            editorBuilder.editor().getHighlighter().addHighlight(from, to, highlightPainter);
        } catch (BadLocationException ex) {
            Logger.getLogger(RichEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void moveTo(int pos) {
        editorBuilder.editor().setCaretPosition(pos);
    }
}
