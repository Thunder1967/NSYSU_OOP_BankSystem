package nsysu.gui.windows;

import nsysu.bank.role.Person;
import static nsysu.util.enumtype.RoleType.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;


/**
 * 用其他 Layout 呼叫 navbar
 */

public class NavigationBarPanel extends JPanel {

    public static JPanel createNavbar(JFrame parentFrame) {
        int navbarHeight = 60;

        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(ThemeColor.NAVIGATION_BAR);
        nav.setPreferredSize(new Dimension(1280, navbarHeight));
        nav.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 20));
        nav.setName("navbar");

        updateNavbarState(nav, parentFrame);

        return nav;
    }

    public static void updateNavbarState(JPanel nav, JFrame parentFrame) {
        int menuWidth = 150;
        int navbarHeight = 60;

        nav.removeAll();
        Person user = UserSession.getInstance().getCurrentUser();

        // Left-side menu
        JPanel leftMenu = new JPanel(new GridBagLayout());
        leftMenu.setOpaque(false);

        GridBagConstraints lgbc = new GridBagConstraints();
        lgbc.gridy = 0;
        lgbc.fill = GridBagConstraints.VERTICAL;
        lgbc.weighty = 1.0;

        // Home
        JLabel home = new JLabel("Home");
        home.setFont(new Font("SansSerif", Font.BOLD, 28));
        home.setForeground(ThemeColor.MENU_TEXT);
        home.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lgbc.gridx = 0;
        lgbc.insets = new Insets(0, 0, 0, 25);
        leftMenu.add(home, lgbc);

        home.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ((MainGUI) parentFrame).switchToPage("HOME");
            }
        });

        if (user != null) {
            String identity = user.getRole();
            
            if (Objects.equals(identity, "user")) {

                // Account menu
                JLabel accountLabel = getJLabel(menuWidth, navbarHeight, "| Account |");

                JPopupMenu accountMenu = new JPopupMenu();
                accountMenu.setBorder(BorderFactory.createLineBorder(ThemeColor.MENU_BORDER, 1));
                accountMenu.setBackground(ThemeColor.MENU_BACKGROUND);
                JMenuItem savingsItem = new JMenuItem("Savings Account");
                JMenuItem timeItem = new JMenuItem("Time Deposit");
                JMenuItem checkingItem = new JMenuItem("Checking Account");
                JMenuItem USDItem = new JMenuItem("USD Account");

                configureMenuItem(savingsItem, parentFrame, "SAVINGSACCOUNT");
                configureMenuItem(timeItem, parentFrame, "TIMEDEPOSIT");
                configureMenuItem(checkingItem, parentFrame, "CHECKINGACCOUNT");
                configureMenuItem(USDItem, parentFrame, "USDACCOUNT");
                accountMenu.add(savingsItem);
                accountMenu.add(timeItem);
                accountMenu.add(checkingItem);
                accountMenu.add(USDItem);

                attachDebounceMenu(accountLabel, accountMenu, menuWidth);

                lgbc.gridx = 1;
                lgbc.insets = new Insets(0, 0, 0, 0);
                leftMenu.add(accountLabel, lgbc);


                // Transaction menu
                JLabel transactionLabel = getJLabel(menuWidth, navbarHeight, "| Transaction |");

                JPopupMenu transactionMenu = new JPopupMenu();
                transactionMenu.setBorder(BorderFactory.createLineBorder(ThemeColor.MENU_BORDER, 1));
                transactionMenu.setBackground(ThemeColor.MENU_BACKGROUND);
                JMenuItem withdrawItem = new JMenuItem("Withdraw");
                JMenuItem depositItem = new JMenuItem("Deposit");
                JMenuItem myItem = new JMenuItem("Transfer To My Account");
                JMenuItem otherItem = new JMenuItem("Transfer To Other's Account");

                configureMenuItem(withdrawItem, parentFrame, "WITHDRAW");
                configureMenuItem(depositItem, parentFrame, "DEPOSIT");
                configureMenuItem(myItem, parentFrame, "TOMYACCOUNT");
                configureMenuItem(otherItem, parentFrame, "TOOTHERSACCOUNT");
                transactionMenu.add(withdrawItem);
                transactionMenu.add(depositItem);
                transactionMenu.add(myItem);
                transactionMenu.add(otherItem);

                attachDebounceMenu(transactionLabel, transactionMenu, menuWidth);

                lgbc.gridx = 2;
                lgbc.insets = new Insets(0, 0, 0, 0);
                leftMenu.add(transactionLabel, lgbc);

                // Record menu
                JLabel recordLabel = getJLabel(menuWidth, navbarHeight, "| Record |");

                recordLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        recordLabel.setBackground(ThemeColor.MENU_BACKGROUND);
                        recordLabel.setForeground(ThemeColor.MENU_TEXT);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        Point mousePos = e.getPoint();

                        // 碰撞邊界篩選
                        if (mousePos.y < recordLabel.getHeight() || mousePos.x < 0 || mousePos.x > recordLabel.getWidth()) {
                            recordLabel.setBackground(ThemeColor.NAVIGATION_BAR);
                            recordLabel.setForeground(ThemeColor.MENU_TEXT);
                        }
                    }
                    public void mouseClicked(MouseEvent e) {
                        if (parentFrame instanceof MainGUI) {
                            ((MainGUI)parentFrame).switchToPage("RECORD");
                        }
                    }
                });

                lgbc.gridx = 3;
                lgbc.insets = new Insets(0, 0, 0, 0);
                leftMenu.add(recordLabel, lgbc);

            } else if (Objects.equals(identity, "administrator")) {

                // Transaction menu
                JLabel transactionLabel = getJLabel(menuWidth, navbarHeight, "| Transaction |");

                transactionLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        transactionLabel.setBackground(ThemeColor.MENU_BACKGROUND);
                        transactionLabel.setForeground(ThemeColor.MENU_TEXT);
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        Point mousePos = e.getPoint();

                        if (mousePos.y < transactionLabel.getHeight() || mousePos.x < 0 || mousePos.x > transactionLabel.getWidth()) {
                            transactionLabel.setBackground(ThemeColor.NAVIGATION_BAR);
                            transactionLabel.setForeground(ThemeColor.MENU_TEXT);
                        }
                    }
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (parentFrame instanceof MainGUI) {
                            ((MainGUI)parentFrame).switchToPage("TRANSACTION");
                        }
                    }
                });

                lgbc.gridx = 1;
                lgbc.insets = new Insets(0, 0, 0, 0);
                leftMenu.add(transactionLabel, lgbc);

                // Management menu
                JLabel managementLabel = getJLabel(menuWidth, navbarHeight, "| Management |");

                JPopupMenu managementMenu = new JPopupMenu();
                managementMenu.setBorder(BorderFactory.createLineBorder(ThemeColor.MENU_BORDER, 1));
                managementMenu.setBackground(ThemeColor.MENU_BACKGROUND);
                JMenuItem addItem = new JMenuItem("Add New User");
                JMenuItem deactivateItem = new JMenuItem("Deactivate User");
                JMenuItem unfreezeItem = new JMenuItem("Unfreeze User");
                JMenuItem searchItem = new JMenuItem("Search");

                configureMenuItem(addItem, parentFrame, "ADD");
                configureMenuItem(deactivateItem, parentFrame, "DEACTIVATE");
                configureMenuItem(unfreezeItem, parentFrame, "UNFREEZE");
                configureMenuItem(searchItem, parentFrame, "SEARCH");
                managementMenu.add(addItem);
                managementMenu.add(deactivateItem);
                managementMenu.add(unfreezeItem);
                managementMenu.add(searchItem);

                attachDebounceMenu(managementLabel, managementMenu, menuWidth);

                lgbc.gridx = 2;
                lgbc.insets = new Insets(0, 0, 0, 0);
                leftMenu.add(managementLabel, lgbc);
            }
        }

        nav.add(leftMenu, BorderLayout.WEST);
        
        JPanel rightMenu = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        rightMenu.setOpaque(false);
        rightMenu.setPreferredSize(new Dimension(240, 60));
        if (user == null) {
            Color startColor = Color.decode("#FFFFFF");
            Color endColor = Color.decode("#FFFFFF");
            GradientButton loginButton = new GradientButton("login", startColor, endColor, 12, ThemeColor.TEXT_BLACK);

            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    MainGUI.applyCall(parentFrame, "LOGIN");
                }
            });

            rightMenu.add(loginButton);
        } else {
            String identity = user.getRole();

            Color startColor = Color.decode("#8E2DE2");
            Color endColor = Color.decode("#4A00E0");
            GradientButton nameDisplay = new GradientButton(user.getName(), startColor, endColor, 12, ThemeColor.TEXT_WHITE);

            /**
             * 點這裡可以登出跟註銷這一整個帳號
             */

            rightMenu.add(nameDisplay);
        }
        
        nav.add(rightMenu, BorderLayout.EAST);
        nav.revalidate();
        nav.repaint();
    }

    private static JLabel getJLabel(int menuWidth, int navbarHeight, String text) {
        JLabel menuLabel = new JLabel(text);
        menuLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        menuLabel.setForeground(ThemeColor.MENU_TEXT);
        menuLabel.setHorizontalAlignment(SwingConstants.CENTER);
        menuLabel.setVerticalAlignment(SwingConstants.CENTER);
        menuLabel.setPreferredSize(new Dimension(menuWidth, navbarHeight));
        menuLabel.setOpaque(true);
        menuLabel.setBackground(ThemeColor.NAVIGATION_BAR);
        return menuLabel;
    }

    private static void configureMenuItem(JMenuItem item, JFrame parentFrame, String pageName) {
        item.setFont(new Font("SansSerif", Font.PLAIN, 14));
        item.setBackground(ThemeColor.MENU_BACKGROUND);
        item.setForeground(ThemeColor.MENU_TEXT);
        item.setOpaque(true);
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));
        item.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(ThemeColor.MENU_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(ThemeColor.MENU_BACKGROUND);
            }
        });

        item.addActionListener(e -> {
            if (parentFrame instanceof MainGUI) {
                ((MainGUI) parentFrame).switchToPage(pageName);
            }
        });
    }


    private static void attachDebounceMenu(JLabel label, JPopupMenu menu, int width) {
        /**
         * vibe-coding
         * cus how tf will I know this timer thing
         */
        Timer hoverTimer = new Timer(100, evt -> {
            if (label.isShowing()) {
                menu.setPreferredSize(new Dimension(width, menu.getPreferredSize().height));
                menu.show(label, 0, label.getHeight());
            }
        });
        hoverTimer.setRepeats(false);

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setBackground(ThemeColor.MENU_BACKGROUND);
                label.setForeground(ThemeColor.MENU_TEXT);
                hoverTimer.restart();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                Point mousePos = e.getPoint();
                hoverTimer.stop(); // 攔截尚未發出的選單

                if (mousePos.y < label.getHeight() || mousePos.x < 0 || mousePos.x > label.getWidth()) {
                    label.setBackground(ThemeColor.NAVIGATION_BAR);
                    label.setForeground(ThemeColor.MENU_TEXT);
                    menu.setVisible(false);
                }
            }
        });

        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (!label.getBounds().contains(SwingUtilities.convertPoint(menu, e.getPoint(), label))) {
                    label.setBackground(ThemeColor.NAVIGATION_BAR);
                    label.setForeground(ThemeColor.MENU_TEXT);
                    menu.setVisible(false);
                }
            }
        });
    }
}