import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class EcoFootprintCalculator {

    // --- Constants for Emission Factors ---
    private static final double EMISSION_CAR_PER_KM = 0.20;
    private static final double EMISSION_BUS_PER_KM = 0.05;
    private static final double EMISSION_ELECTRICITY_PER_KWH = 0.40;

    // Diet impact estimates (kg CO2e per year)
    private static final double MEAT_DIET = 2500.0;
    private static final double MIXED_DIET = 1500.0;
    private static final double VEGETARIAN_DIET = 1000.0;
    private static final double VEGAN_DIET = 700.0;

    // --- Input Utilities ---
    private double getNonNegativeDouble(Scanner scanner, String message) {
        double value = -1;
        while (value < 0) {
            System.out.print(message);
            try {
                value = Double.parseDouble(scanner.nextLine());
                if (value < 0) {
                    System.out.println("Error: Please enter a non-negative number.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Enter a numeric value.");
            }
        }
        return value;
    }

    private int getDietSelection(Scanner scanner) {
        int choice = -1;
        while (choice < 1 || choice > 4) {
            System.out.println("\nSelect Your Diet Type:");
            System.out.println("1) Meat-heavy");
            System.out.println("2) Average");
            System.out.println("3) Vegetarian");
            System.out.println("4) Vegan");
            System.out.print("Enter choice (1-4): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 1 || choice > 4) {
                    System.out.println("Error: Choice must be between 1 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Enter a whole number (1-4).");
            }
        }
        return choice;
    }

    // --- Calculation ---
    public Map<String, Double> computeFootprint(double carKm, double busKm, double electricityKwh, int diet) {
        double transportImpact = carKm * EMISSION_CAR_PER_KM + busKm * EMISSION_BUS_PER_KM;
        double homeImpact = electricityKwh * EMISSION_ELECTRICITY_PER_KWH;
        double dietImpact = switch (diet) {
            case 1 -> MEAT_DIET;
            case 2 -> MIXED_DIET;
            case 3 -> VEGETARIAN_DIET;
            case 4 -> VEGAN_DIET;
            default -> 0.0;
        };

        Map<String, Double> impactBreakdown = new HashMap<>();
        impactBreakdown.put("Transport", transportImpact);
        impactBreakdown.put("Home Energy", homeImpact);
        impactBreakdown.put("Diet", dietImpact);

        return impactBreakdown;
    }

    // --- Recommendation ---
    private void suggestReduction(Map<String, Double> breakdown) {
        String topCategory = "Unknown";
        double maxImpact = 0;

        for (Map.Entry<String, Double> entry : breakdown.entrySet()) {
            if (entry.getValue() > maxImpact) {
                maxImpact = entry.getValue();
                topCategory = entry.getKey();
            }
        }

        System.out.println("\n--- Personalized Tip ---");
        System.out.printf("Highest footprint: %s (%.2f kg CO2e)%n", topCategory, maxImpact);

        switch (topCategory) {
            case "Transport" -> System.out.println("Tip: Walk, bike, or use public transport more often.");
            case "Home Energy" -> System.out.println("Tip: Use energy-efficient appliances and reduce waste.");
            case "Diet" -> System.out.println("Tip: Include more plant-based meals and reduce meat consumption.");
            default -> System.out.println("Tip: Keep monitoring your consumption habits.");
        }
    }

    // --- Main Flow ---
    public void start() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("        🌱 EcoFootprint Calculator       ");

        System.out.println("\n1. Transportation (Annual km)");
        double carKm = getNonNegativeDouble(scanner, "Car distance: ");
        double busKm = getNonNegativeDouble(scanner, "Bus/train distance: ");

        System.out.println("\n2. Home Energy (Annual kWh)");
        double electricity = getNonNegativeDouble(scanner, "Electricity used: ");

        int dietChoice = getDietSelection(scanner);

        Map<String, Double> breakdown = computeFootprint(carKm, busKm, electricity, dietChoice);

        double total = 0;
        for (double value : breakdown.values()) total += value;

        System.out.printf("Total Estimated Footprint: %.2f kg CO2e/year%n", total);

        System.out.println("\nBreakdown by Category:");
        for (Map.Entry<String, Double> entry : breakdown.entrySet()) {
            System.out.printf("%-15s: %.2f kg CO2e%n", entry.getKey(), entry.getValue());
        }

        suggestReduction(breakdown);
        scanner.close();
    }

    public static void main(String[] args) {
        new EcoFootprintCalculator().start();
    }
}