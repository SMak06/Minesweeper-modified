package Game;

import java.io.Serializable;

import Main.MinesweeperMain;

public class UserData implements Serializable {
	
	private String name;
	private String date;
	private MinesweeperMain ob;
	
	public UserData() {}

	public UserData(String name, String date, MinesweeperMain ms) {
		this.ob = ms;	
		this.name = name;
		this.date = date;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDate() {
		return date;
	}
	
	public MinesweeperMain getUserData() {
		return ob;
	}
}

