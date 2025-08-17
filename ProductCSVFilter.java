import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ProductCSVFilter {

    public static void main(String[] args) {
        String inputFile = "products.csv";
        String outputFile = "products_over_1000.csv";
        double threshold = 1000.0;

        try {
            // 1) Create a sample products.csv file with name and price
            createSampleCSV(inputFile);

            // 2–5) Read with BufferedReader, split by comma, parse price, filter > 1000, write with FileWriter
            int matched = filterProducts(inputFile, outputFile, threshold);

            // 6) Print success message and verify new file
            File out = new File(outputFile);
            System.out.println("Success ✅  Wrote " + matched + " product(s) with price > " + threshold +
                               " to: " + out.getAbsolutePath());

            System.out.println("\nVerifying contents of " + outputFile + ":");
            printFile(outputFile);

        } catch (IOException e) {
            System.err.println("Something went wrong: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- Creates a sample CSV with header and a few products ---
    private static void createSampleCSV(String fileName) throws IOException {
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write("name,price\n");          // header
            fw.write("Laptop,55000\n");        // > 1000
            fw.write("Phone,899\n");           // <= 1000
            fw.write("Headphones,1299\n");     // > 1000
            fw.write("TV,34999\n");            // > 1000
            fw.write("Book,499\n");            // <= 1000
            fw.write("Camera,45999\n");        // > 1000
            fw.write("Watch,999\n");           // <= 1000
        }
        System.out.println("Sample file created: " + new File(fileName).getAbsolutePath());
    }

    // --- Reads input CSV, filters rows where price > threshold, writes to a new CSV ---
    private static int filterProducts(String inputFile, String outputFile, double threshold) throws IOException {
        int count = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             FileWriter out = new FileWriter(outputFile)) {

            // Write header in the output file
            out.write("name,price\n");

            // Read and discard the header line from input
            String line = br.readLine(); // header
            if (line == null) {
                return 0; // empty file
            }

            // Process data lines
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }

                String name = parts[0].trim();
                String priceStr = parts[1].trim();

                try {
                    double price = Double.parseDouble(priceStr);
                    if (price > threshold) {
                        out.write(name + "," + price + "\n");
                        count++;
                    }
                } catch (NumberFormatException nfe) {
                    System.err.println("Skipping line (invalid price): " + line);
                }
            }
        }

        return count;
    }

    // --- Reads a file and prints its lines to the console for verification ---
    private static void printFile(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}

