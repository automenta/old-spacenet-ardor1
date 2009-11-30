/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.geom;

import automenta.spacenet.run.*;
import automenta.spacenet.space.*;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Box.BoxShape;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.var.Quat;
import automenta.spacenet.var.V3;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.scenegraph.Spatial;
import com.google.inject.Inject;

/**
 *
 * @author seh
 */
public class DemoBox extends ArdorSpaceTime {

    @Inject
    public DemoBox(final LogicalLayer logicalLayer, final FrameHandler frameWork) {
        super(logicalLayer, frameWork);
    }
    
    public static void main(String[] args) {
        //Multiple windows can be created by calling newWindow repeatedly
        newWindow(DemoBox.class);
    }

    @Override protected void initWindow() {
        final V3 pos = new V3(0,0,0);
        final V3 scale = new V3(1,1,1);
        final Quat orientation = Quat.fromAngles(0, 0, 0);
        
        final Box b = getRoot().add(new Box(pos, scale, orientation, BoxShape.Sided));
        final ColorSurface cs = b.add(new ColorSurface(0.8f, 0.5f, 0.3f));

        b.add(new Repeat() {
            @Override protected void update(double t, double dt, Spatial s) {
                b.move(Math.cos(t), Math.sin(t), 0);
                
                double sc = 1.0 + Math.cos(t*10.0)/5.0;
                b.scale(sc, sc, sc);
                
                float r = (float)((1.0 + Math.cos(t)) * 0.5);
                float g = 0.5f;
                float b = 0.2f;
                cs.color(r, g, b);
            }
        });
    }
}
