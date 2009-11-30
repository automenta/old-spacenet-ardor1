/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.geom;

import automenta.spacenet.run.*;
import automenta.spacenet.space.*;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.var.Quat;
import automenta.spacenet.var.V2;
import automenta.spacenet.var.V3;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.scenegraph.Spatial;
import com.google.inject.Inject;

/**
 *
 * @author seh
 */
public class DemoRect extends ArdorSpaceTime {

    @Inject
    public DemoRect(final LogicalLayer logicalLayer, final FrameHandler frameWork) {
        super(logicalLayer, frameWork);
    }
    
    public static void main(String[] args) {
        //Multiple windows can be created by calling newWindow repeatedly
        newWindow(DemoRect.class);
    }

    @Override protected void initWindow() {
        final V3 pos = new V3(0,0,0);
        final V2 scale = new V2(1,1);
        final Quat orientation = Quat.fromAngles(0, 0, 0);
        
        final Rect r = getRoot().add(new Rect(pos, scale, orientation, RectShape.Rect));
        final ColorSurface cs = r.add(new ColorSurface(0.8f, 0.5f, 0.3f));

        r.add(new Repeat() {
            @Override protected void update(double t, double dt, Spatial s) {
                r.move(Math.cos(t), Math.sin(t), 0);
                
                double sc = 1.0 + Math.cos(t*10.0)/5.0;
                r.scale(sc, sc);
                
                float r = (float)((1.0 + Math.cos(t)) * 0.5);
                float g = 0.5f;
                float b = 0.2f;
                cs.color(r, g, b);
            }
        });
    }
}
