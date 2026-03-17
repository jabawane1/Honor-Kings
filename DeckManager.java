import java.util.*;

public class DeckManager{
    private ArrayList<Card> Deck;

    public ArrayList<Card> prepareDeck(){
        Deck = new ArrayList<>();

        String[] values = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        String[] types = {"C","D","H","S"};

        for(String type : types){
            for(String value : values){
                Deck.add(new Card(value, type));
            }
        }

        Collections.shuffle(Deck);
        return Deck;
    }

    public String selectTrumpSuit(){
        char T = '\0';

        for(int i = 0; i < Deck.size(); i++){
            String S = Deck.get(i).toString();
            if(S.charAt(0) == 'K'){
                T = S.charAt(S.length() - 1);
                break;
            }
        }

        HashMap<Character, String> Suit = new HashMap<>();
        Suit.put('C', "Club");
        Suit.put('D', "Diamond");
        Suit.put('H', "Heart");
        Suit.put('S', "Spade");

        String Trump = Suit.get(T);
        return Trump;
    }
}