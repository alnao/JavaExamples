import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class RockPaperScissors extends JFrame {
    private static final int ROUNDS_TO_WIN = 3;
    private static final String[] CHOICES = {"Carta", "Forbice", "Sasso"};
    
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private int player1Score = 0;
    private int player2Score = 0;
    private int currentRound = 1;
    private String player1Choice = "";
    private String player2Choice = "";
    private String gameMode = "";
    private Random random = new Random();
    
    // Riferimenti ai componenti che devono essere aggiornati
    private JLabel resultLabel;
    private JLabel choicesLabel;
    private JLabel finalWinnerLabel;
    private JLabel finalScoreLabel;

    public RockPaperScissors() {
        setTitle("Carta, Forbice, Sasso");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        createMenuPanel();
        createGamePanel("player1");
        createGamePanel("player2");
        createResultPanel();
        createFinalResultPanel();

        add(cardPanel);
    }

    private void createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Carta, Forbice, Sasso");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton pvpButton = createStyledButton("Giocatore vs Giocatore");
        JButton pvcButton = createStyledButton("Giocatore vs Computer");
        JButton exitButton = createStyledButton("Esci");

        pvpButton.addActionListener(e -> {
            gameMode = "PvP";
            resetGame();
            cardLayout.show(cardPanel, "player1");
        });

        pvcButton.addActionListener(e -> {
            gameMode = "PvC";
            resetGame();
            cardLayout.show(cardPanel, "player1");
        });

        exitButton.addActionListener(e -> System.exit(0));

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(titleLabel);
        menuPanel.add(Box.createVerticalStrut(50));
        menuPanel.add(pvpButton);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(pvcButton);
        menuPanel.add(Box.createVerticalStrut(20));
        menuPanel.add(exitButton);
        menuPanel.add(Box.createVerticalGlue());

        cardPanel.add(menuPanel, "menu");
    }

    private void createGamePanel(String player) {
        JPanel gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JLabel scoreLabel = new JLabel();
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel roundLabel = new JLabel();
        roundLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel playerLabel = new JLabel(player.equals("player1") ? "Giocatore 1: Scegli" : "Giocatore 2: Scegli");
        playerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JPanel buttonPanel = new JPanel();
        for (String choice : CHOICES) {
            JButton button = createStyledButton(choice);
            button.addActionListener(e -> handleChoice(player, choice));
            buttonPanel.add(button);
        }

        gamePanel.add(Box.createVerticalGlue());
        gamePanel.add(scoreLabel);
        gamePanel.add(Box.createVerticalStrut(10));
        gamePanel.add(roundLabel);
        gamePanel.add(Box.createVerticalStrut(30));
        gamePanel.add(playerLabel);
        gamePanel.add(Box.createVerticalStrut(30));
        gamePanel.add(buttonPanel);
        gamePanel.add(Box.createVerticalGlue());

        cardPanel.add(gamePanel, player);
    }

    private void createResultPanel() {
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        resultLabel = new JLabel();
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 20));

        choicesLabel = new JLabel();
        choicesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton continueButton = createStyledButton("Continua");
        continueButton.addActionListener(e -> {
            if (player1Score >= ROUNDS_TO_WIN || player2Score >= ROUNDS_TO_WIN || currentRound > ROUNDS_TO_WIN * 2) {
                updateFinalResultPanel();
                cardLayout.show(cardPanel, "finalResult");
            } else {
                currentRound++;
                player1Choice = "";
                player2Choice = "";
                cardLayout.show(cardPanel, "player1");
            }
        });

        resultPanel.add(Box.createVerticalGlue());
        resultPanel.add(resultLabel);
        resultPanel.add(Box.createVerticalStrut(20));
        resultPanel.add(choicesLabel);
        resultPanel.add(Box.createVerticalStrut(40));
        resultPanel.add(continueButton);
        resultPanel.add(Box.createVerticalGlue());

        cardPanel.add(resultPanel, "result");
    }

    private void createFinalResultPanel() {
        JPanel finalResultPanel = new JPanel();
        finalResultPanel.setLayout(new BoxLayout(finalResultPanel, BoxLayout.Y_AXIS));
        finalResultPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        finalWinnerLabel = new JLabel();
        finalWinnerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        finalWinnerLabel.setFont(new Font("Arial", Font.BOLD, 24));

        finalScoreLabel = new JLabel();
        finalScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        finalScoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton playAgainButton = createStyledButton("Gioca ancora");
        JButton menuButton = createStyledButton("Menu principale");

        playAgainButton.addActionListener(e -> {
            resetGame();
            cardLayout.show(cardPanel, "player1");
        });

        menuButton.addActionListener(e -> {
            resetGame();
            cardLayout.show(cardPanel, "menu");
        });

        finalResultPanel.add(Box.createVerticalGlue());
        finalResultPanel.add(finalWinnerLabel);
        finalResultPanel.add(Box.createVerticalStrut(20));
        finalResultPanel.add(finalScoreLabel);
        finalResultPanel.add(Box.createVerticalStrut(40));
        finalResultPanel.add(playAgainButton);
        finalResultPanel.add(Box.createVerticalStrut(20));
        finalResultPanel.add(menuButton);
        finalResultPanel.add(Box.createVerticalGlue());

        cardPanel.add(finalResultPanel, "finalResult");
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        return button;
    }

    private void handleChoice(String player, String choice) {
        if (player.equals("player1")) {
            player1Choice = choice;
            if (gameMode.equals("PvP")) {
                cardLayout.show(cardPanel, "player2");
            } else {
                player2Choice = CHOICES[random.nextInt(CHOICES.length)];
                determineRoundWinner();
                updateResultPanel();
                cardLayout.show(cardPanel, "result");
            }
        } else {
            player2Choice = choice;
            determineRoundWinner();
            updateResultPanel();
            cardLayout.show(cardPanel, "result");
        }
    }

    private void determineRoundWinner() {
        if (player1Choice.equals(player2Choice)) {
            return;
        }

        if ((player1Choice.equals("Carta") && player2Choice.equals("Sasso")) ||
            (player1Choice.equals("Forbice") && player2Choice.equals("Carta")) ||
            (player1Choice.equals("Sasso") && player2Choice.equals("Forbice"))) {
            player1Score++;
        } else {
            player2Score++;
        }
    }

    private void updateResultPanel() {
        String roundResult = player1Choice.equals(player2Choice) ? "Pareggio!" :
                           (player1Score > player2Score ? "Giocatore 1 vince il round!" :
                           (gameMode.equals("PvP") ? "Giocatore 2 vince il round!" : "Computer vince il round!"));

        resultLabel.setText(roundResult);
        choicesLabel.setText(String.format("G1: %s vs %s: %s", 
                           player1Choice, 
                           gameMode.equals("PvP") ? "G2" : "PC", 
                           player2Choice));
    }

    private void updateFinalResultPanel() {
        String finalWinner = player1Score > player2Score ? "Giocatore 1 vince la partita!" :
                           player2Score > player1Score ? (gameMode.equals("PvP") ? "Giocatore 2" : "Computer") + " vince la partita!" :
                           "Partita terminata in pareggio!";

        finalWinnerLabel.setText(finalWinner);
        finalScoreLabel.setText(String.format("Punteggio finale: %d - %d", player1Score, player2Score));
    }

    private void resetGame() {
        player1Score = 0;
        player2Score = 0;
        currentRound = 1;
        player1Choice = "";
        player2Choice = "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RockPaperScissors game = new RockPaperScissors();
            game.setVisible(true);
        });
    }
}