/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space.geom;

import automenta.spacenet.space.*;
import automenta.spacenet.var.Quat;
import automenta.spacenet.var.V3;
import automenta.spacenet.var.V3.IfV3Changes;
import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class Box extends Space implements HasPosition3, HasScale3 {

    Spatial shapeSpatial;
    private final V3 position;
    private final V3 scale;
    private final Quat ori;
    private IfV3Changes positionChange;
    private IfV3Changes scaleChange;

    public static enum BoxShape {

        Empty, Sided /*, Spheroid*/

    }

    public Box(BoxShape b) {
        this(new V3(), new V3(1, 1, 1), new Quat(), b);
    }

    public Box(V3 position, V3 scale, Quat orientation, BoxShape shape) {
        super();

        this.position = position;
        this.scale = scale;
        this.ori = orientation;


        setShape(shape);
    }

    @Override protected void afterAttached(Spatial newParent) {
        positionChange = position.add(new V3.IfV3Changes() {

            @Override public void onV3Changed(V3 v) {
                positionChanged();
            }
        });
        scaleChange = scale.add(new V3.IfV3Changes() {

            @Override public void onV3Changed(V3 v) {
                sizeChanged();
            }
        });

        //TODO add Orientation change

        positionChanged();
        sizeChanged();

    }

    @Override protected void beforeDetached(Spatial parent) {
        System.out.println(this + " detached from " + parent);
        position.remove(positionChange);
        scale.remove(scaleChange);
    }

    protected void positionChanged() {
        setTranslation(position);
    }

    protected void sizeChanged() {
        setScale(scale);
    }

    public void setShape(BoxShape shape) {
        if (shapeSpatial != null) {
            detachChild(shapeSpatial);
        }


        switch (shape) {
            case Empty:
                shapeSpatial = null;
                break;
            case Sided:
                com.ardor3d.scenegraph.shape.Box b = new com.ardor3d.scenegraph.shape.Box();
                b.setModelBound(new OrientedBoundingBox());
                shapeSpatial = b;
                break;
        }

        if (shapeSpatial != null) {
            attachChild(shapeSpatial);
        }
    }

    public Box move(double px, double py, double pz) {
        position.set(px, py, pz);
        return this;
    }

    public Box scale(double sx, double sy, double sz) {
        scale.set(sx, sy, sz);
        return this;
    }

    @Override public V3 getPosition() {
        return position;
    }

    @Override
    public V3 getSize() {
        return scale;
    }
}
