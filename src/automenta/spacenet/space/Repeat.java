/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space;

import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.scenegraph.controller.ComplexSpatialController;

/**
 *
 * @author seh
 */
abstract public class Repeat<S extends Spatial> extends ComplexSpatialController<S> {

    double t = 0;

    @Override public void update(double dt, S s) {
        update(t, dt, s);
        t += dt;
    }

    abstract protected void update(double t, double dt, S s);



}
