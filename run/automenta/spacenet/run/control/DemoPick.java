/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.control;

import automenta.spacenet.run.ArdorSpaceTime;
import automenta.spacenet.space.control.Touchable;
import automenta.spacenet.space.control.Pressable;
import automenta.spacenet.space.geom.Box;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.surface.ColorSurface;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.intersection.PickData;
import com.ardor3d.math.Vector3;
import com.ardor3d.scenegraph.Node;
import com.google.inject.Inject;

public class DemoPick extends ArdorSpaceTime {


    public class PickableRect extends Rect implements Touchable {
        private final ColorSurface cs;

        public PickableRect() {
            super(RectShape.Rect);

            cs = add(new ColorSurface());
        }

        @Override
        public void touchStart(PickData pick) {
        }

        @Override
        public void touching(PickData pick) {
            
            int numInt = pick.getIntersectionRecord().getNumberOfIntersection();
            if (numInt != 1) {
                System.out.println("odd, # of intersections = " + numInt);
                return;
            }

            Vector3 p = pick.getIntersectionRecord().getIntersectionPoint(0);
            
            cs.color(Math.cos(p.getX()), Math.sin(p.getY()), Math.cos(p.getZ()));
        }

        @Override
        public void touchStop() {
        }

        @Override public boolean isTangible() { return true; }

    }

    public class PickableBox extends Box implements Touchable {
        private final ColorSurface color;

        public PickableBox() {
            super(BoxShape.Sided);

            color = add(new ColorSurface(1,1,1));

            touchStop();
        }

        @Override
        public void touchStart(PickData pick) {
            color.color(1, 0, 0);
        }

        @Override
        public void touchStop() {
            color.color(0, 0, 1);
        }

        @Override
        public void touching(PickData pick) {
        }

        @Override public boolean isTangible() { return true; }

        
    }

    public class PressableBox extends Box implements Pressable {
        private final ColorSurface color;

        public PressableBox() {
            super(BoxShape.Sided);

            color = add(new ColorSurface(1,1,1));

            pressStop(null);
        }


        @Override public boolean isTangible() { return true; }

        @Override
        public void pressStart(PickData pick) {
            color.color(0,1,0);
        }

        @Override
        public void pressStop(PickData pick) {
            color.color(0,0,1);
        }


    }




    @Inject
    public DemoPick(final LogicalLayer logicalLayer, final FrameHandler frameWork) {
        super(logicalLayer, frameWork);

    }

    @Override
    protected void initWindow() {
        getRoot().add(new PickableBox().move(-1, 1, 0));
        getRoot().add(new PickableRect().move(1, 1, 0));
        getRoot().add(new PressableBox().move(-1, -1, 0));

    }

    public static void main(String[] args) {
        //Multiple windows can be created by calling newWindow repeatedly
        newWindow(DemoPick.class);
    }

    
}
