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
public interface Pickable extends Tangible {

    public void pickStart(PickData pick);
    public void pick(PickData pick);
    public void pickStop();

    
}
