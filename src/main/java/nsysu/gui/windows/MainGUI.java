package nsysu.gui.windows;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame{
    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    // constructor
    public MainGUI() {
        setTitle("Online Banking Platform"); // title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // action after frame closed
        setSize(1280, 720); // size
        getContentPane().setBackground(ThemeColor.BACKGROUND); // Background color #0B1426
        setLocationRelativeTo(null); // location (center)
        setLayout(new BorderLayout());

        JPanel navbar = NavigationBarPanel.createNavbar(this);
        add(navbar, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setOpaque(false);
        // layout
        HomeLayout home = new HomeLayout();
        contentPanel.add(home, "HOME");

        add(contentPanel, BorderLayout.CENTER);

        // default layout
        cardLayout.show(contentPanel, "HOME");
    }

    /**
     * vibe-coding
     */
    public void refreshNavbar() {
        Component[] components = this.getContentPane().getComponents();

        for (Component comp : components) {
            if (comp instanceof JPanel && "navbar".equals(comp.getName())) {
                NavigationBarPanel.updateNavbarState((JPanel) comp, this);
                return;
            }
        }
        System.out.println("Warning: navbar component not found in contentPane!");
    }

    public void switchToPage(String pageName) {
        if (cardLayout != null && contentPanel != null) {
            cardLayout.show(contentPanel, pageName);
            // repaint
            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    // call panel
    public static void applyCall(JFrame currentFrame, String panelType) {
        JPanel targetPanel = null;

        switch (panelType.toUpperCase()) {
            case "LOGIN":
                targetPanel = new LoginPanel(currentFrame);
                break;
            case "LOGOUT":
                /*
                 * 可能應該要叫 setting?
                 */
                break;
            default:
                return;
        }


        BlurContainer blurWrapper = new BlurContainer(currentFrame, targetPanel);
        currentFrame.setGlassPane(blurWrapper);
        currentFrame.getGlassPane().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI().setVisible(true));
    }
}
