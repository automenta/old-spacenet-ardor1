/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.geom;

import automenta.spacenet.space.HasPosition3;
import automenta.spacenet.space.HasScale2;
import automenta.spacenet.space.Space;
import automenta.spacenet.var.Quat;
import automenta.spacenet.var.V2;
import automenta.spacenet.var.V2.IfV2Changes;
import automenta.spacenet.var.V3;
import automenta.spacenet.var.V3.IfV3Changes;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class Rect extends Space implements HasPosition3, HasScale2 {

    Spatial shapeSpatial;
    private final V3 position;
    private final V2 scale;
    private final Quat ori;
    private IfV3Changes positionChange;
    private IfV2Changes scaleChange;

    public static enum RectShape {

        Empty, Rect

    }

    public Rect(V3 position, V2 scale, Quat orientation, RectShape shape) {
        super();

        this.position = position;
        this.scale = scale;
        this.ori = orientation;

        setShape(shape);
    }


    protected void afterAttached(Spatial newParent) {
        positionChange = position.add(new V3.IfV3Changes() {
            @Override public void onV3Changed(V3 v) {
                positionChanged();
            }
        });
        scaleChange = scale.add(new IfV2Changes() {
            @Override public void onV2Changed(V2 v) {
                sizeChanged();
            }
        });

        //TODO add Orientation change
    }
    protected void beforeDetached(Spatial parent) {
        System.out.println(this + " detached from " + parent);
        position.remove(positionChange);
        scale.remove(scaleChange);
    }

    protected void positionChanged() {
        setTranslation(position);
    }
    protected void sizeChanged() {
        setScale(scale.getX(), scale.getY(), 1.0);
    }

    public void setShape(RectShape shape) {
        if (shapeSpatial != null) {
            detachChild(shapeSpatial);
        }

        switch (shape) {
            case Empty:
                shapeSpatial = null;
                break;
            case Rect:
                shapeSpatial = new com.ardor3d.scenegraph.shape.Quad("", 1, 1);
                break;
        }

        if (shapeSpatial!=null) {
            attachChild(shapeSpatial);
        }
    }

    public Rect move(double px, double py, double pz) {
        position.set(px, py, pz);
        return this;
    }
    public Rect scale(double sx, double sy) {
        scale.set(sx, sy);
        return this;
    }

//    public ColorSurface add(ColorSurface cs) {
//        //surfaces.add(cs);
//        cs.apply(this);
//        return cs;
//    }

    @Override public V3 getPosition() {
        return position;
    }

    @Override public V2 getSize() {
        return scale;
    }


}
