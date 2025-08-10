import java.util.*;
import java.net.*;
import java.io.*;

public class Client {
    static char T;
    static HashMap<Character, Integer> HMap = new HashMap<>();

    public static void PutHashmap(){
        for(int i = 2; i <= 10; i++){
            char c = (char)(i + 48);
            HMap.put(c, i);
        }

        HMap.put('J',11);
        HMap.put('Q',12);
        HMap.put('K',13);
        HMap.put('A',14);
    }

    public static int Val(String s){
        char F = s.charAt(0);
        char L = s.charAt(s.length()-1);

        int Score=0;
        if(s.length() == 4){
            if(L == T)
                Score = 110;
            else
                Score = 10;
        }else{
            if(L == T)
                Score = 100 + HMap.get(F);
            else
                Score = HMap.get(F);
        }

        return Score;
    }

    public static void AddCardsToDeck(TreeMap<Integer, String> cards, ArrayList<String> deck) {
        for (Integer key : cards.keySet()) 
            deck.add(cards.get(key));
    }

    public static ArrayList<String> Sort(ArrayList<String> Deck){
        TreeMap<Integer,String> C = new TreeMap<>(Collections.reverseOrder());
        TreeMap<Integer,String> D = new TreeMap<>(Collections.reverseOrder());
        TreeMap<Integer,String> H = new TreeMap<>(Collections.reverseOrder());
        TreeMap<Integer,String> S = new TreeMap<>(Collections.reverseOrder());

        for(String s : Deck){
            char L = s.charAt(s.length()-1);
            switch (L) {
                case 'C':
                    C.put(Val(s),s);
                    break;
                case 'D':
                    D.put(Val(s),s);
                    break;
                case 'H':
                    H.put(Val(s),s);
                    break;
                case 'S':
                    S.put(Val(s),s);
                    break;
            }
        }

        ArrayList<String> AnotherDeck = new ArrayList<>();
        switch (T) {
            case 'C':
                AddCardsToDeck(C, AnotherDeck);
                AddCardsToDeck(D, AnotherDeck);
                AddCardsToDeck(H, AnotherDeck);
                AddCardsToDeck(S, AnotherDeck);
                break;
            case 'D':
                AddCardsToDeck(D, AnotherDeck);
                AddCardsToDeck(C, AnotherDeck);
                AddCardsToDeck(H, AnotherDeck);
                AddCardsToDeck(S, AnotherDeck);
                break;
            case 'H':
                AddCardsToDeck(H, AnotherDeck);
                AddCardsToDeck(C, AnotherDeck);
                AddCardsToDeck(D, AnotherDeck);
                AddCardsToDeck(S, AnotherDeck);
                break;
            case 'S':
                AddCardsToDeck(S, AnotherDeck);
                AddCardsToDeck(C, AnotherDeck);
                AddCardsToDeck(D, AnotherDeck);
                AddCardsToDeck(H, AnotherDeck);
                break;
        }
            
        return AnotherDeck;
    }

    public static void main(String[] args) {
        try {
            Scanner scan = new Scanner(System.in);
            
            System.out.println("Enter Your Name : ");
            String username = scan.nextLine();
            System.out.println(username + "\n");

            System.out.println("Enter IP Address of Your Device : ");
            String IP = scan.nextLine();
            System.out.println(IP + "\n");

            Socket sock = new Socket(IP, 6000);
            
            BufferedWriter BF = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            BF.write(username + "\n");
            BF.flush();

            //Other Players
            BufferedReader BR = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String Message = BR.readLine();
            System.out.println("----- " + Message + " -----\n");

            PutHashmap();

            for(int i = 0; i < 4; i++){
                Message = BR.readLine();
                System.out.println(Message);
            }
            
            //Trump Card of Game - {C,D,H,S}
            Message = BR.readLine();
            System.out.println("\n----- Trump Card of Game :" + Message + " -----");
            T = Message.charAt(1);

            //LET'S PLAY
            Message = BR.readLine();
            System.out.println("\n-----" + Message + "-----");

            try{
                for(int i = 0; i < 1; i++){
                    //Round - i
                    Message = BR.readLine();
                    System.out.println("\n-----" + Message + "-----");

                    ArrayList<String> Deck = new ArrayList<>();
                    for(int j = 0; j < 13; j++){
                        Message = BR.readLine();
                        Deck.add(Message);
                    }

                    Deck = Sort(Deck);
                    System.out.println("\n" + Deck);

                    //Your Commit
                    Message = BR.readLine();
                    System.out.println("\n" + Message);

                    int commit = scan.nextInt();
                    BF.write(commit + "\n");
                    BF.flush();

                    for(int j = 0; j < 2; j++){
                        //SubRound - j
                        Message = BR.readLine();
                        System.out.println("\n" + Message);
                        
                        System.out.println("\n" + Deck);

                        for(int k = 0; k < 5; k++){
                            Message = BR.readLine();
                            Integer L = Message.length();
                            
                            if(L == 9){
                                //Your Turn
                                System.out.println("\n" + Message);

                                int index = scan.nextInt();
                                String s = Deck.get(index);

                                Deck.remove(index);
            
                                BF.write(s + "\n");
                                BF.flush();
                            }else
                                System.out.println("\n" + Message);
                        }

                        //Winner
                        Message = BR.readLine();
                        System.out.println("-----" + Message + "-----\n");
                    }
                }

                //Game Over
                Message = BR.readLine();
                System.out.println("-----" + Message + "-----\n");

                Message = BR.readLine();
                System.out.println("-----" + Message + "-----\n");
            }catch(IOException e){
                e.printStackTrace();
            }

            sock.close();
            scan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// import java.io.*;
// import java.net.*;

// public class Client {
//     public static void main(String[] args) {
//         try {
//             // Connect to the server
//             Socket sock = new Socket("localhost", 5000);
//             System.out.println("Connected to server");

//             // Get input and output streams
//             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
//             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));

//             // Read and print the welcome message from the server
//             String Message = in.readLine();
//             System.out.println("Server says: " + Message);

//             // Send a message to the server
//             out.write("Hello from client\n");
//             out.flush();
//             System.out.println("Message sent to server");

//             // Close the sock
//             sock.close();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }
