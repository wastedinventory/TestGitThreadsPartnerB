// Siddharth Nori, 12-10-2025
import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.io.UncheckedIOException;

public class TestProject {

    public static void main(String[] args) {

        // Your two large text files
        Path book1Input  = Paths.get("mobydick.txt");
        Path book1Output = Paths.get("mobydick_output.txt");

        Path book2Input  = Paths.get("warandpeace.txt");     // change name as needed
        Path book2Output = Paths.get("warandpeace_output.txt");

        long startTime = System.nanoTime();

        // Create a pool with 2 threads (one per book)
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // One task per book
        Callable<Void> task1 = createTransformTask(book1Input, book1Output);
        Callable<Void> task2 = createTransformTask(book2Input, book2Output);

        List<Callable<Void>> tasks = Arrays.asList(task1, task2);

        try {
            // Run both tasks in parallel and wait for both to finish
            List<Future<Void>> futures = executor.invokeAll(tasks);

            // Check for exceptions
            for (Future<Void> f : futures) {
                try {
                    f.get(); // will rethrow any exception from the task
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof UncheckedIOException) {
                        ((UncheckedIOException) cause).printStackTrace();
                    } else {
                        e.printStackTrace();
                    }
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        long endTime = System.nanoTime();
        System.out.println("Multi-book parallel computation time: " +
                (endTime - startTime) / 1_000_000 + " ms");
    }

    // Wrap the file transform in a Callable so ExecutorService can run it
    private static Callable<Void> createTransformTask(Path inputFile, Path outputFile) {
        return () -> {
            try {
                transformToOutputFile(inputFile, outputFile);
            } catch (IOException e) {
                // Wrap checked exception so it can travel through Future/ExecutionException
                throw new UncheckedIOException(e);
            }
            return null;
        };
    }

    // Single-threaded transform for one file: input → output
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

    // 🚨 INTENTIONALLY COMPUTATIONALLY EXPENSIVE 🚨
    // Every other character → ASCII → appended to the end of the sentence
    private static String expensiveAsciiAppend(String sentence) {

        String result = "";

        // Wasteful rebuild of original sentence
        for (int i = 0; i < sentence.length(); i++) {
            result = result + sentence.charAt(i);
        }

        // Process every other character
        for (int i = 1; i < sentence.length(); i += 2) {

            // Redundant nested loop
            char target = 0;
            for (int j = 0; j <= i; j++) {
                target = sentence.charAt(j);
            }

            // Redundant ASCII loop
            int ascii = 0;
            for (int k = 0; k < 10; k++) {
                ascii = (int) target;
            }

            // Slow manual int → String
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