import java.io.*;
import java.util.*;
 
public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        try {
            System.out.println("Welcome to the Banking System!");
            System.out.println("1. Launch Text-Based Interface");
            System.out.println("2. Launch Graphical Interface (GUI)");
            System.out.println("3. Exit");
            System.out.print("Choose an option (1-3): ");
            
            int interfaceChoice;
            try {
                interfaceChoice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                scanner.nextLine(); // Consume invalid input
                System.out.println("Invalid input. Defaulting to text-based interface.");
                interfaceChoice = 1;
            }
            
            if (interfaceChoice == 2) {
                // Launch GUI
                System.out.println("Launching GUI interface...");
                System.out.println("New Feature: You can now transfer funds to other customers' accounts!");
                System.out.println("- Use the 'Transfer to Another Customer' option in the dashboard");
                System.out.println("- Search for recipient accounts by username");
                System.out.println("- View all transactions in the Transaction History panel");
                javax.swing.SwingUtilities.invokeLater(() -> {
                    BankingGUI gui = new BankingGUI();
                    gui.setVisible(true);
                });
                return;
            } else if (interfaceChoice == 3) {
                System.out.println("Thank you for using our banking system. Goodbye!");
                System.exit(0);
            } else {
                // Default to text-based interface
                launchTextBasedInterface();
            }
        } finally {
            scanner.close();
        }
    }
    
    private static void launchTextBasedInterface() throws FileNotFoundException {
        Scanner scanner = new Scanner(System.in);
        try {
            FileManager fileManager = new FileManager();
            Customer c1 = new Customer();
            Bank b1 = new Bank();
            
            while (true) {
                welcome();
                int choice;
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                } catch (InputMismatchException e) {
                    scanner.nextLine(); // Consume invalid input
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }
                
                if (choice == 1) {
                    if (c1.login(fileManager)) {
                        String role = fileManager.LoadUsers(c1.UserName, c1.Password);
                        // Reload accounts after successful login to ensure proper association
                        fileManager.LoadAccounts(c1);
                        switch (role) {
                            case "Admin":
                                Admin admin = new Admin();
                                admin.AdminPanel(fileManager);
                                break;
                            case "Employee":
                                while (true) {
                                    System.out.println("\nEmployee Panel:");
                                    System.out.println("1. View Customer Accounts");
                                    System.out.println("2. Search Customer");
                                    System.out.println("3. Generate Reports");
                                    System.out.println("4. Logout");
                                    System.out.print("Choose an option (1-4): ");
                                    int empChoice = scanner.nextInt();
                                    scanner.nextLine();
                                    
                                    employee emp = new employee();
                                    
                                    switch (empChoice) {
                                        case 1:
                                            emp.viewCustomerAccounts();
                                            break;
                                        case 2:
                                            emp.searchCustomer();
                                            break;
                                        case 3:
                                            emp.generateReport();
                                            break;
                                        case 4:
                                            System.out.println("Logging out...");
                                            return;
                                        default:
                                            System.out.println("Invalid option. Try again.");
                                    }
                                }
                            case "Customer":
                                MainPanel(c1, fileManager);
                                break;
                            default:
                                System.out.println("Unknown role.");
                        }
                    }
                } else if (choice == 2) {
                    Customer newCustomer = b1.createUser(fileManager);
                    if (newCustomer != null) {
                        MainPanel(newCustomer, fileManager);
                    }
                } else if (choice == 3) {
                    System.out.println("Thank you for using our banking system. Goodbye!");
                    System.exit(0);
                } else {
                    System.out.println("Invalid option. Please enter 1, 2, or 3.");
                }
            }
        } finally {
            scanner.close();
        }
    }
    public static void welcome() {
        System.out.println("Welcome! Choose:");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Your choice (1-3): ");
    }
    public static void MainPanel(Customer c1, FileManager fileManager) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nMain Panel:");
            System.out.println("1. View Accounts");
            System.out.println("2. Transfer Funds");
            System.out.println("3. Create Account");
            System.out.println("4. Update Profile");
            System.out.println("5. Generate Monthly Statement");
            System.out.println("6. Logout");
            System.out.print("Choose an option (1-6): ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    c1.viewAccount();
                    break;
                case 2:
                    c1.TransferFunds();
                    break;
                case 3:
                    System.out.println("Select account type:");
                    System.out.println("1. Checking Account");
                    System.out.println("2. Saving Account");
                    System.out.println("3. Go Back");
                    System.out.print("Choose an option (1-3): ");
                    int accType = scanner.nextInt();
                    scanner.nextLine();
                    
                    if (accType == 3) {
                        System.out.println("Returning to main menu...");
                        break;
                    }
                    
                    if (accType != 1 && accType != 2) {
                        System.out.println("Invalid account type. Please try again.");
                        break;
                    }
                    
                    Account newAccount;
                    String accTypeStr;

                    if (accType == 1) {
                        newAccount = new checkingAccount();
                        accTypeStr = "Checking";
                    } else {
                        newAccount = new savingAccount();  // savings account will have limits
                        accTypeStr = "Saving";
                    }

                    System.out.print("Enter initial deposit amount (minimum $1, for savings minimum $100): $");
                    int initialDeposit = scanner.nextInt();
                    scanner.nextLine();
                    if (accType == 2 && initialDeposit < 100) {
                        System.out.println("Savings accounts require a minimum initial deposit of $100.");
                        return;
                    }

                    newAccount.AccountNumber = UUID.randomUUID().toString();
                    newAccount.Owner = c1;
                    newAccount.deposit(initialDeposit);
                    newAccount.status = "Active";

                    if (c1.Accounts == null) c1.Accounts = new ArrayList<>();
                    c1.Accounts.add(newAccount);
                    
                    // Ensure username is set before saving the account
                    if (c1.UserName == null || c1.UserName.trim().isEmpty()) {
                        System.out.println("Error: Cannot create account without being logged in.");
                        return;
                    }
                    
                    fileManager.SaveAccount(newAccount.AccountNumber, String.valueOf(newAccount.getBalance()), accTypeStr, c1.UserName);
                    System.out.println("Account created successfully! Account Number: " + newAccount.AccountNumber);
                    break;
                case 4:
                    c1.updateProfile(fileManager);
                    break;
                case 5:
                    c1.generateMonthlyStatement();
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
            }
        }
    }
}

