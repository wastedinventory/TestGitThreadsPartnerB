// Siddharth Nori, 12-10-2025

//Synopsis of code purpose: This code looks at every other character, turns it into an ascii, adds it to a string, and adds that ascii string to the end of the sentence for every sentence in the book.
import java.io.*;
import java.nio.file.*;

public class TestProject {

    public static void main(String[] args) {

        Path inputFile = Paths.get("mobydick.txt");
        Path outputFile = Paths.get("mobydick_output.txt");
		Path inputFile2 = Paths.get("warandpeace.txt");
		Path outputFile2 = Paths.get("warandpeace_output.txt");

        long startTime = System.nanoTime();

        try {
            transformToOutputFile(inputFile, outputFile);
            transformToOutputFile(inputFile2, outputFile2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Single Threaded Computation Time: " +
                (System.nanoTime() - startTime) / 1_000_000 + " ms");
    }

    // ✅ Reads original file, writes transformed output to a NEW FILE
    public static void transformToOutputFile(Path inputFile, Path outputFile)
            throws IOException {

        try (
            BufferedReader reader = Files.newBufferedReader(inputFile);
            BufferedWriter writer = Files.newBufferedWriter(
                    outputFile,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            )
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                String modified = expensiveAsciiAppend(line);
                writer.write(modified);
                writer.newLine();
            }
        }
    }

    private static String expensiveAsciiAppend(String sentence) {

        String result = "";

        // Wasteful character rebuild
        for (int i = 0; i < sentence.length(); i++) {
            result = result + sentence.charAt(i);
        }

        // Every other character → ASCII → append
        for (int i = 1; i < sentence.length(); i += 2) {

            char target = 0;
            for (int j = 0; j <= i; j++) {
                target = sentence.charAt(j);
            }

            int ascii = 0;
            for (int k = 0; k < 10; k++) {
                ascii = (int) target;
            }

            String asciiStr = "";
            String temp = Integer.toString(ascii);
            for (int m = 0; m < temp.length(); m++) {
                asciiStr = asciiStr + temp.charAt(m);
            }

            result = result + asciiStr;
        }

        return result;
    }
}
