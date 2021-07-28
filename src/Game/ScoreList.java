package Game;

import java.io.Serializable;

public class ScoreList implements Serializable {

    private int id;
    private int score;
    private String userName;

    public ScoreList(int ID, String name, int score) {
    	
        this.id = ID;
        this.score = score;
        this.userName = name;
    }

    public int getID() {
        return id;
    }
    public  int getScore(){
        return score;
    }

    public String getUserName(){
        return userName;
    }
    public String getGameName() {
        return "" + userName + score;
    }

}
