/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.space;

import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.var.Quat;
import automenta.spacenet.var.V3;
import automenta.spacenet.var.V3.IfV3Changes;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;

/**
 *
 * @author seh
 */
public class Box extends Space  {

    Spatial shapeSpatial;
    private final V3 position;
    private final V3 scale;
    private final Quat ori;
    private IfV3Changes positionChange;
    private IfV3Changes scaleChange;

    public static enum BoxShape {

        Empty, Sided /*, Spheroid*/

    }

    public Box(V3 position, V3 scale, Quat orientation, BoxShape shape) {
        super();

        this.position = position;
        this.scale = scale;
        this.ori = orientation;

        setShape(shape);
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

    protected void afterAttached(Spatial newParent) {
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
                shapeSpatial = new com.ardor3d.scenegraph.shape.Box();                
                break;
        }

        if (shapeSpatial!=null) {
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

    public ColorSurface add(ColorSurface cs) {
        //surfaces.add(cs);
        cs.apply(this);
        return cs;
    }
}
