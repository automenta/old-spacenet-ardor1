/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.run.control;

import automenta.spacenet.run.ArdorSpaceTime;
import automenta.spacenet.space.control.Draggable;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.surface.ColorSurface;
import automenta.spacenet.space.video.ProcessBox;
import com.ardor3d.math.Ray3;

/**
 *
 * @author seh
 */
public class DemoDraggable extends ProcessBox {

    public class DraggableRect extends Rect implements Draggable {
        private final ColorSurface cs;

        public DraggableRect() {
            super(RectShape.Ellipse);

            cs = add(new ColorSurface());
        }

        @Override
        public boolean isTangible() { return true;         }

        @Override
        public void startDrag(Ray3 rayDragStart) {
            cs.color(0,1,0);
        }

        @Override
        public void continueDrag(Ray3 rayDrag) {
            cs.color(0,1,0);
        }

        @Override
        public void stopDrag(Ray3 rayDragStop) {
            cs.color(0.5,0.5,0.5);
        }




    }

    @Override
    protected void start() {
        add(new DraggableRect());
    }

    public static void main(String[] args) {
        ArdorSpaceTime.newWindow(new DemoDraggable());
    }

}
