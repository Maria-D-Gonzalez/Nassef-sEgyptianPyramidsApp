package com.example;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Scanner;

public class Main {

  // I've used two arrays here for O(1) reading of the pharaohs and pyramids.
  // other structures or additional structures can be used
  private Pharaoh[] pharaohArray;
  private Pyramid[] pyramidArray;
  private final LinkedHashMap<Integer, Pyramid> requestedPyramids = new LinkedHashMap<>();

  public static void main(String[] args) {
    Main app = new Main();
    app.start();
  }

  // main loop for app
  public void start() {
    Scanner scan = new Scanner(System.in);
    char command = '_';

    while (command != 'q') {
      printMenu();
      System.out.print("Enter a command: ");
      command = menuGetCommand(scan);
      executeCommand(command, scan);
    }
  }

  // constructor to initialize the app and read commands
  public Main() {
    String pharaohFile = findJsonFile("pharaoh.json");
    JSONArray pharaohJSONArray = JSONFile.readArray(pharaohFile);
    initializePharaoh(pharaohJSONArray);

    String pyramidFile = findJsonFile("pyramid.json");
    JSONArray pyramidJSONArray = JSONFile.readArray(pyramidFile);
    initializePyramid(pyramidJSONArray);
  }

  private static String findJsonFile(String fileName) {
    String[] candidatePaths = {
      "src/main/resources/" + fileName,
      "demo/src/main/resources/" + fileName,
      "src/main/java/com/example/" + fileName,
      "demo/src/main/java/com/example/" + fileName,
      fileName
    };

    for (String path : candidatePaths) {
      java.io.File file = new java.io.File(path);
      if (file.exists()) {
        return path;
      }
    }

    return fileName;
  }

  private void initializePharaoh(JSONArray pharaohJSONArray) {
    if (pharaohJSONArray == null) {
      pharaohArray = new Pharaoh[0];
      return;
    }

    pharaohArray = new Pharaoh[pharaohJSONArray.size()];

    for (int i = 0; i < pharaohJSONArray.size(); i++) {
      JSONObject o = (JSONObject) pharaohJSONArray.get(i);

      Integer id = toInteger(o, "id");
      String name = (String) o.get("name");
      Integer begin = toInteger(o, "begin");
      Integer end = toInteger(o, "end");
      Integer contribution = toInteger(o, "contribution");
      Integer gold = toInteger(o, "gold");
      String hieroglyphic = (String) o.get("hieroglyphic");

      pharaohArray[i] = new Pharaoh(id, name, begin, end, contribution, gold, hieroglyphic);
    }
  }

  private void initializePyramid(JSONArray pyramidJSONArray) {
    if (pyramidJSONArray == null) {
      pyramidArray = new Pyramid[0];
      return;
    }

    pyramidArray = new Pyramid[pyramidJSONArray.size()];

    for (int i = 0; i < pyramidJSONArray.size(); i++) {
      JSONObject o = (JSONObject) pyramidJSONArray.get(i);

      Integer id = toInteger(o, "id");
      String name = (String) o.get("name");
      JSONArray contributorsJSONArray = (JSONArray) o.get("contributors");
      PyramidContributor[] contributors = new PyramidContributor[contributorsJSONArray == null ? 0 : contributorsJSONArray.size()];
      if (contributorsJSONArray != null) {
        for (int j = 0; j < contributorsJSONArray.size(); j++) {
          Object contributor = contributorsJSONArray.get(j);
          if (contributor instanceof JSONObject contributorObject) {
            Object contributorName = contributorObject.get("name");
            String contributorNameString = contributorName == null ? null : contributorName.toString();
            Pharaoh pharaoh = findPharaohByName(contributorNameString);
            Integer gold = toInteger(contributorObject, "gold");
            Integer totalContribution = toInteger(contributorObject, "totalContribution");
            if (totalContribution == null) {
              totalContribution = toInteger(contributorObject, "contribution");
            }
            if (pharaoh != null) {
              if (gold == null) gold = pharaoh.getGold();
              if (totalContribution == null) totalContribution = pharaoh.getContribution();
            }
            contributors[j] = new PyramidContributor(contributorNameString, gold, totalContribution);
          } else {
            String contributorName = contributor == null ? null : contributor.toString();
            Pharaoh pharaoh = findPharaohByName(contributorName);
            contributors[j] = new PyramidContributor(
              contributorName,
              pharaoh == null ? null : pharaoh.getGold(),
              pharaoh == null ? null : pharaoh.getContribution()
            );
          }
        }
      }

      pyramidArray[i] = new Pyramid(id, name, contributors);
    }
  }

