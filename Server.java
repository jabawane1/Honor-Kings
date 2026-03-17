import java.util.*;
import java.net.*;
import java.io.*;

class Server{
    private ServerSocket serverSocket;

    private Socket[] ClientSocks = new Socket[4];
    private String[] ClientUsers = new String[4];

    private Random random = new Random();

    private char trumpChar, firstTurn;
    private String Trump, CommonString;

    private ArrayList<Card> newDeck;
    private ArrayList<ArrayList<Card>> MainDeck;

    private HashMap<Character, Integer> HMap = new HashMap<>();
    private HashMap<Integer, Integer> pointsTable = new HashMap<>();

    public void sendDeck(){
        MainDeck = new ArrayList<>();
        ArrayList<Card> deckCopy = new ArrayList<>(newDeck);

        for(int i = 0; i < 4; i++){
            ArrayList<Card> playerDeck = new ArrayList<>();

            for(int j = 0; j < 13; j++){
                int index = random.nextInt(deckCopy.size());
                playerDeck.add(deckCopy.remove(index));
            }

            MainDeck.add(playerDeck);
        }

        for(int j = 0; j < 4; j++){
            try{
                ArrayList<String> stringDeck = new ArrayList<>();
                for(Card card : MainDeck.get(j)) 
                    stringDeck.add(card.toString());

                BufferedWriter BF = new BufferedWriter(new OutputStreamWriter(ClientSocks[j].getOutputStream()));
        
                for(String element : stringDeck){
                    BF.write(element);
                    BF.newLine();
                    BF.flush();
                }          
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    
    public void mapRankValues(){
        for(int i = 2; i <= 10; i++){
            char c = (char)(i + 48);
            HMap.put(c, i);
        }

        HMap.put('J', 11);
        HMap.put('Q', 12);
        HMap.put('K', 13);
        HMap.put('A', 14);
    }

    public void sendPlayerNames(){
        for(int i = 0; i < 4; i++){
            try{
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(ClientSocks[i].getOutputStream()));

                for(int j = 0; j < 4; j++){
                    BW.write(i != j ? ClientUsers[j] : "Your New Name " + ClientUsers[j]);
                    BW.newLine();
                }

                BW.flush();

                pointsTable.put(i, 0);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void sendString(String message){
        try{
            for(int i = 0; i < 4; i++){
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(ClientSocks[i].getOutputStream()));
                BW.write(message);
                BW.flush();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setChar(char C){
        this.firstTurn = C;
    }

    public int countScore(String message){
        char startChar = message.charAt(0);
        char endChar = message.charAt(message.length() - 1);

        int score = 0;
        if(message.length() == 4){
            if(endChar == trumpChar)
                score = 110;
            else if(endChar == firstTurn)
                score = 10;
        }else{
            if(endChar == trumpChar)
                score = 100 + HMap.get(startChar);
            else if(endChar == firstTurn)
                score = HMap.get(startChar);
        }

        return score;
    }

    Printer PH = new Printer(ClientUsers);
    public void countSubRoundScore(TreeMap<Integer,Integer> TMap, HashMap<Integer,Integer> roundScore){
        Integer winnerKey = TMap.firstKey();
        Integer winnerIdx = TMap.get(winnerKey);

        roundScore.put(winnerIdx, roundScore.getOrDefault(winnerIdx, 0) + 1);

        System.out.println("\n----- SubRound Score -----\n");
        PH.PrintHashMap(roundScore);
    }

    public void countRoundScore(HashMap<Integer,Integer> roundScore, HashMap<Integer,Integer> commitScore, HashMap<Integer,Integer> pointsTable){
        for(int j = 0; j < 4; j++){
            int RS = roundScore.get(j);
            int CS = commitScore.get(j);

            int newValue = 0;
            int oldValue = pointsTable.get(j);

            if(RS < CS)
                newValue = oldValue + ((-CS) * 10);
            else
                newValue = oldValue + ((CS * 10) + ((RS - CS) * 2));
        
            pointsTable.put(j, newValue);
        }

        System.out.println("\n----- Commit Score -----\n");
        PH.PrintHashMap(commitScore);

        System.out.println("\n----- Points Table -----\n");
        PH.PrintHashMap(pointsTable);
    }

    public void collectCommitScores(Map<Integer, Integer> commitScore){
        for(int j = 0; j < 4; j++){
            try{
                BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(ClientSocks[j].getOutputStream()));
                BW.write("Your Commit");
                BW.newLine();
                BW.flush();

                BufferedReader BR = new BufferedReader(new InputStreamReader(ClientSocks[j].getInputStream()));

                String line = BR.readLine();
                int message = Integer.parseInt(line);

                commitScore.put(j, message);

            }catch(IOException | NumberFormatException e){
                e.printStackTrace();
            }
        }
    }

    public void startSubRound(HashMap<Integer, Integer> roundScore){
        for(int j = 0; j < 2; j++){
            TreeMap<Integer, Integer> TMap = new TreeMap<>(Collections.reverseOrder());
            
            ArrayList<Integer> Rank = new ArrayList<Integer>();
            for(int k = 0; k < 4; k++)
                Rank.add(k);

            int Size = 4;
            for(int k = 0; k < 4; k++){
                try{
                    int randomIndex = random.nextInt(Size);
                    int playerIndex = Rank.get(randomIndex);
                    Rank.remove(randomIndex);

                    BufferedWriter BW = new BufferedWriter(new OutputStreamWriter(ClientSocks[playerIndex].getOutputStream()));
                    BW.write("Your Turn");
                    BW.newLine();
                    BW.flush();

                    BufferedReader BR = new BufferedReader(new InputStreamReader(ClientSocks[playerIndex].getInputStream()));
                    String message = BR.readLine();

                    if(k == 0)
                        setChar(message.charAt(message.length() - 1));

                    int score = countScore(message);
                    TMap.put(score, playerIndex);
                    Size--;

                    String commonString = message + "\n";
                    sendString(commonString);

                }catch(IOException e){
                    e.printStackTrace();
                }
            }

            countSubRoundScore(TMap, roundScore);

            String commonString = "Subround " + (j + 1) + " Winner " + ClientUsers[TMap.get(TMap.firstKey())] + "\n";
            sendString(commonString);
        }
    }

    public void startRound(){
        for(int i = 0; i < 1; i++){
            sendDeck();

            HashMap<Integer, Integer> commitScore = new HashMap<>();
            collectCommitScores(commitScore);
            
            HashMap<Integer, Integer> roundScore = new HashMap<>();
            for(int j = 0; j < 4; j++)
                roundScore.put(j, 0);

            startSubRound(roundScore);

            countRoundScore(roundScore, commitScore, pointsTable);
        }
    }

    public void startServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
        System.out.println("\nServer started. Waiting for clients to connect...");

        try{
            for(int i = 0; i < 4; i++){
                Socket ClientSock = serverSocket.accept();

                BufferedReader BR = new BufferedReader(new InputStreamReader(ClientSock.getInputStream()));
                String ClientUser = BR.readLine();
                
                System.out.println(ClientUser + " Has Joined The Game");
                ClientSocks[i] = ClientSock;
                ClientUsers[i] = ClientUser + i;
            }

            System.out.println("\nName of All Players : ");
            for(int i = 0; i < 4; i++)
                System.out.println(ClientUsers[i]);

            CommonString = "Other Players\n";
            sendString(CommonString);

            sendPlayerNames();

            DeckManager DM = new DeckManager();
            newDeck = DM.prepareDeck();

            Trump = DM.selectTrumpSuit();
            System.out.println("\nTrump Suit of Game : " + Trump);

            mapRankValues();

            CommonString = Trump + "\n";
            sendString(CommonString);
            trumpChar = Trump.charAt(0);

            CommonString = "Let's Play\n";
            sendString(CommonString);

            startRound();

            CommonString = "Game Over";
            sendString(CommonString);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        try{
            ServerSocket serverSocket = new ServerSocket(6000);
            Server server = new Server(); 
            server.startServer(serverSocket);
            
            serverSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}