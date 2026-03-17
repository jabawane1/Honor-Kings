import java.io.Serializable;

public class Card implements Serializable{

    private String value;
    private String type;

    public Card(String value, String type){
        this.value = value;
        this.type = type;
    }

    public String getValue(){
        return value;
    }

    public String getType(){
        return type;
    }

    @Override
    public String toString(){
        return value + "-" + type;
    }
}