package com.example;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {
    private static final int PROJECT_COUNT = 100;
    private static final int MAX_LOCATION_LENGTH = 100;
    private static final int MAX_CONTRIBUTOR_NAME_LENGTH = 100;
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(3);
    private static final List<String> PHARAOHS = List.of(
            "Narmer", "Hor-Aha", "Djer", "Djet", "Den", "Anedjib", "Semerkhet", "Qa'a",
            "Hotepsekhemwy", "Raneb", "Ninetjer", "Weneg", "Sened", "Peribsen", "Khasekhemwy",
            "Djoser", "Sekhemkhet", "Khaba", "Huni", "Sneferu", "Khufu", "Djedefre", "Khafre",
            "Menkaure", "Shepseskaf", "Userkaf", "Sahure", "Neferirkare", "Neferefre", "Nyuserre",
            "Menkauhor", "Djedkare Isesi", "Unas", "Teti", "Userkare", "Pepi I", "Merenre I",
            "Pepi II", "Merenre II", "Mentuhotep II", "Mentuhotep III", "Mentuhotep IV",
            "Amenemhat I", "Senusret I", "Amenemhat II", "Senusret II", "Senusret III",
            "Amenemhat III", "Amenemhat IV", "Sobekneferu", "Seqenenre Tao", "Kamose",
            "Ahmose I", "Amenhotep I", "Thutmose I", "Thutmose II", "Hatshepsut", "Thutmose III",
            "Amenhotep II", "Thutmose IV", "Amenhotep III", "Akhenaten", "Smenkhkare",
            "Tutankhamun", "Ay", "Horemheb", "Ramesses I", "Seti I", "Ramesses II",
            "Merenptah", "Amenmesse", "Seti II", "Siptah", "Tausret", "Ramesses III",
            "Ramesses IV", "Ramesses V", "Ramesses VI", "Ramesses VII", "Ramesses VIII",
            "Ramesses IX", "Ramesses X", "Ramesses XI", "Shoshenq I", "Osorkon I",
            "Osorkon II", "Piye", "Shabaka", "Taharqa", "Psamtik I", "Necho II", "Apries",
            "Amasis II", "Psamtik III", "Nectanebo I", "Nectanebo II", "Ptolemy I Soter",
            "Ptolemy II Philadelphus", "Ptolemy III Euergetes", "Ptolemy IV Philopator",
            "Ptolemy V Epiphanes", "Ptolemy VI Philometor", "Ptolemy VIII Physcon",
            "Ptolemy XII Auletes", "Cleopatra VII"
    );

    private final Map<String, Project> projects = new LinkedHashMap<>();
    private final Set<String> requestedPyramids = new LinkedHashSet<>();
    private BigDecimal availableGold = ZERO;

    public Main() {
        for (int i = 1; i <= PROJECT_COUNT; i++) {
            String id = String.format(Locale.ROOT, "P%03d", i);
            projects.put(id, new Project(id, "Unassigned site"));
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        System.out.println("Global Pyramid Gold Ledger (simulation only)");
        System.out.println("Enter 'help' to see commands, or 'quit' to exit.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (!app.execute(line, scanner)) {
                    break;
                }
            }
        }
    }

    private boolean execute(String line, Scanner scanner) {
        String[] parts = line.split("\\s+", 3);
        String command = parts[0].toLowerCase(Locale.ROOT);

        try {
            switch (command) {
                case "1", "pharaohs" -> printPharaohs();
                case "2" -> promptForPharaoh(scanner);
                case "3" -> printPyramidsAndContributors();
                case "4" -> promptForPyramid(scanner);
                case "5" -> printRequestedPyramids();
                case "help" -> printHelp();
                case "status" -> printStatus();
                case "projects" -> printProjects();
                case "fund" -> fund(requireAmount(parts, 2));
                case "contribute" -> contribute(parts);
                case "allocate" -> allocate(parts);
                case "release" -> release(parts);
                case "locate" -> locate(parts);
                case "quit", "exit" -> {
                    System.out.println("Ledger closed. No gold was physically transferred.");
                    return false;
                }
                default -> System.out.println("Unknown command. Enter 'help' to see available commands.");
            }
        } catch (IllegalArgumentException | ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }

    private void printHelp() {
        System.out.println("""
                Commands (gold amounts are in grams; this is an in-memory simulation):
                  1                                  List pharaohs in the historical catalog
                  pharaohs                           List pharaohs in the historical catalog
                  2                                  Prompt for an ID and show a pharaoh's catalog details
                  3                                  List all pyramids and their recorded contributors
                  4                                  Prompt for a pyramid ID and show its contributors and total
                  5                                  List pyramids looked up with command 4, without duplicates
                  contribute <project-id> <grams> <name> Record a named gold contribution for a pyramid
                  fund <grams>                       Add simulated gold to the treasury
                  allocate <project-id> <grams>      Earmark treasury gold for a project
                  release <project-id> <grams>       Return earmarked gold to the treasury
                  locate <project-id> <location>     Set a project's location
                  projects                           List all 100 projects and allocations
                  status                             Show treasury and allocation totals
                  help                               Show this help
                  quit                               Exit the application
                Amounts must be positive and may have up to 3 decimal places.
                Project IDs range from P001 through P100. Allocations do not send or transfer gold.
                """);
    }

    private void printPharaohs() {
        System.out.println("Pharaohs in the historical catalog (" + PHARAOHS.size() + "):");
        for (int i = 0; i < PHARAOHS.size(); i++) {
            System.out.printf("%3d. %s%n", i + 1, PHARAOHS.get(i));
        }
    }

    private void printPyramidsAndContributors() {
        System.out.println("Pyramids and contributors:");
        for (Project project : projects.values()) {
            System.out.printf("%s | %s | allocated %s g%n",
                    project.id, project.location, format(project.allocatedGold));
            if (project.contributions.isEmpty()) {
                System.out.println("  Contributors: none recorded");
                continue;
            }
            for (Map.Entry<String, BigDecimal> contribution : project.contributions.entrySet()) {
                System.out.printf("  Contributor: %s | %s g%n",
                        contribution.getKey(), format(contribution.getValue()));
            }
        }
    }

    private void promptForPharaoh(Scanner scanner) {
        System.out.print("Enter pharaoh ID (1-" + PHARAOHS.size() + "): ");
        if (!scanner.hasNextLine()) {
            System.out.println("No pharaoh ID was provided.");
            return;
        }

        String input = scanner.nextLine().trim();
        final int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid pharaoh ID. Enter a number from 1 to " + PHARAOHS.size() + ".");
            return;
        }
        if (id < 1 || id > PHARAOHS.size()) {
            System.out.println("Unknown pharaoh ID. Enter a number from 1 to " + PHARAOHS.size() + ".");
            return;
        }

        String name = PHARAOHS.get(id - 1);
        System.out.println("Pharaoh ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Historical details: not yet included in this catalog.");
    }

    private void promptForPyramid(Scanner scanner) {
        System.out.print("Enter pyramid ID (1-" + PROJECT_COUNT + "): ");
        if (!scanner.hasNextLine()) {
            System.out.println("No pyramid ID was provided.");
            return;
        }

        String input = scanner.nextLine().trim();
        final int id;
        try {
            id = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid pyramid ID. Enter a number from 1 to " + PROJECT_COUNT + ".");
            return;
        }
        if (id < 1 || id > PROJECT_COUNT) {
            System.out.println("Unknown pyramid ID. Enter a number from 1 to " + PROJECT_COUNT + ".");
            return;
        }

        Project project = projects.get(String.format(Locale.ROOT, "P%03d", id));
        requestedPyramids.add(project.id);
        System.out.println("Pyramid ID: " + id + " (" + project.id + ")");
        System.out.println("Location: " + project.location);
        if (project.contributions.isEmpty()) {
            System.out.println("Contributors: none recorded");
        } else {
            System.out.println("Contributors:");
            for (Map.Entry<String, BigDecimal> contribution : project.contributions.entrySet()) {
                System.out.printf("  %s | %s g%n",
                        contribution.getKey(), format(contribution.getValue()));
            }
        }
        System.out.println("Total contributions: " + format(totalContributions(project)) + " g");
    }

    private void printRequestedPyramids() {
        System.out.println("Requested pyramids (" + requestedPyramids.size() + "):");
        if (requestedPyramids.isEmpty()) {
            System.out.println("  No pyramids have been requested.");
            return;
        }
        for (String id : requestedPyramids) {
            Project project = projects.get(id);
            System.out.printf("%s | %s%n", project.id, project.location);
        }
    }

    private void printStatus() {
        BigDecimal allocatedGold = totalAllocatedGold();
        System.out.println("Available treasury: " + format(availableGold) + " g");
        System.out.println("Allocated to projects: " + format(allocatedGold) + " g");
        System.out.println("Total accounted gold: " + format(availableGold.add(allocatedGold)) + " g");
        System.out.println("Projects: " + projects.size());
    }

    private void printProjects() {
        for (Project project : projects.values()) {
            System.out.printf("%s | %s | allocated %s g%n",
                    project.id, project.location, format(project.allocatedGold));
        }
    }

    private void fund(BigDecimal amount) {
        availableGold = availableGold.add(amount);
        System.out.println("Added " + format(amount) + " g to the simulated treasury.");
    }

    private void contribute(String[] parts) {
        if (parts.length < 3) {
            throw new IllegalArgumentException("Usage: contribute <project-id> <grams> <contributor name>");
        }
        Project project = requireProject(parts[1]);
        String[] contributionParts = parts[2].split("\\s+", 2);
        if (contributionParts.length != 2 || contributionParts[1].isBlank()) {
            throw new IllegalArgumentException("Usage: contribute <project-id> <grams> <contributor name>");
        }
        BigDecimal amount = parseAmount(contributionParts[0]);
        String contributor = contributionParts[1].trim();
        if (contributor.length() > MAX_CONTRIBUTOR_NAME_LENGTH) {
            throw new IllegalArgumentException(
                    "Contributor name must be " + MAX_CONTRIBUTOR_NAME_LENGTH + " characters or fewer.");
        }

        project.allocatedGold = project.allocatedGold.add(amount);
        project.contributions.merge(contributor, amount, BigDecimal::add);
        System.out.println("Recorded " + format(amount) + " g from " + contributor + " for " + project.id + ".");
    }

    private void allocate(String[] parts) {
        if (parts.length < 3) {
            throw new IllegalArgumentException("Usage: allocate <project-id> <grams>");
        }
        Project project = requireProject(parts[1]);
        BigDecimal amount = parseAmount(parts[2]);
        if (availableGold.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient treasury balance.");
        }
        availableGold = availableGold.subtract(amount);
        project.allocatedGold = project.allocatedGold.add(amount);
        System.out.println("Earmarked " + format(amount) + " g for " + project.id + ".");
    }

    private void release(String[] parts) {
        if (parts.length < 3) {
            throw new IllegalArgumentException("Usage: release <project-id> <grams>");
        }
        Project project = requireProject(parts[1]);
        BigDecimal amount = parseAmount(parts[2]);
        if (project.allocatedGold.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient allocation for " + project.id + ".");
        }
        project.allocatedGold = project.allocatedGold.subtract(amount);
        availableGold = availableGold.add(amount);
        System.out.println("Returned " + format(amount) + " g from " + project.id + " to the simulated treasury.");
    }

    private void locate(String[] parts) {
        if (parts.length < 3) {
            throw new IllegalArgumentException("Usage: locate <project-id> <location>");
        }
        String location = parts[2].trim();
        if (location.isBlank()) {
            throw new IllegalArgumentException("Usage: locate <project-id> <location>");
        }
        if (location.length() > MAX_LOCATION_LENGTH) {
            throw new IllegalArgumentException("Location must be " + MAX_LOCATION_LENGTH + " characters or fewer.");
        }
        Project project = requireProject(parts[1]);
        project.location = location;
        System.out.println("Set " + project.id + " location to " + location + ".");
    }

    private Project requireProject(String id) {
        Project project = projects.get(id.toUpperCase(Locale.ROOT));
        if (project == null) {
            throw new IllegalArgumentException("Unknown project ID. Use P001 through P100.");
        }
        return project;
    }

    private BigDecimal requireAmount(String[] parts, int expectedCount) {
        if (parts.length != expectedCount) {
            throw new IllegalArgumentException("Usage: fund <grams>");
        }
        return parseAmount(parts[1]);
    }

    private BigDecimal parseAmount(String input) {
        final BigDecimal amount;
        try {
            amount = new BigDecimal(input);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a valid gold amount in grams.");
        }
        if (amount.scale() > 3) {
            throw new IllegalArgumentException("Gold amounts may have up to 3 decimal places.");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Gold amount must be greater than zero.");
        }
        return amount.setScale(3, RoundingMode.UNNECESSARY);
    }

    private BigDecimal totalAllocatedGold() {
        BigDecimal total = ZERO;
        for (Project project : projects.values()) {
            total = total.add(project.allocatedGold);
        }
        return total;
    }

    private BigDecimal totalContributions(Project project) {
        BigDecimal total = ZERO;
        for (BigDecimal amount : project.contributions.values()) {
            total = total.add(amount);
        }
        return total;
    }

    private String format(BigDecimal amount) {
        return amount.stripTrailingZeros().toPlainString();
    }

    private static final class Project {
        private final String id;
        private String location;
        private BigDecimal allocatedGold = ZERO;
        private final Map<String, BigDecimal> contributions = new LinkedHashMap<>();

        private Project(String id, String location) {
            this.id = id;
            this.location = location;
        }
    }
}
