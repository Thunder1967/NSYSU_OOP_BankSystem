package nsysu.gui.windows;

import nsysu.bank.role.Person;
import nsysu.util.exception.ClosedUserException;
import nsysu.util.exception.TargetNotFindException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginPanel extends JPanel {

    // Constructor
    public LoginPanel(JFrame parentFrame) {
        setOpaque(false);

        this.setLayout(new GridBagLayout()); // Layout type

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;


        // Translucent background
        TranslucentField card = new TranslucentField(30, true, 5, 0, 40, 30);
        card.setLayout(new GridBagLayout());

        // Back
        JLabel backLabel = new JLabel("⬅", SwingConstants.LEFT);
        backLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        backLabel.setForeground(new Color(255, 255, 255)); // To be decided
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 10, 10, 0);
        card.add(backLabel, gbc);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // event when clicking
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parentFrame.getGlassPane().setVisible(false);
                ((MainGUI)parentFrame).switchToPage("HOME");
                System.out.println("Back to HomePage");
            }
        });

        // Title
        JLabel titleLabel = new JLabel("Welcome Back", SwingConstants.CENTER); // Text
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(255, 255, 255)); // To be decided
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 30, 10, 0);
        card.add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Login to your account", SwingConstants.CENTER); // Text
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(148, 163, 184)); // To be decided
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 30, 30, 0);
        card.add(subtitleLabel, gbc);

        // Error message
        JLabel errorLabel = new JLabel("", SwingConstants.LEFT);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        errorLabel.setForeground(ThemeColor.WARNING);
        gbc.gridy = 3;
        gbc.insets = new Insets(-15, 30, 0, 0);
        card.add(errorLabel, gbc);

        // Input for username
        JTextField nameField = new JTextField(15);
        JPanel nameCard = createInputCard(nameField, "Username");
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 30, 15, 0);
        card.add(nameCard, gbc);

        // Input for password
        JPasswordField passField = new JPasswordField(15);
        JPanel passwordCard = createInputCard(passField, "Password");
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 30, 5, 0);
        card.add(passwordCard, gbc);

        // Forgot password
        JLabel forgotLink = createHyperlink("Forgot Password?", SwingConstants.RIGHT, () -> {
            System.out.println("Grow up. Try to remember it. This is your fault.");
        });
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(-5, 30, 20, 0);
        card.add(forgotLink, gbc);

        // Login button
        Color startColor = Color.decode("#8E2DE2");
        Color endColor = Color.decode("#4A00E0");
        GradientButton loginButton = new GradientButton("LOGIN", startColor, endColor, 16, ThemeColor.TEXT_WHITE);
        loginButton.setPreferredSize(new Dimension(200, 45)); // Size
        gbc.gridy = 7;
        gbc.insets = new Insets(20, 30, 10, 0);
        card.add(loginButton, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String password = passField.getText();

                try{
                    Person person = Person.logIn(name,password);
                    UserSession.getInstance().setCurrentUser(person);

                    parentFrame.getGlassPane().setVisible(false);
                    if (parentFrame instanceof MainGUI) {
                        ((MainGUI) parentFrame).refreshNavbar();
                        ((MainGUI) parentFrame).switchToPage("HOME");
                    }
                }
                catch (TargetNotFindException ex){
                    errorLabel.setText("Incorrect UserName or Password!");
                    nameField.setText("");
                    passField.setText("");
                }
                catch ( ClosedUserException ex){
                    errorLabel.setText("This user has been closed!");
                    nameField.setText("");
                    passField.setText("");
                }
            }
        });

        titleLabel.setFocusable(true);
        titleLabel.requestFocusInWindow();
        this.add(card);
    }

    private JPanel createInputCard(JTextField field, String placeholder) {
        TranslucentField inputField = new TranslucentField(15, true, 2, 5, 2, 5);
        inputField.setLayout(new GridBagLayout());
        inputField.setBorder(null);

        styleTextField(field, placeholder);

        GridBagConstraints fgbc = new GridBagConstraints();
        fgbc.gridx = 0;
        fgbc.gridy = 0;
        fgbc.weightx = 1.0;
        fgbc.fill = GridBagConstraints.HORIZONTAL;
        fgbc.anchor = GridBagConstraints.WEST;

        inputField.add(field, fgbc);
        return inputField;
    }

    private void styleTextField(JTextField field, String placeholder) {
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5)); // Inside border
        field.setFocusable(true);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setForeground(new Color(255, 255, 255, 200)); // Text color
        field.setCaretColor(Color.WHITE); // Cursor color
        field.setHorizontalAlignment(JTextField.LEFT); // Type from left

        field.setText(placeholder);

        if (field instanceof JPasswordField) {
            ((JPasswordField) field).setEchoChar((char) 0);
        }

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar('●'); // Hide the password
                    }
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(255, 255, 255, 150));
                    if (field instanceof JPasswordField) {
                        ((JPasswordField) field).setEchoChar((char) 0);
                    }
                }
            }
        });
    }

    /**
     * vibe-coding
     * but might delete it cus we're not using it
     */
    private JLabel createHyperlink(String text, int alignment, Runnable action) {
        JLabel link = new JLabel(text, alignment);
        link.setFont(new Font("SansSerif", Font.PLAIN, 12));
        Color normalColor = Color.decode("#60A5FA");
        Color hoverColor = Color.decode("#93C5FD");
        link.setForeground(normalColor);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                link.setText("<html><u>" + text + "</u></html>");
                link.setForeground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                link.setText(text);
                link.setForeground(normalColor);
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // 執行傳進來的動作
                if (action != null) {
                    action.run();
                }
            }
        });

        return link;
    }
}
