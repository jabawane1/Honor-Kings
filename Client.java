import java.util.*;
import java.net.*;
import java.io.*;

import java.util.*;

class CardSorter{
    private char trumpChar;
    private HashMap<Character, Integer> HMap;

    public CardSorter(char trump, HashMap<Character, Integer> map){
        trumpChar = trump;
        HMap = map;
    }

    public int countValue(String message){
        char startChar = message.charAt(0);

        int score = 0;
        if(message.length() == 4){
            if(startChar == trumpChar)
                score = 110;
            else
                score = 10;
        }else{
            if(startChar == trumpChar)
                score = 100 + HMap.get(startChar);
            else
                score = HMap.get(startChar);
        }

        return score;
    }

    public void addCardsToDeck(TreeMap<Integer, String> cards, ArrayList<String> deck){
        for(Integer key : cards.keySet())
            deck.add(cards.get(key));
    }

    public ArrayList<String> Sort(ArrayList<String> deck){
        TreeMap<Integer,String> C = new TreeMap<>(Collections.reverseOrder());
        TreeMap<Integer,String> D = new TreeMap<>(Collections.reverseOrder());
        TreeMap<Integer,String> H = new TreeMap<>(Collections.reverseOrder());
        TreeMap<Integer,String> S = new TreeMap<>(Collections.reverseOrder());

        for(String s : deck){
            char L = s.charAt(s.length() - 1);
            switch (L) {
                case 'C': C.put(countValue(s), s); break;
                case 'D': D.put(countValue(s), s); break;
                case 'H': H.put(countValue(s), s); break;
                case 'S': S.put(countValue(s), s); break;
            }
        }

        ArrayList<String> anotherDeck = new ArrayList<>();

        switch (trumpChar) {
            case 'C':
                addCardsToDeck(C, anotherDeck);
                addCardsToDeck(D, anotherDeck);
                addCardsToDeck(H, anotherDeck);
                addCardsToDeck(S, anotherDeck);
                break;
            case 'D':
                addCardsToDeck(D, anotherDeck);
                addCardsToDeck(C, anotherDeck);
                addCardsToDeck(H, anotherDeck);
                addCardsToDeck(S, anotherDeck);
                break;
            case 'H':
                addCardsToDeck(H, anotherDeck);
                addCardsToDeck(C, anotherDeck);
                addCardsToDeck(D, anotherDeck);
                addCardsToDeck(S, anotherDeck);
                break;
            case 'S':
                addCardsToDeck(S, anotherDeck);
                addCardsToDeck(C, anotherDeck);
                addCardsToDeck(D, anotherDeck);
                addCardsToDeck(H, anotherDeck);
                break;
        }

        return anotherDeck;
    }
}

public class Client{
    static CardSorter CS;
    private static String Message; 
    private static BufferedReader BR;

    private static char trumpChar;
    private static HashMap<Character, Integer> HMap = new HashMap<>();

    public static void mapRankValues(){
        for(int i = 2; i <= 10; i++){
            char c = (char)(i + 48);
            HMap.put(c, i);
        }

        HMap.put('J', 11);
        HMap.put('Q', 12);
        HMap.put('K', 13);
        HMap.put('A', 14);
    }

    public static void setReader(Socket sock){
        try{
            BR = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void readAndPrintMessage(){
        try{
            Message = BR.readLine();
            System.out.println("----- " + Message + " -----\n");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        try{
            Scanner scan = new Scanner(System.in);
            
            System.out.println("Enter Your Name : ");
            String username = scan.nextLine();
            System.out.println(username + "\n");

            System.out.println("Enter IP Address of Your Device : ");
            String IP = scan.nextLine();
            System.out.println(IP + "\n");

            Socket sock = new Socket(IP, 6000);
            
            BufferedWriter BF = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            BF.write(username);
            BF.newLine();
            BF.flush();

            setReader(sock);
            readAndPrintMessage(); // Other Players

            mapRankValues();

            for(int i = 0; i < 4; i++){
                Message = BR.readLine();
                System.out.println(Message);
            }
            
            Message = BR.readLine();
            trumpChar = Message.charAt(0);
            System.out.println("\n----- Trump Suit of Game : " + Message + " -----\n"); // Trump Suit of Game - {C, D, H, S}

            CS = new CardSorter(trumpChar, HMap);

            readAndPrintMessage(); // Let's Play

            try{
                for(int i = 0; i < 1; i++){
                    
                    System.out.println("----- Round " + (i + 1) + " -----\n"); // SubRound - I
                    ArrayList<String> Deck = new ArrayList<>();
                    for(int j = 0; j < 13; j++){
                        Message = BR.readLine();
                        Deck.add(Message);
                    }

                    Deck = CS.Sort(Deck);
                    System.out.println(Deck + "\n");
                    
                    Message = BR.readLine(); // Your Commit
                    System.out.println(Message);

                    int commit = scan.nextInt();
                    BF.write(commit + "\n");
                    BF.flush();

                    for(int j = 0; j < 2; j++){
                        System.out.println("\n----- SubRound " + (j + 1) + " -----\n"); // SubRound - J
                        System.out.println(Deck + "\n");

                        for(int k = 0; k < 5; k++){
                            Message = BR.readLine();
                            Integer L = Message.length();
                            
                            System.out.println(Message + "\n"); // Your Turn
                            if(L == 9){
                                int index = scan.nextInt();
                                String s = Deck.get(index);

                                Deck.remove(index);
            
                                BF.write(s + "\n");
                                BF.flush();
                            }
                        }
                        
                        readAndPrintMessage(); // Winner
                    }
                }
                
                readAndPrintMessage(); // Game Over

            }catch(IOException e){
                e.printStackTrace();
            }

            sock.close();
            scan.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}