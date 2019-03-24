package com.example.augusto.shellbox_express.model;

public class Balance {

    boolean status;
    double balance;

    public Balance() {

    }

    public Balance(boolean status, double balance){
        this.status = status;
        this.balance = balance;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
