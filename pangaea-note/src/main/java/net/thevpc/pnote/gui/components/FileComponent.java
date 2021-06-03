/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.gui.components;

import net.thevpc.common.i18n.Str;
import net.thevpc.echo.*;
import net.thevpc.echo.api.AppFileFilter;
import net.thevpc.echo.impl.Applications;
import net.thevpc.pnote.gui.PangaeaNoteFrame;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vpc
 */
public class FileComponent extends HorizontalPane {

    private TextField textField;
    private Button openFile;
    private Button reloadFile;
    private Application app;
    private boolean acceptAllFileFilterUsed;
    private List<AppFileFilter> fileFilters = new ArrayList<>();
    private List<FileChangeListener> listeners = new ArrayList<>();
    private String contentString = "";
    private SelectMode selectMode = SelectMode.FILES_AND_DIRECTORIES;

    public FileComponent(PangaeaNoteFrame win) {
        super(win.app());
        openFile = new Button("OpenFile",()->onShowDialog(),app)
        .with(b->{
            openFile.text().set((Str) null);
            openFile.icon().set(Str.of("folder"));
        });
        reloadFile = new Button("OpenFile",()->doReload(),app)
        .with(b->{
            openFile.text().set((Str) null);
            openFile.icon().set(Str.of("reload"));
        });
        textField.onChange(x->{
            setContentString(x.newValue());
        });
        children().addAll(
                textField,openFile,reloadFile
        );
        //TextAutoCompleteSupport.setup(textField, new FilePathTextAutoComplete());
    }

    public boolean isEditable() {
        return openFile.enabled().get() && textField.editable().get();
    }

    public void setEditable(boolean b) {
        openFile.enabled().set(b);
        textField.editable().set(b);
    }

    private void onShowDialog() {
        FileChooser chooser = new FileChooser(app);
        chooser.acceptFiles().set(getSelectMode() == SelectMode.FILES_ONLY || getSelectMode() == SelectMode.FILES_AND_DIRECTORIES);
        chooser.acceptDirectories().set(getSelectMode() == SelectMode.DIRECTORIES_ONLY || getSelectMode() == SelectMode.FILES_AND_DIRECTORIES);
        File f = Applications.asFile(getContentString());
        if (f != null && f.getParentFile() != null) {
            chooser.currentDirectory().set(f.getPath());
        }
        if (f != null) {
            chooser.selection().set(f.getPath());
        }
        for (AppFileFilter filter : fileFilters) {
            chooser.filters().add(filter);
        }
        if(acceptAllFileFilterUsed){
            chooser.filters().add(new FileFilter(Str.i18n("Message.AnyFileFilter"),"*.*"));
        }
        if (chooser.showOpenDialog(null)) {
            setContentString(chooser.selection().get());
        }
    }

    public boolean isAcceptAllFileFilterUsed() {
        return acceptAllFileFilterUsed;
    }

    public FileComponent setAcceptAllFileFilterUsed(boolean acceptAllFileFilterUsed) {
        this.acceptAllFileFilterUsed = acceptAllFileFilterUsed;
        return this;
    }

    public String getContentString() {
        return Applications.rawString(textField.text(),textField);
    }

    public List<AppFileFilter> getFileFilters() {
        return fileFilters;
    }

    public FileComponent setFileFilters(List<AppFileFilter> fileFilters) {
        this.fileFilters = fileFilters;
        return this;
    }

    public void uninstall() {
    }

    public void install(Application app) {
    }

    public void setContentString(String s) {
        if (s == null) {
            s = "";
        }
        if (!s.equals(contentString)) {
            contentString = s;
            textField.text().set(Str.of(s));
            for (FileChangeListener listener : listeners) {
                listener.onFilePathChanged(s);
            }
            doReload();
        }
    }

    public SelectMode getSelectMode() {
        return selectMode;
    }

    public void setSelectMode(SelectMode selectMode) {
        this.selectMode = selectMode == null ? SelectMode.FILES_AND_DIRECTORIES : selectMode;
    }

    public static enum SelectMode {
        FILES_ONLY,
        DIRECTORIES_ONLY,
        FILES_AND_DIRECTORIES,
    }

    public static interface FileChangeListener {

        void onFilePathChanged(String path);

        void onFilePathReloading(String path);
    }

    public void addFileChangeListener(FileChangeListener changeListener) {
        listeners.add(changeListener);
    }

    public void doReload() {
        for (FileChangeListener listener : listeners) {
            listener.onFilePathReloading(getContentString());
        }
    }
}
