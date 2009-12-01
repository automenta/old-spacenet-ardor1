package automenta.spacenet.space.geom.text2d;

import java.util.LinkedList;

import com.ardor3d.math.ColorRGBA;
import com.ardor3d.renderer.state.BlendState;
import com.ardor3d.renderer.state.BlendState.DestinationFunction;
import com.ardor3d.renderer.state.BlendState.SourceFunction;
import com.ardor3d.renderer.state.MaterialState;
import com.ardor3d.renderer.state.ZBufferState;
import com.ardor3d.scenegraph.Node;
import com.ardor3d.scenegraph.shape.Quad;

/**
 * 
 * @author Victor Porof, blue_veek@yahoo.com
 */
public class BmpTextLineRect extends Node {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BmpFont gFont;
	private String text;
	private ColorRGBA fill;
	private LinkedList<Quad> charQuads = new LinkedList<Quad>();
	private float size;
	private float kerneling;
	private float scale;
	private float spacing;
	private float width;
	private float height;

	public BmpTextLineRect(String text, BmpFont gFont, ColorRGBA fill, float size, float kerneling) {
		super();
		
		this.fill = fill;
		this.gFont = gFont;
		this.size = size;
		this.kerneling = kerneling;

		BlendState bs = new BlendState();
		bs.setBlendEnabled(true);
		bs.setSourceFunction(SourceFunction.SourceAlpha);
		bs.setDestinationFunction(DestinationFunction.OneMinusSourceAlpha);
		bs.setEnabled(true);
		setRenderState(bs);

		setText(text);
	}

	private void construct() {
//		Jme.doLater(new Runnable() {
//
//			@Override public void run() {
				scale = size / gFont.getMetricsHeights();
				spacing = 0;
				for (int i = 0; i < charQuads.size(); i++) {
					if (i < text.length()) {
						float positionX = spacing * scale
								+ gFont.getMetricsWidths()[text.charAt(i)] * scale / 2f;
						float positionY = gFont.getTextDescent() * scale;
						charQuads.get(i).setTranslation(positionX, positionY, 0);

						float sizeX = size;
						float sizeY = size;
                        charQuads.get(i).setScale(sizeX, sizeY, 1);

						if (fill != null) {
							charQuads.get(i).setSolidColor(fill);
						}
						attachChild(charQuads.get(i));

						charQuads.get(i).setRenderState(gFont.getChar(text.charAt(i)));
						spacing += gFont.getMetricsWidths()[text.charAt(i)] + kerneling;
					}
				}
				for (int i = charQuads.size(); i < text.length(); i++) {
					Quad quad = new Quad(String.valueOf(text.charAt(i)), 1f, 1f);

					float positionX = spacing * scale
							+ gFont.getMetricsWidths()[text.charAt(i)] * scale / 2f;
					float positionY = gFont.getTextDescent() * scale;
                    quad.setTranslation(positionX, positionY, quad.getTranslation().getZ());

					float sizeX = size;
					float sizeY = size;
					quad.setScale(sizeX, sizeY, quad.getScale().getZ());

					if (fill != null) {
						quad.setSolidColor(fill);
					}
					attachChild(quad);

					quad.setRenderState(gFont.getChar(text.charAt(i)));
					spacing += gFont.getMetricsWidths()[text.charAt(i)] + kerneling;

					charQuads.add(quad);
				}
				for (int j = text.length(); j < charQuads.size(); j++) {
					detachChild(charQuads.get(j));
				}
				for (int j = text.length(); j < charQuads.size(); j++) {
					charQuads.remove(j);
				}

				width = spacing * scale;
				height = size;


				setTranslation(-0.5f, -0.25f, 0);
				if ((width > 0.0) && (height > 0.0))
					setScale(1.0f / width, 0.5f / height, 1.0f);


                MaterialState ms = new MaterialState();
                ms.setDiffuse(fill);
                setRenderState(ms);

				ZBufferState zb = new ZBufferState();
				zb.setWritable(false);
				zb.setEnabled(true);
				setRenderState(zb);

				//updateRenderState();
				
//			}
//
//		});
                
	}

	public void setText(Object text) {
		this.text = String.valueOf(text);
		construct();
	}

	public String getText() {
		return text;
	}

	public void setFill(ColorRGBA fill) {
		this.fill = fill;
		construct();
	}

	public ColorRGBA getFill() {
		return fill;
	}
	
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	
}