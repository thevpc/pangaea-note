///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package net.thevpc.pnote.gui.tree;
//
//import java.io.File;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
//import net.thevpc.echo.api.components.AppFileChooser;
//import net.thevpc.pnote.gui.PangaeaContentTypes;
//
//public class PangaeaNotSupportedFileFormatsFileFilter implements AppFileFilter {
//
//    private PangaeaNoteFrame win;
//    private Set<String> extensions = new HashSet<String>(Arrays.asList(PangaeaContentTypes.PANGAEA_NOTE_DOCUMENT_FILENAME_EXTENSION, "ctd"));
//    private String description;
//
//    public PangaeaNotSupportedFileFormatsFileFilter(PangaeaNoteFrame win) {
//        this.win = win;
//        description = win.app().i18n().getString("Message.pnoteSupportedFileFilters");
//    }
//
//    public boolean accept(String path,String name) {
//        File f=new File(path);
//        //TODO is it correct to handle such case ??
//        if (f.isDirectory()) {
//            String[] strings = f.list();
//            return strings != null && strings.length > 0; //true;
//        }
//        String e = getExtension(f);
//        if (e == null) {
//            e = "";
//        }
//        e = e.toLowerCase();
//        return extensions.contains(e);
//    }
//
//    @Override
//    public String messageId() {
//        return "Message.pnoteSupportedFileFilters";
////                description;//should it be an Id ??? //TODO FIX ME
//    }
//
//
//
//    public boolean accept(File dir, String name) {
//        String e = getExtension(name);
//        if (e == null) {
//            e = "";
//        }
//        e = e.toLowerCase();
//        return extensions.contains(e);
//    }
//
//    public String getExtension(File f) {
//        if (f != null) {
//            String filename = f.getName();
//            int i = filename.lastIndexOf('.');
//            if (i > 0 && i < filename.length() - 1) {
//                return filename.substring(i + 1).toLowerCase();
//            }
//            ;
//        }
//        return null;
//    }
//
//    public String getExtension(String filename) {
//        if (filename != null) {
//            int i = filename.lastIndexOf('.');
//            if (i > 0 && i < filename.length() - 1) {
//                return filename.substring(i + 1).toLowerCase();
//            }
//        }
//        return null;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//}
