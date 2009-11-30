/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space;

import automenta.spacenet.space.surface.ColorSurface;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class Space extends Node {

    public <S extends Spatial> S add(S  s) {
        if (attachChild(s) == -1)
            return null;
        return s;
    }

    public <S extends Spatial> S remove(S s) {
        if (detachChild(s) == -1)
            return null;
        return s;
    }


    public Repeat add(Repeat r) {
        addController(r);
        return r;
    }
    public Repeat remove(Repeat r) {
        if (!removeController(r))
            return null;
        return r;
    }

    public ColorSurface add(ColorSurface cs) {
        //surfaces.add(cs);
        cs.apply(this);
        return cs;
    }

        @Override
    protected void setParent(Node parent) {
        Spatial previousParent = getParent();

        if (parent == null) {
            beforeDetached(previousParent);
        }

        super.setParent(parent);

        if (parent != null) {
            afterAttached(parent);
        }
    }

    protected void afterAttached(Spatial parent) {
    }

    protected void beforeDetached(Spatial previousParent) {
    }



}