abstract class User {
    String UserID;
    String UserName;
    String Password;
    String Email;
    String Name;

    boolean login(FileManager fileManager){
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\nLogin Options:");
            System.out.println("1. Enter credentials");
            System.out.println("2. Go back to main menu");
            System.out.print("Choose an option (1-2): ");
            
            int loginChoice;
            try {
                loginChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                scanner.nextLine(); // Consume invalid input
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
            
            if (loginChoice == 2) {
                return false;
            }
            
            if (loginChoice != 1) {
                System.out.println("Invalid option. Please try again.");
                continue;
            }
            
            String user;
            do {
                System.out.print("Enter username (or type 'back' to return): ");
                user = scanner.nextLine();
                if (user.equalsIgnoreCase("back")) {
                    return false;
                }
            } while (user.trim().isEmpty());

            String pass;
            do {
                System.out.print("Enter password (minimum 6 characters, or type 'back' to return): ");
                pass = scanner.nextLine();
                if (pass.equalsIgnoreCase("back")) {
                    return false;
                }
                if (pass.length() < 6) {
                    System.out.println("Password must be at least 6 characters long.");
                    pass = "";
                }
            } while (pass.trim().isEmpty());

            try {
                String role = fileManager.LoadUsers(user, pass);
                if (role != null) {
                    System.out.println("Login success! Role: " + role);
                    this.UserName = user;
                    this.Password = pass;
                    return true;
                } else {
                    System.out.println("Invalid username or password.");
                    System.out.println("1. Try again");
                    System.out.println("2. Return to main menu");
                    System.out.print("Choose an option (1-2): ");
                    
                    int retryChoice;
                    try {
                        retryChoice = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                    } catch (InputMismatchException e) {
                        scanner.nextLine(); // Consume invalid input
                        System.out.println("Invalid input. Returning to login options.");
                        continue;
                    }
                    
                    if (retryChoice == 2) {
                        return false;
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("User file not found.");
                return false;
            }
        }
    }

    void updateProfile(FileManager fileManager) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\nUpdate Profile:");
        System.out.println("1. Continue");
        System.out.println("2. Return to Main Menu");
        System.out.print("Choose an option (1-2): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice != 1) {
            System.out.println("Returning to Main Menu...");
            return;
        }
        
        System.out.println("\nCurrent Profile Information:");
        System.out.println("User ID: " + UserID);
        System.out.println("Username: " + UserName);
        System.out.println("Email: " + (Email != null ? Email : "Not set"));
        System.out.println("Name: " + (Name != null ? Name : "Not set"));
        
        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Email");
        System.out.println("2. Name");
        System.out.println("3. Password");
        System.out.println("4. Cancel");
        System.out.print("Choose an option (1-4): ");
        
        int updateChoice = scanner.nextInt();
        scanner.nextLine();
        
        switch (updateChoice) {
            case 1:
                System.out.print("Enter new email (or type 'cancel' to return): ");
                String newEmail = scanner.nextLine();
                if (newEmail.equalsIgnoreCase("cancel")) {
                    System.out.println("Operation canceled.");
                    return;
                }
                Email = newEmail;
                fileManager.updateUserProfile(UserID, "email", newEmail);
                System.out.println("Email updated successfully.");
                break;
                
            case 2:
                System.out.print("Enter new name (or type 'cancel' to return): ");
                String newName = scanner.nextLine();
                if (newName.equalsIgnoreCase("cancel")) {
                    System.out.println("Operation canceled.");
                    return;
                }
                Name = newName;
                fileManager.updateUserProfile(UserID, "name", newName);
                System.out.println("Name updated successfully.");
                break;
                
            case 3:
                System.out.print("Enter current password: ");
                String currentPassword = scanner.nextLine();
                if (!currentPassword.equals(Password)) {
                    System.out.println("Incorrect password. Update canceled.");
                    return;
                }
                
                String newPassword;
                do {
                    System.out.print("Enter new password (minimum 6 characters, or type 'cancel' to return): ");
                    newPassword = scanner.nextLine();
                    if (newPassword.equalsIgnoreCase("cancel")) {
                        System.out.println("Operation canceled.");
                        return;
                    }
                    if (newPassword.length() < 6) {
                        System.out.println("Password must be at least 6 characters long.");
                        newPassword = "";
                    }
                } while (newPassword.trim().isEmpty());
                
                fileManager.resetPassword(UserID, newPassword);
                Password = newPassword;
                System.out.println("Password updated successfully.");
                break;
                
            case 4:
                System.out.println("Update canceled.");
                break;
                
            default:
                System.out.println("Invalid option. Please enter a number between 1 and 4.");
        }
    }
}

abstract class Account {
    String AccountNumber;
    int Balance;
    Customer Owner;
    ArrayList<Transaction> transactions = new ArrayList<>();
    String status = "Active";
    abstract void deposit(int amount);
    abstract void withdraw(int amount);

    int getBalance() {
        return Balance;
    }

    void getTransactionHistory() {
        for (Transaction t : transactions) {
            t.getTransactionDetails();
        }
    }
}

class Transaction {
    String TransactionID;
    String TimeStamp;
    String Type;
    int amount;
    Account sourceAccount;
    Account destinationAccount;
    String Status;

    void getTransactionDetails() {
        System.out.println("Transaction ID: " + TransactionID);
        System.out.println("Timestamp: " + TimeStamp);
        System.out.println("Type: " + Type);
        System.out.println("Amount: " + amount);
        System.out.println("Source Account: " + (sourceAccount != null ? sourceAccount.AccountNumber : "N/A"));
        System.out.println("Destination Account: " + (destinationAccount != null ? destinationAccount.AccountNumber : "N/A"));
        System.out.println("Status: " + Status);
        System.out.println("New Balance: " + (destinationAccount != null ? destinationAccount.getBalance() : sourceAccount.getBalance()));
        System.out.println("---------------------------");
    }
}

class Customer extends User {
    String Address;
    ArrayList<Account> Accounts;
    void create(FileManager f1, String UserId, String userName, String password, String email, String name, String role) {
        UserID = UserId;
        UserName = userName;
        Password = password;
        Email = email;
        Name = name;
        role="Customer";
        f1.SaveUser(UserID, Password, role);
    }

