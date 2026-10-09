package BankingManagementSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

/** Desktop interface for the existing MySQL-backed banking application. */
public class BankingUI extends JFrame {
    private static final Color NAVY = new Color(20, 37, 63);
    private static final Color BLUE = new Color(37, 99, 235);
    private static final Color BACKGROUND = new Color(244, 247, 251);
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel pages = new JPanel(cardLayout);
    private Connection connection;
    private String email;
    private long accountNumber;
    private JLabel welcomeLabel;
    private JLabel accountLabel;
    private JLabel balanceLabel;

    public BankingUI() {
        super("GalgotiasOwnBank | Online Banking");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 620));
        setSize(1080, 720);
        setLocationRelativeTo(null);
        pages.add(createAuthPage(), "auth");
        pages.add(createDashboardPage(), "dashboard");
        setContentPane(pages);
        connect();
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = System.getenv().getOrDefault("BANK_DB_URL", "jdbc:mysql://localhost:3306/banking_system");
            String user = System.getenv().getOrDefault("BANK_DB_USER", "root");
            String password = System.getenv().getOrDefault("BANK_DB_PASSWORD", "Admin@123");
            connection = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> showError("Could not connect to MySQL. Check that the database is running and your connection settings are correct.\n\n" + e.getMessage()));
        }
    }

    private JPanel createAuthPage() {
        JPanel root = new JPanel(new GridLayout(1, 2));
        JPanel brand = new JPanel();
        brand.setBackground(NAVY);
        brand.setLayout(new BoxLayout(brand, BoxLayout.Y_AXIS));
        brand.setBorder(new EmptyBorder(75, 58, 50, 50));
        JLabel logo = label("GalgotiasOwnBank", 16, Color.WHITE, true);
        JLabel headline = label("Banking that\nmoves with you.", 38, Color.WHITE, true);
        JLabel sub = label("A simple, secure place to manage your money.", 15, new Color(197, 211, 231), false);
        brand.add(logo); brand.add(Box.createVerticalStrut(100)); brand.add(headline); brand.add(Box.createVerticalStrut(18)); brand.add(sub);
        JPanel formArea = new JPanel(new GridBagLayout());
        formArea.setBackground(BACKGROUND);
        JPanel form = new JPanel();
        form.setBackground(Color.WHITE); form.setBorder(new EmptyBorder(34, 38, 34, 38));
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS)); form.setPreferredSize(new Dimension(410, 455));
        form.add(label("Welcome back", 27, NAVY, true));
        form.add(Box.createVerticalStrut(7)); form.add(label("Sign in or create your account to continue.", 14, new Color(107, 119, 139), false));
        form.add(Box.createVerticalStrut(25));
        JTextField loginEmail = field("Email address");
        JPasswordField loginPassword = passwordField("Password");
        JButton login = button("Sign in", BLUE, Color.WHITE);
        JButton register = button("Create an account", new Color(235, 241, 250), NAVY);
        form.add(labeledField("EMAIL ADDRESS", loginEmail));
        form.add(Box.createVerticalStrut(15));
        form.add(labeledField("PASSWORD", loginPassword));
        form.add(Box.createVerticalStrut(22));
        form.add(login);
        form.add(Box.createVerticalStrut(11));
        form.add(register);
        login.addActionListener(e -> login(loginEmail.getText().trim(), new String(loginPassword.getPassword())));
        register.addActionListener(e -> showRegistration());
        formArea.add(form);
        root.add(brand); root.add(formArea);
        return root;
    }

    private JPanel createDashboardPage() {
        JPanel root = new JPanel(new BorderLayout()); root.setBackground(BACKGROUND);
        JPanel top = new JPanel(new BorderLayout()); top.setBackground(Color.WHITE); top.setBorder(new EmptyBorder(20, 34, 20, 34));
        top.add(label("GalgotiasOwnBank  /  PERSONAL BANKING", 15, NAVY, true), BorderLayout.WEST);
        JButton logout = button("Sign out", new Color(235, 241, 250), NAVY);
        logout.addActionListener(e -> { email = null; accountNumber = 0; cardLayout.show(pages, "auth"); }); top.add(logout, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);
        JPanel content = new JPanel(new BorderLayout(0, 22)); content.setOpaque(false); content.setBorder(new EmptyBorder(30, 54, 34, 54));
        JPanel greeting = new JPanel(); greeting.setOpaque(false); greeting.setLayout(new BoxLayout(greeting, BoxLayout.Y_AXIS));
        welcomeLabel = label("Good day", 30, NAVY, true); greeting.add(welcomeLabel); greeting.add(Box.createVerticalStrut(7)); greeting.add(label("Here is your account at a glance.", 15, new Color(107, 119, 139), false));
        content.add(greeting, BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 18)); center.setOpaque(false);
        JPanel summary = card(); summary.setLayout(new BorderLayout(20, 0));
        JPanel balanceInfo = new JPanel(); balanceInfo.setOpaque(false); balanceInfo.setLayout(new BoxLayout(balanceInfo, BoxLayout.Y_AXIS));
        balanceInfo.add(label("AVAILABLE BALANCE", 12, new Color(107, 119, 139), true)); balanceInfo.add(Box.createVerticalStrut(8));
        balanceLabel = label("••••••", 30, NAVY, true); balanceInfo.add(balanceLabel);
        accountLabel = label("Account number", 13, new Color(107, 119, 139), false);
        summary.add(balanceInfo, BorderLayout.WEST); summary.add(accountLabel, BorderLayout.CENTER);
        JButton balanceAction = button("Check bank balance", BLUE, Color.WHITE);
        balanceAction.setPreferredSize(new Dimension(205, 48)); balanceAction.setMaximumSize(new Dimension(230, 48));
        balanceAction.addActionListener(e -> showBalance()); summary.add(balanceAction, BorderLayout.EAST);
        JPanel actions = new JPanel(new BorderLayout(0, 12)); actions.setOpaque(false);
        JLabel actionsTitle = label("Your banking tools", 21, NAVY, true); actions.add(actionsTitle, BorderLayout.NORTH);
        JPanel actionGrid = new JPanel(new GridLayout(2, 3, 14, 14)); actionGrid.setOpaque(false);
        JButton deposit = actionButton("Deposit money", "Add funds to your account", BLUE, Color.WHITE);
        JButton withdraw = actionButton("Withdraw money", "Take money out", BLUE, Color.WHITE);
        JButton transfer = actionButton("Transfer money", "Send money to another account", BLUE, Color.WHITE);
        JButton balance = actionButton("Check bank balance", "View your current balance", Color.WHITE, NAVY);
        JButton details = actionButton("Account details", "View your account number", Color.WHITE, NAVY);
        deposit.addActionListener(e -> transactionDialog("Deposit"));
        withdraw.addActionListener(e -> transactionDialog("Withdraw"));
        transfer.addActionListener(e -> transferDialog());
        balance.addActionListener(e -> showBalance());
        details.addActionListener(e -> showAccountDetails());
        actionGrid.add(deposit); actionGrid.add(withdraw); actionGrid.add(transfer); actionGrid.add(balance); actionGrid.add(details);
        JPanel unusedCell = new JPanel(); unusedCell.setOpaque(false); actionGrid.add(unusedCell);
        actions.add(actionGrid, BorderLayout.CENTER);
        center.add(summary, BorderLayout.NORTH); center.add(actions, BorderLayout.CENTER);
        content.add(center, BorderLayout.CENTER); root.add(content, BorderLayout.CENTER);
        return root;
    }

    private void login(String enteredEmail, String password) {
        if (connection == null || connectionIsClosed()) { showError("The database is not connected. Restart after starting MySQL."); return; }
        try (PreparedStatement ps = connection.prepareStatement("SELECT full_name FROM User WHERE email = ? AND password = ?")) {
            ps.setString(1, enteredEmail); ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { showError("Email or password is incorrect."); return; }
                email = enteredEmail;
                if (!accountExists(email)) { showOpenAccount(rs.getString("full_name")); return; }
                accountNumber = findAccount(email); showDashboard(rs.getString("full_name"));
            }
        } catch (SQLException ex) { showError("Could not sign in: " + ex.getMessage()); }
    }

    private void showRegistration() {
        JTextField name = field("Full name"), userEmail = field("Email address"); JPasswordField pass = passwordField("Password");
        JPanel panel = dialogForm(new JComponent[]{labeledField("FULL NAME", name), labeledField("EMAIL ADDRESS", userEmail), labeledField("PASSWORD", pass)});
        int result = JOptionPane.showConfirmDialog(this, panel, "Create your account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        if (name.getText().isBlank() || userEmail.getText().isBlank() || pass.getPassword().length == 0) { showError("Please complete every field."); return; }
        try (PreparedStatement check = connection.prepareStatement("SELECT 1 FROM User WHERE email = ?")) {
            check.setString(1, userEmail.getText().trim()); try (ResultSet rs = check.executeQuery()) { if (rs.next()) { showError("An account already exists for that email."); return; } }
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO User(full_name, email, password) VALUES (?, ?, ?)")) {
                ps.setString(1, name.getText().trim()); ps.setString(2, userEmail.getText().trim()); ps.setString(3, new String(pass.getPassword())); ps.executeUpdate();
            }
            JOptionPane.showMessageDialog(this, "Registration complete. You can now sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) { showError("Could not register: " + ex.getMessage()); }
    }

    private void showOpenAccount(String fullName) {
        JTextField initial = field("Initial deposit (₹)"); JPasswordField pin = passwordField("Choose a security PIN");
        JPanel panel = dialogForm(new JComponent[]{labeledField("INITIAL DEPOSIT (₹)", initial), labeledField("SECURITY PIN", pin)});
        int result = JOptionPane.showConfirmDialog(this, panel, "Open your bank account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) { email = null; return; }
        try {
            BigDecimal amount = new BigDecimal(initial.getText().trim());
            String securityPin = new String(pin.getPassword());
            if (amount.signum() < 0 || securityPin.isBlank()) { showError("Enter a non-negative deposit and a security PIN."); return; }
            accountNumber = nextAccountNumber();
            try (PreparedStatement ps = connection.prepareStatement("INSERT INTO Accounts(account_number, full_name, email, balance, security_pin) VALUES (?, ?, ?, ?, ?)")) {
                ps.setLong(1, accountNumber); ps.setString(2, fullName); ps.setString(3, email); ps.setBigDecimal(4, amount); ps.setString(5, securityPin); ps.executeUpdate();
            }
            showDashboard(fullName);
        } catch (NumberFormatException ex) { showError("Enter a valid initial deposit amount."); }
        catch (SQLException ex) { showError("Could not open account: " + ex.getMessage()); }
    }

    private void transactionDialog(String action) {
        JTextField amountField = field("Amount (₹)"); JPasswordField pin = passwordField("Security PIN");
        JPanel panel = dialogForm(new JComponent[]{labeledField("AMOUNT (₹)", amountField), labeledField("SECURITY PIN", pin)});
        if (JOptionPane.showConfirmDialog(this, panel, action, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            BigDecimal amount = new BigDecimal(amountField.getText().trim());
            if (amount.signum() <= 0) { showError("Amount must be greater than zero."); return; }
            connection.setAutoCommit(false);
            try {
                if (!pinMatches(accountNumber, new String(pin.getPassword()))) throw new SQLException("Security PIN is incorrect.");
                if (action.equals("Withdraw")) {
                    try (PreparedStatement ps = connection.prepareStatement("UPDATE Accounts SET balance = balance - ? WHERE account_number = ? AND balance >= ?")) {
                        ps.setBigDecimal(1, amount); ps.setLong(2, accountNumber); ps.setBigDecimal(3, amount);
                        if (ps.executeUpdate() == 0) throw new SQLException("Insufficient balance.");
                    }
                } else {
                    try (PreparedStatement ps = connection.prepareStatement("UPDATE Accounts SET balance = balance + ? WHERE account_number = ?")) {
                        ps.setBigDecimal(1, amount); ps.setLong(2, accountNumber); ps.executeUpdate();
                    }
                }
                connection.commit(); JOptionPane.showMessageDialog(this, action + " completed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE); showBalance();
            } catch (SQLException ex) { connection.rollback(); showError(ex.getMessage()); }
            finally { connection.setAutoCommit(true); }
        } catch (SQLException ex) { showError("Transaction failed: " + ex.getMessage()); }
    }

    private void transferDialog() {
        JTextField receiver = field("Recipient account number"), amountField = field("Amount (₹)"); JPasswordField pin = passwordField("Security PIN");
        JPanel panel = dialogForm(new JComponent[]{labeledField("RECIPIENT ACCOUNT NUMBER", receiver), labeledField("AMOUNT (₹)", amountField), labeledField("SECURITY PIN", pin)});
        if (JOptionPane.showConfirmDialog(this, panel, "Transfer money", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try {
            long recipient = Long.parseLong(receiver.getText().trim()); BigDecimal amount = new BigDecimal(amountField.getText().trim());
            if (recipient == accountNumber || amount.signum() <= 0) { showError("Choose another account and enter an amount greater than zero."); return; }
            connection.setAutoCommit(false);
            try {
                if (!pinMatches(accountNumber, new String(pin.getPassword()))) throw new SQLException("Security PIN is incorrect.");
                try (PreparedStatement debit = connection.prepareStatement("UPDATE Accounts SET balance = balance - ? WHERE account_number = ? AND balance >= ?");
                     PreparedStatement credit = connection.prepareStatement("UPDATE Accounts SET balance = balance + ? WHERE account_number = ?")) {
                    debit.setBigDecimal(1, amount); debit.setLong(2, accountNumber); debit.setBigDecimal(3, amount);
                    if (debit.executeUpdate() != 1) throw new SQLException("Insufficient balance.");
                    credit.setBigDecimal(1, amount); credit.setLong(2, recipient);
                    if (credit.executeUpdate() != 1) throw new SQLException("Recipient account was not found.");
                }
                connection.commit(); JOptionPane.showMessageDialog(this, "Transfer completed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE); showBalance();
            } catch (SQLException ex) { connection.rollback(); showError(ex.getMessage()); }
            finally { connection.setAutoCommit(true); }
        } catch (NumberFormatException ex) { showError("Enter a valid recipient account number and amount."); }
        catch (SQLException ex) { showError("Transfer failed: " + ex.getMessage()); }
    }

    private void showBalance() {
        JPasswordField pin = passwordField("Security PIN");
        if (JOptionPane.showConfirmDialog(this, dialogForm(new JComponent[]{labeledField("SECURITY PIN", pin)}), "Verify your PIN", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
        try (PreparedStatement ps = connection.prepareStatement("SELECT balance FROM Accounts WHERE account_number = ? AND security_pin = ?")) {
            ps.setLong(1, accountNumber); ps.setString(2, new String(pin.getPassword()));
            try (ResultSet rs = ps.executeQuery()) { if (!rs.next()) { showError("Security PIN is incorrect."); return; } balanceLabel.setText("₹" + rs.getBigDecimal("balance").toPlainString()); }
        } catch (SQLException ex) { showError("Could not load balance: " + ex.getMessage()); }
    }

    private void showAccountDetails() {
        try (PreparedStatement ps = connection.prepareStatement("SELECT full_name, email, account_number FROM Accounts WHERE account_number = ?")) {
            ps.setLong(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) { showError("Account details could not be found."); return; }
                String details = "Name: " + rs.getString("full_name") + "\nEmail: " + rs.getString("email") + "\nAccount number: " + rs.getLong("account_number");
                JOptionPane.showMessageDialog(this, details, "Account details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) { showError("Could not load account details: " + ex.getMessage()); }
    }

    private void showDashboard(String name) {
        welcomeLabel.setText("Welcome, " + name);
        accountLabel.setText("Account number  •  " + accountNumber);
        balanceLabel.setText("••••••"); cardLayout.show(pages, "dashboard");
    }

    private boolean accountExists(String userEmail) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM Accounts WHERE email = ?")) { ps.setString(1, userEmail); try (ResultSet rs = ps.executeQuery()) { return rs.next(); } }
    }
    private long findAccount(String userEmail) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT account_number FROM Accounts WHERE email = ?")) { ps.setString(1, userEmail); try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getLong(1); throw new SQLException("Account not found"); } }
    }
    private long nextAccountNumber() throws SQLException {
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery("SELECT COALESCE(MAX(account_number), 10000099) + 1 FROM Accounts")) { rs.next(); return rs.getLong(1); }
    }
    private boolean pinMatches(long account, String pin) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM Accounts WHERE account_number = ? AND security_pin = ?")) { ps.setLong(1, account); ps.setString(2, pin); try (ResultSet rs = ps.executeQuery()) { return rs.next(); } }
    }
    private boolean connectionIsClosed() { try { return connection == null || connection.isClosed(); } catch (SQLException e) { return true; } }
    private JPanel dialogForm(JComponent[] fields) { JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setBorder(new EmptyBorder(8, 5, 4, 5)); for (int i = 0; i < fields.length; i++) { p.add(fields[i]); if (i + 1 < fields.length) p.add(Box.createVerticalStrut(10)); } return p; }
    private JPanel labeledField(String title, JComponent input) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        JLabel caption = label(title, 11, new Color(73, 88, 110), true);
        caption.setBorder(new EmptyBorder(0, 0, 7, 0));
        caption.setAlignmentX(Component.LEFT_ALIGNMENT);
        input.setAlignmentX(Component.LEFT_ALIGNMENT);
        group.add(caption);
        group.add(input);
        return group;
    }
    private JPanel card() { JPanel p = new JPanel(); p.setBackground(Color.WHITE); p.setBorder(new EmptyBorder(28, 28, 28, 28)); return p; }
    private JButton actionButton(String title, String description, Color background, Color foreground) {
        JButton b = new JButton("<html><div style='text-align:left'><b>" + title + "</b><br><span style='font-size:11px'>" + description + "</span></div></html>");
        b.setFont(new Font("SansSerif", Font.PLAIN, 14)); b.setForeground(foreground); b.setBackground(background); b.setOpaque(true); b.setContentAreaFilled(true); b.setFocusPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT); b.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(222, 229, 239)), new EmptyBorder(16, 18, 16, 18)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
    private JTextField field(String placeholder) { JTextField f = new JTextField(); f.setFont(new Font("SansSerif", Font.PLAIN, 15)); f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 227, 237)), new EmptyBorder(12, 13, 12, 13))); f.setPreferredSize(new Dimension(300, 46)); f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46)); f.putClientProperty("JTextField.placeholderText", placeholder); return f; }
    private JPasswordField passwordField(String placeholder) { JPasswordField f = new JPasswordField(); f.setFont(new Font("SansSerif", Font.PLAIN, 15)); f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 227, 237)), new EmptyBorder(12, 13, 12, 13))); f.setPreferredSize(new Dimension(300, 46)); f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46)); f.putClientProperty("JTextField.placeholderText", placeholder); return f; }
    private JButton button(String text, Color background, Color foreground) { JButton b = new JButton(text); b.setFont(new Font("SansSerif", Font.BOLD, 14)); b.setForeground(foreground); b.setBackground(background); b.setOpaque(true); b.setContentAreaFilled(true); b.setFocusPainted(false); b.setBorder(new EmptyBorder(13, 18, 13, 18)); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); b.setAlignmentX(Component.LEFT_ALIGNMENT); b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48)); return b; }
    private JLabel label(String text, int size, Color color, boolean bold) { JLabel l = new JLabel("<html>" + text.replace("\n", "<br>") + "</html>"); l.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size)); l.setForeground(color); return l; }
    private void showError(String message) { JOptionPane.showMessageDialog(this, message, "GalgotiasOwnBank", JOptionPane.ERROR_MESSAGE); }
}
