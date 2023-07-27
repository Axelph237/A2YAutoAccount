package org.example;

public class Main {
    public static void main(String[] args) {
        OptionsHandler options = new OptionsHandler();
        AccountHandler accounts = new AccountHandler( options );


        ProgramGUI gui = new ProgramGUI("Account Specifications", options, accounts);
    }
}