    void viewAccount() {
        if (Accounts == null || Accounts.isEmpty()) {
            System.out.println("You have no accounts.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < Accounts.size(); i++) {
            Account acc = Accounts.get(i);
            System.out.println((i + 1) + ". Account Number: " + acc.AccountNumber);
            System.out.println("   Balance: " + acc.getBalance());
            System.out.println("   Status: " + (acc.status != null ? acc.status : "No status set"));
            System.out.println("--------------------------");
        }
        System.out.print("Select an account (1-" + Accounts.size() + ") or 0 to go back: ");
        int accChoice = scanner.nextInt();
        scanner.nextLine();
        if (accChoice == 0 || accChoice > Accounts.size()) {
            return;
        }
        Account selectedAcc = Accounts.get(accChoice - 1);
        while (true) {
            System.out.println("\nSelected Account: " + selectedAcc.AccountNumber);
            System.out.println("1. Deposit");
            System.out.println("2. Withdraw");
            System.out.println("3. Back");
            System.out.print("Choose an option (1-3): ");
            int action = scanner.nextInt();
            scanner.nextLine();
            if (action == 1) {
                System.out.print("Enter deposit amount ($1 or more): $");
                int amount = scanner.nextInt();
                scanner.nextLine();
                if (amount <= 0) {
                    System.out.println("Invalid amount. Please enter a positive number.");
                    continue;
                }
                selectedAcc.deposit(amount);
            } else if (action == 2) {
                System.out.print("Enter withdrawal amount ($1 or more): $");
                int amount = scanner.nextInt();
                scanner.nextLine();
                if (amount <= 0) {
                    System.out.println("Invalid amount. Please enter a positive number.");
                    continue;
                }
                selectedAcc.withdraw(amount);
            } else if (action == 3) {
                break;
            } else {
                System.out.println("Invalid option. Please enter a number between 1 and 3.");
            }
        }
    }

    void TransferFunds() {
        if (Accounts == null || Accounts.size() < 2) {
            System.out.println("You need at least 2 accounts to transfer funds.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select source account:");
        for (int i = 0; i < Accounts.size(); i++) {
            System.out.println((i + 1) + ". " + Accounts.get(i).AccountNumber + " - Balance: " + Accounts.get(i).getBalance());
        }
        System.out.print("Choose source account (1-" + Accounts.size() + ") or 0 to cancel: ");
        int sourceIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (sourceIndex == -1) {
            System.out.println("Transfer canceled.");
            return;
        }
        
        if (sourceIndex < 0 || sourceIndex >= Accounts.size()) {
            System.out.println("Invalid account selection.");
            return;
        }

        System.out.println("Select destination account:");
        for (int i = 0; i < Accounts.size(); i++) {
            if (i == sourceIndex) continue;
            System.out.println((i + 1) + ". " + Accounts.get(i).AccountNumber + " - Balance: " + Accounts.get(i).getBalance());
        }
        System.out.print("Choose destination account (1-" + Accounts.size() + ", except " + (sourceIndex + 1) + ") or 0 to cancel: ");
        int destIndex = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (destIndex == -1) {
            System.out.println("Transfer canceled.");
            return;
        }
        
        if (destIndex < 0 || destIndex >= Accounts.size() || destIndex == sourceIndex) {
            System.out.println("Invalid account selection.");
            return;
        }

        System.out.print("Enter amount to transfer ($1 or more): $");
        int amount = scanner.nextInt();
        scanner.nextLine();

        Account source = Accounts.get(sourceIndex);
        Account dest = Accounts.get(destIndex);

        if (amount <= 0) {
            System.out.println("Invalid amount. Please enter a positive number.");
            return;
        }
        
        if (amount > source.getBalance()) {
            System.out.println("Insufficient funds. Available balance: $" + source.getBalance());
            return;
        }

        source.withdraw(amount);
        dest.deposit(amount);

        Transaction transfer = new Transaction();
        transfer.TransactionID = UUID.randomUUID().toString();
        transfer.TimeStamp = new Date().toString();
        transfer.Type = "Transfer";
        transfer.amount = amount;
        transfer.sourceAccount = source;
        transfer.destinationAccount = dest;
        transfer.Status = "Completed";
        source.transactions.add(transfer);
        dest.transactions.add(transfer);

        // Optional: Save to file
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("transactions.txt", true))) {
            bw.write(transfer.TransactionID + "," + transfer.TimeStamp + "," + transfer.Type + "," + amount + "," + source.AccountNumber + "," + dest.AccountNumber + "," + transfer.Status + "\n");
        } catch (IOException e) {
            System.out.println("Failed to write transaction to file.");
        }

        System.out.println("Transfer complete. Transaction receipt:");
        transfer.getTransactionDetails();
    }
    void generateMonthlyStatement() {
        if (Accounts == null || Accounts.isEmpty()) {
            System.out.println("You have no accounts to generate statements for.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select account to generate statement for:");
        for (int i = 0; i < Accounts.size(); i++) {
            System.out.println((i + 1) + ". Account Number: " + Accounts.get(i).AccountNumber);
        }
        System.out.println((Accounts.size() + 1) + ". Go back");
        System.out.print("Choose an option (1-" + (Accounts.size() + 1) + "): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice == Accounts.size() + 1) {
            return;
        }
        
        if (choice < 1 || choice > Accounts.size()) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }
        
        String accNum = Accounts.get(choice - 1).AccountNumber;

        File file = new File("transactions.txt");
        if (!file.exists()) {
            System.out.println("No transaction history found.");
            return;
        }

        boolean found = false;
        try (Scanner fileScanner = new Scanner(file)) {
            System.out.println("\n--- Monthly Statement for Account " + accNum + " ---");
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 6 &&
                        (parts[4].equals(accNum) || parts[5].equals(accNum))) {

                    System.out.println("Date: " + parts[1]);
                    System.out.println("Type: " + parts[2]);
                    System.out.println("Amount: $" + parts[3]);
                    System.out.println("From: " + parts[4]);
                    System.out.println("To: " + parts[5]);
                    System.out.println("Status: " + parts[6]);
                    System.out.println("---------------------------");
                    found = true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading transaction file.");
        }

        if (!found) {
            System.out.println("No transactions found for this account.");
        }
        
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}

class employee extends User {
    String employeeId;
    String position;

    employee() {
        // Default constructor
    }
    
