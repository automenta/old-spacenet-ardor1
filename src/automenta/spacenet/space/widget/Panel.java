/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.widget;

import automenta.spacenet.space.control.Touchable;
import automenta.spacenet.space.geom.Rect;
import com.ardor3d.intersection.PickData;

public class Panel extends Rect implements Touchable {

    public Panel() {
        super(RectShape.Rect);
    }

    @Override
    public void touchStart(PickData pick) {
    }

    @Override
    public void touching(PickData pick) {
    }

    @Override
    public void touchStop() {
    }

    @Override
    public boolean isTangible() {
        return true;
    }
    
}
