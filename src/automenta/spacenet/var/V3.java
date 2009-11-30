/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.var;

import com.ardor3d.math.Vector3;
import java.util.LinkedList;
import java.util.List;

public class V3 extends Vector3 {

    //TODO lazy instantiate list with ensureChangesAllocated
    private List<IfV3Changes> ifChanges = new LinkedList();

    public V3(double x, double y, double z) {
        super(x, y, z);
    }

    public abstract static class IfV3Changes {
        abstract public void onV3Changed(V3 v);
    }

    public IfV3Changes add(IfV3Changes i) {
        ifChanges.add(i);
        return i;
    }
    
    public IfV3Changes remove(IfV3Changes i) {
        ifChanges.remove(i);
        return i;
    }

    @Override public Vector3 set(double x, double y, double z) {
        Vector3 v = super.set(x, y, z);
        for (IfV3Changes i : ifChanges) {
            i.onV3Changed(this);
        }
        return v;
    }

    @Override
    protected void finalize() throws Throwable {
        if (ifChanges!=null) {
            ifChanges.clear();
            ifChanges = null;
        }

        super.finalize();
    }




}
