/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space;

import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.Spatial;
import com.ardor3d.util.GameTaskQueue;
import com.ardor3d.util.GameTaskQueueManager;

/**
 *
 * @author seh
 */
public class Space extends Node {

    public <S extends Spatial> S add(S  s) {
        if (attachChild(s) == -1)
            return null;
        return s;
    }

    public <S extends Spatial> S remove(S s) {
        if (detachChild(s) == -1)
            return null;
        return s;
    }


    public Repeat add(Repeat r) {
        addController(r);
        return r;
    }
    public Repeat remove(Repeat r) {
        if (!removeController(r))
            return null;
        return r;
    }
}
