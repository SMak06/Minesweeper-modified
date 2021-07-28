package Main;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import Game.GUI;

public class MinesweeperMain implements Serializable {

	private int id;
	private int mines = 40;
	private int time = 1000;
	private String name;
	private String date;
	private String client = "minesweeper-1";
	private int layout[] = new int[16 * 16];

	public MinesweeperMain() {

		for (int i = 0; i < 16 * 16; i++) {
			layout[i] = 10;
		}
	}

	public MinesweeperMain(int ID, String player, String date) {

		this.id = ID;
		this.name = player;
		this.date = date;

	}

	public void newGame() {

		for (int i = 0; i < 16 * 16; i++) {
			layout[i] = 10;
		}
		mines = 40;
		time = 1000;
	}

	public int[] getLayout() {
		return layout;
	}

	public int getTime() {
		return time;
	}

	public int getMines() {
		return mines;
	}

	public void setTimeLeft(int timeLeft) {
		this.time = timeLeft;
	}

	public void setMinesLeft(int minesLeft) {
		this.mines = minesLeft;
	}

	public String getClient() {
		return client;
	}

	public void setName(String name) {
		this.client = name;
	}

	public void setLayout(int layout[]) {
		this.layout = Arrays.copyOf(layout, layout.length);
	}

	public String getGameName() {
		return ("" + name + date);
	}

	public int getID() {
		return id;
	}

	private String user = "defaultPlayer";
	private int score = 0;

	public MinesweeperMain(String player, int score) {
		this.user = player;
		this.score = score;
	}

	public void setUser(String player) {
		this.user = player;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getUser() {
		return user;
	}

	public int getScore() {
		return score;
	}

	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	public void Score() {

		System.out.println("Score Saved");

		try {

			socket = new Socket("127.0.0.1", 8000);
			
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());

			int in = inputStream.read();
			System.out.println("input: " + in);
			MinesweeperMain result = new MinesweeperMain(client, (int) time/10);
			outputStream.write(206);outputStream.flush();
			outputStream.writeObject(result);outputStream.flush();
			int rs = inputStream.read();
			System.out.println("response: " + rs);

		} catch (IOException io) {

			io.printStackTrace();

		}
	}
}