  private Integer toInteger(JSONObject o, String key) {
    Object value = o.get(key);
    return value == null ? null : Integer.valueOf(value.toString());
  }

  private Pharaoh findPharaohByName(String name) {
    if (name == null) return null;
    for (Pharaoh pharaoh : pharaohArray) {
      if (name.equalsIgnoreCase(pharaoh.getName())) return pharaoh;
    }
    return null;
  }

  private static Character menuGetCommand(Scanner scan) {
    Character command = '_';
    String rawInput = scan.nextLine();

    if (rawInput.length() > 0) {
      command = Character.toLowerCase(rawInput.charAt(0));
    }

    return command;
  }

  private void printAllPharaoh() {
    for (Pharaoh pharaoh : pharaohArray) {
      printMenuLine();
      pharaoh.print();
      printMenuLine();
    }
  }

  private void printAllPyramids() {
    for (Pyramid pyramid : pyramidArray) {
      printMenuLine();
      pyramid.print();
      printMenuLine();
    }
  }

  private void printPyramidById(Scanner scan) {
    System.out.print("Enter a pyramid ID: ");
    String rawId = scan.nextLine().trim();
    int id;
    try {
      id = Integer.parseInt(rawId);
    } catch (NumberFormatException e) {
      System.out.println("ERROR: Invalid pyramid ID");
      return;
    }

    for (Pyramid pyramid : pyramidArray) {
      if (pyramid.getId() != null && pyramid.getId().equals(id)) {
        requestedPyramids.put(pyramid.getId(), pyramid);
        printMenuLine();
        pyramid.printDetails();
        printMenuLine();
        return;
      }
    }
    System.out.println("No pyramid found with ID: " + id);
  }

  private void printRequestedPyramids() {
    if (requestedPyramids.isEmpty()) {
      System.out.println("No pyramids have been requested.");
      return;
    }

    for (Pyramid pyramid : requestedPyramids.values()) {
      printMenuLine();
      pyramid.print();
      printMenuLine();
    }
  }

  private void printPharaohById(Scanner scan) {
    System.out.print("Enter a pharaoh ID: ");
    String rawId = scan.nextLine().trim();
    int id;
    try {
      id = Integer.parseInt(rawId);
    } catch (NumberFormatException e) {
      System.out.println("ERROR: Invalid pharaoh ID");
      return;
    }

    for (Pharaoh pharaoh : pharaohArray) {
      if (pharaoh.getId() != null && pharaoh.getId().equals(id)) {
        printMenuLine();
        pharaoh.print();
        printMenuLine();
        return;
      }
    }
    System.out.println("No pharaoh found with ID: " + id);
  }

  private boolean executeCommand(Character command, Scanner scan) {
    boolean success = true;

    switch (command) {
      case '1' -> printAllPharaoh();
      case '2' -> printPharaohById(scan);
      case '3' -> printAllPyramids();
      case '4' -> printPyramidById(scan);
      case '5' -> printRequestedPyramids();
      case 'q' -> System.out.println("Thank you for using Nassef's Egyptian Pyramid App!");
      default -> {
        System.out.println("ERROR: Unknown command");
        success = false;
      }
    }

    return success;
  }

  private static void printMenuCommand(char command, String desc) {
    System.out.printf("%s\t\t%s\n", command, desc);
  }

  private static void printMenuLine() {
    System.out.println(
      "--------------------------------------------------------------------------"
    );
  }

