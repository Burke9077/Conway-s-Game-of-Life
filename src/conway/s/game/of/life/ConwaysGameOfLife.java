package conway.s.game.of.life;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author burke9077
 */
public class ConwaysGameOfLife extends JFrame implements ActionListener {
    private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(800, 604);
    private static final int BLOCK_SIZE = 10;
    private JMenuBar mb_menu;
    private JMenu m_file, m_game, m_help;
    private JMenuItem mi_file_options, mi_file_exit;
    private JMenuItem mi_game_autofill, mi_game_play, mi_game_stop, mi_game_reset;
    private JMenuItem mi_help_about, mi_help_source;
    private int i_movesPerSecond = 3;
    private boolean[][] b_gameBoard;
    private GameBoard gameBoard;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Setup the swing specifics
        JFrame game = new ConwaysGameOfLife();
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setTitle("Conway's Game of Life");
        game.setIconImage(new ImageIcon(ConwaysGameOfLife.class.getResource("/images/logo.png")).getImage());
        game.setSize(DEFAULT_WINDOW_SIZE);
        game.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - game.getWidth())/2, 
                (Toolkit.getDefaultToolkit().getScreenSize().height - game.getHeight())/2);
        game.setVisible(true);
    }
    
    public ConwaysGameOfLife() {
        // Setup menu
        mb_menu = new JMenuBar();
        setJMenuBar(mb_menu);
        m_file = new JMenu("File");
        mb_menu.add(m_file);
        m_game = new JMenu("Game");
        mb_menu.add(m_game);
        m_help = new JMenu("Help");
        mb_menu.add(m_help);
        mi_file_options = new JMenuItem("Options");
        mi_file_options.addActionListener(this);
        mi_file_exit = new JMenuItem("Exit");
        mi_file_exit.addActionListener(this);
        m_file.add(mi_file_options);
        m_file.add(new JSeparator());
        m_file.add(mi_file_exit);
        mi_game_autofill = new JMenuItem("Autofill");
        mi_game_play = new JMenuItem("Play");
        mi_game_stop = new JMenuItem("Stop");
        mi_game_reset = new JMenuItem("Reset");
        m_game.add(mi_game_autofill);
        m_game.add(new JSeparator());
        m_game.add(mi_game_play);
        m_game.add(mi_game_stop);
        m_game.add(mi_game_reset);
        mi_help_about = new JMenuItem("About");
        mi_help_source = new JMenuItem("Source");
        m_help.add(mi_help_about);
        m_help.add(mi_help_source);
        // Setup the game board
        gameBoard = new GameBoard();
        add(gameBoard);
        // Deal with users wanting to resize the window
        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                int width = getGameBoardDimension().width;
                int height = getGameBoardDimension().height;
                while (width%BLOCK_SIZE != 0) {
                    width++;
                }
                while (height%BLOCK_SIZE != 0) {
                    height++;
                }
                setGameBoardDimension(new Dimension(width, height));
            }
            @Override
            public void componentMoved(ComponentEvent e) {}
            @Override
            public void componentShown(ComponentEvent e) {}
            @Override
            public void componentHidden(ComponentEvent e) {}
        });
    }
    
    public Dimension getGameBoardDimension() {
        return gameBoard.getSize();
    }
    
    public void setGameBoardDimension(Dimension newDimension) {
        gameBoard.setSize(newDimension);
    }
    
    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource().equals(mi_file_exit)) {
            // Exit the game
            System.exit(0);
        } else if (ae.getSource().equals(mi_file_options)) {
            // Put up an options panel to change the number of moves per second
            JFrame f_options = new JFrame();
            f_options.setTitle("Options");
            f_options.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - f_options.getWidth())/2, 
                (Toolkit.getDefaultToolkit().getScreenSize().height - f_options.getHeight())/2);
            f_options.setResizable(false);
            JPanel p_options = new JPanel();
            p_options.setOpaque(false);
            f_options.add(p_options);
            p_options.add(new JLabel("Number of moves per second:"));
            Integer[] secondOptions = {1,2,3,4,5,10,15,20};
            final JComboBox cb_seconds = new JComboBox(secondOptions);
            p_options.add(cb_seconds);
            cb_seconds.setSelectedItem(i_movesPerSecond);
            cb_seconds.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent ae) {
                    i_movesPerSecond = (Integer)cb_seconds.getSelectedItem();
                }
            });
            f_options.setVisible(true);
        } else if (ae.getSource().equals(mi_game_autofill)) {
            // Put up the JFrame to let the user auto fill a certain part of the screen
        }
    }
    
    private class GameBoard extends JPanel implements MouseListener {
        private List<Point> fillCells;
        private List<Point> killCells;

        public GameBoard() {
            fillCells = new ArrayList<>(1);
            killCells = new ArrayList<>(1);
            this.addMouseListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (Point fillCell : fillCells) {
                int cellX = BLOCK_SIZE + (fillCell.x * BLOCK_SIZE);
                int cellY = BLOCK_SIZE + (fillCell.y * BLOCK_SIZE);
                g.setColor(Color.BLUE);
                g.fillRect(cellX, cellY, BLOCK_SIZE, BLOCK_SIZE);
            }
            for (Point killCell : killCells) {
                int cellX = BLOCK_SIZE + (killCell.x * BLOCK_SIZE);
                int cellY = BLOCK_SIZE + (killCell.y * BLOCK_SIZE);
                g.clearRect(cellX, cellY, BLOCK_SIZE, BLOCK_SIZE);
            }
            g.setColor(Color.BLACK);
            g.drawRect(BLOCK_SIZE, BLOCK_SIZE, getWidth()-BLOCK_SIZE*2, getHeight()-BLOCK_SIZE*2);
            for (int i = BLOCK_SIZE; i <= getWidth()-BLOCK_SIZE*2; i += BLOCK_SIZE) {
                g.drawLine(i, BLOCK_SIZE, i, getHeight()-BLOCK_SIZE);
            }
            for (int i = BLOCK_SIZE; i <= getHeight()-BLOCK_SIZE*2; i += BLOCK_SIZE) {
                g.drawLine(BLOCK_SIZE, i, getWidth()-BLOCK_SIZE, i);
            }
            // Setup the array based on the new game board size
            b_gameBoard = new boolean[((getWidth()-BLOCK_SIZE*2)/10)][((getHeight()-BLOCK_SIZE*2)/10)];
            repaint();
        }

        public void fillCell(int x, int y) {
            fillCells.add(new Point(x, y));
        }
        
        public void killCell(int x, int y) {
            killCells.add(new Point(x, y));
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            // Check to make sure the user clicked in a valid area
            int xMaxValid = getWidth()-(getWidth()%10)-BLOCK_SIZE;
            int yMaxValid = getHeight()-(getHeight()%10)-BLOCK_SIZE;
        }
        @Override
        public void mouseClicked(MouseEvent me) {}
        @Override
        public void mousePressed(MouseEvent me) {}
        @Override
        public void mouseEntered(MouseEvent me) {}
        @Override
        public void mouseExited(MouseEvent me) {}
    }
}
