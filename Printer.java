import java.util.*;

public class Printer{

    static String[] ClientUsers = new String[4];
    public Printer(String[] ClientUsers){
        Printer.ClientUsers = ClientUsers;
    }

    public static String CenterString(String str, int width){
        int padding = (width - str.length()) / 2;
        StringBuilder padded = new StringBuilder();

        for(int i = 0; i < padding; i++)
            padded.append(' ');

        padded.append(str);

        while(padded.length() < width)
            padded.append(' ');

        return padded.toString();
    }

    public static void PrintHorizontalLine(int width){
        for(int i = 0; i < width; i++)
            System.out.print("-");

        System.out.println();
    }

    public static void PrintHashMap(HashMap<Integer, Integer> M){
        HashMap<String, Integer> map = new HashMap<>();

        for(Map.Entry<Integer, Integer> entry : M.entrySet()){
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

        for(Map.Entry<String, Integer> entry : map.entrySet()){
            String key = entry.getKey();
            String value = entry.getValue().toString();
            String paddedKey = CenterString(key, maxKeyWidth);
            String paddedValue = CenterString(value, maxValueWidth);
            System.out.println("| " + paddedKey + " | " + paddedValue + " |");
            PrintHorizontalLine(TotalWidth);
        }
    }
}