    employee(String employeeId, String position) {
        this.employeeId = employeeId;
        this.position = position;
    }

    void viewCustomerAccounts() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\nView Customer Accounts:");
        System.out.println("1. Continue");
        System.out.println("2. Return to Employee Panel");
        System.out.print("Choose an option (1-2): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice != 1) {
            System.out.println("Returning to Employee Panel...");
            return;
        }
        
        System.out.print("Enter customer username to view accounts (or type 'cancel' to return): ");
        String targetUser = scanner.nextLine();
        
        if (targetUser.equalsIgnoreCase("cancel")) {
            System.out.println("Operation canceled.");
            return;
        }

        try {
            File file = new File("accounts.txt");
            if (!file.exists()) {
                System.out.println("No accounts found.");
                return;
            }

            try (Scanner fileScanner = new Scanner(file)) {
                int index = 1;
                boolean found = false;

                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length == 4 && parts[3].equals(targetUser)) {
                        String accID = parts[0];
                        String balance = parts[1];
                        String type = parts[2];
                        String status = parts.length >= 5 ? parts[4] : "Active";
                        System.out.println(index++ + ". Account Number: " + accID);
                        System.out.println("   Balance: " + balance);
                        System.out.println("   Type: " + type);
                        System.out.println("   Status: " + status);
                        System.out.println("--------------------------");
                        found = true;
                    }
                }

                if (!found) {
                    System.out.println("No accounts found for user: " + targetUser);
                }
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading accounts.");
            e.printStackTrace();
        }
    }
    
    void searchCustomer() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\nSearch Customer:");
        System.out.println("1. Continue");
        System.out.println("2. Return to Employee Panel");
        System.out.print("Choose an option (1-2): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice != 1) {
            System.out.println("Returning to Employee Panel...");
            return;
        }
        
        System.out.println("Search by:");
        System.out.println("1. Username");
        System.out.println("2. Name");
        System.out.println("3. Cancel");
        System.out.print("Choose an option (1-3): ");
        
        int searchOption = scanner.nextInt();
        scanner.nextLine();
        
        if (searchOption == 3) {
            System.out.println("Operation canceled.");
            return;
        }
        
        if (searchOption != 1 && searchOption != 2) {
            System.out.println("Invalid option. Operation canceled.");
            return;
        }
        
        System.out.print("Enter search term: ");
        String searchTerm = scanner.nextLine();
        
        try {
            File file = new File("users.txt");
            if (!file.exists()) {
                System.out.println("No users found.");
                return;
            }
            
            try (Scanner fileScanner = new Scanner(file)) {
                int index = 1;
                boolean found = false;
                
                System.out.println("\n--- Search Results ---");
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    String[] parts = line.split(",");
                    
                    // Only search for customer accounts
                    if (parts.length >= 3 && parts[2].equals("Customer")) {
                        boolean match = false;
                        
                        if (searchOption == 1 && parts[0].toLowerCase().contains(searchTerm.toLowerCase())) {
                            // Search by username
                            match = true;
                        } else if (searchOption == 2 && parts.length >= 5 && 
                                  parts[4].toLowerCase().contains(searchTerm.toLowerCase())) {
                            // Search by name
                            match = true;
                        }
                        
                        if (match) {
                            System.out.println(index++ + ". Username: " + parts[0]);
                            if (parts.length >= 4) System.out.println("   Email: " + parts[3]);
                            if (parts.length >= 5) System.out.println("   Name: " + parts[4]);
                            System.out.println("--------------------------");
                            found = true;
                        }
                    }
                }
                
                if (!found) {
                    System.out.println("No customers found matching your search.");
                }
            }
            
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while searching for customers.");
            e.printStackTrace();
        }
    }
    
    void generateReport() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\nGenerate Report:");
        System.out.println("1. Continue");
        System.out.println("2. Return to Employee Panel");
        System.out.print("Choose an option (1-2): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice != 1) {
            System.out.println("Returning to Employee Panel...");
            return;
        }
        
        System.out.println("Select report type:");
        System.out.println("1. Account Summary Report");
        System.out.println("2. Transaction Activity Report");
        System.out.println("3. Cancel");
        System.out.print("Choose an option (1-3): ");
        
        int reportType = scanner.nextInt();
        scanner.nextLine();
        
        if (reportType == 3) {
            System.out.println("Operation canceled.");
            return;
        }
        
        switch (reportType) {
            case 1:
                generateAccountSummaryReport();
                break;
            case 2:
                generateTransactionActivityReport();
                break;
            default:
                System.out.println("Invalid option. Operation canceled.");
        }
    }
    
    private void generateAccountSummaryReport() {
        System.out.println("\n--- Account Summary Report ---");
        int totalAccounts = 0;
        int totalCheckingAccounts = 0;
        int totalSavingAccounts = 0;
        int totalBalance = 0;
        
        try {
            File file = new File("accounts.txt");
            if (!file.exists()) {
                System.out.println("No accounts found.");
                return;
            }
            
            try (Scanner fileScanner = new Scanner(file)) {
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        totalAccounts++;
                        
                        // Count by account type
                        if ("Checking".equalsIgnoreCase(parts[2])) {
                            totalCheckingAccounts++;
                        } else if ("Saving".equalsIgnoreCase(parts[2])) {
                            totalSavingAccounts++;
                        }
                        
                        // Add to total balance
                        try {
                            totalBalance += Integer.parseInt(parts[1]);
                        } catch (NumberFormatException e) {
                            // Skip if balance can't be parsed
                        }
                    }
                }
            }
            
            System.out.println("Total Accounts: " + totalAccounts);
            System.out.println("Checking Accounts: " + totalCheckingAccounts);
            System.out.println("Saving Accounts: " + totalSavingAccounts);
            System.out.println("Total Balance Across All Accounts: $" + totalBalance);
            
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while generating the report.");
            e.printStackTrace();
        }
    }
    
    private void generateTransactionActivityReport() {
        System.out.println("\n--- Transaction Activity Report ---");
        int totalTransactions = 0;
        int totalDeposits = 0;
        int totalWithdrawals = 0;
        int totalTransfers = 0;
        int totalDepositAmount = 0;
        int totalWithdrawalAmount = 0;
        int totalTransferAmount = 0;
        
        try {
            File file = new File("transactions.txt");
            if (!file.exists()) {
                System.out.println("No transactions found.");
                return;
            }
            
            try (Scanner fileScanner = new Scanner(file)) {
                while (fileScanner.hasNextLine()) {
                    String line = fileScanner.nextLine();
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 7) {
                        totalTransactions++;
                        
                        String type = parts[2];
                        int amount = 0;
                        try {
                            amount = Integer.parseInt(parts[3]);
                        } catch (NumberFormatException e) {
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
                                totalTransfers++;
                                totalTransferAmount += amount;
                                break;
                        }
                    }
                }
            }
            
            System.out.println("Total Transactions: " + totalTransactions);
            System.out.println("Deposits: " + totalDeposits + " ($" + totalDepositAmount + ")");
            System.out.println("Withdrawals: " + totalWithdrawals + " ($" + totalWithdrawalAmount + ")");
            System.out.println("Transfers: " + totalTransfers + " ($" + totalTransferAmount + ")");
            
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
            
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while generating the report.");
            e.printStackTrace();
        }
    }
}

