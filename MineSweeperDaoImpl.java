package com.nyu.edu.minesweepergame.dao;

import com.nyu.edu.minesweepergame.model.Game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MineSweeperDaoImpl {
    private Connection connection = null;
    private PreparedStatement insertQueryStmt;
    private PreparedStatement getAllSavedGamesStmt;
    private PreparedStatement insertWonGamesQueryStmt;
    private PreparedStatement getAllWonGamesQueryStmt;

    public MineSweeperDaoImpl() throws SQLException {
        String url = "jdbc:sqlite:minesweeper.db";
        try {
            connection = DriverManager.getConnection(url);
            insertQueryStmt = connection.prepareStatement("INSERT INTO Game (game_name, save_time, game_data, user_name) VALUES (?,?,?,?)");
            insertWonGamesQueryStmt = connection.prepareStatement("INSERT INTO WonGames (game_name, score, save_time, game_data, user_name) VALUES (?, ?,?,?,?)");
            getAllSavedGamesStmt = connection.prepareStatement("Select * from Game");
            getAllWonGamesQueryStmt = connection.prepareStatement("Select * from WonGames");
        } catch (SQLException e) {
            throw e;
        }
    }

    public void insertWonGames(Game game) throws Exception {
        PreparedStatement pStmt = null;
        ByteArrayOutputStream bAout = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(bAout);
        try {
            objOut.writeObject(game);
            objOut.flush();
            pStmt = insertWonGamesQueryStmt;
            pStmt.setString(1, game.getGameName());
            pStmt.setString(2, game.getGameName());
            pStmt.setTimestamp(3, game.getSavedTime());
            pStmt.setBytes(4, bAout.toByteArray());
            pStmt.setString(5, game.getUserName());
            pStmt.execute();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objOut.close();
            bAout.close();
            if (pStmt != null) {
                pStmt.close();
            }
        }
    }

    public void insertSavedGames(Game game) throws Exception {
        PreparedStatement pStmt = null;
        ByteArrayOutputStream bAout = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(bAout);
        try {
            objOut.writeObject(game);
            objOut.flush();
            pStmt = insertQueryStmt;
            pStmt.setString(1, game.getGameName());
            pStmt.setTimestamp(2, game.getSavedTime());
            pStmt.setBytes(3, bAout.toByteArray());
            pStmt.setString(4, game.getUserName());
            pStmt.execute();
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            objOut.close();
            bAout.close();
            if (pStmt != null) {
                pStmt.close();
            }
        }
    }

    public List<Game> getAllSavedGames() throws Exception {
        ObjectInputStream ois = null;
        ByteArrayInputStream baip = null;
        List<Game> allSavedGames = new ArrayList<>();
        PreparedStatement pStmt = null;
        try {
            pStmt = getAllSavedGamesStmt;
            ResultSet resultSet = pStmt.executeQuery();
            allSavedGames = new ArrayList<>();
            while (resultSet.next()) {
                byte[] studBlob = resultSet.getBytes("game_data");
                baip = new ByteArrayInputStream(studBlob);
                ois = new ObjectInputStream(baip);
                allSavedGames.add((Game) ois.readObject());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baip != null && ois != null) {
                baip.close();
                ois.close();
            }
            if (pStmt != null) {
                pStmt.close();
            }
        }
        return allSavedGames;
    }

    public List<Game> getAllWonGames() throws Exception {
        ObjectInputStream ois = null;
        ByteArrayInputStream baip = null;
        List<Game> allWonGames = new ArrayList<>();
        ;
        PreparedStatement pStmt = null;
        try {
            pStmt = getAllWonGamesQueryStmt;
            ResultSet resultSet = pStmt.executeQuery();
            allWonGames = new ArrayList<>();
            while (resultSet.next()) {
                byte[] studBlob = resultSet.getBytes("game_data");
                baip = new ByteArrayInputStream(studBlob);
                ois = new ObjectInputStream(baip);
                allWonGames.add((Game) ois.readObject());
            }
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baip != null && ois != null) {
                baip.close();
                ois.close();
            }
            if (pStmt != null) {
                pStmt.close();
            }
        }
        return allWonGames;
    }

    public List<Game> getTopFiveGame() {
        List<Game> top5Games = new ArrayList<>();
        try {
            List<Game> allGames = getAllWonGames();
            Collections.sort(allGames, (o1, o2) -> {
                Integer i = o2.getScore();
                return i.compareTo(o1.getScore()); // sort ascending
            });
            top5Games = allGames.stream().limit(5).collect(Collectors.toList());
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return top5Games;
    }
}
