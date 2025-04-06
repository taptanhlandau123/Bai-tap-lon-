package com.example.expensemanage.model;

public class Data {

    private String note;
    private String id;
    private  String date;

    private int amount;
    private String type;


    public Data(int amount, String type, String id, String note, String date) {
        this.amount = amount;
        this.type = type;
        this.id = id;
        this.note = note;
        this.date = date;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public Data(){

    }

}