class Admin extends employee {
    Boolean SecurityClearance;
    Boolean AdminPrivileges;
    void AdminPanel(FileManager fileManager) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nAdmin Panel:");
            System.out.println("1. Modify System Settings");
            System.out.println("2. User Management");
            System.out.println("3. Override Transaction Limits");
            System.out.println("4. Create Employee Account");
            System.out.println("5. Modify Account Status");
            System.out.println("6. Disable Account");
            System.out.println("7. View All System Activity");
            System.out.println("8. Generate Reports");
            System.out.println("9. Fix Accounts Without Owners");
            System.out.println("10. Logout");
            System.out.print("Choose an option (1-10): ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 1:
                    modifySystemSettings();
                    break;
                case 2:
                    userManagement(fileManager);
                    break;
                case 3:
                    overrideTransactionLimits();
                    break;
                case 4:
                    createEmployeeAccount(fileManager);
                    break;
                case 5:
                    modifyAccountStatus(fileManager);
                    break;
                case 6:
                    disableAccount(fileManager);
                    break;
                case 7:
                    viewAllSystemActivity(fileManager);
                    break;
                case 8:
                    generateReports(fileManager);
                    break;
                case 9:
                    System.out.print("Enter default username to assign to accounts without owners (or type 'cancel'): ");
                    String defaultUsername = scanner.nextLine();
                    if (defaultUsername.equalsIgnoreCase("cancel")) {
                        System.out.println("Operation canceled.");
                        break;
                    }
                    fileManager.fixAccountsWithoutOwners(defaultUsername);
                    break;
                case 10:
                    System.out.println("Logging out from Admin panel...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 10.");
            }
        }
    }

    void modifySystemSettings() {
        System.out.println("System settings can be modified here.");
    }

    void userManagement(FileManager fileManager) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nUser Management:");
        System.out.println("1. Create Employee Account");
        System.out.println("2. Modify Account Status");
        System.out.println("3. Disable Account");
        System.out.println("4. Return to Admin Panel");
        System.out.print("Choose an option (1-4): ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                createEmployeeAccount(fileManager);
                break;
            case 2:
                modifyAccountStatus(fileManager);
                break;
            case 3:
                disableAccount(fileManager);
                break;
            case 4:
                System.out.println("Returning to Admin Panel...");
                break;
            default:
                System.out.println("Invalid option. Please enter a number between 1 and 4.");
        }
    }
    void overrideTransactionLimits() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter new transaction limit: ");
        double newLimit = scanner.nextDouble();
        System.out.println("Transaction limit set to " + newLimit);
    }
    void createEmployeeAccount(FileManager fileManager) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\nCreate Employee Account:");
        System.out.println("1. Continue");
        System.out.println("2. Return to Admin Panel");
        System.out.print("Choose an option (1-2): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice != 1) {
            System.out.println("Returning to Admin Panel...");
            return;
        }
        
        System.out.print("Enter Employee ID (or type 'cancel' to return): ");
        String empID = scanner.nextLine();
        if (empID.equalsIgnoreCase("cancel")) {
            System.out.println("Operation canceled.");
            return;
        }
        
        System.out.print("Enter Employee Name (or type 'cancel' to return): ");
        String empName = scanner.nextLine();
        if (empName.equalsIgnoreCase("cancel")) {
            System.out.println("Operation canceled.");
            return;
        }
        
        System.out.print("Enter Employee Password (minimum 6 characters, or type 'cancel' to return): ");
        String empPassword = scanner.nextLine();
        if (empPassword.equalsIgnoreCase("cancel")) {
            System.out.println("Operation canceled.");
            return;
        }
        
        if (empPassword.length() < 6) {
            System.out.println("Password must be at least 6 characters long. Operation canceled.");
            return;
        }

        // Save employee data to file
        fileManager.SaveUser(empID, empPassword, "Employee");
        System.out.println("Employee account created successfully.");
    }

    void modifyAccountStatus(FileManager fileManager) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\nModify Account Status:");
        System.out.println("1. Continue");
        System.out.println("2. Return to Admin Panel");
        System.out.print("Choose an option (1-2): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice != 1) {
            System.out.println("Returning to Admin Panel...");
            return;
        }
        
        System.out.print("Enter Account ID to modify (or type 'cancel' to return): ");
        String accountID = scanner.nextLine();
        if (accountID.equalsIgnoreCase("cancel")) {
            System.out.println("Operation canceled.");
            return;
        }
        
        System.out.println("Select new status:");
        System.out.println("1. Active");
        System.out.println("2. Disabled");
        System.out.println("3. Frozen");
        System.out.println("4. Closed");
        System.out.println("5. Cancel operation");
        System.out.print("Choose an option (1-5): ");
        
        int statusChoice = scanner.nextInt();
        scanner.nextLine();
        
        String newStatus;
        switch (statusChoice) {
            case 1:
                newStatus = "Active";
                break;
            case 2:
                newStatus = "Disabled";
                break;
            case 3:
                newStatus = "Frozen";
                break;
            case 4:
                newStatus = "Closed";
                break;
            case 5:
                System.out.println("Operation canceled.");
                return;
            default:
                System.out.println("Invalid choice. Operation canceled.");
                return;
        }

        fileManager.modifyAccountStatus(accountID, newStatus);
        System.out.println("Account status updated to: " + newStatus);
    }

    void disableAccount(FileManager fileManager) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\nDisable Account:");
        System.out.println("1. Continue");
        System.out.println("2. Return to Admin Panel");
        System.out.print("Choose an option (1-2): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        
        if (choice != 1) {
            System.out.println("Returning to Admin Panel...");
            return;
        }
        
        System.out.print("Enter Account ID to disable (or type 'cancel' to return): ");
        String accountID = scanner.nextLine();
        if (accountID.equalsIgnoreCase("cancel")) {
            System.out.println("Operation canceled.");
            return;
        }
        
        System.out.println("Are you sure you want to disable this account?");
        System.out.println("1. Yes, disable the account");
        System.out.println("2. No, cancel operation");
        System.out.print("Choose an option (1-2): ");
        
        int confirmChoice = scanner.nextInt();
        scanner.nextLine();
        
        if (confirmChoice != 1) {
            System.out.println("Operation canceled.");
            return;
        }

        fileManager.disableAccount(accountID);
        System.out.println("Account disabled successfully.");
    }
    void viewAllSystemActivity(FileManager fileManager) {
        System.out.println("Viewing all system activity...");
        fileManager.viewAllTransactions();
    }
    void generateReports(FileManager fileManager) {
        System.out.println("Generating administrative reports...");
        fileManager.generateReport();
    }
}

