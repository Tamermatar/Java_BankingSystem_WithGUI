import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class BankingGUI extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Customer currentCustomer;
    private FileManager fileManager;
    
    // Login panel components
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    // Account panel components
    private JTable accountsTable;
    private DefaultTableModel accountsTableModel;
    
    // Transaction panel components
    private JComboBox<String> sourceAccountCombo;
    private JComboBox<String> destinationAccountCombo;
    private JTextField amountField;
    
    // External transfer components
    private JComboBox<String> externalSourceAccountCombo;
    private JTextField recipientUsernameField;
    private JTextField recipientAccountField;
    private JTextField externalAmountField;
    
    // Transaction history components
    private JTable transactionTable;
    private DefaultTableModel transactionTableModel;
    
    public BankingGUI() {
        fileManager = new FileManager();
        
        setTitle("Banking System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Initialize the card layout and main panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create different panels
        JPanel welcomePanel = createWelcomePanel();
        JPanel loginPanel = createLoginPanel();
        JPanel registerPanel = createRegisterPanel();
        JPanel dashboardPanel = createDashboardPanel();
        JPanel accountsPanel = createAccountsPanel();
        JPanel transferPanel = createTransferPanel();
        JPanel externalTransferPanel = createExternalTransferPanel();
        JPanel createAccountPanel = createNewAccountPanel();
        JPanel profilePanel = createProfilePanel();
        JPanel transactionHistoryPanel = createTransactionHistoryPanel();
        
        // Add panels to the card layout
        mainPanel.add(welcomePanel, "Welcome");
        mainPanel.add(loginPanel, "Login");
        mainPanel.add(registerPanel, "Register");
        mainPanel.add(dashboardPanel, "Dashboard");
        mainPanel.add(accountsPanel, "Accounts");
        mainPanel.add(transferPanel, "Transfer");
        mainPanel.add(externalTransferPanel, "ExternalTransfer");
        mainPanel.add(createAccountPanel, "CreateAccount");
        mainPanel.add(profilePanel, "Profile");
        mainPanel.add(transactionHistoryPanel, "TransactionHistory");
        
        // Show the welcome panel first
        cardLayout.show(mainPanel, "Welcome");
        
        add(mainPanel);
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome to the Banking System", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.NORTH);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton exitButton = new JButton("Exit");
        
        loginButton.addActionListener(e -> cardLayout.show(mainPanel, "Login"));
        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "Register"));
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);
        buttonsPanel.add(exitButton);
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Login to Your Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");
        
        loginButton.addActionListener(e -> handleLogin());
        backButton.addActionListener(e -> {
            usernameField.setText("");
            passwordField.setText("");
            cardLayout.show(mainPanel, "Welcome");
        });
        
        buttonsPanel.add(loginButton);
        buttonsPanel.add(backButton);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String role = fileManager.LoadUsers(username, password);
            if (role != null) {
                switch (role) {
                    case "Customer":
                        currentCustomer = new Customer();
                        currentCustomer.UserName = username;
                        currentCustomer.Password = password;
                        fileManager.LoadAccounts(currentCustomer);
                        updateAccountsTable();
                        cardLayout.show(mainPanel, "Dashboard");
                        break;
                    case "Employee":
                        employee currentEmployee = new employee();
                        currentEmployee.UserName = username;
                        currentEmployee.Password = password;
                        showEmployeePanel(currentEmployee);
                        break;
                    case "Admin":
                        Admin currentAdmin = new Admin();
                        currentAdmin.UserName = username;
                        currentAdmin.Password = password;
                        showAdminPanel(currentAdmin);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "Unknown role", "Login Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error during login: " + e.getMessage(), "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Register New Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        
        JLabel userIdLabel = new JLabel("User ID:");
        JTextField userIdField = new JTextField(20);
        
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        
        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField(20);
        
        formPanel.add(userIdLabel);
        formPanel.add(userIdField);
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");
        
        registerButton.addActionListener(e -> {
            String userId = userIdField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            String name = nameField.getText();
            
            if (userId.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "All fields are required", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(panel, "Password must be at least 6 characters long", "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Customer customer = new Customer();
            customer.create(fileManager, userId, username, password, email, name, "Customer");
            
            JOptionPane.showMessageDialog(panel, "Registration successful! You can now login.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields and go back to welcome
            userIdField.setText("");
            usernameField.setText("");
            passwordField.setText("");
            emailField.setText("");
            nameField.setText("");
            cardLayout.show(mainPanel, "Welcome");
        });
        
        backButton.addActionListener(e -> {
            userIdField.setText("");
            usernameField.setText("");
            passwordField.setText("");
            emailField.setText("");
            nameField.setText("");
            cardLayout.show(mainPanel, "Welcome");
        });
        
        buttonsPanel.add(registerButton);
        buttonsPanel.add(backButton);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Customer Dashboard", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Menu panel
        JPanel menuPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        JButton viewAccountsButton = new JButton("View Accounts");
        JButton transferButton = new JButton("Transfer Between My Accounts");
        JButton externalTransferButton = new JButton("Transfer to Another Customer");
        JButton transactionHistoryButton = new JButton("Transaction History");
        JButton createAccountButton = new JButton("Create Account");
        JButton profileButton = new JButton("Update Profile");
        JButton logoutButton = new JButton("Logout");
        
        viewAccountsButton.addActionListener(e -> {
            updateAccountsTable();
            cardLayout.show(mainPanel, "Accounts");
        });
        
        transferButton.addActionListener(e -> {
            updateTransferCombos();
            cardLayout.show(mainPanel, "Transfer");
        });
        
        externalTransferButton.addActionListener(e -> {
            updateExternalTransferSourceCombo();
            cardLayout.show(mainPanel, "ExternalTransfer");
        });
        
        transactionHistoryButton.addActionListener(e -> {
            updateTransactionHistoryTable();
            cardLayout.show(mainPanel, "TransactionHistory");
        });
        
        createAccountButton.addActionListener(e -> cardLayout.show(mainPanel, "CreateAccount"));
        profileButton.addActionListener(e -> cardLayout.show(mainPanel, "Profile"));
        
        logoutButton.addActionListener(e -> {
            currentCustomer = null;
            cardLayout.show(mainPanel, "Welcome");
        });
        
        menuPanel.add(viewAccountsButton);
        menuPanel.add(transferButton);
        menuPanel.add(externalTransferButton);
        menuPanel.add(transactionHistoryButton);
        menuPanel.add(createAccountButton);
        menuPanel.add(profileButton);
        menuPanel.add(logoutButton);
        
        panel.add(menuPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Your Accounts", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table for accounts
        String[] columnNames = {"Account Number", "Type", "Balance", "Status"};
        accountsTableModel = new DefaultTableModel(columnNames, 0);
        accountsTable = new JTable(accountsTableModel);
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton depositButton = new JButton("Deposit");
        JButton withdrawButton = new JButton("Withdraw");
        JButton backButton = new JButton("Back to Dashboard");
        
        depositButton.addActionListener(e -> {
            int selectedRow = accountsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an account to deposit to", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showDepositDialog(selectedRow);
        });
        
        withdrawButton.addActionListener(e -> {
            int selectedRow = accountsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select an account to withdraw from", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showWithdrawDialog(selectedRow);
        });
        
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        
        actionPanel.add(depositButton);
        actionPanel.add(withdrawButton);
        actionPanel.add(backButton);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void showDepositDialog(int selectedRow) {
        Account selectedAccount = currentCustomer.Accounts.get(selectedRow);
        
        JDialog dialog = new JDialog(this, "Deposit to Account", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Account Number:"));
        panel.add(new JLabel(selectedAccount.AccountNumber));
        
        panel.add(new JLabel("Current Balance:"));
        panel.add(new JLabel("$" + selectedAccount.getBalance()));
        
        panel.add(new JLabel("Deposit Amount:"));
        JTextField amountField = new JTextField(10);
        panel.add(amountField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        
        confirmButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Perform deposit
                selectedAccount.deposit(amount);
                
                // Create transaction record
                Transaction deposit = new Transaction();
                deposit.TransactionID = java.util.UUID.randomUUID().toString();
                deposit.TimeStamp = new java.util.Date().toString();
                deposit.Type = "Deposit";
                deposit.amount = amount;
                deposit.sourceAccount = null;
                deposit.destinationAccount = selectedAccount;
                deposit.Status = "Completed";
                selectedAccount.transactions.add(deposit);
                
                // Save transaction to file
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("transactions.txt", true))) {
                    bw.write(deposit.TransactionID + "," + deposit.TimeStamp + "," + deposit.Type + "," + amount + "," + 
                             "N/A" + "," + selectedAccount.AccountNumber + "," + deposit.Status + "\n");
                } catch (IOException ex) {
                    System.out.println("Failed to write transaction to file.");
                }
                
                // Update account balance in accounts.txt
                updateAccountBalance(selectedAccount.AccountNumber, selectedAccount.getBalance());
                
                JOptionPane.showMessageDialog(dialog, "Deposit successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateAccountsTable();
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showWithdrawDialog(int selectedRow) {
        Account selectedAccount = currentCustomer.Accounts.get(selectedRow);
        
        JDialog dialog = new JDialog(this, "Withdraw from Account", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        panel.add(new JLabel("Account Number:"));
        panel.add(new JLabel(selectedAccount.AccountNumber));
        
        panel.add(new JLabel("Current Balance:"));
        panel.add(new JLabel("$" + selectedAccount.getBalance()));
        
        panel.add(new JLabel("Withdraw Amount:"));
        JTextField amountField = new JTextField(10);
        panel.add(amountField);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");
        
        confirmButton.addActionListener(e -> {
            try {
                int amount = Integer.parseInt(amountField.getText());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (amount > selectedAccount.getBalance()) {
                    JOptionPane.showMessageDialog(dialog, "Insufficient funds", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check withdrawal limits for savings account
                if (selectedAccount instanceof savingAccount) {
                    savingAccount savings = (savingAccount) selectedAccount;
                    if (savings.withdrawalsThisMonth.size() >= savings.withdrawalLimit) {
                        JOptionPane.showMessageDialog(dialog, "You have reached the maximum number of withdrawals for this month", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (amount > selectedAccount.getBalance() - savings.minimumBalance) {
                        JOptionPane.showMessageDialog(dialog, "Cannot withdraw beyond minimum balance of $" + savings.minimumBalance, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                // Perform withdrawal
                selectedAccount.withdraw(amount);
                
                // Create transaction record
                Transaction withdrawal = new Transaction();
                withdrawal.TransactionID = java.util.UUID.randomUUID().toString();
                withdrawal.TimeStamp = new java.util.Date().toString();
                withdrawal.Type = "Withdrawal";
                withdrawal.amount = amount;
                withdrawal.sourceAccount = selectedAccount;
                withdrawal.destinationAccount = null;
                withdrawal.Status = "Completed";
                selectedAccount.transactions.add(withdrawal);
                
                // Save transaction to file
                try (BufferedWriter bw = new BufferedWriter(new FileWriter("transactions.txt", true))) {
                    bw.write(withdrawal.TransactionID + "," + withdrawal.TimeStamp + "," + withdrawal.Type + "," + amount + "," + 
                             selectedAccount.AccountNumber + "," + "N/A" + "," + withdrawal.Status + "\n");
                } catch (IOException ex) {
                    System.out.println("Failed to write transaction to file.");
                }
                
                // Update account balance in accounts.txt
                updateAccountBalance(selectedAccount.AccountNumber, selectedAccount.getBalance());
                
                JOptionPane.showMessageDialog(dialog, "Withdrawal successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateAccountsTable();
                dialog.dispose();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void updateAccountBalance(String accountNumber, int newBalance) {
        File file = new File("accounts.txt");
        File tempFile = new File("accounts_temp.txt");
        
        try (Scanner scanner = new Scanner(file);
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                
                if (parts.length >= 4 && parts[0].equals(accountNumber)) {
                    // Update balance
                    parts[1] = String.valueOf(newBalance);
                    
                    // Write updated line
                    StringBuilder newLine = new StringBuilder();
                    for (int i = 0; i < parts.length; i++) {
                        newLine.append(parts[i]);
                        if (i < parts.length - 1) {
                            newLine.append(",");
                        }
                    }
                    writer.write(newLine.toString());
                } else {
                    writer.write(line);
                }
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating account balance", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Replace the old file with the new one
        if (!file.delete()) {
            JOptionPane.showMessageDialog(this, "Error updating account balance", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!tempFile.renameTo(file)) {
            JOptionPane.showMessageDialog(this, "Error updating account balance", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
    private void updateAccountsTable() {
        accountsTableModel.setRowCount(0);
        
        if (currentCustomer != null && currentCustomer.Accounts != null) {
            for (Account account : currentCustomer.Accounts) {
                String accountType = account instanceof checkingAccount ? "Checking" : "Saving";
                Object[] row = {
                    account.AccountNumber,
                    accountType,
                    account.getBalance(),
                    account.status
                };
                accountsTableModel.addRow(row);
            }
        }
    }
    
    private JPanel createTransferPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Transfer Funds", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JLabel sourceLabel = new JLabel("From Account:");
        sourceAccountCombo = new JComboBox<>();
        
        JLabel destLabel = new JLabel("To Account:");
        destinationAccountCombo = new JComboBox<>();
        
        JLabel amountLabel = new JLabel("Amount:");
        amountField = new JTextField(10);
        
        formPanel.add(sourceLabel);
        formPanel.add(sourceAccountCombo);
        formPanel.add(destLabel);
        formPanel.add(destinationAccountCombo);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton transferButton = new JButton("Transfer");
        JButton backButton = new JButton("Back");
        
        transferButton.addActionListener(e -> handleTransfer());
        backButton.addActionListener(e -> {
            amountField.setText("");
            cardLayout.show(mainPanel, "Dashboard");
        });
        
        buttonsPanel.add(transferButton);
        buttonsPanel.add(backButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateTransferCombos() {
        sourceAccountCombo.removeAllItems();
        destinationAccountCombo.removeAllItems();
        
        if (currentCustomer != null && currentCustomer.Accounts != null) {
            for (Account account : currentCustomer.Accounts) {
                String displayText = account.AccountNumber + " (Balance: $" + account.getBalance() + ")";
                sourceAccountCombo.addItem(displayText);
                destinationAccountCombo.addItem(displayText);
            }
        }
    }
    
    private void handleTransfer() {
        if (sourceAccountCombo.getSelectedIndex() == destinationAccountCombo.getSelectedIndex()) {
            JOptionPane.showMessageDialog(this, "Source and destination accounts cannot be the same", "Transfer Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int amount = Integer.parseInt(amountField.getText());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Account sourceAccount = currentCustomer.Accounts.get(sourceAccountCombo.getSelectedIndex());
            Account destAccount = currentCustomer.Accounts.get(destinationAccountCombo.getSelectedIndex());
            
            if (amount > sourceAccount.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient funds", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Perform transfer
            sourceAccount.withdraw(amount);
            destAccount.deposit(amount);
            
            // Create transaction record
            Transaction transfer = new Transaction();
            transfer.TransactionID = java.util.UUID.randomUUID().toString();
            transfer.TimeStamp = new java.util.Date().toString();
            transfer.Type = "Transfer";
            transfer.amount = amount;
            transfer.sourceAccount = sourceAccount;
            transfer.destinationAccount = destAccount;
            transfer.Status = "Completed";
            sourceAccount.transactions.add(transfer);
            destAccount.transactions.add(transfer);
            
            // Save transaction to file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("transactions.txt", true))) {
                bw.write(transfer.TransactionID + "," + transfer.TimeStamp + "," + transfer.Type + "," + amount + "," + 
                         sourceAccount.AccountNumber + "," + destAccount.AccountNumber + "," + transfer.Status + "\n");
            } catch (IOException e) {
                System.out.println("Failed to write transaction to file.");
            }
            
            // Update account balances in accounts.txt
            File file = new File("accounts.txt");
            File tempFile = new File("accounts_temp.txt");
            
            try (Scanner scanner = new Scanner(file);
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    
                    if (parts.length >= 4) {
                        if (parts[0].equals(sourceAccount.AccountNumber)) {
                            // Update source account balance
                            parts[1] = String.valueOf(sourceAccount.getBalance());
                        } else if (parts[0].equals(destAccount.AccountNumber)) {
                            // Update destination account balance
                            parts[1] = String.valueOf(destAccount.getBalance());
                        }
                        
                        // Write updated line
                        StringBuilder newLine = new StringBuilder();
                        for (int i = 0; i < parts.length; i++) {
                            newLine.append(parts[i]);
                            if (i < parts.length - 1) {
                                newLine.append(",");
                            }
                        }
                        writer.write(newLine.toString());
                    } else {
                        writer.write(line);
                    }
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating account balances", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Replace the old file with the new one
            if (!file.delete()) {
                JOptionPane.showMessageDialog(this, "Error updating account balances", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!tempFile.renameTo(file)) {
                JOptionPane.showMessageDialog(this, "Error updating account balances", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JOptionPane.showMessageDialog(this, "Transfer successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            amountField.setText("");
            updateTransferCombos();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Transfer Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createNewAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Create New Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JLabel typeLabel = new JLabel("Account Type:");
        String[] accountTypes = {"Checking", "Saving"};
        JComboBox<String> typeCombo = new JComboBox<>(accountTypes);
        
        JLabel amountLabel = new JLabel("Initial Deposit:");
        JTextField depositField = new JTextField(10);
        
        formPanel.add(typeLabel);
        formPanel.add(typeCombo);
        formPanel.add(amountLabel);
        formPanel.add(depositField);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createButton = new JButton("Create Account");
        JButton backButton = new JButton("Back");
        
        createButton.addActionListener(e -> {
            try {
                int initialDeposit = Integer.parseInt(depositField.getText());
                String accountType = (String) typeCombo.getSelectedItem();
                
                if (accountType.equals("Saving") && initialDeposit < 100) {
                    JOptionPane.showMessageDialog(panel, "Savings accounts require a minimum initial deposit of $100", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (initialDeposit <= 0) {
                    JOptionPane.showMessageDialog(panel, "Initial deposit must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Account newAccount;
                if (accountType.equals("Checking")) {
                    newAccount = new checkingAccount();
                } else {
                    newAccount = new savingAccount();
                }
                
                newAccount.AccountNumber = java.util.UUID.randomUUID().toString();
                newAccount.Owner = currentCustomer;
                newAccount.deposit(initialDeposit);
                newAccount.status = "Active";
                
                if (currentCustomer.Accounts == null) {
                    currentCustomer.Accounts = new ArrayList<>();
                }
                currentCustomer.Accounts.add(newAccount);
                
                fileManager.SaveAccount(newAccount.AccountNumber, String.valueOf(newAccount.getBalance()), accountType, currentCustomer.UserName);
                
                JOptionPane.showMessageDialog(panel, "Account created successfully!\nAccount Number: " + newAccount.AccountNumber, "Success", JOptionPane.INFORMATION_MESSAGE);
                depositField.setText("");
                cardLayout.show(mainPanel, "Dashboard");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Please enter a valid deposit amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        backButton.addActionListener(e -> {
            depositField.setText("");
            cardLayout.show(mainPanel, "Dashboard");
        });
        
        buttonsPanel.add(createButton);
        buttonsPanel.add(backButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Update Profile", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        
        JLabel nameLabel = new JLabel("Full Name:");
        JTextField nameField = new JTextField(20);
        
        JLabel currentPasswordLabel = new JLabel("Current Password:");
        JPasswordField currentPasswordField = new JPasswordField(20);
        
        JLabel newPasswordLabel = new JLabel("New Password (leave blank to keep current):");
        JPasswordField newPasswordField = new JPasswordField(20);
        
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(currentPasswordLabel);
        formPanel.add(currentPasswordField);
        formPanel.add(newPasswordLabel);
        formPanel.add(newPasswordField);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateButton = new JButton("Update Profile");
        JButton backButton = new JButton("Back");
        
        updateButton.addActionListener(e -> {
            String email = emailField.getText();
            String name = nameField.getText();
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            
            if (!currentPassword.equals(currentCustomer.Password)) {
                JOptionPane.showMessageDialog(panel, "Current password is incorrect", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!email.isEmpty()) {
                currentCustomer.Email = email;
                fileManager.updateUserProfile(currentCustomer.UserID, "email", email);
            }
            
            if (!name.isEmpty()) {
                currentCustomer.Name = name;
                fileManager.updateUserProfile(currentCustomer.UserID, "name", name);
            }
            
            if (!newPassword.isEmpty()) {
                if (newPassword.length() < 6) {
                    JOptionPane.showMessageDialog(panel, "Password must be at least 6 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                currentCustomer.Password = newPassword;
                fileManager.resetPassword(currentCustomer.UserID, newPassword);
            }
            
            JOptionPane.showMessageDialog(panel, "Profile updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            emailField.setText("");
            nameField.setText("");
            currentPasswordField.setText("");
            newPasswordField.setText("");
            cardLayout.show(mainPanel, "Dashboard");
        });
        
        backButton.addActionListener(e -> {
            emailField.setText("");
            nameField.setText("");
            currentPasswordField.setText("");
            newPasswordField.setText("");
            cardLayout.show(mainPanel, "Dashboard");
        });
        
        buttonsPanel.add(updateButton);
        buttonsPanel.add(backButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createExternalTransferPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Transfer to Another Customer", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        
        JLabel sourceLabel = new JLabel("From Account:");
        externalSourceAccountCombo = new JComboBox<>();
        
        JLabel recipientUsernameLabel = new JLabel("Recipient Username:");
        recipientUsernameField = new JTextField(20);
        
        JLabel recipientAccountLabel = new JLabel("Recipient Account Number:");
        recipientAccountField = new JTextField(20);
        
        JLabel amountLabel = new JLabel("Amount:");
        externalAmountField = new JTextField(10);
        
        JButton searchButton = new JButton("Search Recipient Accounts");
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(searchButton);
        
        formPanel.add(sourceLabel);
        formPanel.add(externalSourceAccountCombo);
        formPanel.add(recipientUsernameLabel);
        formPanel.add(recipientUsernameField);
        formPanel.add(new JLabel("")); // Empty cell for alignment
        formPanel.add(searchPanel);
        formPanel.add(recipientAccountLabel);
        formPanel.add(recipientAccountField);
        formPanel.add(amountLabel);
        formPanel.add(externalAmountField);
        
        searchButton.addActionListener(e -> searchRecipientAccounts());
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton transferButton = new JButton("Transfer");
        JButton backButton = new JButton("Back");
        
        transferButton.addActionListener(e -> handleExternalTransfer());
        backButton.addActionListener(e -> {
            recipientUsernameField.setText("");
            recipientAccountField.setText("");
            externalAmountField.setText("");
            cardLayout.show(mainPanel, "Dashboard");
        });
        
        buttonsPanel.add(transferButton);
        buttonsPanel.add(backButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void searchRecipientAccounts() {
        String recipientUsername = recipientUsernameField.getText();
        
        if (recipientUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a recipient username to search", "Search Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ArrayList<String[]> recipientAccounts = findAllRecipientAccounts(recipientUsername);
        
        if (recipientAccounts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No accounts found for username: " + recipientUsername, "Search Result", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog to display the accounts
        JDialog dialog = new JDialog(this, "Select Recipient Account", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel dialogPanel = new JPanel(new BorderLayout());
        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table for accounts
        String[] columnNames = {"Account Number", "Type", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable accountsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        
        // Add accounts to table
        for (String[] account : recipientAccounts) {
            model.addRow(new Object[]{account[0], account[2], account.length >= 5 ? account[4] : "Active"});
        }
        
        dialogPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton selectButton = new JButton("Select");
        JButton cancelButton = new JButton("Cancel");
        
        selectButton.addActionListener(e -> {
            int selectedRow = accountsTable.getSelectedRow();
            if (selectedRow != -1) {
                String accountNumber = (String) model.getValueAt(selectedRow, 0);
                recipientAccountField.setText(accountNumber);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select an account", "Selection Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(selectButton);
        buttonPanel.add(cancelButton);
        
        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(dialogPanel);
        dialog.setVisible(true);
    }
    
    private ArrayList<String[]> findAllRecipientAccounts(String username) {
        ArrayList<String[]> accounts = new ArrayList<>();
        
        try {
            File file = new File("accounts.txt");
            if (!file.exists()) {
                return accounts;
            }
            
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    
                    if (parts.length >= 4 && parts[3].equals(username)) {
                        // Skip current user's accounts
                        if (currentCustomer != null && username.equals(currentCustomer.UserName)) {
                            continue;
                        }
                        accounts.add(parts);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return accounts;
    }
    
    private void updateExternalTransferSourceCombo() {
        externalSourceAccountCombo.removeAllItems();
        
        if (currentCustomer != null && currentCustomer.Accounts != null) {
            for (Account account : currentCustomer.Accounts) {
                String displayText = account.AccountNumber + " (Balance: $" + account.getBalance() + ")";
                externalSourceAccountCombo.addItem(displayText);
            }
        }
    }
    
    private void handleExternalTransfer() {
        if (externalSourceAccountCombo.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a source account", "Transfer Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String recipientUsername = recipientUsernameField.getText();
        String recipientAccountNumber = recipientAccountField.getText();
        
        if (recipientUsername.isEmpty() || recipientAccountNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter recipient username and account number", "Transfer Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int amount = Integer.parseInt(externalAmountField.getText());
            
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Account sourceAccount = currentCustomer.Accounts.get(externalSourceAccountCombo.getSelectedIndex());
            
            if (amount > sourceAccount.getBalance()) {
                JOptionPane.showMessageDialog(this, "Insufficient funds", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Verify recipient account exists
            Account recipientAccount = findRecipientAccount(recipientUsername, recipientAccountNumber);
            
            if (recipientAccount == null) {
                JOptionPane.showMessageDialog(this, "Recipient account not found", "Transfer Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Perform transfer
            sourceAccount.withdraw(amount);
            
            // Create transaction record
            Transaction transfer = new Transaction();
            transfer.TransactionID = java.util.UUID.randomUUID().toString();
            transfer.TimeStamp = new java.util.Date().toString();
            transfer.Type = "External Transfer";
            transfer.amount = amount;
            transfer.sourceAccount = sourceAccount;
            transfer.Status = "Completed";
            sourceAccount.transactions.add(transfer);
            
            // Save transaction to file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("transactions.txt", true))) {
                bw.write(transfer.TransactionID + "," + transfer.TimeStamp + "," + transfer.Type + "," + amount + "," + 
                         sourceAccount.AccountNumber + "," + recipientAccountNumber + "," + transfer.Status + "\n");
            } catch (IOException e) {
                System.out.println("Failed to write transaction to file.");
            }
            
            // Update recipient account balance in accounts.txt
            updateRecipientAccountBalance(recipientAccountNumber, amount);
            
            JOptionPane.showMessageDialog(this, "Transfer successful", "Success", JOptionPane.INFORMATION_MESSAGE);
            recipientUsernameField.setText("");
            recipientAccountField.setText("");
            externalAmountField.setText("");
            updateExternalTransferSourceCombo();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Transfer Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Account findRecipientAccount(String username, String accountNumber) {
        try {
            File file = new File("accounts.txt");
            if (!file.exists()) {
                return null;
            }
            
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    
                    if (parts.length >= 4 && parts[0].equals(accountNumber) && parts[3].equals(username)) {
                        // Found matching account
                        Account account;
                        if ("Checking".equalsIgnoreCase(parts[2])) {
                            account = new checkingAccount();
                        } else {
                            account = new savingAccount();
                        }
                        account.AccountNumber = parts[0];
                        account.Balance = Integer.parseInt(parts[1]);
                        if (parts.length >= 5) {
                            account.status = parts[4];
                        }
                        return account;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void updateRecipientAccountBalance(String accountNumber, int amount) {
        File file = new File("accounts.txt");
        File tempFile = new File("accounts_temp.txt");
        
        try {
            if (!file.exists()) {
                return;
            }
            
            try (Scanner scanner = new Scanner(file);
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    
                    if (parts.length >= 4 && parts[0].equals(accountNumber)) {
                        // Update balance
                        int currentBalance = Integer.parseInt(parts[1]);
                        int newBalance = currentBalance + amount;
                        parts[1] = String.valueOf(newBalance);
                        
                        // Write updated line
                        StringBuilder newLine = new StringBuilder();
                        for (int i = 0; i < parts.length; i++) {
                            newLine.append(parts[i]);
                            if (i < parts.length - 1) {
                                newLine.append(",");
                            }
                        }
                        writer.write(newLine.toString());
                    } else {
                        writer.write(line);
                    }
                    writer.newLine();
                }
            }
            
            // Replace the old file with the new one
            file.delete();
            tempFile.renameTo(file);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private JPanel createTransactionHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Transaction History", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel accountLabel = new JLabel("Filter by Account: ");
        JComboBox<String> accountFilterCombo = new JComboBox<>();
        accountFilterCombo.addItem("All Accounts");
        
        if (currentCustomer != null && currentCustomer.Accounts != null) {
            for (Account account : currentCustomer.Accounts) {
                accountFilterCombo.addItem(account.AccountNumber);
            }
        }
        
        JButton filterButton = new JButton("Apply Filter");
        filterButton.addActionListener(e -> {
            String selectedAccount = accountFilterCombo.getSelectedItem().toString();
            updateTransactionHistoryTable(selectedAccount.equals("All Accounts") ? null : selectedAccount);
        });
        
        filterPanel.add(accountLabel);
        filterPanel.add(accountFilterCombo);
        filterPanel.add(filterButton);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Table for transactions
        String[] columnNames = {"Date", "Type", "Amount", "From Account", "To Account", "Status"};
        transactionTableModel = new DefaultTableModel(columnNames, 0);
        transactionTable = new JTable(transactionTableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton backButton = new JButton("Back to Dashboard");
        
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        
        buttonsPanel.add(backButton);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateTransactionHistoryTable() {
        updateTransactionHistoryTable(null);
    }
    
    private void updateTransactionHistoryTable(String accountFilter) {
        transactionTableModel.setRowCount(0);
        
        if (currentCustomer == null || currentCustomer.Accounts == null) {
            return;
        }
        
        // First add transactions from memory (current session)
        for (Account account : currentCustomer.Accounts) {
            if (accountFilter != null && !account.AccountNumber.equals(accountFilter)) {
                continue;
            }
            
            for (Transaction transaction : account.transactions) {
                String fromAccount = transaction.sourceAccount != null ? transaction.sourceAccount.AccountNumber : "N/A";
                String toAccount = transaction.destinationAccount != null ? transaction.destinationAccount.AccountNumber : "N/A";
                
                Object[] row = {
                    transaction.TimeStamp,
                    transaction.Type,
                    transaction.amount,
                    fromAccount,
                    toAccount,
                    transaction.Status
                };
                transactionTableModel.addRow(row);
            }
        }
        
        // Then add transactions from file that might not be in memory
        try {
            File file = new File("transactions.txt");
            if (!file.exists()) {
                return;
            }
            
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    
                    if (parts.length >= 7) {
                        String fromAccount = parts[4];
                        String toAccount = parts[5];
                        
                        // Check if this transaction involves the current customer's accounts
                        boolean isCustomerTransaction = false;
                        for (Account account : currentCustomer.Accounts) {
                            if (account.AccountNumber.equals(fromAccount) || account.AccountNumber.equals(toAccount)) {
                                isCustomerTransaction = true;
                                break;
                            }
                        }
                        
                        // Apply account filter if specified
                        if (accountFilter != null && !fromAccount.equals(accountFilter) && !toAccount.equals(accountFilter)) {
                            continue;
                        }
                        
                        if (isCustomerTransaction) {
                            Object[] row = {
                                parts[1], // Timestamp
                                parts[2], // Type
                                parts[3], // Amount
                                parts[4], // From Account
                                parts[5], // To Account
                                parts[6]  // Status
                            };
                            
                            // Check if this transaction is already in the table (to avoid duplicates)
                            boolean isDuplicate = false;
                            for (int i = 0; i < transactionTableModel.getRowCount(); i++) {
                                if (transactionTableModel.getValueAt(i, 0).equals(parts[1]) && 
                                    transactionTableModel.getValueAt(i, 3).equals(parts[4]) && 
                                    transactionTableModel.getValueAt(i, 4).equals(parts[5])) {
                                    isDuplicate = true;
                                    break;
                                }
                            }
                            
                            if (!isDuplicate) {
                                transactionTableModel.addRow(row);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showEmployeePanel(employee emp) {
        // Create a new JFrame for the employee panel
        JFrame empFrame = new JFrame("Employee Panel");
        empFrame.setSize(800, 600);
        empFrame.setLocationRelativeTo(this);
        empFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Create tabbed pane for different employee functions
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add tabs for different employee functions
        tabbedPane.addTab("View Customer Accounts", createViewCustomerAccountsPanel(emp));
        tabbedPane.addTab("Search Customers", createSearchCustomersPanel(emp));
        tabbedPane.addTab("Generate Reports", createGenerateReportsPanel(emp));
        
        // Add logout button at the bottom
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            empFrame.dispose();
            this.setVisible(true);
            cardLayout.show(this.mainPanel, "Welcome");
        });
        bottomPanel.add(logoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Set up the frame
        empFrame.add(mainPanel);
        empFrame.setVisible(true);
        this.setVisible(false);
    }
    
    private JPanel createViewCustomerAccountsPanel(employee emp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel at the top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel usernameLabel = new JLabel("Customer Username: ");
        JTextField usernameField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        
        searchPanel.add(usernameLabel);
        searchPanel.add(usernameField);
        searchPanel.add(searchButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table for accounts
        String[] columnNames = {"Account Number", "Type", "Balance", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable accountsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action panel at the bottom
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewTransactionsButton = new JButton("View Transactions");
        actionPanel.add(viewTransactionsButton);
        panel.add(actionPanel, BorderLayout.SOUTH);
        
        // Search button action
        searchButton.addActionListener(e -> {
            String username = usernameField.getText();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a customer username", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            model.setRowCount(0);
            
            try {
                File file = new File("accounts.txt");
                if (!file.exists()) {
                    JOptionPane.showMessageDialog(panel, "No accounts found", "Information", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                boolean found = false;
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.trim().isEmpty()) continue;
                        
                        String[] parts = line.split(",");
                        if (parts.length >= 4 && parts[3].equals(username)) {
                            String accountId = parts[0];
                            String balance = parts[1];
                            String type = parts[2];
                            String status = parts.length > 4 ? parts[4] : "Active";
                            
                            model.addRow(new Object[]{accountId, type, balance, status});
                            found = true;
                        }
                    }
                }
                
                if (!found) {
                    JOptionPane.showMessageDialog(panel, "No accounts found for user: " + username, "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error searching for accounts: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // View transactions button action
        viewTransactionsButton.addActionListener(e -> {
            int selectedRow = accountsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Please select an account to view transactions", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String accountId = (String) model.getValueAt(selectedRow, 0);
            showTransactionsDialog(accountId);
        });
        
        return panel;
    }
    
    private void showTransactionsDialog(String accountId) {
        JDialog dialog = new JDialog(this, "Account Transactions", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table for transactions
        String[] columnNames = {"Date", "Type", "Amount", "From Account", "To Account", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable transactionsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load transactions for the account
        try {
            File file = new File("transactions.txt");
            if (file.exists()) {
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.trim().isEmpty()) continue;
                        
                        String[] parts = line.split(",");
                        if (parts.length >= 7 && (parts[4].equals(accountId) || parts[5].equals(accountId))) {
                            model.addRow(new Object[]{
                                parts[1], // Date
                                parts[2], // Type
                                parts[3], // Amount
                                parts[4], // From Account
                                parts[5], // To Account
                                parts[6]  // Status
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
    private JPanel createSearchCustomersPanel(employee emp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Search panel at the top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search: ");
        JTextField searchField = new JTextField(20);
        JComboBox<String> searchTypeCombo = new JComboBox<>(new String[]{"Username", "Name"});
        JButton searchButton = new JButton("Search");
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Search by: "));
        searchPanel.add(searchTypeCombo);
        searchPanel.add(searchButton);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table for customers
        String[] columnNames = {"Username", "Name", "Email", "Number of Accounts"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable customersTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(customersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Search button action
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText();
            if (searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Please enter a search term", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int searchType = searchTypeCombo.getSelectedIndex();
            model.setRowCount(0);
            
            try {
                // First, get all users
                Map<String, String[]> users = new HashMap<>();
                File usersFile = new File("users.txt");
                if (usersFile.exists()) {
                    try (Scanner scanner = new Scanner(usersFile)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line.trim().isEmpty()) continue;
                            
                            String[] parts = line.split(",");
                            if (parts.length >= 3 && parts[2].equals("Customer")) {
                                users.put(parts[0], parts);
                            }
                        }
                    }
                }
                
                // Then, count accounts for each user
                Map<String, Integer> accountCounts = new HashMap<>();
                File accountsFile = new File("accounts.txt");
                if (accountsFile.exists()) {
                    try (Scanner scanner = new Scanner(accountsFile)) {
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line.trim().isEmpty()) continue;
                            
                            String[] parts = line.split(",");
                            if (parts.length >= 4) {
                                String owner = parts[3];
                                accountCounts.put(owner, accountCounts.getOrDefault(owner, 0) + 1);
                            }
                        }
                    }
                }
                
                // Now search and populate the table
                boolean found = false;
                for (Map.Entry<String, String[]> entry : users.entrySet()) {
                    String username = entry.getKey();
                    String[] userData = entry.getValue();
                    
                    boolean matches = false;
                    if (searchType == 0) { // Username
                        matches = username.toLowerCase().contains(searchTerm.toLowerCase());
                    } else { // Name
                        String name = userData.length > 4 ? userData[4] : "";
                        matches = name.toLowerCase().contains(searchTerm.toLowerCase());
                    }
                    
                    if (matches) {
                        String name = userData.length > 4 ? userData[4] : "";
                        String email = userData.length > 3 ? userData[3] : "";
                        int accountCount = accountCounts.getOrDefault(username, 0);
                        
                        model.addRow(new Object[]{username, name, email, accountCount});
                        found = true;
                    }
                }
                
                if (!found) {
                    JOptionPane.showMessageDialog(panel, "No customers found matching your search", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error searching for customers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }
    
    private JPanel createGenerateReportsPanel(employee emp) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Generate Reports", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Report options panel
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        
        JButton accountSummaryButton = new JButton("Generate Account Summary Report");
        JButton transactionActivityButton = new JButton("Generate Transaction Activity Report");
        
        optionsPanel.add(accountSummaryButton);
        optionsPanel.add(transactionActivityButton);
        
        panel.add(optionsPanel, BorderLayout.CENTER);
        
        // Account Summary Report action
        accountSummaryButton.addActionListener(e -> {
            try {
                File reportFile = new File("account_summary_report.txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
                    writer.write("ACCOUNT SUMMARY REPORT - " + new java.util.Date().toString() + "\n\n");
                    
                    int totalAccounts = 0;
                    int totalCheckingAccounts = 0;
                    int totalSavingAccounts = 0;
                    int totalBalance = 0;
                    
                    File accountsFile = new File("accounts.txt");
                    if (accountsFile.exists()) {
                        try (Scanner scanner = new Scanner(accountsFile)) {
                            while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                if (line.trim().isEmpty()) continue;
                                
                                String[] parts = line.split(",");
                                if (parts.length >= 3) {
                                    totalAccounts++;
                                    
                                    if ("Checking".equalsIgnoreCase(parts[2])) {
                                        totalCheckingAccounts++;
                                    } else if ("Saving".equalsIgnoreCase(parts[2])) {
                                        totalSavingAccounts++;
                                    }
                                    
                                    try {
                                        totalBalance += Integer.parseInt(parts[1]);
                                    } catch (NumberFormatException ex) {
                                    }
                                }
                            }
                        }
                    }
                    
                    writer.write("Total Accounts: " + totalAccounts + "\n");
                    writer.write("Checking Accounts: " + totalCheckingAccounts + "\n");
                    writer.write("Saving Accounts: " + totalSavingAccounts + "\n");
                    writer.write("Total Balance Across All Accounts: $" + totalBalance + "\n");
                }
                
                JOptionPane.showMessageDialog(panel, "Account Summary Report generated: account_summary_report.txt");
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error generating report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Transaction Activity Report action
        transactionActivityButton.addActionListener(e -> {
            try {
                File reportFile = new File("transaction_activity_report.txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
                    writer.write("TRANSACTION ACTIVITY REPORT - " + new java.util.Date().toString() + "\n\n");
                    
                    int totalTransactions = 0;
                    int totalDeposits = 0;
                    int totalWithdrawals = 0;
                    int totalTransfers = 0;
                    int totalDepositAmount = 0;
                    int totalWithdrawalAmount = 0;
                    int totalTransferAmount = 0;
                    
                    File transactionsFile = new File("transactions.txt");
                    if (transactionsFile.exists()) {
                        try (Scanner scanner = new Scanner(transactionsFile)) {
                            while (scanner.hasNextLine()) {
                                String line = scanner.nextLine();
                                if (line.trim().isEmpty()) continue;
                                
                                String[] parts = line.split(",");
                                if (parts.length >= 7) {
                                    totalTransactions++;
                                    
                                    String type = parts[2];
                                    int amount = 0;
                                    try {
                                        amount = Integer.parseInt(parts[3]);
                                    } catch (NumberFormatException ex) {
                                        continue;
                                    }
                                    
                                    switch (type) {
                                        case "Deposit":
                                            totalDeposits++;
                                            totalDepositAmount += amount;
                                            break;
                                        case "Withdrawal":
                                            totalWithdrawals++;
                                            totalWithdrawalAmount += amount;
                                            break;
                                        case "Transfer":
                                        case "External Transfer":
                                            totalTransfers++;
                                            totalTransferAmount += amount;
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    
                    writer.write("Total Transactions: " + totalTransactions + "\n");
                    writer.write("Deposits: " + totalDeposits + " ($" + totalDepositAmount + ")\n");
                    writer.write("Withdrawals: " + totalWithdrawals + " ($" + totalWithdrawalAmount + ")\n");
                    writer.write("Transfers: " + totalTransfers + " ($" + totalTransferAmount + ")\n");
                }
                
                JOptionPane.showMessageDialog(panel, "Transaction Activity Report generated: transaction_activity_report.txt");
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error generating report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        return panel;
    }
    
    private void showAdminPanel(Admin admin) {
        // Create a new JFrame for the admin panel
        JFrame adminFrame = new JFrame("Admin Panel");
        adminFrame.setSize(800, 600);
        adminFrame.setLocationRelativeTo(this);
        adminFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Create tabbed pane for different admin functions
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add tabs for different admin functions
        tabbedPane.addTab("User Management", createUserManagementPanel(admin));
        tabbedPane.addTab("Account Management", createAccountManagementPanel(admin));
        tabbedPane.addTab("System Activity", createSystemActivityPanel(admin));
        tabbedPane.addTab("Create Employee", createEmployeeCreationPanel(admin));
        
        // Add logout button at the bottom
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            adminFrame.dispose();
            this.setVisible(true);
            cardLayout.show(this.mainPanel, "Welcome");
        });
        bottomPanel.add(logoutButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Set up the frame
        adminFrame.add(mainPanel);
        adminFrame.setVisible(true);
        this.setVisible(false);
    }
    
    private JPanel createUserManagementPanel(Admin admin) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table for users
        String[] columnNames = {"User ID", "Username", "Role", "Email", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable usersTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(usersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load users into table
        loadUsersIntoTable(model);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton disableButton = new JButton("Disable User");
        JButton enableButton = new JButton("Enable User");
        JButton refreshButton = new JButton("Refresh");
        
        disableButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                String userId = (String) model.getValueAt(selectedRow, 0);
                // Implement user disabling logic
                JOptionPane.showMessageDialog(panel, "User " + userId + " has been disabled.");
                loadUsersIntoTable(model);
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a user to disable.");
            }
        });
        
        enableButton.addActionListener(e -> {
            int selectedRow = usersTable.getSelectedRow();
            if (selectedRow != -1) {
                String userId = (String) model.getValueAt(selectedRow, 0);
                // Implement user enabling logic
                JOptionPane.showMessageDialog(panel, "User " + userId + " has been enabled.");
                loadUsersIntoTable(model);
            } else {
                JOptionPane.showMessageDialog(panel, "Please select a user to enable.");
            }
        });
        
        refreshButton.addActionListener(e -> loadUsersIntoTable(model));
        
        controlPanel.add(disableButton);
        controlPanel.add(enableButton);
        controlPanel.add(refreshButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadUsersIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        
        try {
            File file = new File("users.txt");
            if (!file.exists()) {
                return;
            }
            
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String userId = parts[0];
                        String password = parts[1];
                        String role = parts[2];
                        String email = parts.length > 3 ? parts[3] : "";
                        String name = parts.length > 4 ? parts[4] : "";
                        String status = "Active"; // Default status
                        
                        model.addRow(new Object[]{userId, userId, role, email, status});
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadAccountsIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        
        try {
            File file = new File("accounts.txt");
            if (!file.exists()) {
                return;
            }
            
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String accountId = parts[0];
                        String balance = parts[1];
                        String type = parts[2];
                        String owner = parts[3];
                        String status = parts.length > 4 ? parts[4] : "Active";
                        
                        model.addRow(new Object[]{accountId, owner, type, balance, status});
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private JPanel createSystemActivityPanel(Admin admin) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table for transactions
        String[] columnNames = {"Transaction ID", "Date", "Type", "Amount", "From", "To", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable transactionsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load transactions into table
        loadTransactionsIntoTable(model);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Refresh");
        JButton generateReportButton = new JButton("Generate Report");
        
        refreshButton.addActionListener(e -> loadTransactionsIntoTable(model));
        
        generateReportButton.addActionListener(e -> {
            try {
                File reportFile = new File("admin_report.txt");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(reportFile))) {
                    writer.write("ADMIN REPORT - " + new java.util.Date().toString() + "\n\n");
                    writer.write("TRANSACTION SUMMARY:\n");
                    
                    int totalTransactions = model.getRowCount();
                    int totalDeposits = 0;
                    int totalWithdrawals = 0;
                    int totalTransfers = 0;
                    double totalAmount = 0;
                    
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String type = (String) model.getValueAt(i, 2);
                        double amount = Double.parseDouble(model.getValueAt(i, 3).toString());
                        totalAmount += amount;
                        
                        if (type.equals("Deposit")) totalDeposits++;
                        else if (type.equals("Withdrawal")) totalWithdrawals++;
                        else if (type.equals("Transfer") || type.equals("External Transfer")) totalTransfers++;
                    }
                    
                    writer.write("Total Transactions: " + totalTransactions + "\n");
                    writer.write("Total Deposits: " + totalDeposits + "\n");
                    writer.write("Total Withdrawals: " + totalWithdrawals + "\n");
                    writer.write("Total Transfers: " + totalTransfers + "\n");
                    writer.write("Total Amount: $" + totalAmount + "\n");
                }
                
                JOptionPane.showMessageDialog(panel, "Report generated successfully: admin_report.txt");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(panel, "Error generating report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        controlPanel.add(refreshButton);
        controlPanel.add(generateReportButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadTransactionsIntoTable(DefaultTableModel model) {
        model.setRowCount(0);
        
        try {
            File file = new File("transactions.txt");
            if (!file.exists()) {
                return;
            }
            
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 7) {
                        model.addRow(new Object[]{
                            parts[0], // Transaction ID
                            parts[1], // Date
                            parts[2], // Type
                            parts[3], // Amount
                            parts[4], // From
                            parts[5], // To
                            parts[6]  // Status
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private JPanel createEmployeeCreationPanel(Admin admin) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Create Employee Account", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));
        
        JLabel employeeIdLabel = new JLabel("Employee ID:");
        JTextField employeeIdField = new JTextField(20);
        
        JLabel employeeNameLabel = new JLabel("Employee Name:");
        JTextField employeeNameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        
        JLabel positionLabel = new JLabel("Position:");
        JTextField positionField = new JTextField(20);
        
        formPanel.add(employeeIdLabel);
        formPanel.add(employeeIdField);
        formPanel.add(employeeNameLabel);
        formPanel.add(employeeNameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(positionLabel);
        formPanel.add(positionField);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton createButton = new JButton("Create Employee");
        JButton clearButton = new JButton("Clear");
        
        createButton.addActionListener(e -> {
            String empId = employeeIdField.getText();
            String empName = employeeNameField.getText();
            String password = new String(passwordField.getPassword());
            String position = positionField.getText();
            
            if (empId.isEmpty() || empName.isEmpty() || password.isEmpty() || position.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (password.length() < 6) {
                JOptionPane.showMessageDialog(panel, "Password must be at least 6 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create employee account
            fileManager.SaveUser(empId, password, "Employee");
            
            JOptionPane.showMessageDialog(panel, "Employee account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields
            employeeIdField.setText("");
            employeeNameField.setText("");
            passwordField.setText("");
            positionField.setText("");
        });
        
        clearButton.addActionListener(e -> {
            employeeIdField.setText("");
            employeeNameField.setText("");
            passwordField.setText("");
            positionField.setText("");
        });
        
        buttonsPanel.add(createButton);
        buttonsPanel.add(clearButton);
        
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createAccountManagementPanel(Admin admin) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create table for accounts
        String[] columnNames = {"Account Number", "Owner", "Type", "Balance", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable accountsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(accountsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load accounts into table
        loadAccountsIntoTable(model);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton activateButton = new JButton("Activate Account");
        JButton freezeButton = new JButton("Freeze Account");
        JButton disableButton = new JButton("Disable Account");
        JButton refreshButton = new JButton("Refresh");
        
        activateButton.addActionListener(e -> {
            int selectedRow = accountsTable.getSelectedRow();
            if (selectedRow != -1) {
                String accountId = (String) model.getValueAt(selectedRow, 0);
                changeAccountStatus(accountId, "Active");
                model.setValueAt("Active", selectedRow, 4);
                JOptionPane.showMessageDialog(panel, "Account " + accountId + " has been activated.");
            } else {
                JOptionPane.showMessageDialog(panel, "Please select an account to activate.");
            }
        });
        
        freezeButton.addActionListener(e -> {
            int selectedRow = accountsTable.getSelectedRow();
            if (selectedRow != -1) {
                String accountId = (String) model.getValueAt(selectedRow, 0);
                changeAccountStatus(accountId, "Frozen");
                model.setValueAt("Frozen", selectedRow, 4);
                JOptionPane.showMessageDialog(panel, "Account " + accountId + " has been frozen.");
            } else {
                JOptionPane.showMessageDialog(panel, "Please select an account to freeze.");
            }
        });
        
        disableButton.addActionListener(e -> {
            int selectedRow = accountsTable.getSelectedRow();
            if (selectedRow != -1) {
                String accountId = (String) model.getValueAt(selectedRow, 0);
                changeAccountStatus(accountId, "Disabled");
                model.setValueAt("Disabled", selectedRow, 4);
                JOptionPane.showMessageDialog(panel, "Account " + accountId + " has been disabled.");
            } else {
                JOptionPane.showMessageDialog(panel, "Please select an account to disable.");
            }
        });
        
        refreshButton.addActionListener(e -> loadAccountsIntoTable(model));
        
        controlPanel.add(activateButton);
        controlPanel.add(freezeButton);
        controlPanel.add(disableButton);
        controlPanel.add(refreshButton);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void changeAccountStatus(String accountId, String newStatus) {
        File file = new File("accounts.txt");
        File tempFile = new File("accounts_temp.txt");
        
        try {
            if (!file.exists()) {
                return;
            }
            
            try (Scanner scanner = new Scanner(file);
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    
                    if (parts.length >= 4 && parts[0].equals(accountId)) {
                        // Update status
                        if (parts.length >= 5) {
                            parts[4] = newStatus;
                        } else {
                            String newLine = line + "," + newStatus;
                            writer.write(newLine);
                        }
                        
                        // Write updated line
                        StringBuilder newLine = new StringBuilder();
                        for (int i = 0; i < parts.length; i++) {
                            newLine.append(parts[i]);
                            if (i < parts.length - 1) {
                                newLine.append(",");
                            }
                        }
                        writer.write(newLine.toString());
                    } else {
                        writer.write(line);
                    }
                    writer.newLine();
                }
            }
            
            // Replace the old file with the new one
            file.delete();
            tempFile.renameTo(file);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankingGUI gui = new BankingGUI();
            gui.setVisible(true);});}
} 