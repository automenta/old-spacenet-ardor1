/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.widget;

import automenta.spacenet.space.control.Pickable;
import automenta.spacenet.space.geom.Rect;
import com.ardor3d.intersection.PickData;

public class Panel extends Rect implements Pickable {

    public Panel() {
        super(RectShape.Rect);
    }

    @Override
    public void pickStart(PickData pick) {
    }

    @Override
    public void pick(PickData pick) {
    }

    @Override
    public void pickStop() {
    }

    @Override
    public boolean isTangible() {
        return true;
    }
    
}