class checkingAccount extends Account {
    void deposit(int amount) {
        Balance += amount;
        Transaction transaction = new Transaction();
        transaction.TransactionID = UUID.randomUUID().toString();
        transaction.TimeStamp = new Date().toString();
        transaction.Type = "Deposit";
        transaction.amount = amount;
        transaction.sourceAccount = null;
        transaction.destinationAccount = this;
        transaction.Status = "Completed";
        transactions.add(transaction);
        
        // Save to accounts file
        File file = new File("accounts.txt");
        File tempFile = new File("accounts_temp.txt");
        try (Scanner scanner = new Scanner(file);
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(AccountNumber)) {
                    writer.write(AccountNumber + "," + Balance + "," + parts[2] + "," + parts[3] + (parts.length > 4 ? "," + parts[4] : "") + "\n");
                } else {
                    writer.write(line + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating account balance in file");
            return;
        }
        
        // Replace old file with new one
        if (!file.delete() || !tempFile.renameTo(file)) {
            System.out.println("Error updating account file");
            return;
        }
        
        System.out.println("Deposited " + amount + " to checking account " + AccountNumber);
    }

    void withdraw(int amount) {
        if(amount <= Balance) {
            Balance -= amount;
            Transaction transaction = new Transaction();
            transaction.TransactionID = UUID.randomUUID().toString();
            transaction.TimeStamp = new Date().toString();
            transaction.Type = "Withdrawal";
            transaction.amount = amount;
            transaction.sourceAccount = this;
            transaction.destinationAccount = null;
            transaction.Status = "Completed";
            transactions.add(transaction);
            
            // Save to accounts file
            File file = new File("accounts.txt");
            File tempFile = new File("accounts_temp.txt");
            try (Scanner scanner = new Scanner(file);
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length >= 4 && parts[0].equals(AccountNumber)) {
                        writer.write(AccountNumber + "," + Balance + "," + parts[2] + "," + parts[3] + (parts.length > 4 ? "," + parts[4] : "") + "\n");
                    } else {
                        writer.write(line + "\n");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error updating account balance in file");
                return;
            }
            
            // Replace old file with new one
            if (!file.delete() || !tempFile.renameTo(file)) {
                System.out.println("Error updating account file");
                return;
            }
            
            System.out.println("Withdrew " + amount + " from checking account " + AccountNumber);
        } else {
            System.out.println("Insufficient balance for withdrawal.");
        }
    }
}

class savingAccount extends Account {
    double interestRate;
    double minimumBalance;
    double withdrawalLimit;
    ArrayList<String> withdrawalsThisMonth = new ArrayList<>();
    
    savingAccount() {
        this.interestRate = 0.02;
        this.minimumBalance = 100;
        this.withdrawalLimit = 3;
    }
    
