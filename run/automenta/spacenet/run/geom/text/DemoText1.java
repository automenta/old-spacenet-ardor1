/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package automenta.spacenet.run.geom.text;

import automenta.spacenet.run.ArdorSpaceTime;
import com.ardor3d.framework.FrameHandler;
import com.ardor3d.input.logical.LogicalLayer;
import com.ardor3d.ui.text.BMFont;
import com.ardor3d.ui.text.BMText;
import com.ardor3d.util.resource.ResourceLocator;
import com.ardor3d.util.resource.ResourceSource;
import com.google.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @Override
    protected void initWindow() {
            getRoot().add(new BMText("", "Abcdef", font));
    }
}
