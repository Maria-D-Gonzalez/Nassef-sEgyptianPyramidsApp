package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Path PHARAOH_JSON_PATH = Path.of(
        "C:\\Users\\maria\\OneDrive\\Documents\\GitHub\\Nassef'sEgyptianPyramidsApp\\pharaoh.json"
    );

    public static void main(String[] args) {
        try (Scanner scan = new Scanner(System.in)) {
            Character command = '_';

            // loop until user quits
            while (command != 'q') {
                printMenu();
                System.out.print("Enter a command: ");
                command = menuGetCommand(scan);

                executeCommand(command);
            }
        }
    }

    //
    // menu functions
    //
    private static void printMenuLine() {
        System.out.println("----------------------------------------------------------");
    }

    private static void printMenuCommand(Character command, String desc) {
        System.out.printf("%s\t%s\n", command, desc);
    }

    // prints the menu
    public static void printMenu() {
        printMenuLine();
        System.out.println("Nassef's Egyptian Pyramids App");
        printMenuLine();

        printMenuCommand('1', "List all the pharaohs");
        printMenuCommand('2', "Display a specific Egyptian pharaoh");
        printMenuCommand('3', "List all the pyramids");
        printMenuCommand('4', "Display specific pyramid");
        printMenuCommand('5', "Display a list of requested pyramids");
        printMenuCommand('q', "Quit");

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
            case '1' -> listPharaohs();
            case '2' -> System.out.println("Displaying a specific pharaoh...");
            case '3' -> System.out.println("Listing all the pyramids...");
            case '4' -> System.out.println("Displaying a specific pyramid...");
            case '5' -> System.out.println("Displaying requested pyramids...");
            case 'q' -> System.out.println("Exiting the program...");
            default -> System.out.println("Invalid command.");
        }
    }

    private static void listPharaohs() {
        try {
            String json = Files.readString(PHARAOH_JSON_PATH);
            Matcher objects = Pattern.compile("\\{([^{}]*)\\}").matcher(json);
            boolean found = false;

            while (objects.find()) {
                String object = objects.group(1);
                Integer id = integerField(object, "id");
                String name = stringField(object, "name");
                Integer begin = integerField(object, "begin");
                Integer end = integerField(object, "end");
                Integer contribution = integerField(object, "contribution");
                String hieroglyphic = stringField(object, "hieroglyphic");

                if (id != null && name != null && begin != null && end != null
                    && contribution != null && hieroglyphic != null) {
                    new Pharaoh(id, name, begin, end, contribution, hieroglyphic).print();
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No pharaoh records found in " + PHARAOH_JSON_PATH);
            }
        } catch (IOException e) {
            System.out.println("Could not read pharaoh data from " + PHARAOH_JSON_PATH
                + ": " + e.getMessage());
        }
    }

    private static Integer integerField(String object, String field) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field)
            + "\"\\s*:\\s*(?:\"(-?\\d+)\"|(-?\\d+))").matcher(object);
        if (!matcher.find()) {
            return null;
        }
        return Integer.valueOf(matcher.group(1) != null ? matcher.group(1) : matcher.group(2));
    }

    private static String stringField(String object, String field) {
        Matcher matcher = Pattern.compile("\"" + Pattern.quote(field)
            + "\"\\s*:\\s*\"((?:\\\\.|[^\"\\\\])*)\"").matcher(object);
        return matcher.find() ? matcher.group(1) : null;
    }
}

// pharoah class, that corresponds to the information in the json file
class Pharaoh {
    // using protected member values, so other classes in this package can have access
    protected Integer id;
    protected String name;
    protected Integer begin;
    protected Integer end;
    protected Integer contribution;
    protected String hieroglyphic;

    // constructor
    public Pharaoh(
        Integer pharaohId,
        String pharaohName,
        Integer pharaohBegin,
        Integer pharaohEnd,
        Integer pharaohContribution,
        String pharaohHieroglyphic
    ) {
        id = pharaohId;
        name = pharaohName;
        begin = pharaohBegin;
        end = pharaohEnd;
        contribution = pharaohContribution;
        hieroglyphic = pharaohHieroglyphic;
    }

    // print pharaoh
    public void print() {
        System.out.printf("Pharaoh %s\n", name);
        System.out.printf("\tid: %d\n", id);
        System.out.printf("\tbegin: %d B.C.\n", begin);
        System.out.printf("\tend: %d B.C.\n", end);
        System.out.printf("\tcontribution: %d gold coins\n", contribution);
        System.out.printf("\thieroglyphic: %s\n", hieroglyphic);
    }
}
// pyramid class, that corresponds to the information in the json file
class Pyramid {

  protected Integer id;
  protected String name;
  protected String[] contributors;

  // constructor
  public Pyramid(
    Integer pyramidId,
    String pyramidName,
    String[] pyramidContributors
  ) {
    id = pyramidId;
    name = pyramidName;
    contributors = pyramidContributors;
  }
}
