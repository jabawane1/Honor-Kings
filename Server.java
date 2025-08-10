import java.util.*;
import java.net.*;
import java.io.*;

class Server {
    private class Card implements Serializable{
        String value;
        String type;

        Card(String value, String type){
            this.value = value;
            this.type = type;
        }

        public String toString(){
            return value + "-" + type;
        }
    }

    public ServerSocket serverSocket;

    static Socket[] ClientSocks = new Socket[4];
    static String[] ClientUsers = new String[4];

    Random random = new Random();

    static int Score;

    static char FirstTurn;
    static char startChar;
    static char endChar; 
    
    static String Trump;
    static String CommonString;

    static ArrayList<Card> Deck;
    static ArrayList<ArrayList<Card>> MainDeck;
    static HashMap<Character, Integer> HMap = new HashMap<>();
    
    public void MakeDeck() {
        Deck = new ArrayList<Card>();
        String[] Values = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};
        String[] Types = {"C","D","H","S"};

        for (int i = 0; i < Types.length; i++){
            for (int j = 0; j < Values.length; j++){
                Card card = new Card(Values[j], Types[i]);
                Deck.add(card);
            }
        }

        for (int i = 0; i < Deck.size(); i++){
            int j = random.nextInt(Deck.size());
            Card Card1 = Deck.get(i);
            Card Card2 = Deck.get(j);
            Deck.set(i, Card2);
            Deck.set(j, Card1);
        }
    }

    public void TrumpCard() {
        char T = '\0';
        for (int i = 0; i < 52; i++){
            String S = Deck.get(i).toString();
            if(S.charAt(0) == 'J'){
                int L = S.length(); 
                T = S.charAt(L - 1);
                break;
            }
        }

        HashMap<Character, String> Suit = new HashMap<>();
        Suit.put('C', " Club");
        Suit.put('D', " Diamond");
        Suit.put('H', " Heart");
        Suit.put('S', " Spade");

        String TrumpLine = "\nTrump Card of Game :";
        Trump = Suit.get(T);
        TrumpLine += Trump;
            
        System.out.print(TrumpLine + "\n");
    }

    public void DivideDeck() {
        MainDeck = new ArrayList<>();
        ArrayList<Card> TempDeck = new ArrayList<>(Deck);
    
        for (int i = 0; i < 4; i++) {
            ArrayList<Card> deck = new ArrayList<>();
    
            for (int j = 0; j < 13; j++) {
                int Index = random.nextInt(TempDeck.size());
                Card card = TempDeck.remove(Index);
                deck.add(card);
            }
    
            MainDeck.add(deck);
        }
    }
    
    public void PutHashmap(){
        for(int i = 2; i <= 10; i++){
            char c=(char)(i + 48);
            HMap.put(c,i);
        }

        HMap.put('J',11);
        HMap.put('Q',12);
        HMap.put('K',13);
        HMap.put('A',14);
    }

    private static String CenterString(String str, int width) {
        int padding = (width - str.length()) / 2;
        StringBuilder padded = new StringBuilder();
        for (int i = 0; i < padding; i++)
            padded.append(' ');
        
        padded.append(str);
        while (padded.length() < width)
            padded.append(' ');
        
        return padded.toString();
    }
    private static void PrintHorizontalLine(int width) {
        for (int i = 0; i < width; i++)
            System.out.print("-");
        
        System.out.println();
    }

    public static void PrintHashMap(HashMap<Integer, Integer> M) {
        HashMap<String, Integer> map = new HashMap<>();

        for (Map.Entry<Integer, Integer> entry : M.entrySet()){
            String key = ClientUsers[entry.getKey()];
            map.put(key,entry.getValue());
        }

        int maxKeyWidth = Math.max(map.keySet().stream().mapToInt(String::length).max().orElse(0), "Name".length());
        int maxValueWidth = Math.max(map.values().stream().mapToInt(value -> value.toString().length()).max().orElse(0), "Score".length());
        int TotalWidth = maxKeyWidth + maxValueWidth + 7;

        PrintHorizontalLine(TotalWidth);

        String HeaderKey = CenterString("Name", maxKeyWidth);
        String HeaderValue = CenterString("Score", maxValueWidth);
        System.out.println("| " + HeaderKey + " | " + HeaderValue + " |");

        PrintHorizontalLine(TotalWidth);

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            String paddedKey = CenterString(key, maxKeyWidth);
            String paddedValue = CenterString(value, maxValueWidth);
            System.out.println("| " + paddedKey + " | " + paddedValue + " |");
            PrintHorizontalLine(TotalWidth);
        }
    }

    public void SendString(String message){
        try{
            for (int i = 0; i < 4; i++) {
                BufferedWriter BF = new BufferedWriter(new OutputStreamWriter(ClientSocks[i].getOutputStream()));
                BF.write(message);
                BF.flush();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public int CountScore(String message,char FirstTurn){
        startChar = message.charAt(0);
        endChar = message.charAt(message.length() - 1);

        if (message.length() == 4){
            if (endChar == Trump.charAt(0))
                Score = 110;
            else if(endChar == FirstTurn)
                Score = 10;
            else
                Score = 0;
        }else{
            if (endChar == Trump.charAt(0))
                Score = 100 + HMap.get(startChar);
            else if(endChar == FirstTurn)
                Score = HMap.get(startChar);
            else
                Score = 0;
        }

        return Score;
    }

    public void CountSubRoundScore(TreeMap<Integer,Integer> TMap,HashMap<Integer,Integer> RoundScore){
        Integer WinnerKey = TMap.firstKey();
        Integer WinnerIdx = TMap.get(WinnerKey);

        int Old_Value = RoundScore.get(WinnerIdx);
        int New_Value = Old_Value + 1;

        RoundScore.put(WinnerIdx,New_Value);
        System.out.println("\n----- SubRound Score -----\n");
        PrintHashMap(RoundScore);
    }

    public void CountRoundScore(HashMap<Integer,Integer> RoundScore,HashMap<Integer,Integer> CommitScore,HashMap<Integer,Integer> PointsTable){
        for(int j = 0; j < 4; j++){
            int RS = RoundScore.get(j);
            int CS = CommitScore.get(j);

            if(RS < CS){
                int Old_Value = PointsTable.get(j);
                int New_Value = Old_Value + ((-CS)*10);
                PointsTable.put(j,New_Value);
            }else{
                int Old_Value = PointsTable.get(j);
                int New_Value = Old_Value + ((CS*10) + ((RS-CS)*2));
                PointsTable.put(j,New_Value);
            }
        }

        System.out.println("\n----- Commit Score -----\n");
        PrintHashMap(CommitScore);

        System.out.println("\n----- Points Table -----\n");
        PrintHashMap(PointsTable);
    }

    public void StartServer(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
        System.out.println("\nServer started. Waiting for clients to connect...");

        try{
            for (int i = 0; i < 4; i++) {
                Socket ClientSock = serverSocket.accept();

                BufferedReader BR = new BufferedReader(new InputStreamReader(ClientSock.getInputStream()));
                String ClientUser = BR.readLine();
                
                System.out.println(ClientUser + " Has Joined The Game");
                ClientSocks[i] = ClientSock;
                ClientUsers[i] = ClientUser + i;
            }

            System.out.println("\nName of All Players : \n");
            for(int i = 0; i < 4; i++)
                System.out.println(ClientUsers[i]);

            CommonString = "Other Players\n";
            SendString(CommonString);

            for (int i = 0; i < 4; i++) {
                BufferedWriter BF = new BufferedWriter(new OutputStreamWriter(ClientSocks[i].getOutputStream()));
                for(int j = 0; j < 4; j++){
                    if(i != j){
                        BF.write(ClientUsers[j] + "\n");
                        BF.flush();
                    }else{
                        BF.write("Your New Name " + ClientUsers[j] + "\n");
                        BF.flush();
                    }
                }
            }

            MakeDeck();
            TrumpCard();
            PutHashmap();

            CommonString = Trump + "\n";
            SendString(CommonString);

            CommonString = "LET'S PLAY \n";
            SendString(CommonString);

            HashMap<Integer, Integer> PointsTable = new HashMap<>();
            for(int i = 0; i < 4; i++)
                PointsTable.put(i,0);

            for(int i = 0; i < 1; i++){
                String RoundString = "----- Round " + (i + 1) + " -----";
                System.out.println("\n" + RoundString);

                CommonString = RoundString + "\n";
                SendString(CommonString);
                DivideDeck();

                HashMap<Integer, Integer> CommitScore = new HashMap<>();
                HashMap<Integer, Integer> RoundScore = new HashMap<>();
                for(int j = 0; j < 4; j++){
                    CommitScore.put(j,0);
                    RoundScore.put(j,0);
                }

                for(int j = 0; j < 4; j++){
                    try {
                        ArrayList<String> stringDeck = new ArrayList<>();
                        for (Card card : MainDeck.get(j)) 
                            stringDeck.add(card.toString());

                        BufferedWriter BF = new BufferedWriter(new OutputStreamWriter(ClientSocks[j].getOutputStream()));
                
                        for (String element : stringDeck) {
                            BF.write(element);
                            BF.newLine();
                            BF.flush();
                        }          
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                for(int j = 0; j < 4; j++){
                    BufferedWriter BF = new BufferedWriter(new OutputStreamWriter(ClientSocks[j].getOutputStream()));
                    BF.write("Your Commit\n");
                    BF.flush();

                    BufferedReader BR = new BufferedReader(new InputStreamReader(ClientSocks[j].getInputStream()));
                    int message = Integer.parseInt(BR.readLine());
                    
                    CommitScore.put(j, message);
                }

                for(int j = 0; j < 2; j++){
                    TreeMap<Integer, Integer> TMap = new TreeMap<>(Collections.reverseOrder());
                    ArrayList<Integer> Rank = new ArrayList<Integer>();

                    for (int k = 0; k < 4; k++)
                        Rank.add(k);
    
                    int Size = 4;

                    String SubRoundString = "----- Subround " + (j + 1) + " -----";
                    System.out.println("\n" + SubRoundString);

                    CommonString = SubRoundString + "\n";
                    SendString(CommonString);

                    for (int k = 0; k < 4; k++){
                        int R = random.nextInt(Size);
                        int S = Rank.get(R);
                        Rank.remove(R);
    
                        System.out.println("PLAYER " + ClientUsers[S] + " TURN : ");

                        BufferedWriter BF = new BufferedWriter(new OutputStreamWriter(ClientSocks[S].getOutputStream()));
                        BF.write("Your Turn\n");
                        BF.flush();

                        BufferedReader BR = new BufferedReader(new InputStreamReader(ClientSocks[S].getInputStream()));
                        String message = BR.readLine();
                        System.out.println(message);
                        
                        if(k == 0)
                            FirstTurn = message.charAt(message.length() - 1);
                            
                        Score = CountScore(message, FirstTurn);
                        
                        TMap.put(Score,S);
                        Size--;

                        CommonString = message + "\n";
                        SendString(CommonString);
                    }

                    CountSubRoundScore(TMap, RoundScore);

                    CommonString = " Subround " + (j + 1) + " Winner " + ClientUsers[TMap.get(TMap.firstKey())] + " \n";
                    SendString(CommonString);
                }
                
                CountRoundScore(RoundScore, CommitScore, PointsTable);
            }

            CommonString = "Game Over\n";
            SendString(CommonString);
            
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(6000);
            Server server = new Server(); 
            server.StartServer(serverSocket);
            
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// import java.io.*;
// import java.net.*;

// public class Server {
//     public static void main(String[] args) {
//         try {
//             // Create server socket
//             ServerSocket serverSocket = new ServerSocket(5000);
//             System.out.println("Server started. Waiting for clients to connect...");

//             // Array to store client sockets
//             Socket[] ClientSocks = new Socket[4];

//             // Accept connections from up to 4 clients
//             for (int i = 0; i < 4; i++) {
//                 Socket ClientSock = serverSocket.accept();
//                 System.out.println("Client connected: " + ClientSock);
//                 ClientSocks[i] = ClientSock;
//             }

//             // Send a welcome message to each client
//             for (int i = 0; i < 4; i++) {
//                 BufferedWriter out = new BufferedWriter(new OutputStreamWriter(ClientSocks[i].getOutputStream()));
//                 out.write("Welcome, you are client " + (i + 1) + "\n");
//                 out.flush();
//             }

//             // Receive a message from each client
//             for (int i = 0; i < 4; i++) {
//                 BufferedReader in = new BufferedReader(new InputStreamReader(ClientSocks[i].getInputStream()));
//                 String message = in.readLine();
//                 System.out.println("Message from client " + (i + 1) + ": " + message);
//             }

//             // Close all client sockets
//             for (Socket socket : ClientSocks) {
//                 socket.close();
//             }

//             // Close the server socket
//             serverSocket.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }
