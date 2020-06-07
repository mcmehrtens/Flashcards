package flashcards;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class FlashcardIOHandler {
    private final CardSet cs;
    private final Scanner scanner;
    private final List<String> log;

    private String finalExportPath; // This should be null unless -export is present at runtime.

    /**
     * Default constructor for FlashcardIOHandler. This class
     * will manage all input by the user.
     */
    public FlashcardIOHandler(Scanner scanner) {
        this.cs = new CardSet();
        this.scanner = scanner;
        this.log = new ArrayList<>();
        finalExportPath = null;
    }

    /**
     * Main logic function processing user input.
     *
     * @param input String from CLI. Passed in from main function.
     */
    public void processInput(String input) {
        input = input.toLowerCase();
        switch(input) {
            case "add":
                addCommand();
                break;
            case "remove":
                removeCommand();
                break;
            case "import":
                importCommand();
                break;
            case "export":
                exportCommand();
                break;
            case "ask":
                askCommand();
                break;
            case "log":
                logCommand();
                break;
            case "hardest card":
                hardestCardCommand();
                break;
            case "reset stats":
                resetStatsCommand();
                break;
            default:
                printText("Invalid input for menu. Please only enter listed menu actions.\n");
        }
    }

    /**
     * Processes the add command from user input. Checks to make sure
     * there are no duplicate cards being added!
     */
    private void addCommand() {
        String term;
        String definition;

        printTextNL("The card:");
        term = getInput();
        if (cs.getCardByTerm(term) != null) {
            printTextNL("The card \"" + term + "\" already exists.");
            return;
        }

        printTextNL("The definition of the card:");
        definition = getInput();
        if (cs.getCardByDefinition(definition) != null) {
            printTextNL("The definition \"" + definition + "\" already exists.");
            return;
        }

        printTextNL("The pair (\"" + term + "\":\"" + definition + "\") has been added.");
        cs.addCard(term, definition, 0);
    }

    /**
     * Processes the remove command from user input. Removes the card
     * or provides user feedback if unsuccessful.
     */
    private void removeCommand() {
        String term;

        printTextNL("The card:");
        term = getInput();

        if (cs.getCardByTerm(term) != null) {
            cs.removeCard(term);
            printTextNL("The card has been removed.");
        } else {
            printTextNL("Can't remove \"" + term + "\": there is no such card.");
        }
    }

    /**
     * Process the import command from user input. Prints an
     * error if the file path input is non-existent.
     */
    private void importCommand() {
        String path;

        printTextNL("File name:");
        path = getInput();

        if (!(new File(path).exists())) {
            printTextNL("File not found.");
        } else {
            printTextNL(importCardsFromFile(path) + " cards have been loaded.");
        }
    }

    /**
     * Processes the export command from user input.
     */
    private void exportCommand() {
        String path;

        printTextNL("File name:");
        path = getInput();

        printTextNL(exportCardsToFile(path) + " cards have been saved.");
    }

    /**
     * Processes the ask command from user input. First of all, the way we get random questions
     * without randomly displaying the same questions over and over again is by creating a CardSet
     * object that has the same cards as the instance of CardSet. Then, we call the getRandomCard()
     * function on the DUPLICATE CardSet. Each time we ask, we remove that card from the duplicate set.
     * Once we've cycled through the deck of flashcards but there are still more questions to ask,
     * we just add all the cards back into the duplicate deck and continue. This ensures every card
     * will get asked once per cycle (just like a normal flashcard deck).
     */
    private void askCommand() {
        CardSet tempCardSet = new CardSet(cs);
        int numQuestions;
        String input;

        printTextNL("How many times to ask?");
        numQuestions = Integer.parseInt(getInput());

        while(numQuestions-- != 0) {
            if (tempCardSet.size() == 0) tempCardSet.addCards(cs);

            Card c = tempCardSet.getRandomCard();

            printTextNL("Print the definition of \"" + c.getTerm() + "\":");
            input = getInput();

            if (input.equalsIgnoreCase(c.getDefinition())) {
                printTextNL("Correct answer");
            } else if (cs.getCardByDefinition(input) != null) {
                printTextNL("Wrong answer. The correct one is \"" + c.getDefinition() + "\", " +
                        "you've just written the definition of \"" + cs.getCardByDefinition(input).getTerm() + "\".");
                cs.getCardByTerm(c.getTerm()).setMistakes(cs.getCardByTerm(c.getTerm()).getMistakes() + 1);
            } else {
                printTextNL("Wrong answer. The correct one is \"" + c.getDefinition() + "\".");
                cs.getCardByTerm(c.getTerm()).setMistakes(cs.getCardByTerm(c.getTerm()).getMistakes() + 1);
            }

            tempCardSet.removeCard(c.getTerm());
        }
    }

    /**
     * Prints a log of all user interactions with the program to a file.
     * First it creates the output String that will be written to a file
     * via a StringBuilder. Then output the log to a file via PrintWriter.
     */
    public void logCommand() {
        printTextNL("File name:");
        File file = new File(getInput());
        StringBuilder output = new StringBuilder();

        for (String s : log) {
            output.append(s);
        }

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.print(output);
        } catch (FileNotFoundException e) {
            printTextNL("ERROR: FileNotFoundException when writing to file.\n" +
                    "Text to write:\n\"" + output + "\"");
            e.printStackTrace();
        }

        printTextNL("The log has been saved.");
    }

    /**
     * Prints the card(s) most often missed to the screen.
     */
    public void hardestCardCommand() {
        ArrayList<Card> hardestCards = new ArrayList<>();
        int mistakes = 0;

        for (int i = 0; i < cs.size(); i++) {
            if (cs.getCard(i).getMistakes() > mistakes) {
                mistakes = cs.getCard(i).getMistakes();
                hardestCards.clear();
                hardestCards.add(cs.getCard(i));
            } else if (cs.getCard(i).getMistakes() != 0 && cs.getCard(i).getMistakes() == mistakes) {
                hardestCards.add(cs.getCard(i));
            }
        }

        if (hardestCards.size() == 0) {
            printTextNL("There are no cards with errors.");
        } else if (hardestCards.size() == 1) {
            printTextNL("The hardest card is \"" + hardestCards.get(0).getTerm() + "\". You have " +
                    mistakes + " errors answering it.");
        } else {
            printText("The hardest cards are ");
            for (int i = 0; i < hardestCards.size() - 1; i++) {
                printText("\"" + hardestCards.get(i).getTerm() + "\", ");
            }
            printTextNL("\"" + hardestCards.get(hardestCards.size() - 1).getTerm() + "\". You have " +
                    mistakes + " errors answering them.");
        }
    }

    /**
     * Sets all card mistakes in the CardSet to 0.
     */
    public void resetStatsCommand() {
        for (int i = 0; i < cs.size(); i++) {
            cs.getCard(i).setMistakes(0);
        }
        printTextNL("Card statistics has been reset.");
    }

    /**
     * Writes cards from an ArrayList of cards to a file. If
     * a file/directory does not exist, it will create one.
     * If a file exists with this name, overwrite the file.
     * Format:
     *
     * TERM:DEFINITION:MISTAKES
     * German:Deutsche:0
     * I am:Ich bin:3
     * ... : ... : ...
     *
     * @param path String Path to a .txt file.
     * @return int Number of cards exported.
     */
    public int exportCardsToFile(String path) {
        int numCardsExported = 0;
        File file = new File(path);
        StringBuilder output = new StringBuilder();
        output.append("TERM:DEFINITION:MISTAKES\n");
        for (int i = 0; i < cs.size(); i++) {
            output.append(cs.getCard(i).getTerm()).append(":").append(cs.getCard(i).getDefinition()).append(":").append(cs.getCard(i).getMistakes()).append("\n");
            numCardsExported++;
        }

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.print(output);
            return numCardsExported;
        } catch (FileNotFoundException e) {
            printTextNL("ERROR: FileNotFoundException when writing to file.\n" +
                    "Text to write:\n\"" + output + "\"");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Read cards from a formatted file. Adds them line by line
     * to the provided CardSet. Updates any already existing
     * cards if terms are equal but definitions are not. Skips
     * duplicate entries.
     *
     * TERM:DEFINITION
     * German:Deutsche
     * I am:Ich bin
     * ... : ...
     *
     * @param path String Path to a .txt file.
     * @return int Number of cards imported.
     */
    public int importCardsFromFile(String path) {
        File file = new File(path);
        int numCardsImported = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine();

            String line;
            String[] lineArray;
            while((line = br.readLine()) != null) {
                lineArray = line.split(":");

                // Check to see if a card exists by term and then check if definitions are not equal.
                // If they are not equal, update the current entry.
                if (cs.getCardByTerm(lineArray[0]) != null &&
                        !cs.getCardByTerm(lineArray[0]).getDefinition().equalsIgnoreCase(lineArray[1])) {
                    cs.removeCard(lineArray[0]);

                    // Check to see if a card exists by term and then check if definitions are equal.
                    // If they are equal, skip this entry in the file (no duplicates).
                } else if (cs.getCardByTerm(lineArray[0]) != null &&
                        cs.getCardByTerm(lineArray[0]).getDefinition().equalsIgnoreCase(lineArray[1])) {
                    numCardsImported++;
                    continue;

                    // Check to see if a card exists by definition.
                    // If one does, skip (no duplicates).
                    // NOTE: this was guaranteed in the problem desc. to not happen, but just in case.
                } else if (cs.getCardByDefinition(lineArray[1]) != null) {
                    continue;
                }

                cs.addCard(lineArray[0], lineArray[1], Integer.parseInt(lineArray[2]));
                numCardsImported++;
            }
        } catch (FileNotFoundException e) {
            printTextNL("ERROR: FileNotFoundException occurred. Import incomplete/unsuccessful.");
            e.printStackTrace();
        } catch (IOException e) {
            printTextNL("ERROR: IOException occurred. Import incomplete/unsuccessful.");
            e.printStackTrace();
        }

        return numCardsImported;
    }

    /**
     * Displays text to the console and stores the output lines
     * in the log file.
     *
     * @param s String to print to console.
     */
    public void printText(String s) {
        System.out.print(s);
        log.add(s);
    }

    /**
     * Prints a new line to the screen and stores the new line in log.
     */
    public void printNewLine() {
        log.add("\n");
        System.out.println();
    }

    /**
     * Displays text to the console and stores the output lines
     * in the log file. Adds a newline character to the end of the String.
     *
     * @param s String to print to console.
     */
    public void printTextNL(String s) {
        printText(s);
        printNewLine();
    }

    /**
     * Prints the menu to System.out.
     */
    public void printMenu() {
        printText("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):\n");
    }

    /**
     * Gets input from the scanner stores the input lines in
     * the log file.
     *
     * @return String Returns the String the user inputted.
     */
    public String getInput() {
        String s = scanner.nextLine().trim();
        log.add(s + "\n");
        return s;
    }

    /**
     * Default setter for the finalExportPath.
     *
     * @param s Path for final export.
     */
    public void setFinalExportPath(String s) {
        finalExportPath = s;
    }

    /**
     * Default getter for the finalExportPath.
     *
     * @return String The final export path associated with this Handler.
     */
    public String getFinalExportPath() {
        return finalExportPath;
    }
}
