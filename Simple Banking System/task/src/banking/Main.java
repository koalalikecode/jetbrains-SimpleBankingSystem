package banking;
import org.sqlite.SQLiteDataSource;

import java.sql.*;
import java.util.Scanner;

public class Main {
    static int count = 0;
    static int choice;
    static Statement statement;
    static Connection con = null;
    static void createConnection() {
        try {
            String url = "jdbc:sqlite:card.s3db";
            SQLiteDataSource dataSource = new SQLiteDataSource();
            dataSource.setUrl(url);
            con = dataSource.getConnection();
            statement = con.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static void createTable() {
        try {
            String table = "create table if not exists card (" +
                    "id INTEGER," +
                    "number TEXT," +
                    "pin TEXT," +
                    "balance INTEGER DEFAULT 0);";
            statement.executeUpdate(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static void addData(Account account) {
        try {
            statement.executeUpdate("INSERT INTO card(id, number, pin) VALUES(" +
                    count + ",'" +
                    account.getAccount() + "','" +
                    account.getPin() + "');");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    static void create() {
        Account account = new Account();
        account.generate();
        System.out.println();
        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        System.out.println(account.getAccount());
        System.out.println("Your card PIN:");
        System.out.println(account.getPin());
        System.out.println();
        addData(account);
        count += 1;
    }
    static void addIncome(String account, String pin) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter income:");
        int income = scanner.nextInt();
        try{
            statement.executeUpdate("UPDATE card SET balance = balance + " + income +
                    " WHERE " + "number =" + account + " AND " +
                    "pin =" + pin);
            System.out.println("Income was added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println();
    }
    static boolean checkLuhn(String account) {
        boolean result = false;
        int sum = 0;
        char[] digits = account.toCharArray();
        int[] accountNumbers = new int[digits.length];
        for (int i = 0; i < digits.length; i++) {
            accountNumbers[i] = digits[i] - '0';
        }
        for(int i = 0; i < accountNumbers.length - 1; i++) {
            if ((i + 1) %2 == 1) {
                accountNumbers[i] *= 2;
            }
            if (accountNumbers[i] > 9) {
                accountNumbers[i] -= 9;
            }
            sum += accountNumbers[i];
        }
        sum += accountNumbers[accountNumbers.length - 1];
        if (sum % 10 == 0) {
            result = true;
        }
        return result;
    }
    static boolean isCardExist(String account) {
        boolean check = false;
        try {
            ResultSet result = statement.executeQuery("SELECT id FROM card WHERE "+
                    "number =" + account);
            if (result.next()) {
                check = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check;
    }
    static void transfer(String account) {
        boolean check = true;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String input_card = scanner.nextLine();
        if (!checkLuhn(input_card)) {
            check = false;
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        }
        if (input_card.equals(account)) {
            check = false;
            System.out.println("You can't transfer money to the same account!");
        }
        if (!isCardExist(input_card)) {
            check = false;
            System.out.println("Such a card does not exist.");
        }
        if (check) {
            System.out.println("Enter how much money you want to transfer:");
            int money = scanner.nextInt();
            boolean isEnough = false;
            try {
                ResultSet result = statement.executeQuery("SELECT balance FROM card WHERE "+
                        "number =" + account);
                result.next();
                if (result.getInt("balance") - money > 0) {
                    isEnough = true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (isEnough) {
                try {
                    statement.executeUpdate("UPDATE card SET balance = balance - " + money +
                            " WHERE " + "number =" + account);
                    statement.executeUpdate("UPDATE card SET balance = balance + " + money +
                            " WHERE " + "number =" + input_card);
                    System.out.println("Success!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Not enough money!");
            }
        }
        System.out.println();
    }
    static void closeAccount(String account) {
        try{
            statement.executeUpdate("DELETE FROM card WHERE " + "number =" + account);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("The account has been closed!");
        System.out.println();
    }
    static void sucessLogin(String account, String pin) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("You have successfully logged in!");
        System.out.println();
        do{
            System.out.println("1. Balance");
            System.out.println("2. Add income");
            System.out.println("3. Do transfer");
            System.out.println("4. Close account");
            System.out.println("5. Log out");
            System.out.println("0. Exit");
            choice = scanner.nextInt();
            System.out.println();
            switch(choice) {
                case 1:
                    try {
                        ResultSet result = statement.executeQuery("SELECT balance FROM card WHERE "+
                                "number =" + account + " AND " +
                                "pin =" + pin);
                        result.next();
                        int balance = result.getInt("balance");
                        System.out.print("Balance: ");
                        System.out.println(balance);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    addIncome(account, pin);
                    break;
                case 3:
                    transfer(account);
                    break;
                case 4:
                    closeAccount(account);
                    break;
                case 5:
                    System.out.println("You have successfully logged out!");
                    System.out.println();
                    break;
                case 0:
                    System.out.println("Bye!");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + choice);
            }
        } while (choice != 5 && choice != 0 && choice != 4);
    }
    static void login() {
        Scanner scanner = new Scanner(System.in);
        boolean check = false;
        System.out.println();
        System.out.println("Enter your card number:");
        String account = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pin = scanner.nextLine();
        System.out.println();
        try {
            ResultSet result = statement.executeQuery("SELECT id FROM card WHERE "+
                    "number =" + account + " AND " +
                    "pin =" + pin);
            if (result.next()) {
                check = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(check) {
            sucessLogin(account, pin);
        } else {
            System.out.println("Wrong card number or PIN!");
            System.out.println();
        }
    }
    public static void main(String[] args) {
        createConnection();
        createTable();
        Scanner scanner = new Scanner(System.in);

        do{
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");
            choice = scanner.nextInt();
            switch(choice) {
                case 1:
                    create();
                    break;
                case 2:
                    login();
                    break;
                case 0:
                    System.out.println();
                    System.out.println("Bye!");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + choice);
            }
        } while (choice != 0);
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}