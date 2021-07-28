package Main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Random;
import javax.swing.*;

public class MinesweeperBoard extends JPanel {

    private final int number_of_images = 13;
    private final int size_of_cell = 15;
    private final int cell_cover = 10;
    private final int cell_mark = 10;
    private final int empty = 0;
    private final int mine_cell = 9;
    private final int covered_mine = mine_cell + cell_cover;
    private final int marked_mine = covered_mine + cell_mark;
    private final int margin_on_top = 30;
    
    private final int no_of_mines = 40;

    private int[] field;
    private boolean inGame;
    private int minesLeft;
    private Image[] image;
    
    private final int DRAW_MINE = 9;
    private final int DRAW_COVER = 10;
    private final int DRAW_MARK = 11;
    private final int DRAW_WRONG_MARK = 12;
    
    private final int N_ROWS = 16;
    private final int N_COLS = 16;
    private final int BOARD_WIDTH = N_COLS * size_of_cell + 1;
    private final int BOARD_HEIGHT = N_ROWS * size_of_cell + 1;
    private int boxes;
    private final JLabel statusbar;
    private MinesweeperMain minesweeperObj;

    private JLabel timeLabel = new JLabel("Time Remaining: 1000");
    private JPanel timePanel = new JPanel();
    private Timer timer;

    public MinesweeperBoard(JLabel statusbar, MinesweeperMain obj) {

        this.statusbar = statusbar;
        this.minesweeperObj = obj;
        timeP();
        start();
    }

