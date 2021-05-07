/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.thevpc.pnote.core.types.diagram.ser;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import net.thevpc.diagram4j.model.JDiagramBounds;
import net.thevpc.nuts.NutsElement;
import net.thevpc.nuts.NutsElementFactoryContext;
import net.thevpc.nuts.NutsElementMapper;
import net.thevpc.nuts.NutsObjectElement;

/**
 *
 * @author vpc
 */
public class JDiagramBoundsMapper implements NutsElementMapper<JDiagramBounds> {
    
    public JDiagramBoundsMapper() {
    }

    @Override
    public Object destruct(JDiagramBounds src, Type typeOfSrc, NutsElementFactoryContext context) {
        Map<String, Integer> des = new LinkedHashMap<>();
        des.put("minX", src.getMinX());
        des.put("maxX", src.getMaxX());
        des.put("minY", src.getMinY());
        des.put("maxY", src.getMaxY());
        return des;
    }

    @Override
    public NutsElement createElement(JDiagramBounds src, Type typeOfSrc, NutsElementFactoryContext context) {
        return context.defaultObjectToElement(destruct(src, typeOfSrc, context), typeOfSrc);
    }

    @Override
    public JDiagramBounds createObject(NutsElement o, Type typeOfResult, NutsElementFactoryContext context) {
        NutsObjectElement oo = o.asObject();
        return new JDiagramBounds(oo.getInt("minX"), oo.getInt("minY"), oo.getInt("maxX"), oo.getInt("maxY"));
    }
    
}
