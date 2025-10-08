/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.ser;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import net.thevpc.diagram4j.model.JDiagramEdge;
import net.thevpc.diagram4j.model.JDiagramGeometry;
import net.thevpc.diagram4j.model.JDiagramPort;
import net.thevpc.diagram4j.model.shapes.CircleShape;
import net.thevpc.diagram4j.model.shapes.EllipseShape;
import net.thevpc.diagram4j.model.shapes.RectangleShape;
import net.thevpc.diagram4j.model.shapes.SegmentShape;
import net.thevpc.diagram4j.model.shapes.SquareShape;
import net.thevpc.diagram4j.model.shapes.TextShape;
import net.thevpc.nuts.NElement;
import net.thevpc.nuts.NutsElementFactoryContext;
import net.thevpc.nuts.NutsElementMapper;
import net.thevpc.nuts.NObjectElement;

/**
 *
 * @author thevpc
 */
public class JDiagramGeometryMapper implements NutsElementMapper<JDiagramGeometry> {

    private Map<String, String> shortNameToClass = new HashMap<>();
    private Map<String, String> classSimpleName = new HashMap<>();

    public JDiagramGeometryMapper() {
        register(JDiagramEdge.class, "edge");
        register(CircleShape.class, "circle");
        register(EllipseShape.class, "ellipse");
        register(RectangleShape.class, "rectangle");
        register(SegmentShape.class, "segment");
        register(SquareShape.class, "square");
        register(TextShape.class, "text");
        register(JDiagramPort.class, "port");
    }

    private void register(Class clz, String preferredName, String... otherNames) {
        classSimpleName.put(clz.getName(), preferredName);
        shortNameToClass.put(preferredName, clz.getName());
        for (String otherName : otherNames) {
            shortNameToClass.put(otherName, clz.getName());
        }
    }

    @Override
    public Object destruct(JDiagramGeometry src, Type typeOfSrc, NutsElementFactoryContext context) {
        Map<String, Object> m = (Map) context.defaultDestruct(src, typeOfSrc);
        String typeName = src.getClass().getName();
        String preferredName = classSimpleName.get(typeName);
        if (preferredName == null) {
            preferredName = typeName;
        }
        m.put("class", preferredName);
        return m;
    }

    @Override
    public NElement createElement(JDiagramGeometry src, Type typeOfSrc, NutsElementFactoryContext context) {
        return context.objectToElement(destruct(src, typeOfSrc, context), null);
    }

    @Override
    public JDiagramGeometry createObject(NElement o, Type typeOfResult, NutsElementFactoryContext context) {
        NObjectElement oo = o.asObject();
        String clazz = oo.getString("class");
        String clz = shortNameToClass.get(clazz);
        if (clz == null) {
            clz = clazz;
        }
        try {
            return (JDiagramGeometry) context.defaultElementToObject(o, Class.forName(clz));
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