    public void timeP(){
        timePanel.setLayout(new BoxLayout(timePanel, BoxLayout.X_AXIS));
        timePanel.add(timeLabel);
        add(timePanel);

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                minesweeperObj.setTimeLeft(minesweeperObj.getTime() - 1);
                timeLabel.setText("Time Remaining: " + minesweeperObj.getTime());
                timeLabel.repaint();
                if(minesweeperObj.getTime() <= 0){
                    repaint();
                }
            }
        });
    }

    private void start() {

        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        image = new Image[number_of_images];

        for (int i = 0; i < number_of_images; i++) {

            String path = "./minesweepertiles/" + i + ".png";
            image[i] = (new ImageIcon(path)).getImage();
        }

        addMouseListener(new MinesAdapter());
        newGame();
    }



    public void newGame(boolean loadedFromDB) {
        if(timer.isRunning()){
            timer.stop();
        }
        inGame = true;
        boxes = N_ROWS * N_COLS;
        minesLeft = this.minesweeperObj.getMines();
        this.minesweeperObj.setTimeLeft(1000);
        field = Arrays.copyOf(this.minesweeperObj.getLayout(), this.minesweeperObj.getLayout().length);

        statusbar.setText(Integer.toString(minesLeft));
        repaint();
    }

    public void newGame() {

        int cell;
        if(timer.isRunning()){
            timer.stop();
        }
        Random random = new Random();
        inGame = true;
        boxes = N_ROWS * N_COLS;
        field = new int[boxes];
        minesweeperObj.setTimeLeft(1000);

        minesLeft = minesweeperObj.getMines();
        for (int i = 0; i < boxes; i++) {

            field[i] = cell_cover;
        }

        statusbar.setText(Integer.toString(minesLeft));
        int i = 0;


        while (i < no_of_mines) {

            int position = (int) (boxes * random.nextDouble());

            if ((position < boxes)
                    && (field[position] != covered_mine)) {

                int current_col = position % N_COLS;
                field[position] = covered_mine;
                i++;

                if (current_col > 0) {
                    cell = position - 1 - N_COLS;
                    if (cell >= 0) {
                        if (field[cell] != covered_mine) {
                            field[cell] += 1;
                        }
                    }
                    cell = position - 1;
                    if (cell >= 0) {
                        if (field[cell] != covered_mine) {
                            field[cell] += 1;
                        }
                    }

                    cell = position + N_COLS - 1;
                    if (cell < boxes) {
                        if (field[cell] != covered_mine) {
                            field[cell] += 1;
                        }
                    }
                }

                cell = position - N_COLS;
                if (cell >= 0) {
                    if (field[cell] != covered_mine) {
                        field[cell] += 1;
                    }
                }

                cell = position + N_COLS;
                if (cell < boxes) {
                    if (field[cell] != covered_mine) {
                        field[cell] += 1;
                    }
                }

                if (current_col < (N_COLS - 1)) {
                    cell = position - N_COLS + 1;
                    if (cell >= 0) {
                        if (field[cell] != covered_mine) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + N_COLS + 1;
                    if (cell < boxes) {
                        if (field[cell] != covered_mine) {
                            field[cell] += 1;
                        }
                    }
                    cell = position + 1;
                    if (cell < boxes) {
                        if (field[cell] != covered_mine) {
                            field[cell] += 1;
                        }
                    }
                }
            }
        }
        repaint();
        minesweeperObj.setLayout(field);
    }

    private void find_empty_cells(int j) {

        int current_col = j % N_COLS;
        int cell;

        if (current_col > 0) {
            cell = j - N_COLS - 1;
            if (cell >= 0) {
                if (field[cell] > mine_cell) {
                    field[cell] -= cell_cover;
                    if (field[cell] == empty) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j - 1;
            if (cell >= 0) {
                if (field[cell] > mine_cell) {
                    field[cell] -= cell_cover;
                    if (field[cell] == empty) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS - 1;
            if (cell < boxes) {
                if (field[cell] > mine_cell) {
                    field[cell] -= cell_cover;
                    if (field[cell] == empty) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

        cell = j - N_COLS;
        if (cell >= 0) {
            if (field[cell] > mine_cell) {
                field[cell] -= cell_cover;
                if (field[cell] == empty) {
                    find_empty_cells(cell);
                }
            }
        }

        cell = j + N_COLS;
        if (cell < boxes) {
            if (field[cell] > mine_cell) {
                field[cell] -= cell_cover;
                if (field[cell] == empty) {
                    find_empty_cells(cell);
                }
            }
        }

        if (current_col < (N_COLS - 1)) {
            cell = j - N_COLS + 1;
            if (cell >= 0) {
                if (field[cell] > mine_cell) {
                    field[cell] -= cell_cover;
                    if (field[cell] == empty) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + N_COLS + 1;
            if (cell < boxes) {
                if (field[cell] > mine_cell) {
                    field[cell] -= cell_cover;
                    if (field[cell] == empty) {
                        find_empty_cells(cell);
                    }
                }
            }

            cell = j + 1;
            if (cell < boxes) {
                if (field[cell] > mine_cell) {
                    field[cell] -= cell_cover;
                    if (field[cell] == empty) {
                        find_empty_cells(cell);
                    }
                }
            }
        }

    }

    @Override
    public void paintComponent(Graphics g) {
        int uncover = 0;

        if (minesweeperObj.getTime() == 0) {
            inGame = false;
        }

        for (int i = 0; i < N_ROWS; i++) {

            for (int j = 0; j < N_COLS; j++) {

                int cell = field[(i * N_COLS) + j];

                if (inGame && cell == mine_cell) {

                    inGame = false;
                }

                if (!inGame) {

                    if (cell == covered_mine) {
                        cell = DRAW_MINE;
                    } else if (cell == marked_mine) {
                        cell = DRAW_MARK;
                    } else if (cell > covered_mine) {
                        cell = DRAW_WRONG_MARK;
                    } else if (cell > mine_cell) {
                        cell = DRAW_COVER;
                    }

                } else {

                    if (cell > covered_mine) {
                        cell = DRAW_MARK;
                    } else if (cell > mine_cell) {
                        cell = DRAW_COVER;
                        uncover++;
                    }
                }
                g.drawImage(image[cell], (j * size_of_cell),
                        (i * size_of_cell) + margin_on_top, this);
            }
        }

        if (uncover == 0 && inGame) {
            inGame = false;
            minesweeperObj.Score();
            statusbar.setText("Game won");
            timer.stop();
        } else if (!inGame) {
            statusbar.setText("Game lost");
            timer.stop();
        }
    }

    private class MinesAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {

            if(!timer.isRunning()){
                timer.start();
            }

            int x = e.getX();
            int y = e.getY() - margin_on_top;

            int cCol = x / size_of_cell;
            int cRow = y / size_of_cell;

            boolean doRepaint = false;

            if (!inGame) {

                newGame();
                repaint();
            }

            if ((x < N_COLS * size_of_cell) && (y < N_ROWS * size_of_cell)) {

                if (e.getButton() == MouseEvent.BUTTON3) {

                    if (field[(cRow * N_COLS) + cCol] > mine_cell) {

                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] <= covered_mine) {

                            if (minesLeft > 0) {
                                field[(cRow * N_COLS) + cCol] += cell_mark;
                                minesLeft--;
                                String msg = Integer.toString(minesLeft);
                                statusbar.setText(msg);
                            } else {
                                statusbar.setText("No marks left");
                            }
                        } else {

                            field[(cRow * N_COLS) + cCol] -= cell_mark;
                            minesLeft++;
                            String msg = Integer.toString(minesLeft);
                            statusbar.setText(msg);
                        }
                    }

                } else {

                    if (field[(cRow * N_COLS) + cCol] > covered_mine) {

                        return;
                    }

                    if ((field[(cRow * N_COLS) + cCol] > mine_cell)
                            && (field[(cRow * N_COLS) + cCol] < marked_mine)) {

                        field[(cRow * N_COLS) + cCol] -= cell_cover;
                        doRepaint = true;

                        if (field[(cRow * N_COLS) + cCol] == mine_cell) {
                            inGame = false;
                        }

                        if (field[(cRow * N_COLS) + cCol] == empty) {
                            find_empty_cells((cRow * N_COLS) + cCol);
                        }
                    }
                }

                if (doRepaint) {
                    minesweeperObj.setLayout(field);
                    repaint();
                }
            }
        }
    }
}
