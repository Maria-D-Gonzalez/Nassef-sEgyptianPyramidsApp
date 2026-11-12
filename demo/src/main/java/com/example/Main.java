package com.example;

import java.util.Scanner;

public class Main {
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
    System.out.println("Nassef's Egyptian Pyramids App");
    printMenuLine();
    
    printMenuCommand('1', "List all the pharaohs"); // asks the user to enter a number for A
    printMenuCommand('2', "Display a specific Egyptian pharaoh"); // asks the user to enter a number for B
    printMenuCommand('3', "List all the pyramids"); // List all the pyramids and the contributors associated with them
    printMenuCommand('4', "Display specific pyramid"); // Displays information for specific pyramid information by asking the user for a pyramid id number
    printMenuCommand('5', "Display a list of requested pyramids"); // if the user gets information on the same pyramid multiple times, this report only prints each pyramid once
    printMenuCommand('q', "Quit"); // asks the user to quit the program

    printMenuLine();

  }

  private static Character menuGetCommand(Scanner scan) {
    String input = scan.nextLine().trim();
    if (input.isEmpty()) {
      return '_';
    }
    return input.charAt(0);
  }

  private static void executeCommand(Character command) {
    switch (command) {
      case '1' -> System.out.println("Listing all the pharaohs...");
      case '2' -> System.out.println("Displaying a specific pharaoh...");
      case '3' -> System.out.println("Listing all the pyramids...");
      case '4' -> System.out.println("Displaying a specific pyramid...");
      case '5' -> System.out.println("Displaying requested pyramids...");
      case 'q' -> System.out.println("Exiting the program...");
      default -> System.out.println("Invalid command.");
    }
  }
}