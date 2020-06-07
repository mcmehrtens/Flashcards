package flashcards;

import java.util.Scanner;

/**
 * <h1>Flashcards</h1>
 * The Flashcards program reads and writes flashcards to a file,
 * provide a CLI interface for adding or removing cards and testing
 * the user with the cached flashcards.
 *
 * @author Matthew Mehrtens
 * @version 2.0
 * @since 6-7-2020
 */
public class Main {

    /**
     * Main method for the Flashcards program. Runs the main
     * CLI program loop.
     *
     * @param args -import and -export commands used to automatically import and export CardSets.
     */
    public static void main(String[] args) {
        // Create scanner and a FlashcardIOHandler object
        Scanner scanner = new Scanner(System.in);
        FlashcardIOHandler handler = new FlashcardIOHandler(scanner);

        // Process run arguments
        processArgs(args, handler);

        // Print the first menu and get first input
        handler.printMenu();
        String input = handler.getInput();

        /* This method is the main loop. Pass all the input to the IOHandler. */
        while(!input.equalsIgnoreCase("exit")) {
            handler.processInput(input);

            handler.printNewLine();
            handler.printMenu();

            input = handler.getInput();
        }

        // Say goodbye!
        handler.printTextNL("Bye bye!");

        // Check to see if a final export needs to happen!
        if (handler.getFinalExportPath() != null) {
            handler.printTextNL(handler.exportCardsToFile(handler.getFinalExportPath()) + " cards have been saved.");
        }

        // Clean up some memory.
        scanner.close();
    }

    /**
     * Processed the arguments from start time. We're looking for
     * -import or -export.
     *
     * @param args Arguments from start time.
     * @param handler The handler in reference.
     */
    private static void processArgs(String[] args, FlashcardIOHandler handler) {
        // Loop through every argument in the program arguments array
        for (int i = 0; i < args.length; i++) {
            // If the user does not include a value after a keyword, skip it.
            // Skip logic if the argument is a keyword
            if (args[i].toCharArray()[0] == '-' && args[i + 1].toCharArray()[0] == '-') continue;
            if (args[i].toCharArray()[0] != '-') continue;

            switch (args[i].toLowerCase()) {
                case "-import":
                    handler.printTextNL(handler.importCardsFromFile(args[i + 1]) + " cards have been loaded.");
                    handler.printNewLine();
                    break;
                case "-export":
                    handler.setFinalExportPath(args[i + 1]);
                    break;
                default:
                    handler.printTextNL("ERROR: Invalid argument. Program may not run as expected.");
            }
        }
    }
}
