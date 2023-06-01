package SnakeLadder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class SnakeAndLadderService extends JFrame {

    private final SnakeAndLadderBoard snakeAndLadderBoard;
    private Queue<Player> players;
    private boolean isGameCompleted;
    private int noOfDices;
    private boolean shouldGameContinueTillLastPlayer;
    private boolean shouldAllowMultipleDiceRollOnSix;
    private static final int DEFAULT_BOARD_SIZE = 100;
    private static final int DEFAULT_NO_OF_DICES = 1;

    private JLabel currentPlayerLabel;
    private JTextArea logTextArea;
    private JButton rollDiceButton;

    public SnakeAndLadderService(int boardSize) {
        this.snakeAndLadderBoard = new SnakeAndLadderBoard(boardSize);
        this.players = new LinkedList<Player>();
        this.noOfDices = SnakeAndLadderService.DEFAULT_NO_OF_DICES;

        initializeGUI();
        setupGame();
    }

    private void initializeGUI() {
        setTitle("Snake and Ladder Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        currentPlayerLabel = new JLabel();
        add(currentPlayerLabel, BorderLayout.NORTH);

        logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        add(scrollPane, BorderLayout.CENTER);

        rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rollDice();
            }
        });
        add(rollDiceButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupGame() {
        int noOfSnakes = Integer.parseInt(JOptionPane.showInputDialog("Enter number of Snakes:"));
        List<Snake> snakes = new ArrayList<Snake>();
        for (int i = 0; i < noOfSnakes; i++) {
            int start = Integer.parseInt(JOptionPane.showInputDialog("Enter the starting position for Snake " + (i + 1) + ":"));
            int end = Integer.parseInt(JOptionPane.showInputDialog("Enter the ending position for Snake " + (i + 1) + ":"));
            snakes.add(new Snake(start, end));
        }

        int noOfLadders = Integer.parseInt(JOptionPane.showInputDialog("Enter number of Ladders:"));
        List<Ladder> ladders = new ArrayList<Ladder>();
        for (int i = 0; i < noOfLadders; i++) {
            int start = Integer.parseInt(JOptionPane.showInputDialog("Enter the starting position for Ladder " + (i + 1) + ":"));
            int end = Integer.parseInt(JOptionPane.showInputDialog("Enter the ending position for Ladder " + (i + 1) + ":"));
            ladders.add(new Ladder(start, end));
        }

        int noOfPlayers = Integer.parseInt(JOptionPane.showInputDialog("Enter number of players:"));
        List<Player> playerList = new ArrayList<Player>();
        for (int i = 0; i < noOfPlayers; i++) {
            String playerName = JOptionPane.showInputDialog("Enter the name of Player " + (i + 1) + ":");
            playerList.add(new Player(playerName));
        }

        snakeAndLadderBoard.setSnakes(snakes);
        snakeAndLadderBoard.setLadders(ladders);

        setPlayers(playerList);
        currentPlayerLabel.setText("Current Player: " + players.peek().getName());
        logTextArea.append("Game Setup Complete!\n");

        startGame();
    }

    private void setPlayers(List<Player> playerList) {
        this.players = new LinkedList<Player>();
        Map<String, Integer> playerPieces = new HashMap<String, Integer>();
        for (Player player : playerList) {
            this.players.add(player);
            playerPieces.put(player.getId(), 0);
        }
        snakeAndLadderBoard.setPlayerPieces(playerPieces);
    }

    private int getNewPositionAfterGoingThroughSnakesAndLadders(int newPosition) {
        int prevPosition;
        do {
            prevPosition = newPosition;
            for (Snake snake : snakeAndLadderBoard.getSnakes()) {
                if (snake.getStart() == newPosition) {
                    newPosition = snake.getEnd();
                }
            }
            for (Ladder ladder : snakeAndLadderBoard.getLadders()) {
                if (ladder.getStart() == newPosition) {
                    newPosition = ladder.getEnd();
                }
            }
        } while (prevPosition != newPosition);
        return newPosition;
    }

    private void movePlayer(Player player, int positions) {
        int oldPosition = snakeAndLadderBoard.getPlayerPieces().get(player.getId());
        int newPosition = oldPosition + positions;
        int boardSize = snakeAndLadderBoard.getSize();

        if (newPosition > boardSize) {
            newPosition = oldPosition;
        } else {
            newPosition = getNewPositionAfterGoingThroughSnakesAndLadders(newPosition);
        }

        snakeAndLadderBoard.getPlayerPieces().put(player.getId(), newPosition);

        logTextArea.append(player.getName() + " rolled a " + positions + " and moved from " + oldPosition + " to " + newPosition + "\n");
    }

    private int getTotalValueAfterDiceRolls() {
        return DiceService.roll();
    }

    private boolean hasPlayerWon(Player player) {
        int playerPosition = snakeAndLadderBoard.getPlayerPieces().get(player.getId());
        int winningPosition = snakeAndLadderBoard.getSize();
        return playerPosition == winningPosition;
    }

    private boolean isGameCompleted() {
        int currentNumberOfPlayers = players.size();
        return currentNumberOfPlayers < snakeAndLadderBoard.getInitialNoOfPlayers();
    }

    private void rollDice() {
        if (isGameCompleted()) {
            logTextArea.append("Game Over\n");
            rollDiceButton.setEnabled(false);
            return;
        }

        Player currentPlayer = players.peek();
        int totalDiceValue = getTotalValueAfterDiceRolls();
        movePlayer(currentPlayer, totalDiceValue);

        if (hasPlayerWon(currentPlayer)) {
            logTextArea.append(currentPlayer.getName() + " wins the game!\n");
            snakeAndLadderBoard.getPlayerPieces().remove(currentPlayer.getId());
        } else {
            players.add(players.poll());
            currentPlayerLabel.setText("Current Player: " + players.peek().getName());
        }
    }

    private void startGame() {
        logTextArea.append("Game Started!\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new SnakeAndLadderService(DEFAULT_BOARD_SIZE);
            }
        });
    }

