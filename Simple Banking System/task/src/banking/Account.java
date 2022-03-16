package banking;

import java.util.Random;

public class Account {
    String account = "400000";
    String pin = "";
    private int lastDigit() {
        int result;
        int sum = 0;
        char[] digits = account.toCharArray();
        int[] accountNumbers = new int[digits.length];
        for (int i = 0; i < digits.length; i++) {
            accountNumbers[i] = digits[i] - '0';
        }
        for(int i = 0; i < accountNumbers.length; i++) {
            if ((i + 1) %2 == 1) {
                accountNumbers[i] *= 2;
            }
            if (accountNumbers[i] > 9) {
                accountNumbers[i] -= 9;
            }
            sum += accountNumbers[i];
        }
        result = sum % 10 == 0 ? 0 : 10 - sum % 10;
        return result;
    }
    public void generate() {
        Random random = new Random();
        for(int i = 0; i < 9; i++) {
            account += random.nextInt(10);
        }
        account += lastDigit();
        for(int i = 0; i < 4; i++) {
            pin += random.nextInt(10);
        }
    }
    public String getAccount() {
        return account;
    }
    public String getPin() {
        return pin;
    }
}
