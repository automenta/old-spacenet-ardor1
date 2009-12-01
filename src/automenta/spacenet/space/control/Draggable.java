/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.control;

import com.ardor3d.math.Ray3;

/**
 *
 * @author seh
 */
public interface Draggable extends Tangible {

    public void startDrag(Ray3 rayDragStart);
    public void continueDrag(Ray3 rayDrag);
    public void stopDrag(Ray3 rayDragStop);

}
