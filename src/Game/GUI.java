package Game;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.JTableHeader;
import Main.MinesweeperBoard;
import Main.MinesweeperMain;

public class GUI extends JFrame {

	private JButton saveGame;
	private JButton[] loadGame;
	private static JFrame mainFrame, frame;
	private JPanel PANEL, S_PANEL, L_PANEL, M_PANEL, N_PANEL, SC_PANEL;
	private JTextField text_NAME = new JTextField(25);
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	private Socket socket;
	private JTable table;

	private MinesweeperBoard object;
	private static MinesweeperMain ob = new MinesweeperMain();

	private ArrayList<MinesweeperMain> lists;
	private ArrayList<ScoreList> scores;

	public MinesweeperMain getGame() {
		return ob;
	}

	private final JLabel statusbar;
	private JLabel label_NAME = new JLabel("Name: ");
	private JMenuBar menu;
	private JMenu OPTIONS_MENU;
	private JMenuItem R_MENU, L_MENU, S_MENU, E_MENU, HS_MENU, N_MENU;


	public GUI() {
		newBoard(16 * 15, 16 * 15 + 100);
		statusbar = new JLabel("");
		object = new MinesweeperBoard(statusbar, ob);
		addMainPanel();
		playerNameMenu();
		boardAdd();
		mainFrame.getContentPane().add(PANEL);
		mainFrame.setVisible(true);
	}

	public void newBoard(int width, int height) {
		mainFrame = new JFrame();
		mainFrame.setTitle("Minesweeper ");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(width, height);
		mainFrame.setResizable(false);
		mainFrame.setJMenuBar(menuAdd());
		PANEL = new JPanel(new BorderLayout());
		M_PANEL = new JPanel();
		PANEL = new JPanel(new BorderLayout());
		M_PANEL = new JPanel(new BorderLayout());
		N_PANEL = new JPanel();
	}

	public void boardAdd() {
		M_PANEL.add(object, BorderLayout.CENTER);
		M_PANEL.add(statusbar, BorderLayout.SOUTH);
	}

	public JMenuBar menuAdd() {
		menu = new JMenuBar();
		menuBar();
		return menu;
	}

	public void addMainPanel() {
		PANEL.add(M_PANEL, BorderLayout.CENTER);
	}

