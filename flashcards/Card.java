package flashcards;

public final class Card {
    private final String term;
    private final String definition;
    private int mistakes;

    /**
     * Card constructor. Sets the private and final fields:
     * term and definition. These fields, as indicated by the
     * final modifier, cannot be changed. A card object is final.
     *
     * @param term A string of any length representing the term.
     * @param definition A string of any length representing the definition.
     * @param mistakes An integer count of the number of mistakes for this card.
     */
    public Card(String term, String definition, int mistakes) {
        this.term = term;
        this.definition = definition;
        this.mistakes = mistakes;
    }

    /**
     * Gets the term of this card instance.
     *
     * @return String The card's term.
     */
    public String getTerm() {
        return term;
    }

    /**
     * Gets the definition of this card instance.
     *
     * @return String The card's definition.
     */
    public String getDefinition() {
        return definition;
    }

    /**
     * Gets the number of mistakes for this card instance.
     *
     * @return int The number of mistakes.
     */
    public int getMistakes() { return mistakes; }

    /**
     * Sets the number of mistakes for this card instance.
     *
     * @param mistakes Number of mistakes to set.
     */
    public void setMistakes(int mistakes) { this.mistakes = mistakes; }

    /**
     * Function is for debugging purposes. Is not used within
     * program logic.
     *
     * @return String A String representation of the card.
     */
    @Override
    public String toString() {
        return "Term: " + getTerm() + "\n" + "Definition: " + getDefinition();
    }
}
