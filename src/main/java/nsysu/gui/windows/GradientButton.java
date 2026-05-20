package nsysu.gui.windows;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

class GradientButton extends JButton {
    private final Color color1;
    private final Color color2;

    public GradientButton(String text, Color c1, Color c2, int s, Color tc) {
        super(text);
        this.color1 = c1;
        this.color2 = c2;

        // Basic setting
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(tc);
        setFont(new Font("SansSerif", Font.BOLD, s));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Anti-aliasing
        GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), 0, color2); // Color
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Round corner

        // Text
        FontMetrics fm = g2.getFontMetrics();
        Rectangle2D r = fm.getStringBounds(getText(), g2);
        int x = (getWidth() - (int) r.getWidth()) / 2;
        int y = (getHeight() - (int) r.getHeight()) / 2 + fm.getAscent();

        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}
