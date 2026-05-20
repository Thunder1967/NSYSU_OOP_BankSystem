package nsysu.gui.windows;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class TranslucentField extends JPanel {
    private int radius; // Round-corner
    private boolean outline;

    public TranslucentField(int r, boolean o, int tb, int lb, int bb, int rb) {
        radius = r;
        outline = o;
        setOpaque(false); // To show background color
        setBorder(BorderFactory.createEmptyBorder(tb, lb, bb, rb)); // Inside border
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Anti-aliasing

        // Color
        g2.setColor(new Color(255, 255, 255, 40));
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));

        // Borderline
        if (outline) {
            g2.setColor(new Color(255, 255, 255, 80));
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
        }

        g2.dispose();
    }
}