//    public SnakeAndLadderService() {
//        this(SnakeAndLadderService.DEFAULT_BOARD_SIZE);
//    }
    /*
     * ====Setters for making the game more extensible====
     */

//    public void setNoOfDices(int noOfDices) {
//        this.noOfDices = noOfDices;
//    }
//
//    public void setShouldGameContinueTillLastPlayer(boolean shouldGameContinueTillLastPlayer) {
//        this.shouldGameContinueTillLastPlayer = shouldGameContinueTillLastPlayer;
//    }
//
//    public void setShouldAllowMultipleDiceRollOnSix(boolean shouldAllowMultipleDiceRollOnSix) {
//        this.shouldAllowMultipleDiceRollOnSix = shouldAllowMultipleDiceRollOnSix;
//    }
    
    /*
     * ==================Initialize board==================
     */
//	public void setPlayers(List<Player> players) {
//        this.players = new LinkedList<Player>();
//        this.initialnoofplayers = players.size();
//        Map<String, Integer> playerPieces = new HashMap<String, Integer>();
//        for (Player player : players) {
//            this.players.add(player);
//            playerPieces.put(player.getId(), 0); //Each player has a piece which is initially kept outside the board (i.e., at position 0).
//        }
//        snakeAndLadderBoard.setPlayerPieces(playerPieces); //  Add pieces to board
//    }
	
	public void setSnakes(List<Snake> snakes) {
		snakeAndLadderBoard.setSnakes(snakes);	//Add Snake to board
	}
	public void setLadders(List<Ladder> ladders) {
        snakeAndLadderBoard.setLadders(ladders); // Add ladders to board
    }
	
	/*
     * ==========Core business logic for the game==========
     */

//    private int getNewPositionAfterGoingThroughSnakesAndLadders(int newPosition) {
//
//    	int prevposition;
//    	do {
//    		prevposition = newPosition;
//    		for (Snake snake : snakeAndLadderBoard.getSnakes()) {
//                if (snake.getStart() == newPosition) {
//                    newPosition = snake.getEnd(); // Whenever a piece ends up at a position with the head of the snake, the piece should go down to the position of the tail of that snake.
//                }
//            }
//
//    		for (Ladder ladder : snakeAndLadderBoard.getLadders()) {
//                if (ladder.getStart() == newPosition) {
//                    newPosition = ladder.getEnd(); // Whenever a piece ends up at a position with the start of the ladder, the piece should go up to the position of the end of that ladder.
//                }
//            }
//
//    	}while(prevposition!=newPosition);
//
//    	return newPosition;
//
//    }
    
//    private void movePlayer(Player player, int positions) {
//
//        int oldPosition = snakeAndLadderBoard.getPlayerPieces().get(player.getId());
//        // Based on the dice value, the player moves their piece forward
//        //that number of cells.
//        int newPosition = oldPosition + positions;
//
//        int boardSize = snakeAndLadderBoard.getSize();
//
//        // Can modify this logic to handle side case when there are multiple dices
//        //(Optional requirements)
//        if (newPosition > boardSize) {
//        	// After the dice roll, if a piece is supposed to move
//        	//outside position 100, it does not move.
//            newPosition = oldPosition;
//        } else {
//            newPosition = getNewPositionAfterGoingThroughSnakesAndLadders(newPosition);
//        }
//
//        snakeAndLadderBoard.getPlayerPieces().put(player.getId(), newPosition);
//
//        System.out.println(player.getName() + " rolled a " + positions + " and moved from " + oldPosition +" to " + newPosition);
//
//
//    }
    
//    private int getTotalValueAfterDiceRolls() {
//
//        // Can use noOfDices and setShouldAllowMultipleDiceRollOnSix
//    	//here to get total value (Optional requirements)
//        return DiceService.roll();
//    }
//    private boolean hasPlayerWon(Player player) {
//
//        // Can change the logic a bit to handle special cases
//    	//when there are more than one dice (Optional requirements)
//        int playerPosition = snakeAndLadderBoard.getPlayerPieces().get(player.getId());
//        int winningPosition = snakeAndLadderBoard.getSize();
//        return playerPosition == winningPosition;
//        // A player wins if it exactly reaches the position 100 and the game ends there.
//    }
    
//    private boolean isGameCompleted() {
//        // Can use shouldGameContinueTillLastPlayer to
//    	//change the logic of determining if game is completed (Optional requirements)
//        int currentNumberOfPlayers = players.size();
//        return currentNumberOfPlayers < snakeAndLadderBoard.getInitialNoOfPlayers();
//    }

//    public void startGame() {
//        while (!isGameCompleted()) {
//            int totalDiceValue = getTotalValueAfterDiceRolls(); // Each player rolls the dice when their turn comes.
//            Player currentPlayer = players.poll();
//            movePlayer(currentPlayer, totalDiceValue);
//            if (hasPlayerWon(currentPlayer)) {
//                System.out.println(currentPlayer.getName() + " wins the game");
//                snakeAndLadderBoard.getPlayerPieces().remove(currentPlayer.getId());
//            } else {
//                players.add(currentPlayer);
//            }
//        }
//    }

    
}