  public static void printMenu() {
    printMenuLine();
    System.out.println("Nassef's Egyptian Pyramids App");
    printMenuLine();
    System.out.printf("Command\t\tDescription\n");
    System.out.printf("-------\t\t---------------------------------------\n");
    printMenuCommand('1', "List all the pharaohs");
    printMenuCommand('2', "Display a pharaoh by ID");
    printMenuCommand('3', "List all the pyramids and their contributors");
    printMenuCommand('4', "Display a pyramid and its contributor details by ID");
    printMenuCommand('5', "Report requested pyramids without duplicates");
    printMenuCommand('q', "Quit");
    printMenuLine();
  }
}

class Pharaoh {
  private final Integer id;
  private final String name;
  private final Integer begin;
  private final Integer end;
  private final Integer contribution;
  private final Integer gold;
  private final String hieroglyphic;

  public Pharaoh(Integer id, String name, Integer begin, Integer end, Integer contribution, Integer gold, String hieroglyphic) {
    this.id = id;
    this.name = name;
    this.begin = begin;
    this.end = end;
    this.contribution = contribution;
    this.gold = gold;
    this.hieroglyphic = hieroglyphic;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Integer getContribution() {
    return contribution;
  }

  public Integer getGold() {
    return gold;
  }

  public void print() {
    System.out.printf(
      "ID: %d, Name: %s, Reign: %d-%d, Contribution: %d, Hieroglyphic: %s%n",
      id, name, begin, end, contribution, hieroglyphic
    );
  }
}

class Pyramid {
  private final Integer id;
  private final String name;
  private final PyramidContributor[] contributors;

  public Pyramid(Integer id, String name, PyramidContributor[] contributors) {
    this.id = id;
    this.name = name;
    this.contributors = contributors;
  }

  public void print() {
    String[] contributorNames = new String[contributors.length];
    for (int i = 0; i < contributors.length; i++) {
      contributorNames[i] = contributors[i].getName();
    }
    System.out.printf("ID: %d, Name: %s, Contributors: %s%n", id, name, java.util.Arrays.toString(contributorNames));
  }

  public Integer getId() {
    return id;
  }

  public void printDetails() {
    System.out.printf("ID: %s, Name: %s%n", id, name);
    for (PyramidContributor contributor : contributors) {
      System.out.printf(
        "Contributor: %s, Gold: %s, Total contribution: %s%n",
        contributor.getName(), contributor.getGold(), contributor.getTotalContribution()
      );
    }
  }
}

class PyramidContributor {
  private final String name;
  private final Integer gold;
  private final Integer totalContribution;

  public PyramidContributor(String name, Integer gold, Integer totalContribution) {
    this.name = name;
    this.gold = gold;
    this.totalContribution = totalContribution;
  }

  public String getName() {
    return name;
  }

  public Integer getGold() {
    return gold;
  }

  public Integer getTotalContribution() {
    return totalContribution;
  }
}

class JSONFile {
  public static JSONArray readArray(String fileName) {
    JSONParser jsonParser = new JSONParser();

    try (FileReader reader = new FileReader(fileName)) {
      Object obj = jsonParser.parse(reader);
      if (!(obj instanceof JSONArray)) {
        throw new IllegalStateException("JSON file does not contain an array: " + fileName);
      }
      return (JSONArray) obj;
    } catch (IOException | ParseException e) {
      throw new IllegalStateException("Unable to read JSON file: " + fileName, e);
    }
  }
}

// Small self-contained JSON reader so this file does not require an external library.
class JSONArray extends ArrayList<Object> {}

class JSONObject extends HashMap<String, Object> {}

class ParseException extends Exception {
  ParseException(String message) {
    super(message);
  }
}

class JSONParser {
  private String text;
  private int position;

  public Object parse(Reader reader) throws IOException, ParseException {
    StringBuilder contents = new StringBuilder();
    char[] buffer = new char[2048];
    int count;
    while ((count = reader.read(buffer)) != -1) {
      contents.append(buffer, 0, count);
    }
    text = contents.toString();
    position = 0;
    Object result = parseValue();
    skipWhitespace();
    if (position != text.length()) {
      throw error("Unexpected trailing content");
    }
    return result;
  }

