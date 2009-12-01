/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.geom.text;

import automenta.spacenet.run.ArdorSpaceTime;
import automenta.spacenet.space.geom.Rect;
import automenta.spacenet.space.geom.Rect.RectShape;
import automenta.spacenet.space.geom.text2d.BmpTextLineRect;
import automenta.spacenet.space.geom.text2d.BmpFont;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.scenegraph.Spatial;
import com.google.inject.Inject;
import java.awt.Font;

/**
 *
 * @author seh
 */
public class DemoText1 extends ArdorSpaceTime {

    @Inject public DemoText1(final LogicalLayer logicalLayer, final FrameHandler frameWork) {
        super(logicalLayer, frameWork);
    }

    public static void main(String[] args) {
        //Multiple windows can be created by calling newWindow repeatedly
        newWindow(DemoText1.class);
    }

    Font awtFont = new Font("Arial", Font.PLAIN, 32);

    protected Spatial newText(String text, float size) {
        BmpFont font = new BmpFont(awtFont);
        ColorRGBA fillColor = new ColorRGBA(ColorRGBA.ORANGE);
        float kerneling = 0f;
        BmpTextLineRect tn = new BmpTextLineRect("Abc", font, fillColor, size, kerneling);
        return tn;
    }

    @Override
    protected void initWindow() {        
        getRoot().add(newText("Abc", 32));

        Rect r = getRoot().add(new Rect(RectShape.Rect).move(2,1,0));
        r.add(newText("Dyz", 32));

    }
}
