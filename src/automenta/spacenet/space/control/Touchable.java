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
public interface Touchable extends Tangible {

    public void touchStart(PickData pick);
    public void touching(PickData pick);
    public void touchStop();

    
}
