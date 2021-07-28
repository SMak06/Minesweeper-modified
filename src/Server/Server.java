package Server;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import Game.UserData;
import Main.MinesweeperMain;
import Game.ScoreList;

public class Server {

	ArrayList outputStreams;
	private UserData u;
	private Connection connection;

	Server() {}

	public static void main(String args[]) {
		new Server().go();
	}

	public void go() {
		outputStreams = new ArrayList();

		try {
			connection = DriverManager.getConnection("jdbc:sqlite:MinesweeperDatabase.db");

		} catch (SQLException e) {
			System.err.println("Connection error: " + e);
			System.exit(1);
		}

		System.out.println("Initiating Server...");
		try {
			ServerSocket server = new ServerSocket(8000);
			System.out.println("Server initiated...");
			while (true) {

				Socket clientSocket = server.accept();

				ObjectInputStream serverInputStream = new ObjectInputStream(clientSocket.getInputStream());
				ObjectOutputStream serverOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());

				System.out.println("Client Connected...");

				serverOutputStream.write(100);
				serverOutputStream.flush();

				int c = serverInputStream.read();
				if (c == 200) {
					UserData userData = (UserData) serverInputStream.readObject();
					int returnCode = saveData(userData);
					serverOutputStream.write(returnCode);
					serverOutputStream.flush();
				}

				if (c == 202) {
					ArrayList<MinesweeperMain> mainList = retrieveData();
					serverOutputStream.writeObject(mainList);
					serverOutputStream.flush();
				}

				if (c == 204) {
					int id = serverInputStream.read();

					UserData userData = getUserData(id);
					serverOutputStream.writeObject(userData);
					serverOutputStream.flush();
				}
				if (c == 206) {
					MinesweeperMain score = (MinesweeperMain) serverInputStream.readObject();
					int returnCode = saveScore(score);
					serverOutputStream.write(returnCode);
					serverOutputStream.flush();
				}
				if (c == 208) {
					ArrayList<ScoreList> score = retrieveScores();
					serverOutputStream.writeObject(score);
					serverOutputStream.flush();
				}

			}
		} catch (IOException e) {
			System.out.println("Cause1" + e.getCause());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Cause2" + e.getCause());
			e.printStackTrace();
		}
	}

	public int saveData(UserData userData) {
		System.out.println("Name: " + userData.getName());
		System.out.println("Date: " + userData.getDate());
		System.out.println("Y: " + userData.getUserData().toString());

		String sql = "INSERT INTO gamedata(name, date_time, serialized_obj) values(?,?,?)";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, userData.getName());
			ps.setString(2, userData.getDate());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oout = new ObjectOutputStream(baos);
			oout.writeObject(userData);
			oout.close();
			ps.setBytes(3, baos.toByteArray());

			int result = ps.executeUpdate();
			ps.close();

			if (result > 0) {
				return 201;
			} else {
				return 404;
			}

		} catch (IOException | SQLException e) {
			e.printStackTrace();
			return 404;
		}
	}

	public int saveScore(MinesweeperMain score) {

		System.out.println("Score: " + score.getScore());
		String sql = "INSERT INTO scores(name, score) values(?,?)";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, score.getUser());
			ps.setString(2, "" + score.getScore());

			int result = ps.executeUpdate();
			ps.close();

			if (result > 0) {
				return 207;
			} else {
				return 404;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return 404;
		}
	}

	public ArrayList<ScoreList> retrieveScores() {

		ArrayList<ScoreList> alist = new ArrayList<>();
		int id;
		int score;
		String name;

		String sql = "Select id, name, score from scores order by score desc limit 5";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet result = ps.executeQuery();

			while (result.next()) {
				id = result.getInt(1);
				name = result.getString(2);
				score = Integer.parseInt(result.getString(3));

				ScoreList mainList = new ScoreList(id, name, score);
				System.out.println("Fetched: " + mainList.getGameName());

				alist.add(mainList);
			}

			result.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return alist;
	}

	public ArrayList<MinesweeperMain> retrieveData() {

		ArrayList<MinesweeperMain> alist = new ArrayList<MinesweeperMain>();
		int id;
		String name, datetime;

		String sql = "Select id, name, date_time from gamedata";
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ResultSet result = ps.executeQuery();

			while (result.next()) {
				id = result.getInt(1);
				name = result.getString(2);
				datetime = result.getString(3);
				MinesweeperMain mainList = new MinesweeperMain(id, name, datetime);
				System.out.println("Saved Game item: " + mainList.getGameName());
				alist.add(mainList);
			}
			result.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return alist;
	}

	public UserData getUserData(int id) {
		UserData userData = new UserData();

		String sql = "Select name, date_time, serialized_obj from gamedata where id = ?";
		System.out.println(id);
		try {
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setInt(1, id);
			ResultSet result = ps.executeQuery();
			result.next();
			byte[] buffer = result.getBytes("serialized_obj");
			ObjectInputStream objectIn = null;
			if (buffer != null)
				objectIn = new ObjectInputStream(new ByteArrayInputStream(buffer));
			userData = (UserData) objectIn.readObject();

			System.out.println("User: " + userData.getName());
			System.out.println("Date: " + userData.getDate());

			result.close();
			ps.close();

		} catch (IOException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return userData;
	}

}
