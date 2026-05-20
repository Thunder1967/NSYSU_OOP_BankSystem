package nsysu.gui.windows;

import javax.swing.*;
import java.awt.*;

public class HomeLayout extends JPanel {
    public HomeLayout() {
        setLayout(null);
        setBackground(ThemeColor.BACKGROUND); // Background color #0B1426

        // Title
        JLabel title = new JLabel("<html>Title</html>");
        title.setFont(new Font("SansSerif", Font.BOLD, 64));
        title.setBounds(50, 100, 800, 200);
        title.setForeground(ThemeColor.TEXT_WHITE);
        add(title);

        // Subtitle
        JLabel sub = new JLabel("<html>subtitle</html>");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 18));
        sub.setForeground(ThemeColor.TEXT_WHITE);
        sub.setBounds(55, 280, 600, 60);
        add(sub);
    }
}