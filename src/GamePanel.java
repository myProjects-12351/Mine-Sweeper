import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

public class GamePanel extends JPanel implements MouseListener {
    static final int BOARD_WIDTH = 1200;
    static final int BOARD_HEIGHT = 600;
    static final int UNIT_SIZE = 50;
    static final int AMOUNT_OF_MINES = 50;
    static final int DISTANCE = 3;
    char result = 'l';
    boolean isRunning = true;
    final int[][] board = new int[BOARD_HEIGHT/UNIT_SIZE][BOARD_WIDTH/UNIT_SIZE];
    final boolean[][] flags = new boolean[BOARD_HEIGHT/UNIT_SIZE][BOARD_WIDTH/UNIT_SIZE];

    Random random;

    GamePanel(){
        random = new Random();
        this.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addMouseListener(this);
        startGame();
    }

    private void startGame(){
        setMines();
        print();
    }

    private void setMines(){
        for(int i=0; i<AMOUNT_OF_MINES; ) {
            int x = random.nextInt(BOARD_WIDTH / UNIT_SIZE);
            int y = random.nextInt(BOARD_HEIGHT / UNIT_SIZE);
            if(board[y][x] != -1) {
                board[y][x] = -1;
                i++;
            }
        }
    }

    private void checkSquare(int x, int y){
        if(board[y][x] == -1){
            isRunning = false;
            gameOver(getGraphics());
        }else {
            board[y][x] = countMines(x,y);
            repaint();
        }
    }

    private int countMines(int x, int y){
        int count = 0;
        // Górny rząd
        if (y > 0) {
            if (x > 0 && board[y - 1][x - 1] == -1) count++;
            if (board[y - 1][x] == -1) count++;
            if (x < board[0].length - 1 && board[y - 1][x + 1] == -1) count++;
        }

        // Środkowy rząd
        if (x > 0 && board[y][x - 1] == -1) count++;
        if (x < board[0].length - 1 && board[y][x + 1] == -1) count++;

        // Dolny rząd
        if (y < board.length - 1) {
            if (x > 0 && board[y + 1][x - 1] == -1) count++;
            if (board[y + 1][x] == -1) count++;
            if (x < board[0].length - 1 && board[y + 1][x + 1] == -1) count++;
        }
        return count;
    }

    private void travel(int x,int y, int distance){
        if(x<0 || y<0 || y>=board.length || x>=board[0].length || distance>=DISTANCE || board[y][x]==-1 || board[y][x] == 10)
            return;

        int mines = countMines(x,y);

        if(mines == 0)
            board[y][x] = 9;
        else
            board[y][x] = mines;

        travel(x+1, y, distance+1);
        travel(x-1, y, distance+1);
        travel(x, y+1, distance+1);
        travel(x, y-1, distance+1);
    }

    private void gameOver(Graphics g){
        switch(result){
            case 'l' -> {
                g.setColor(Color.red);
                g.setFont(new Font("Ink Free", Font.BOLD, 75));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("Game Over: LOST", (BOARD_WIDTH - metrics.stringWidth("Game Over"))/2, BOARD_HEIGHT/2);
            }
            case 'w' -> {
                g.setColor(Color.green);
                g.setFont(new Font("Ink Free", Font.BOLD, 75));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("Game Over: WIN", (BOARD_WIDTH - metrics.stringWidth("Game Over"))/2, BOARD_HEIGHT/2);
            }
        }
    }

    private boolean ifWin(){
        for(int i=0; i< board.length; i++){
            for (int j=0; j<board[0].length; j++){
                if(board[i][j] == 0)
                    return false;
            }
        }
        result = 'w';
        isRunning = false;
        return true;
    }

    private void print(){
        for(int i=0; i< board.length; i++){
            for(int j=0; j<board[i].length; j++){
                System.out.print(" ["+board[i][j]+"] ");
            }
            System.out.println();
        }
//        for(int i=0; i< board.length; i++){
//            for(int j=0; j<board[i].length; j++){
//                System.out.print(" ["+flags[i][j]+"] ");
//            }
//            System.out.println();
//        }
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);

        if(isRunning) {
            //matrix
            for (int i = 0; i < BOARD_WIDTH; i += UNIT_SIZE)
                g.drawLine(i, 0, i, BOARD_HEIGHT);
            for (int i = 0; i < BOARD_HEIGHT; i += UNIT_SIZE)
                g.drawLine(0, i, BOARD_WIDTH, i);

            //board
            for (int i = 0; i < board.length; i++) {
                for (int j = 0; j < board[i].length; j++) {
                    if(flags[i][j]){
                        g.setColor(Color.GREEN);
                        g.fillOval(j * UNIT_SIZE, i * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    }
                    else if (board[i][j] == 9) {
                        g.setColor(Color.GRAY);
                        g.fillRect(j * UNIT_SIZE, i * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    } else if (board[i][j] != -1 && board[i][j] != 0) {
                        g.setColor(Color.GRAY);
                        g.fillRect(j * UNIT_SIZE, i * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

                        g.setColor(Color.WHITE);
                        String text = String.valueOf(board[i][j]);
                        g.setFont(new Font("Ink Free", Font.BOLD, UNIT_SIZE));

                        FontMetrics metrics = g.getFontMetrics();
                        int x = j * UNIT_SIZE + (UNIT_SIZE - metrics.stringWidth(text)) / 2;
                        int y = i * UNIT_SIZE + (UNIT_SIZE - metrics.getHeight()) / 2 + metrics.getAscent();

                        g.drawString(text, x, y);
                    }
                }
            }
        }else{
            gameOver(g);
            return;
        }
        print();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / UNIT_SIZE;
        int y = e.getY() / UNIT_SIZE;
        System.out.println("X:"+x+" Y:"+y);

        switch(e.getButton()){
            case 1->{
                checkSquare(x, y);
                travel(x,y,0);

            }
            case 3->{
                flags[y][x] = !flags[y][x];
                repaint();
            }
        }
        ifWin();
    }

    // do not use
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
}