    void deposit(int amount) {
        Balance += amount;
        Transaction transaction = new Transaction();
        transaction.TransactionID = UUID.randomUUID().toString();
        transaction.TimeStamp = new Date().toString();
        transaction.Type = "Deposit";
        transaction.amount = amount;
        transaction.sourceAccount = null;
        transaction.destinationAccount = this;
        transaction.Status = "Completed";
        transactions.add(transaction);
        
        // Save to accounts file
        File file = new File("accounts.txt");
        File tempFile = new File("accounts_temp.txt");
        try (Scanner scanner = new Scanner(file);
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(AccountNumber)) {
                    writer.write(AccountNumber + "," + Balance + "," + parts[2] + "," + parts[3] + (parts.length > 4 ? "," + parts[4] : "") + "\n");
                } else {
                    writer.write(line + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating account balance in file");
            return;
        }
        
        // Replace old file with new one
        if (!file.delete() || !tempFile.renameTo(file)) {
            System.out.println("Error updating account file");
            return;
        }
        
        System.out.println("Deposited " + amount + " to saving account " + AccountNumber);
    }

    void withdraw(int amount) {
        if (withdrawalsThisMonth.size() >= 3) {
            System.out.println("You have reached the maximum number of withdrawals for this month.");
            return;
        }
        if (amount > Balance - minimumBalance) {
            System.out.println("Cannot withdraw beyond minimum balance.");
            return;
        }
        Balance -= amount;
        
        withdrawalsThisMonth.add(new Date().toString());
        Transaction transaction = new Transaction();
        transaction.TransactionID = UUID.randomUUID().toString();
        transaction.TimeStamp = new Date().toString();
        transaction.Type = "Withdrawal";
        transaction.amount = amount;
        transaction.sourceAccount = this;
        transaction.destinationAccount = null;
        transaction.Status = "Completed";
        transactions.add(transaction);
        
        // Save to accounts file
        File file = new File("accounts.txt");
        File tempFile = new File("accounts_temp.txt");
        try (Scanner scanner = new Scanner(file);
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length >= 4 && parts[0].equals(AccountNumber)) {
                    writer.write(AccountNumber + "," + Balance + "," + parts[2] + "," + parts[3] + (parts.length > 4 ? "," + parts[4] : "") + "\n");
                } else {
                    writer.write(line + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating account balance in file");
            return;
        }
        
        // Replace old file with new one
        if (!file.delete() || !tempFile.renameTo(file)) {
            System.out.println("Error updating account file");
            return;
        }
        
        System.out.println("Withdrew " + amount + " from saving account " + AccountNumber);
    }

    void calculateInterest() {
        double interest = Balance * interestRate;
        System.out.println("Calculated interest: " + interest);
    }

    void applyMonthlyInterest() {
        double interest = Balance * interestRate;
        Balance += interest;
        System.out.println("Applied monthly interest: " + interest);
        Transaction transaction = new Transaction();
        transaction.TransactionID = UUID.randomUUID().toString();
        transaction.TimeStamp = new Date().toString();
        transaction.Type = "Interest";
        transaction.amount = (int)interest;
        transaction.sourceAccount = null;
        transaction.destinationAccount = this;
        transaction.Status = "Completed";
        transactions.add(transaction);
    }
}

class Bank {
    ArrayList<User> Users;
    ArrayList<Transaction> Transactions;
    ArrayList<Account> Accounts;

    Customer createUser(FileManager fileManager) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("\nRegistration Options:");
        System.out.println("1. Create new account");
        System.out.println("2. Return to main menu");
        System.out.print("Choose an option (1-2): ");
        
        int choice;
        try {
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Consume invalid input
            System.out.println("Invalid input. Returning to main menu.");
            return null;
        }
        
        if (choice != 1) {
            System.out.println("Returning to main menu...");
            return null;
        }
        
        String userID = "";
        do {
            System.out.print("Enter user ID (or type 'back' to return): ");
            userID = scanner.nextLine();
            if (userID.equalsIgnoreCase("back")) {
                System.out.println("Registration canceled.");
                return null;
            }
        } while (userID.trim().isEmpty());

        String userName = "";
        do {
            System.out.print("Enter username (or type 'back' to return): ");
            userName = scanner.nextLine();
            if (userName.equalsIgnoreCase("back")) {
                System.out.println("Registration canceled.");
                return null;
            }
        } while (userName.trim().isEmpty());

        String password = "";
        do {
            System.out.print("Enter password (minimum 6 characters, or type 'back' to return): ");
            password = scanner.nextLine();
            if (password.equalsIgnoreCase("back")) {
                System.out.println("Registration canceled.");
                return null;
            }
            if (password.length() < 6) {
                System.out.println("Password must be at least 6 characters long.");
                password = "";
            }
        } while (password.trim().isEmpty());

        String email = "";
        do {
            System.out.print("Enter email (or type 'back' to return): ");
            email = scanner.nextLine();
            if (email.equalsIgnoreCase("back")) {
                System.out.println("Registration canceled.");
                return null;
            }
        } while (email.trim().isEmpty());

        String name = "";
        do {
            System.out.print("Enter full name (or type 'back' to return): ");
            name = scanner.nextLine();
            if (name.equalsIgnoreCase("back")) {
                System.out.println("Registration canceled.");
                return null;
            }
        } while (name.trim().isEmpty());
        
        Customer customer = new Customer();
        customer.create(fileManager, userID, userName, password, email, name, "Customer");
        customer.UserName = userName; // Explicitly set the username in the Customer object
        customer.Password = password; // Explicitly set the password in the Customer object
        
        System.out.println("User created successfully! Username: " + customer.UserName);
        return customer;
    }
}

class FileManager {
    void SaveUser(String user, String password, String role) {
        try {
            File file = new File("users.txt");
            if (file.createNewFile()) {System.out.println("Users File created: " + file.getName());}
            FileWriter fileWriter = new FileWriter(file, true); // Append mode
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.newLine();
            bw.write(user + "," + password + "," + role);
            bw.newLine();
            bw.close();
            System.out.println("User successfully added.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    String LoadUsers(String userID, String password) throws FileNotFoundException {
        File file = new File("users.txt");
        if (!file.exists()) return null;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(userID) && parts[1].equals(password)) {
                    return parts[2];
                }
            }
        }
        return null;
    }

    void SaveAccount(String AccountID, String Balance, String Type, String ownerUsername) {
        try {
            // Ensure ownerUsername is not null
            if (ownerUsername == null || ownerUsername.equals("null")) {
                System.out.println("Error: Cannot save account without a valid username.");
                return;
            }
            
            File file = new File("accounts.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file, true); // Append mode
            BufferedWriter bw = new BufferedWriter(fileWriter);
            bw.write(AccountID + "," + Balance + "," + Type + "," + ownerUsername + "\n");
            bw.close();
            System.out.println("Account successfully added.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving account.");
            e.printStackTrace();
        }
    }
    void LoadAccounts(Customer c1) {
        try {
            File file = new File("accounts.txt");
            if (!file.exists()) {
                System.out.println("Accounts file not found.");
                return;
            }
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.trim().isEmpty()) continue; // Skip empty lines
                
                String[] parts = line.split(",");
                boolean isUserAccount = (parts.length >= 4 && parts[3] != null && !parts[3].equals("null") && parts[3].equals(c1.UserName)) || 
                                       (c1.UserName == null); // Load all accounts if no user is logged in yet
                                   
                if (parts.length >= 3) {
        
                    String accountID = parts[0];
                    String balance = parts[1];
                    String type = parts[2];
                    
                    if (c1.UserName != null && parts.length >= 4 && !parts[3].equals(c1.UserName)) {
                        continue;  
                    }
                    
                    Account newAccount;
                    if ("Checking".equalsIgnoreCase(type.trim())) {
                        newAccount = new checkingAccount();
                    } else {
                        newAccount = new savingAccount();
                    }
                    newAccount.AccountNumber = accountID;
                    try {
                        newAccount.Balance = Integer.parseInt(balance.trim());
                    } catch (NumberFormatException e) {
                        newAccount.Balance = 0;
                    }
                    newAccount.Owner = c1;
                    if (c1.Accounts == null) {
                        c1.Accounts = new ArrayList<>();
                    }
                    c1.Accounts.add(newAccount);
                    System.out.println("Loaded Account ID: " + accountID + ", Balance: " + balance + ", Type: " + type);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while loading accounts.");
            e.printStackTrace();
        }
    }
    void resetPassword(String userID, String newPassword) {
        File file = new File("users.txt");
        File tempFile = new File("users_temp.txt");

        try (Scanner scanner = new Scanner(file);
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(userID)) {
                    bw.write(userID + "," + newPassword + "," + parts[2]);
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }

            file.delete();
            tempFile.renameTo(file);
            System.out.println("Password reset successfully.");
        } catch (IOException e) {
            System.out.println("Error resetting password.");
            e.printStackTrace();
        }
    }
    void modifyAccountStatus(String accountID, String newStatus) {
        File file = new File("accounts.txt");
        File tempFile = new File("accounts_temp.txt");
        String ownerUsername = null;

        try (Scanner scanner = new Scanner(file);
             BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts[0].equals(accountID)) {
                    if (parts.length >= 4) {
                        ownerUsername = parts[3];
                    }
                    
                    String currentStatus = parts.length >= 5 ? parts[4] : "Active";
                    if (!currentStatus.equals(newStatus)) {
                        if (parts.length >= 5) {
                            bw.write(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + newStatus + "\n");
                        } else {
                            bw.write(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + newStatus + "\n");
                        }
                        
                        if (ownerUsername != null && !ownerUsername.equals("null")) {
                            createNotification(ownerUsername, "Account Status Change", 
                                "Your account " + accountID + " status has been changed to " + newStatus);
                        }
                    } else {
                        bw.write(line + "\n");
                    }
                } else {
                    bw.write(line + "\n");
                }
            }

