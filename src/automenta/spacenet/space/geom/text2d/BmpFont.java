package automenta.spacenet.space.geom.text2d;

import com.ardor3d.image.Image.Format;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import com.ardor3d.image.Texture;
import com.ardor3d.renderer.state.TextureState;
import com.ardor3d.util.TextureManager;
import java.util.logging.Logger;

/**
 * 
 * @author Victor Porof, blue_veek@yahoo.com
 */
public class BmpFont {
    Format fontFormat = Format.Guess; //Format.GuessNoCompression

    private static final Logger logger = Logger.getLogger(BmpFont.class.getName());
    //private static Map<VectorFont, GFont> gFonts = new HashMap();
    private int type = BufferedImage.TYPE_INT_ARGB;
    //private Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
    private BufferedImage tempImage;
    private LinkedList<TextureState> charList;
    private int totalCharSet = 256;
    private int fontWidth = 1;
    private int fontHeight = 1;
    private int[] tWidths;
    private int tHeights;
    private int tAscent;
    private int tDescent;

    public BmpFont(Font font) {
        tempImage = new BufferedImage(fontWidth, fontHeight, type);
        Graphics2D g = (Graphics2D) tempImage.getGraphics();
        g.setFont(font);

        this.tAscent = g.getFontMetrics().getMaxAscent();
        this.tDescent = g.getFontMetrics().getMaxDescent();

        this.charList = new LinkedList<TextureState>();

        this.tWidths = new int[totalCharSet];
        this.tHeights = tAscent + tDescent;

        for (int i = 0; i < totalCharSet; i++) {
            this.fontWidth = g.getFontMetrics().charWidth((char) i);
            this.fontHeight = g.getFontMetrics().getHeight();

            if (fontWidth < 1) {
                fontWidth = 1;
            }
            tWidths[i] = fontWidth;

            //TODO this needs tweaked
            float fontAspect = (float) (tHeights * 1.25) / (fontWidth);
            //float fontAspect = 1.0f;

            int size = font.getSize();
            float posX = font.getSize() / 2f - fontWidth / 2f;
            float posY = tAscent - tDescent;
            BufferedImage bImage = new BufferedImage(size, size * (int) (fontAspect), type);
            Graphics2D gt = (Graphics2D) bImage.getGraphics();
            gt.setFont(font);
            gt.drawString(String.valueOf((char) i), posX, posY);

            TextureState cTextureState = new TextureState();
            
            Texture cTexure = TextureManager.loadFromImage(TextureManagerExt.loadImage(bImage, true),
                Texture.MinificationFilter.Trilinear,
                fontFormat, true);

            cTextureState.setTexture(cTexure);

            charList.add(cTextureState);
        }
    }

    public TextureState getChar(int charCode) {
        return charList.get(charCode);
    }

    public float getTextAscent() {
        return tAscent;
    }

    public float getTextDescent() {
        return tDescent;
    }

    public int[] getMetricsWidths() {
        return tWidths;
    }

    public int getMetricsHeights() {
        return tHeights;
    }
//	public static synchronized GFont get(VectorFont font, Renderer renderer) {
//		float fontSize = 64f;
//		GFont g = gFonts.get(font);
//		if (g == null) {
//			Font gf = font.getFont().deriveFont(fontSize);
//			g = new GFont(gf, renderer);
//			gFonts.put(font, g);
//		}
//		return g;
//	}
}
