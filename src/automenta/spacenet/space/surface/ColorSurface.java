/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package automenta.spacenet.space.surface;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.scenegraph.Spatial;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author seh
 */
public class ColorSurface {
    private final List<Spatial> spatials = new LinkedList();
    private final ColorRGBA color;
    final MaterialState cs = new MaterialState();

    public ColorSurface(ColorRGBA color) {
        super();

        this.color = color;        
    }

    public ColorSurface(float r, float g, float b) {
        this(new ColorRGBA(r, g, b, 1.0f));
    }

    public ColorSurface() {
        this(0.5f, 0.5f, 0.5f);
    }
    
    public void apply(Spatial s) {
        applyTo(s);
        spatials.add(s);
    }

    protected void applyTo(Spatial s) {
        cs.setDiffuse(color);
        s.setRenderState(cs);
    }

    protected void applyAll() {
        for (Spatial s : spatials) {
            applyTo(s);
        }
    }

    public void color(float r, float g, float b) {
        color.set(r, g, b, 1.0f);
        applyAll();
    }

    public void color(double r, double g, double b) {
        color((float)r, (float)g, (float)b);
    }
}
