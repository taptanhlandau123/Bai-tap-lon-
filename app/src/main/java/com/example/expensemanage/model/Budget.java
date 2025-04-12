package com.example.expensemanage.model;

public class Budget {
    private int amount;
    private String month;

    public Budget() {
        // Bắt buộc phải có constructor rỗng để Firebase dùng
    }

    public Budget(int amount, String month) {
        this.amount = amount;
        this.month = month;
    }

    public int getAmount() {
        return amount;
    }

    public String getMonth() {
        return month;
    }
}
