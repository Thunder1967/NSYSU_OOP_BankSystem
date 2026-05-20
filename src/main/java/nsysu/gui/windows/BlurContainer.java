package nsysu.gui.windows;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This is a container to display modal popups with a blurred background.
 * Here's the brief execution pipeline:
 * 1. Snapshots the current JFrame content as a BufferedImage.
 * 2. Reduces image dimensions to minimize computational complexity.
 * 3. Applies a spatial convolution (Box Blur) to the downsampled image.
 * 4. Scales the image back to original dimensions, leveraging interpolation for smoothness.
 *
 * Implementation Note:
 * This was optimized with AI assistance.
 */

public class BlurContainer extends JPanel {
    private BufferedImage blurredImage;
    private final JFrame parent;

    // constructor
    public BlurContainer(JFrame parent, JPanel content) {
        this.parent = parent;
        setLayout(new GridBagLayout());
        setOpaque(false);

        refreshBlur();
        add(content); // add the designated panel
    }

    // make the background blurry
    public void refreshBlur() {
        BufferedImage screenshot = new BufferedImage(parent.getWidth(), parent.getHeight(), BufferedImage.TYPE_INT_RGB);
        parent.paint(screenshot.getGraphics());
        this.blurredImage = applyZoom(screenshot, 4, 4);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (blurredImage != null) {
            g.drawImage(blurredImage, 0, 0, null);
            g.setColor(new Color(11, 20, 38, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private BufferedImage applyBlur(BufferedImage src, int radius) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage dest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // box blur
        float weight = 1.0f / (radius * radius);
        float[] data = new float[radius * radius];
        for (int i = 0; i < data.length; i++) data[i] = weight;

        java.awt.image.Kernel kernel = new java.awt.image.Kernel(radius, radius, data);
        java.awt.image.ConvolveOp op = new java.awt.image.ConvolveOp(kernel, java.awt.image.ConvolveOp.EDGE_NO_OP, null);

        return op.filter(src, dest);
    }

    private BufferedImage applyZoom(BufferedImage src, int factor, int radius) {
        int w = src.getWidth();
        int h = src.getHeight();
        int sw = w / factor;
        int sh = h / factor;

        // reduce the size of the image
        BufferedImage smallImage = new BufferedImage(sw, sh, BufferedImage.TYPE_INT_RGB);
        Graphics2D gSmall = smallImage.createGraphics();
        // bilinear
        gSmall.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gSmall.drawImage(src, 0, 0, sw, sh, null);
        gSmall.dispose();

        smallImage = applyBlur(smallImage, radius); // blur

        // enlarge the size of the image
        BufferedImage dest = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D gDest = dest.createGraphics();
        // bilinear
        gDest.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gDest.drawImage(smallImage, 0, 0, w, h, null);
        gDest.dispose();

        return dest;
    }
}