	public void playerNameMenu() {
		N_PANEL.setLayout(new BoxLayout(N_PANEL, BoxLayout.X_AXIS));

		label_NAME.setHorizontalAlignment(SwingConstants.RIGHT);
		label_NAME.setPreferredSize(new Dimension(50, 50));
		label_NAME.setMaximumSize(new Dimension(50, 50));

		text_NAME.setHorizontalAlignment(SwingConstants.LEFT);
		text_NAME.setPreferredSize(new Dimension(200, 50));
		text_NAME.setMaximumSize(new Dimension(150, 30));

		text_NAME.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				ob.setUser(text_NAME.getText());
			}
			public void removeUpdate(DocumentEvent e) {
				ob.setUser(text_NAME.getText());
			}
			public void changedUpdate(DocumentEvent e) {
				ob.setUser(text_NAME.getText());
			}
		});

		N_PANEL.add(label_NAME);
		N_PANEL.add(text_NAME);
	}

	public void playerSave() {
		S_PANEL.add(N_PANEL, BorderLayout.NORTH);
	}

	public void menuBar() {
		OPTIONS_MENU = new JMenu("File");
		OPTIONS_MENU.getAccessibleContext().setAccessibleDescription("Game Options");
		menu.add(OPTIONS_MENU);
		menu.setBackground(Color.WHITE);
		optionsMenu();
	}

	public void optionsMenu() {
		
		N_MENU = new JMenuItem("New");
		L_MENU = new JMenuItem("Load");
		S_MENU = new JMenuItem("Save");
		HS_MENU = new JMenuItem("HighScores");
		E_MENU = new JMenuItem("Exit");
		N_MENU.addActionListener(new MenuHandler());
		L_MENU.addActionListener(new MenuHandler());
		S_MENU.addActionListener(new MenuHandler());
		HS_MENU.addActionListener(new MenuHandler());
		E_MENU.addActionListener(new MenuHandler());
		OPTIONS_MENU.add(N_MENU);
		OPTIONS_MENU.add(L_MENU);
		OPTIONS_MENU.add(S_MENU);
		OPTIONS_MENU.add(HS_MENU);
		OPTIONS_MENU.add(E_MENU);
	}

	public void selectExitDialog() {
		System.out.println("Closing Minesweeper...");
		System.exit(0);
	}

	public void gameSave() {
		
		frame = new JFrame();
		frame.setTitle("Save Game");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(300, 100);
		frame.setResizable(false);
		PANEL = new JPanel(new BorderLayout());
		frame.getContentPane().add(PANEL);
		S_PANEL = new JPanel();
		PANEL.add(S_PANEL, BorderLayout.CENTER);
		S_PANEL.setLayout(new BoxLayout(S_PANEL, BoxLayout.X_AXIS));
		playerSave();
		saveGame = new JButton("Save Game");
		saveGame.setHorizontalAlignment(SwingConstants.CENTER);
		saveGame.setPreferredSize(new Dimension(150, 30));
		saveGame.setMaximumSize(new Dimension(150, 30));
		saveGame.addActionListener(new SaveGame());
		S_PANEL.add(saveGame);
		frame.setVisible(true);
		
	}

	public ArrayList<MinesweeperMain> retrievePastGames() {
		
		ArrayList<MinesweeperMain> lists = new ArrayList<>();

		System.out.println("Loading Connection...");
		try {
			
			socket = new Socket("127.0.0.1", 8000);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			System.out.println("Connected");
			int in = inputStream.read();
			System.out.println(in);
			outputStream.write(202);
			outputStream.flush();
			lists = (ArrayList<MinesweeperMain>) inputStream.readObject();
			socket.close();

		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}

		return lists;
	}

	public ArrayList<ScoreList> topScores() {
		ArrayList<ScoreList> lists = new ArrayList<>();
		System.out.println("Loading Connection...");
		try {
			socket = new Socket("127.0.0.1", 8000);
			outputStream = new ObjectOutputStream(socket.getOutputStream());
			inputStream = new ObjectInputStream(socket.getInputStream());
			System.out.println("Connected");
			int in = inputStream.read();
			System.out.println(in);
			outputStream.write(208);
			outputStream.flush();
			lists = (ArrayList<ScoreList>) inputStream.readObject();
			socket.close();

		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return lists;
	}

	public void gameLoad() {
		frame = new JFrame();
		frame.setTitle("Load Game");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(400, 750);
		frame.setResizable(true);
		PANEL = new JPanel(new BorderLayout());
		frame.getContentPane().add(PANEL);
		L_PANEL = new JPanel();
		PANEL.add(L_PANEL, BorderLayout.CENTER);
		L_PANEL.setLayout(new BoxLayout(L_PANEL, BoxLayout.Y_AXIS));
		lists = retrievePastGames();
		loadGame = new JButton[lists.size()];

		for (int i = 0; i < lists.size(); i++) {

			loadGame[i] = new JButton(lists.get(i).getGameName());
			loadGame[i].setHorizontalAlignment(SwingConstants.CENTER);
			loadGame[i].setPreferredSize(new Dimension(450, 35));
			loadGame[i].setMaximumSize(new Dimension(450, 35));
			loadGame[i].addActionListener(new LoadGame());

			L_PANEL.add(loadGame[i]);
		}
		frame.setVisible(true);
	}

	public void highScore() {
		frame = new JFrame();
		frame.setTitle("Ranking");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(700, 200);
		frame.setResizable(true);

		PANEL = new JPanel(new BorderLayout());
		frame.getContentPane().add(PANEL);

		SC_PANEL = new JPanel();
		PANEL.add(SC_PANEL, BorderLayout.NORTH);

		scores = topScores();

		String[][] rows = new String[scores.size()][3];
		for (int i = 0; i < scores.size(); i++) {
			rows[i][0] = Integer.toString(i + 1);
			rows[i][1] = scores.get(i).getUserName();
			rows[i][2] = Integer.toString(scores.get(i).getScore());
		}
		String[] header = { "Standing", "User", "Score" };
		table = new JTable(rows, header);
		JTableHeader headerComp = table.getTableHeader();
		headerComp.setBackground(Color.WHITE);
		headerComp.setSize(100, 50);
		table.setRowHeight(30);
		headerComp.setFont(new Font("Arial", Font.PLAIN, 14));
		table.setFont(new Font("Arial", Font.PLAIN, 14));
		SC_PANEL.add(new JScrollPane(table));
		PANEL.add(SC_PANEL);
		frame.setVisible(true);
	}

	class MenuHandler implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource();
			if (source == R_MENU) {
				selectExitDialog();
			} else if (source == L_MENU) {
				gameLoad();
			} else if (source == S_MENU) {
				gameSave();
			} else if (source == HS_MENU) {
				highScore();
			} else if (source == N_MENU) {
				ob.newGame();
				object.newGame();
			} else if (source == E_MENU) {
				selectExitDialog();
			}
		}
	}

	class LoadGame implements ActionListener {

		public void actionPerformed(ActionEvent event) {

			UserData data = new UserData();

			Object source = event.getSource();

			int id = -1;
			for (int i = 0; i < loadGame.length; i++) {
				if (source == loadGame[i]) {
					id = lists.get(i).getID();
					break;
				}
			}

			System.out.println("Loading Connection...");
			try {
				socket = new Socket("127.0.0.1", 8000);
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				inputStream = new ObjectInputStream(socket.getInputStream());
				int in = inputStream.read();
				System.out.println(in);
				outputStream.write(204);
				outputStream.flush();
				outputStream.write(id);
				outputStream.flush();
				data = (UserData) inputStream.readObject();
				System.out.println(data.getUserData().getLayout());
				socket.close();

			} catch (ClassNotFoundException | IOException ex) {
				ex.printStackTrace();
			}

			frame.setVisible(false);
			MinesweeperMain mm = data.getUserData();
			ob.setLayout(mm.getLayout());
			ob.setMinesLeft(mm.getMines());
			ob.setTimeLeft(mm.getTime());
			ob.setUser(mm.getClient());
			object.newGame(true);
			text_NAME.setText(data.getName());
		}
	}

	public void alertSave(int code) {

		switch (code) {
		case 201:
			JOptionPane.showMessageDialog(mainFrame, "Your game has been saved successfully!");
			break;
		default:
			JOptionPane.showMessageDialog(mainFrame, "Failed to Save!!!");
			break;
		}
	}

	class SaveGame implements ActionListener {

		public void actionPerformed(ActionEvent event) {
			String username = text_NAME.getText();
			ob.setUser(username);
			LocalDateTime myDateObj = LocalDateTime.now();
			System.out.println("Before formatting: " + myDateObj);
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

			String gameDate = myDateObj.format(myFormatObj);

			System.out.println("Loading Connection...");
			try {
				socket = new Socket("127.0.0.1", 8000);
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				inputStream = new ObjectInputStream(socket.getInputStream());

				System.out.println("Connected");
				int init = inputStream.read();
				System.out.println(init);

				UserData yd = new UserData(username, gameDate, ob);
				outputStream.write(200);
				outputStream.flush();
				outputStream.writeObject(yd);
				outputStream.flush();
				int reply = inputStream.read();
				System.out.println("Response:" + reply);
				alertSave(reply);
				outputStream.close();
				inputStream.close();
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

			frame.setVisible(false);
		}
	}
}
