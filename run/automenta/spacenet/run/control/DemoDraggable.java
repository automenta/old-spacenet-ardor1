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
import com.ardor3d.math.Plane;
import com.ardor3d.math.Ray3;
import com.ardor3d.math.Vector3;

/**
 *
 * @author seh
 */
public class DemoDraggable extends ProcessBox {

    public class DraggableRect extends Rect implements Draggable {

        private final ColorSurface cs;

        public DraggableRect() {
            super(RectShape.Rect);

            cs = add(new ColorSurface());
        }

        @Override
        public boolean isTangible() {
            return true;
        }

        @Override
        public void startDrag(Ray3 rayDragStart) {
            cs.color(0, 1, 0);
        }

        @Override
        public void continueDrag(Ray3 rayDrag) {
            cs.color(0, 1, 0);

            double x = getWorldTranslation().getX();
            double y = getWorldTranslation().getY();
            double z = getWorldTranslation().getZ();


            Vector3 a = new Vector3(x, y, z);
            Vector3 b = new Vector3(x + 1, y, z);
            Vector3 c = new Vector3(x, y + 1, z);
            

            Plane p = new Plane();
            p.setPlanePoints(a, b, c);

            Vector3 i = new Vector3();
            rayDrag.intersects(p, i);

            //Vector3[] verts = new Vector3[] { a, b, c, d };
            //rayDrag.intersects(verts, i);

//            IntersectionRecord ir = getWorldBound().intersectsWhere(rayDrag);
//            Vector3 i = ir.getIntersectionPoint(ir.getClosestIntersection());

            System.out.println("  - intersect: " + i + " " + rayDrag);

            getPosition().set(i);
        }

        @Override
        public void stopDrag(Ray3 rayDragStop) {
            cs.color(0.5, 0.5, 0.5);
        }
    }

    @Override
    protected void start() {
        int n = 1;
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                add(new DraggableRect().move(x * 2, y * 2, 0));
            }
        }
    }

    public static void main(String[] args) {
        ArdorSpaceTime.newWindow(new DemoDraggable());
    }
}
