/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.geom;

import automenta.spacenet.space.HasPosition3;
import automenta.spacenet.space.HasScale2;
import automenta.spacenet.space.Space;
import automenta.spacenet.var.Quat;
import automenta.spacenet.var.Quat.IfQuatChanges;
import automenta.spacenet.var.V2;
import automenta.spacenet.var.V2.IfV2Changes;
import automenta.spacenet.var.V3;
import automenta.spacenet.var.V3.IfV3Changes;
import com.ardor3d.bounding.OrientedBoundingBox;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.shape.Disk;
import com.ardor3d.scenegraph.shape.Quad;

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
    private RectShape currentShape;
    private IfQuatChanges oriChange;


    public static enum RectShape {

        Empty, Rect, Ellipse

    }

    public Rect(RectShape shape) {
        this(new V3(0), new V2(1), new Quat(), shape);
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
        oriChange = ori.add(new IfQuatChanges() {
            @Override public void onQuatChanged(Quat q) {
                oriChanged();
            }
        });

        positionChanged();
        sizeChanged();
        oriChanged();

    }
    protected void beforeDetached(Spatial parent) {
        //System.out.println(this + " detached from " + parent);
        position.remove(positionChange);
        scale.remove(scaleChange);
        ori.remove(oriChange);
    }

    protected void positionChanged() {
        setTranslation(position);
    }
    protected void sizeChanged() {
        setScale(scale.getX(), scale.getY(), getZScale());
    }
    protected void oriChanged() {
        setRotation(ori);
    }

    public void setShape(RectShape shape) {
        if (this.currentShape == shape)
            return;
        
        if (shapeSpatial != null) {
            detachChild(shapeSpatial);
        }

        switch (shape) {
            case Empty:
                shapeSpatial = null;
                break;
            case Rect:
                Quad s = new com.ardor3d.scenegraph.shape.Quad("", 1, 1);
                s.setModelBound(new OrientedBoundingBox());
                shapeSpatial = s;
                break;
            case Ellipse:
                Disk d = new com.ardor3d.scenegraph.shape.Disk("", 12, 12, 0.5);
                d.setModelBound(new OrientedBoundingBox());
                shapeSpatial = d;
                break;
        }

        this.currentShape = shape;

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

    protected double getZScale() {
        return 1.0;
    }


}
