/*
 * things to be added:
 * adjust the full screen responsiveiness
 * add a high score button
 * add a timer
 * design the button.
 * 
 */



import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.Timer;

public class MatchCards {

    class Card {
        String cardName;
        ImageIcon cardImageIcon;

        //ctor
        Card(String cardName, ImageIcon cardImageIcon) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    //list of all cards to track cardNames
    String[] cardList = { 
        "alpine",
        "aston_martin",
        "ferrari",
        "haas",
        "mclaren",
        "mercedes",
        "RB_VCARB_01",
        "red_bull",
        "kick",
        "williams"
    };

    int rows = 4;
    int columns = 5;
    int cardWidth = 100;
    int cardHeight = 130;

    //Creates a deck of cards with cardNames and cardImageIcons
    ArrayList<Card> cardSet; 

    //Image Icon to track all the cards when flipped back.
    ImageIcon cardBackImageIcon;

    int boardWidth = columns * cardWidth; //5*128 = 640px
    int boardHeight = rows * cardHeight; //4*90 = 360px

    JFrame frame = new JFrame("F1 Match Cards Memory Game");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    ArrayList<JButton> board;
    Timer hideCardTimer; //let the user see cards after 1.5 seconds before flipped
    boolean gameReady = false;
    JButton card1Selected;
    JButton card2Selected;
    
    MatchCards() {
        setupCards();
        shuffleCards();

        
        //frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //error text
        textLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Errors: " + Integer.toString(errorCount));

        textPanel.setPreferredSize(new Dimension(boardWidth, 30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        //card game board
        board = new ArrayList<JButton>();
        boardPanel.setLayout(new GridLayout(rows, columns));
        for (int i = 0; i < cardSet.size(); i++) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWidth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!gameReady) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();
                    if (tile.getIcon() == cardBackImageIcon) {
                        if (card1Selected == null) {
                            card1Selected = tile;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon(cardSet.get(index).cardImageIcon);
                        }
                        else if (card2Selected == null) {
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon(cardSet.get(index).cardImageIcon);

                            if (card1Selected.getIcon() != card2Selected.getIcon()) {
                                errorCount += 1;
                                textLabel.setText("Errors: " + Integer.toString(errorCount));
                              hideCardTimer.start();
                            }
                            else {
                                card1Selected = null;
                                card2Selected = null;
                            }
                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        //restart game button
        restartButton.setFont(new Font("Arial", Font.PLAIN, 16));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize(new Dimension(boardWidth, 30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameReady) {
                    return;
                }

                gameReady = false;
                restartButton.setEnabled(false);
                card1Selected = null;
                card2Selected = null;
                shuffleCards();

                //re assign buttons with new cards
                for (int i = 0; i < board.size(); i++) {
                    board.get(i).setIcon(cardSet.get(i).cardImageIcon);
                }

                errorCount = 0;
                textLabel.setText("Errors: " + Integer.toString(errorCount));
                hideCardTimer.start();
            }
        });
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        //start game
        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false); //to ensure it only excutes once.
        hideCardTimer.start();

        

    }

    void setupCards() {
        cardSet = new ArrayList<>();
        for (String cardName : cardList) {
            URL imageUrl = getClass().getResource("/f1_cars/" + cardName + ".jpg");
            if (imageUrl != null) {
                Image cardImg = new ImageIcon(imageUrl).getImage();
                ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
                Card card = new Card(cardName, cardImageIcon);
                cardSet.add(card);
            } else {
                System.err.println("\n F1 car image not found for: " + cardName);
            }
        }
    
        cardSet.addAll(cardSet);
    
        URL backImageUrl = getClass().getResource("/f1_cars/f1_back.jpg");
        if (backImageUrl != null) {
            Image cardBackImg = new ImageIcon(backImageUrl).getImage();
            cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH));
        } else {
            System.err.println("\n F1 themed Back image not found.");
        }
    }
    

    void shuffleCards() {
        System.out.println("\n" + cardSet);
        //shuffle
        for (int i = 0; i < cardSet.size(); i++) {
            int j = (int) (Math.random() * cardSet.size()); //get random index
            //swap
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j));
            cardSet.set(j, temp);
        }
        System.out.println("\n" + cardSet + "\n");
    }

    
    void hideCards() {

        if (gameReady && card1Selected != null && card2Selected != null) { //only flip 2 cards
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;
        }
        else { //flip all cards face down
            for (int i = 0; i < board.size(); i++) {
                board.get(i).setIcon(cardBackImageIcon);
            }
            gameReady = true;
            restartButton.setEnabled(true);
        }
    }
    

   
}