            file.delete();
            tempFile.renameTo(file);
            System.out.println("Account status modified.");

        } catch (IOException e) {
            System.out.println("Error modifying account status.");
            e.printStackTrace();
        }
    }
    void disableAccount(String accountID) {
        modifyAccountStatus(accountID, "Disabled");
    }
    void viewAllTransactions() {
        File file = new File("transactions.txt");
        if (!file.exists()) {
            System.out.println("No transactions found.");
            return;
        }

        try (Scanner scanner = new Scanner(file)) {
            System.out.println("\n--- All Transactions ---");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                System.out.println("ID: " + parts[0]);
                System.out.println("Date: " + parts[1]);
                System.out.println("Type: " + parts[2]);
                System.out.println("Amount: $" + parts[3]);
                System.out.println("From: " + parts[4]);
                System.out.println("To: " + parts[5]);
                System.out.println("Status: " + parts[6]);
                System.out.println("---------------------------");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error reading transaction file.");
        }
    }
    void generateReport() {
        System.out.println("Generating system-wide report...");
    }
    void fixAccountsWithoutOwners(String defaultUsername) {
        File file = new File("accounts.txt");
        File tempFile = new File("accounts_temp.txt");
        
        try {
            if (!file.exists()) {
                System.out.println("Accounts file not found.");
                return;
            }
            
            try (Scanner scanner = new Scanner(file);
                 BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
                
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) continue;
                    
                    String[] parts = line.split(",");
                    if (parts.length < 4 || parts[3] == null || parts[3].equals("null")) {
                        bw.write(parts[0] + "," + parts[1] + "," + parts[2] + "," + defaultUsername);
                        System.out.println("Fixed account: " + parts[0] + " - assigned to " + defaultUsername);
                    } else {
                        bw.write(line);
                    }
                    bw.newLine();
                }
            }
            
            file.delete();
            tempFile.renameTo(file);
            
            System.out.println("Account ownership fixes completed.");
        } catch (IOException e) {
            System.out.println("An error occurred while fixing accounts.");
            e.printStackTrace();
        }
    }
    void updateUserProfile(String userID, String field, String newValue) {
        File file = new File("users.txt");
        File tempFile = new File("users_temp.txt");
        
        try {
            if (!file.exists()) {
                System.out.println("Users file not found.");
                return;
            }
            
            boolean foundUser = false;
            String userLine = "";
            
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine() && !foundUser) {
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    if (parts.length >= 3 && parts[0].equals(userID)) {
                        foundUser = true;
                        userLine = line;
                    }
                }
            }
            
            if (!foundUser) {
                System.out.println("User not found. Cannot update profile.");
                return;
            }
            
            try (Scanner scanner = new Scanner(file);
                 BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
                
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.trim().isEmpty()) {
                        bw.newLine();
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 3 && parts[0].equals(userID)) {

                        StringBuilder updatedLine = new StringBuilder();
                        updatedLine.append(parts[0]).append(","); // UserID
                        updatedLine.append(parts[1]).append(","); // Password
                        updatedLine.append(parts[2]); // Role
                        
                        if (parts.length > 3) {
                            if (field.equalsIgnoreCase("email")) {
                                updatedLine.append(",").append(newValue); // Updated email
                                
                                if (parts.length > 4) {
                                    updatedLine.append(",").append(parts[4]);
                                }
                            } else if (field.equalsIgnoreCase("name")) {
                                updatedLine.append(",").append(parts[3]); // Existing email
                                
                                // Add updated name
                                updatedLine.append(",").append(newValue);
                            }
                        } else {
                            if (field.equalsIgnoreCase("email")) {
                                updatedLine.append(",").append(newValue);
                            } else if (field.equalsIgnoreCase("name")) {
                                updatedLine.append(","); // Empty email field
                                updatedLine.append(",").append(newValue);
                            }
                        }
                        
                        bw.write(updatedLine.toString());
                    } else {
                        bw.write(line);
                    }
                    bw.newLine();
                }
            }
            
            file.delete();
            tempFile.renameTo(file);
            
            System.out.println("Profile updated successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating profile.");
            e.printStackTrace();
        }
    }
    void createNotification(String username, String title, String message) {
        try {
            File file = new File("notifications.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            
            FileWriter fileWriter = new FileWriter(file, true); // Append mode
            BufferedWriter bw = new BufferedWriter(fileWriter);
            String notificationId = UUID.randomUUID().toString();
            String timestamp = new Date().toString();
            boolean read = false; 
            bw.write(notificationId + "," + username + "," + timestamp + "," + title + "," + message + "," + read + "\n");
            bw.close();
            System.out.println("Notification created for user: " + username);
        } catch (IOException e) {
            System.out.println("Error creating notification.");
            e.printStackTrace();
        }
    }
}
