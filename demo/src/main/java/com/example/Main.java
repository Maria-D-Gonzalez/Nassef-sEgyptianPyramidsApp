package com.example;

import java.util.Scanner;

public class Main {
  private static float a;
  private static float b;

    public static void main(String[] args) {
        try (Scanner scan = new Scanner(System.in)) {
            Character command = '_';
            
            // loop until user quits
            while (command != 'q') {
                printMenu();
                System.out.print("Enter a command: ");
                command = menuGetCommand(scan);
                
                executeCommand(command);
            }   }
  }

  //
  // menu functions
  //
  private static void printMenuLine() {
    System.out.println(
      "----------------------------------------------------------"
    );
  }

  private static void printMenuCommand(Character command, String desc) {
    System.out.printf("%s\t%s\n", command, desc);
  }

  // prints the menu
  public static void printMenu() {
    printMenuLine();
    System.out.println("ChavviCalc");
    printMenuLine();
    
    printMenuCommand('a', "Enter a value for A"); // asks the user to enter a number for A
    printMenuCommand('b', "Enter a value for B"); // asks the user to enter a number for B
    printMenuCommand('+', "Calculate A + B"); // A gets the value of A plus B in other words A = A + B
    printMenuCommand('-', "Calculate A - B"); // A gets the value of A minus B in other words A = A - B
    printMenuCommand('*', "Calculate A * B"); // A gets the value of A times B in other words A = A * B
    printMenuCommand('/', "Calculate A / B"); // A gets the value of A divided by B in other words A = A / B. dividing by 0 will result in invalid user input 
    printMenuCommand('c', "Clear A and B"); // clears the values of A and B. A gets the value of 0 and B gets the value of 0. in other words A = 0 and B = 0
    printMenuCommand('q', "Quit"); // asks the user to quit the program

    printMenuLine();

  }