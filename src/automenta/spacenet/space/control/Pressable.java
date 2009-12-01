/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.control;

import com.ardor3d.intersection.PickData;

/**
 *
 * @author seh
 */
public interface Pressable extends Tangible {

    public void pressStart(PickData pick);
    public void pressStop(PickData pick);

}
