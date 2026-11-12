package com.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    private static final Path PHARAOH_JSON_PATH = Path.of(
        "C:\\Users\\maria\\OneDrive\\Documents\\GitHub\\Nassef'sEgyptianPyramidsApp\\pharaoh.json"
    );
    private static final Path PYRAMID_JSON_PATH = PHARAOH_JSON_PATH.resolveSibling("pyramid.json");
    private static final List<Pyramid> requestedPyramids = new ArrayList<>();

    public static void main(String[] args) {
        try (Scanner scan = new Scanner(System.in)) {
            Character command = '_';

            // loop until user quits
            while (command != 'q') {
                printMenu();
                System.out.print("Enter a command: ");
                command = menuGetCommand(scan);

                executeCommand(command, scan);
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

    private static void executeCommand(Character command, Scanner scan) {
        switch (command) {
            case '1' -> listPharaohs();
            case '2' -> displaySpecificPharaoh(scan);
            case '3' -> listPyramids();
            case '4' -> displaySpecificPyramid(scan);
            case '5' -> listRequestedPyramids();
            case 'q' -> System.out.println("Exiting the program...");
            default -> System.out.println("Invalid command.");
        }
    }

    private static void listPharaohs() {
        try {
            List<Pharaoh> pharaohs = readPharaohs();
            if (pharaohs.isEmpty()) {
                System.out.println("No pharaoh records found in " + PHARAOH_JSON_PATH);
                return;
            }
            for (Pharaoh pharaoh : pharaohs) {
                pharaoh.print();
            }
        } catch (IOException e) {
            System.out.println("Could not read pharaoh data from " + PHARAOH_JSON_PATH
                + ": " + e.getMessage());
        }
    }

    private static void displaySpecificPharaoh(Scanner scan) {
        System.out.print("Enter pharaoh ID: ");
        String input = scan.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid pharaoh ID. Please enter a number.");
            return;
        }

        try {
            for (Pharaoh pharaoh : readPharaohs()) {
                if (pharaoh.id == id) {
                    pharaoh.print();
                    return;
                }
            }
            System.out.println("No pharaoh found with ID " + id + ".");
        } catch (IOException e) {
            System.out.println("Could not read pharaoh data from " + PHARAOH_JSON_PATH
                + ": " + e.getMessage());
        }
    }

    private static List<Pharaoh> readPharaohs() throws IOException {
        String json = Files.readString(PHARAOH_JSON_PATH);
        Matcher objects = Pattern.compile("\\{([^{}]*)\\}").matcher(json);
        List<Pharaoh> pharaohs = new ArrayList<>();

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
                pharaohs.add(new Pharaoh(id, name, begin, end, contribution, hieroglyphic));
            }
        }
        return pharaohs;
    }

    private static void listPyramids() {
        try {
            List<Pyramid> pyramids = readPyramids();
            if (pyramids.isEmpty()) {
                System.out.println("No pyramid records found in " + PYRAMID_JSON_PATH);
                return;
            }
            for (Pyramid pyramid : pyramids) {
                pyramid.print();
            }
        } catch (IOException e) {
            System.out.println("Could not read pyramid data from " + PYRAMID_JSON_PATH
                + ": " + e.getMessage());
        }
    }

    private static void displaySpecificPyramid(Scanner scan) {
        System.out.print("Enter pyramid ID: ");
        String input = scan.nextLine().trim();
        int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid pyramid ID. Please enter a number.");
            return;
        }

        try {
            for (Pyramid pyramid : readPyramids()) {
                if (pyramid.id == id) {
                    recordPyramidRequest(pyramid);
                    pyramid.print();
                    return;
                }
            }
            System.out.println("No pyramid found with ID " + id + ".");
        } catch (IOException e) {
            System.out.println("Could not read pyramid data from " + PYRAMID_JSON_PATH
                + ": " + e.getMessage());
        }
    }

    private static void recordPyramidRequest(Pyramid pyramid) {
        for (Pyramid requested : requestedPyramids) {
            if (requested.id.equals(pyramid.id)) {
                return;
            }
        }
        requestedPyramids.add(pyramid);
    }

    private static void listRequestedPyramids() {
        if (requestedPyramids.isEmpty()) {
            System.out.println("No pyramids have been requested yet.");
            return;
        }

        System.out.println("Requested pyramids:");
        for (Pyramid pyramid : requestedPyramids) {
            pyramid.print();
        }
    }

    private static List<Pyramid> readPyramids() throws IOException {
        String json = Files.readString(PYRAMID_JSON_PATH);
        Matcher objects = Pattern.compile("\\{([^{}]*)\\}").matcher(json);
        List<Pyramid> pyramids = new ArrayList<>();

        while (objects.find()) {
            String object = objects.group(1);
            Integer id = integerField(object, "id");
            String name = stringField(object, "name");
            String[] contributors = stringArrayField(object, "contributors");

            if (id != null && name != null && contributors != null) {
                pyramids.add(new Pyramid(id, name, contributors));
            }
        }
        return pyramids;
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

    private static String[] stringArrayField(String object, String field) {
        Matcher array = Pattern.compile("\"" + Pattern.quote(field)
            + "\"\\s*:\\s*\\[([^\\]]*)\\]").matcher(object);
        if (!array.find()) {
            return null;
        }

        Matcher values = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"").matcher(array.group(1));
        List<String> strings = new ArrayList<>();
        while (values.find()) {
            strings.add(values.group(1));
        }
        return strings.toArray(new String[0]);
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

    public Pyramid(Integer pyramidId, String pyramidName, String[] pyramidContributors) {
        id = pyramidId;
        name = pyramidName;
        contributors = pyramidContributors;
    }

    public void print() {
        System.out.printf("Pyramid %s%n", name);
        System.out.printf("\tid: %d%n", id);
        System.out.println("\tcontributors:");
        for (String contributor : contributors) {
            System.out.printf("\t\t%s%n", contributor);
        }
    }
}
