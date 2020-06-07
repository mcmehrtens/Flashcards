package flashcards;

import java.util.ArrayList;
import java.util.Random;

public class CardSet {
    private final ArrayList<Card> cards;
    private final Random rand;

    /**
     * Blank CardSet constructor. It initializes the cards ArrayList to an
     * empty ArrayList and initializes the Random number generator.
     */
    public CardSet() {
        this.cards = new ArrayList<>();
        this.rand = new Random();
    }

    /**
     * CardSet constructor. Initializes the cards ArrayList and adds cards
     * to the ArrayList from the input CardSet.
     *
     * @param cs All cards in this CardSet will be added to the cards instance
     */
    public CardSet(CardSet cs) {
        this.cards = new ArrayList<>();

        for (int i = 0; i < cs.size(); i++) {
            addCard(cs.getCard(i).getTerm(), cs.getCard(i).getDefinition(), cs.getCard(i).getMistakes());
        }

        this.rand = new Random();
    }

    /**
     * Adds a card to the deck. Although cards are custom objects,
     * the fields term and definition are private and final to avoid
     * issues with object references.
     *
     * @param term String input representing the term of the card to be added.
     * @param definition String input representing the definition of the card to be added.
     */
    public void addCard(String term, String definition, int mistakes) {
        cards.add(new Card(term, definition, mistakes));
    }

    /**
     * Extension of the addCard function. Takes a CardSet and adds all the cards in the
     * parameter to THIS CardSet.
     *
     * @param cs CardSet with cards to be added to THIS CardSet.
     */
    public void addCards(CardSet cs) {
        for (int i = 0; i < cs.size(); i++) {
            addCard(cs.getCard(i).getTerm(), cs.getCard(i).getDefinition(), cs.getCard(i).getMistakes());
        }
    }

    /**
     * Removes a card from the deck. Outputs an error to System.out if
     * the card does not exist.
     *
     * @param term String for the term of the card to be removed.
     */
    public void removeCard(String term) {
        if (getCardByTerm(term) == null) {
            System.out.println("Can't remove \"" + term + "\": there is no such card.");
        } else {
            cards.remove(getCardByTerm(term));
        }
    }

    /**
     * Returns a card by index.
     *
     * @param index Index of a card within the cards ArrayList.
     * @return Card The card at the index.
     */
    public Card getCard(int index) {
        return cards.get(index);
    }

    /**
     * Returns a card by term (IGNORES CASE).
     *
     * @param term Term of a card within the cards ArrayList.
     * @return Card The card with the term <b>or</b> null if no card was found.
     */
    public Card getCardByTerm(String term) {
        for (Card c : cards) {
            if (c.getTerm().equalsIgnoreCase(term)) return c;
        }
        return null;
    }

    /**
     * Returns a card by definition (IGNORES CASE).
     *
     * @param definition Definition of a card within the cards ArrayList.
     * @return Card The card with the definition <b>or</b> null if no card was found.
     */
    public Card getCardByDefinition(String definition) {
        for (Card c : cards) {
            if (c.getDefinition().equalsIgnoreCase(definition)) return c;
        }
        return null;
    }

    /**
     * Returns a random card from the cards ArrayList based on
     * a random number generator initialized in the constructor.
     *
     * @return Card Random card from the cards ArrayList.
     */
    public Card getRandomCard() {
        return cards.get(rand.nextInt(size()));
    }

    /**
     * Returns the number of cards in this CardSet.
     *
     * @return int The size of the cards ArrayList.
     */
    public int size() {
        return cards.size();
    }
}
