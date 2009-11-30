/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var;

import com.ardor3d.math.Quaternion;
import com.ardor3d.math.type.ReadOnlyQuaternion;


public class Quat extends Quaternion {

    public Quat(ReadOnlyQuaternion source) {
        super(source);
    }
    
    public static Quat fromAngles(double heading, double attitude, double bank) {
        Quaternion q = new Quaternion().fromEulerAngles(heading, attitude, bank);
        return new Quat(q);
    }
}
