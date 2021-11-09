/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.editor.tools;

import java.awt.BasicStroke;
import java.awt.Stroke;
import net.thevpc.diagram4j.render.strokes.SloppyStroke;

/**
 *
 * @author thevpc
 */
public class StrokeUtils {

    public static Stroke parseStroke(String s) {
        if (s == null || s.trim().length() == 0) {
            return null;
        }
        if (s.startsWith("basic:")) {
            String[] a = s.substring("basic:".length()).split(";");
            float width = 1;
            int cap = BasicStroke.CAP_SQUARE;
            int join = BasicStroke.JOIN_MITER;
            float miterlimit = 10.0f;
            float dash[] = null;
            float dash_phase = 0;
            for (String ss : a) {
                if (ss.length() > 0) {
                    String[] kv = ss.split("=");
                    if (kv.length != 2) {
                        continue;
                    }
                    String v = kv[1];
                    switch (kv[0]) {
                        case "width": {
                            width = Float.parseFloat(v);
                            break;
                        }
                        case "miterlimit": {
                            miterlimit = Float.parseFloat(v);
                            break;
                        }
                        case "dash_phase": {
                            dash_phase = Float.parseFloat(v);
                            break;
                        }
                        case "cap": {
                            cap = Integer.parseInt(v);
                            break;
                        }
                        case "join": {
                            join = Integer.parseInt(v);
                            break;
                        }
                        case "dash": {
                            if (v.trim().length() > 0) {
                                String[] vf = v.split(",");
                                dash = new float[vf.length];
                                for (int i = 0; i < dash.length; i++) {
                                    dash[i] = Float.parseFloat(vf[i]);
                                }
                            }
                            break;
                        }
                    }
                }
            }
            return new BasicStroke(width, cap, join, miterlimit,
                    dash, dash_phase);
        }
        if (s.startsWith("sloppy:")) {
            String[] a = s.substring("sloppy:".length()).split(";");
            float width = 1;
            float sloppiness = 0;
            for (String ss : a) {
                if (ss.length() > 0) {
                    String[] kv = ss.split("=");
                    String v = kv[1];
                    switch (kv[0]) {
                        case "width": {
                            width = Float.parseFloat(v);
                            break;
                        }
                        case "sloppiness": {
                            sloppiness = Float.parseFloat(v);
                            break;
                        }
                    }
                }
            }
            return new SloppyStroke(width, sloppiness);
        }
        return new BasicStroke();
    }

    public static String formatStroke(Stroke s) {
        if (s == null) {
            return null;
        }
        if (s instanceof BasicStroke) {
            BasicStroke b = (BasicStroke) s;
            StringBuilder sb = new StringBuilder();
            sb.append("basic:");
            sb.append("width=").append(b.getLineWidth());
            sb.append(";miterlimit=").append(b.getMiterLimit());
            sb.append(";dash_phase=").append(b.getDashPhase());
            sb.append(";cap=").append(b.getEndCap());
            sb.append(";join=").append(b.getLineJoin());
            sb.append(";dash=");
            if (b.getDashArray() != null) {
                float[] da = b.getDashArray();
                for (int i = 0; i < da.length; i++) {
                    float f = da[i];
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(f);
                }
            }
            return sb.toString();
        }
        if (s instanceof SloppyStroke) {
            SloppyStroke b = (SloppyStroke) s;
            StringBuilder sb = new StringBuilder();
            sb.append("sloppy:");
            sb.append("width=").append(b.getWidth());
            sb.append(";sloppiness=").append(b.getSloppiness());
            return sb.toString();
        }
//        System.out.println("unknown "+s);
        return "basic:width=1";
    }
}
