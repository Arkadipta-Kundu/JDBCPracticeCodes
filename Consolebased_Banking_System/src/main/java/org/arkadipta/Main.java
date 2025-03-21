package org.arkadipta;

import java.sql.*;
import java.util.Scanner;

public class Main {
    // Declare a static connection variable
    private static Connection conn;

    // Declare a static variable to store the logged-in account number
    private static int loggedInAccountNumber;

    public static void loginIntoAccount() throws SQLException {
        Scanner sc1 = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your account number: ");
            int accountNumber = sc1.nextInt();
            System.out.print("Enter your password: ");
            String password = sc1.next();

            boolean userValidated = false;
            String retrievePass = "select password from bankaccounts where accountnumber = ?";
            PreparedStatement retrievePassStatement = conn.prepareStatement(retrievePass);
            retrievePassStatement.setInt(1, accountNumber);
            ResultSet rs = retrievePassStatement.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // Compare storedPassword with the provided one
                userValidated = password.equals(storedPassword); // Use proper hashing in real applications
            }

            if (userValidated) {
                System.out.println("Hello User");
                loggedInAccountNumber = accountNumber;
                while (true) {
                    System.out.println();
                    System.out.println(
                            "1. Deposit\n" +
                                    "2. Withdraw\n" +
                                    "3. Transfer\n" +
                                    "4. View Balance\n" +
                                    "5. Log Out\n" +
                                    "6. Back to Main Menu"
                    );

                    System.out.print("Enter your choice: ");
                    int choice = sc1.nextInt();

                    switch (choice) {
                        case 1:
                            deposit();
                            break;
                        case 2:
                            withdraw();
                            break;
                        case 3:
                            transfer();
                            break;
                        case 4:
                            viewBalance();
                            break;
                        case 5:
                            loggedInAccountNumber = -1; // Clear the logged-in account number
                            System.out.println("Logged out successfully.");
                            System.exit(0);
                            return;
                        case 6:
                            loggedInAccountNumber = -1; // Clear the logged-in account number
                            System.out.println("Logged out successfully.");
                            return; // Go back to the main menu
                        default:
                            System.out.println("Invalid choice, please try again.");
                            break;
                    }
                }
            } else {
                System.out.println("Invalid account number or password, please try again.");
            }
        }
    }

    public static void createAccount() throws SQLException {
        System.out.println("Create Account");
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String name = sc.nextLine();
        System.out.print("Enter your password: ");
        String password = sc.nextLine();
        System.out.print("Enter initial amount: ");
        double amount = sc.nextDouble();

        String createAccountQuery = "INSERT INTO bankaccounts (name, password, balance) VALUES (?, ?, ?)";
        PreparedStatement createAccountStatement = conn.prepareStatement(createAccountQuery);

        createAccountStatement.setString(1, name);
        createAccountStatement.setString(2, password);
        createAccountStatement.setDouble(3, amount);

        int rowsUpdated = createAccountStatement.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("Account Created Successfully!");
        }
    }

    public static void deposit() throws SQLException {
        Scanner sc = new Scanner(System.in);
//        System.out.print("Enter the account number: ");
//        int accountNumber = sc.nextInt();
        System.out.print("Enter the amount to deposit: ");
        double amount = sc.nextDouble();

        String depositSql = "UPDATE bankaccounts SET balance = balance + ? WHERE accountnumber = ?";
        PreparedStatement depositStatement = conn.prepareStatement(depositSql);

        depositStatement.setDouble(1, amount);
        depositStatement.setInt(2, loggedInAccountNumber); // Corrected to set as integer

        int rowsUpdated = depositStatement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.print("Amount Deposit successful ! and your ");
        }
        viewBalance();
    }

    public static void withdraw() throws SQLException {
        Scanner sc = new Scanner(System.in);
//        System.out.print("Enter the account number: ");
//        int accountNumber = sc.nextInt();
        System.out.print("Enter the amount to Withdraw: ");
        double amount = sc.nextDouble();

        String withdrawSql = "UPDATE bankaccounts SET balance = balance - ? WHERE accountnumber = ?";
        PreparedStatement withdrawStatement = conn.prepareStatement(withdrawSql);

        withdrawStatement.setDouble(1, amount);
        withdrawStatement.setInt(2, loggedInAccountNumber);

        int rowsUpdated = withdrawStatement.executeUpdate();

        if (rowsUpdated > 0) {
            System.out.print("Amount Withdraw successfull ! and your ");
        }
        viewBalance();
    }

    public static void transfer() throws SQLException {
        conn.setAutoCommit(false);
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the receiver's account number:");
        int receiverAccountNumber = sc.nextInt();
        System.out.print("Enter the amount:");
        Double amount = sc.nextDouble();

        String debitSql = "UPDATE bankaccounts SET balance = balance - ? WHERE accountnumber = ?";
        PreparedStatement debitStatement = conn.prepareStatement(debitSql);
        debitStatement.setDouble(1,amount);
        debitStatement.setInt(2,loggedInAccountNumber);
        int debitRes = debitStatement.executeUpdate();

        String craditSql = "UPDATE bankaccounts SET balance = balance + ? WHERE accountnumber = ?";
        PreparedStatement craditStatement = conn.prepareStatement(craditSql);
        debitStatement.setDouble(1,amount);
        debitStatement.setInt(2,receiverAccountNumber);
        int creditRes = debitStatement.executeUpdate();

        if(creditRes>0 && debitRes>0){
            System.out.println("Transaction Successful and your ");
            viewBalance();
            conn.commit();
        }else {
            System.out.println("Transaction Failed");
            conn.rollback();
        }


    }

    public static void viewBalance() throws SQLException {
        String checkBalance = "SELECT balance FROM bankaccounts WHERE accountnumber = ?";
        PreparedStatement checkBalanceStatement = conn.prepareStatement(checkBalance);
        checkBalanceStatement.setInt(1, loggedInAccountNumber);
        ResultSet rs = checkBalanceStatement.executeQuery();
        if (rs.next()) {
            System.out.println("Current balance is " + rs.getDouble("balance"));
        }

    }

    public static void startApp() throws SQLException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("Welcome to the Banking System");
            System.out.println();
            System.out.println(
                    "1. Create Account\n" +
                            "2. Login into Account\n" +
                            "3. Exit"
            );

            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    loginIntoAccount();
                    break;
                case 3:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
                    break;
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Initialize the static connection
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Demo", "postgres", "0000");
//            System.out.println("Database Connected!");

            startApp();

            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