  private Object parseValue() throws ParseException {
    skipWhitespace();
    if (position >= text.length()) throw error("Unexpected end of input");
    char current = text.charAt(position);
    if (current == '{') return parseObject();
    if (current == '[') return parseArray();
    if (current == '"') return parseString();
    if (current == 't') return parseLiteral("true", Boolean.TRUE);
    if (current == 'f') return parseLiteral("false", Boolean.FALSE);
    if (current == 'n') return parseLiteral("null", null);
    if (current == '-' || Character.isDigit(current)) return parseNumber();
    throw error("Unexpected character");
  }

  private JSONObject parseObject() throws ParseException {
    JSONObject object = new JSONObject();
    position++;
    skipWhitespace();
    if (consume('}')) return object;
    while (true) {
      skipWhitespace();
      if (position >= text.length() || text.charAt(position) != '"') {
        throw error("Expected an object key");
      }
      String key = parseString();
      skipWhitespace();
      expect(':');
      object.put(key, parseValue());
      skipWhitespace();
      if (consume('}')) return object;
      expect(',');
    }
  }

  private JSONArray parseArray() throws ParseException {
    JSONArray array = new JSONArray();
    position++;
    skipWhitespace();
    if (consume(']')) return array;
    while (true) {
      array.add(parseValue());
      skipWhitespace();
      if (consume(']')) return array;
      expect(',');
    }
  }

  private String parseString() throws ParseException {
    expect('"');
    StringBuilder value = new StringBuilder();
    while (position < text.length()) {
      char current = text.charAt(position++);
      if (current == '"') return value.toString();
      if (current == '\\') {
        if (position >= text.length()) throw error("Incomplete escape sequence");
        char escaped = text.charAt(position++);
        switch (escaped) {
          case '"' -> value.append(escaped);
          case '\\' -> value.append(escaped);
          case '/' -> value.append(escaped);
          case 'b' -> value.append('\b');
          case 'f' -> value.append('\f');
          case 'n' -> value.append('\n');
          case 'r' -> value.append('\r');
          case 't' -> value.append('\t');
          case 'u' -> {
            if (position + 4 > text.length()) throw error("Incomplete unicode escape");
            try {
              value.append((char) Integer.parseInt(text.substring(position, position + 4), 16));
            } catch (NumberFormatException e) {
              throw error("Invalid unicode escape");
            }
            position += 4;
          }
          default -> throw error("Invalid escape sequence");
        }
      } else {
        if (current < 0x20) throw error("Unescaped control character");
        value.append(current);
      }
    }
    throw error("Unterminated string");
  }

  private Object parseNumber() throws ParseException {
    int start = position;
    consume('-');
    if (consume('0')) {
      if (position < text.length() && Character.isDigit(text.charAt(position))) {
        throw error("Leading zero in number");
      }
    } else {
      consumeDigits();
    }
    boolean decimal = false;
    if (consume('.')) {
      decimal = true;
      consumeDigits();
    }
    if (position < text.length() && (text.charAt(position) == 'e' || text.charAt(position) == 'E')) {
      decimal = true;
      position++;
      if (!consume('+')) consume('-');
      consumeDigits();
    }
    String number = text.substring(start, position);
    try {
      return decimal ? Double.valueOf(number) : Long.valueOf(number);
    } catch (NumberFormatException e) {
      throw error("Invalid number");
    }
  }

  private void consumeDigits() throws ParseException {
    int start = position;
    while (position < text.length() && Character.isDigit(text.charAt(position))) position++;
    if (start == position) throw error("Expected a digit");
  }

  private Object parseLiteral(String literal, Object value) throws ParseException {
    if (!text.startsWith(literal, position)) throw error("Invalid literal");
    position += literal.length();
    return value;
  }

  private void skipWhitespace() {
    while (position < text.length() && Character.isWhitespace(text.charAt(position))) position++;
  }

  private boolean consume(char expected) {
    if (position < text.length() && text.charAt(position) == expected) {
      position++;
      return true;
    }
    return false;
  }

  private void expect(char expected) throws ParseException {
    if (!consume(expected)) throw error("Expected '" + expected + "'");
  }

  private ParseException error(String message) {
    return new ParseException(message + " at position " + position);
  }
}
