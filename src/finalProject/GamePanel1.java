// Jerome Kim
// May 16, 2022
// GamePanel1.java
// Class with JPanel as its parent class for single player game screen. 
// The player avoids being hit by the computer enemy jet by moving around
// and shooting them. The enemy jets spawn outside of the screen, and they 
// respawn immediately after they are hit.
package finalProject;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel1 extends JPanel implements Runnable {

	// The dimensions of the panel is the size of the screen
	static final Dimension panelSize = Toolkit.getDefaultToolkit().getScreenSize();
	static final int PANEL_WIDTH = (int) panelSize.getWidth();
	// set panel height relative to the screen size minus 20 because
	// the height of the title bar is 30.
	static final int PANEL_HEIGHT = (int) panelSize.getHeight() - 30;
	static final int fps = 144;
	int frameCount = 0; // number of frames passed
	int timeCount = 0; // time passed in seconds
	int gameState; // the state in which the game is at
	int menuState = 1;
	int playState = 2;
	int endState = 3;
	int enemyStartCount = 1; // number of enemies to begin with
	int score = 0;
	int highScore;
	int recordCount;
	boolean running;
	boolean mouseInPanel;
	boolean diffScreen = true; // difficulty screen
	String playerName;
	String highScorePlayer;
	Player player = new Player(); // create Player object
	Item items = new Item(); // create Item object
	Thread gameThread; // create Thread
	BufferedImage jet; // Images
	BufferedImage enemyJet;
	BufferedImage background;
	BufferedImage item1;
	BufferedImage item2;
	Font pixelFont; // Font
	JTextField tf; // TextField for user input
	Point p; // point for mouse cursor location
	ArrayList<Enemy> enemy = new ArrayList<Enemy>(); // create enemy array list
	File recordFile = new File("src\\files\\highScore.txt");
	Scanner freader;
	BufferedWriter fwriter;

	/**
	 * 
	 * Create a new GamePanel1 with JPanel as its parent class.
	 *
	 */
	public GamePanel1() {
		this.setPreferredSize(panelSize); // set preferred size to the screen size
		this.setMaximumSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setBackground(Color.BLACK);
		this.addKeyListener(new MyKeyAdapter()); // add KeyLisetener for key input
		this.addMouseListener(new MyMouseAdapter()); // add MouseListener for mouse input
		this.setFocusable(true);
		this.setLayout(null); // set layout manager as null
		this.setTextField();
		this.setGame(); // set up for the game
		this.startThread(); // start the game thread
	}

	/**
	 * 
	 * Set up variables for the game.
	 * 
	 */
	public void setGame() {
		gameState = menuState;
		// Initialize instance variables
		player.x = PANEL_WIDTH / 2 - Player.width / 2; // set location for Player
		player.y = PANEL_HEIGHT / 2 - Player.height / 2;
		player.setPlayer(); // use set player method
		for (int i = 0; i < enemyStartCount; i++) {
			enemy.add(new Enemy());
			enemy.get(i).setEnemy();
		}
		items.setItem(); // setItem method from Item class
		try { // error handling with file
				// Set images
			jet = ImageIO.read(new File("src\\files\\spaceJet1.png"));
			enemyJet = ImageIO.read(new File("src\\files\\spaceJet3.png"));
			background = ImageIO.read(new File("src\\files\\background.png"));
			item1 = ImageIO.read(new File("src\\files\\speedUp.png"));
			item2 = ImageIO.read(new File("src\\files\\shootUp.png"));
			// Create and register font
			pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("src\\files\\PixelFont.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(pixelFont);
			// high score record file
			freader = new Scanner(recordFile);
			if (freader.hasNextLine()) {
				highScorePlayer = freader.nextLine(); // get high score player's name from the text file
				highScore = Integer.parseInt(freader.nextLine()); // get high score from the text file
			}
			freader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Method to create and start the Thread of the program.
	 * 
	 */
	public void startThread() {
		gameThread = new Thread(this); // target for the Thread is the GamePanel1 object
		gameThread.start(); // start Thread
	}

	/**
	 * 
	 * Sets the TextField for user's name input.
	 * 
	 */
	public void setTextField() {
		// JTextField for user input
		tf = new JTextField();
		tf.setHorizontalAlignment(JTextField.CENTER);
		tf.setBackground(Color.BLACK);
		tf.setForeground(Color.WHITE);
		tf.setFont(new Font("Monospaced", Font.BOLD, 40));
		tf.setSize(new Dimension(400, 50));
		tf.setBounds((PANEL_WIDTH - tf.getWidth()) / 2, 7 * (PANEL_HEIGHT - tf.getHeight() - 30) / 20, tf.getWidth(),
				tf.getHeight()); // set location and dimension of the text field
	}

	@Override
	// Run method is automatically invoked when start method
	// is called upon gameThread.
	public void run() { // override run method from Runnable interface
		double nanoSeconds = 1000000000 / fps;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		running = true;

		while (running == true) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / nanoSeconds;
			lastTime = currentTime;
			if (delta >= 1) { // for every time frame becomes over 1,
				// get mouse location relative to the frame and panel
				if (gameState == menuState) {
					p = PlayGame.frame.getMousePosition();
				} else if (gameState == playState) {
					p = PlayGame.panel.getMousePosition();
					frameCount++; // increase count of frames passed
				}
				playerMove(); // move player
				playerShoot(); // move player's bullet
				enemyMove(); // move enemy
				checkCollision(); // check collision
				if (frameCount > 0 && frameCount % fps == 0) { // for every second,
					timeCount++; // keep track of time passed in seconds
					addEnemy(); // add enemy depending on the time
				}
				repaint(); // after updating, repaint the visual components onto the screen
				delta--;
			}
		}
	}

	/**
	 * 
	 * Updates the x and y values of the Player's jet based on the keys pressed by
	 * the user.
	 * 
	 */
	public void playerMove() {
		if (gameState == playState && mouseInPanel) { // rotate only when the mouse cursor is on the panel
			try {
				// Set the player's jet's angle towards the mouse cursor
				player.radAngle = Math.atan2((player.y + Player.height / 2) - p.y, p.x - (player.x + Player.width / 2));
				// The atan2 method returns the angle by doing the inverse tan of opposite over
				// adjacent. Here, the length of the opposite is y coordinate of the jet's
				// center minus the the mouse y coordinate. The length of the adjacent is the
				// mouse x coordinate minus the x coordinate of the jet's center.
			} catch (Exception e) {
				e.printStackTrace();
			}
			// Make the radian angle negative because the directions of the graphics rotate
			// method and the trigonometry quadrant are opposites. Add half pi to the radian
			// angle because the angle in rotate method in graphics starts half pi greater
			// than the actual trigonometry quadrant.
			player.radAngle = -player.radAngle + Math.PI / 2;
		}
		player.move(); // use move method from the Player class
	}

	/**
	 *
	 * Updates the x and y values of the bullets based on the user's key input and
	 * the direction of their shooting.
	 * 
	 */
	public void playerShoot() {
		player.shootBullet(); // use shootBullet method from the Player class
	}

	/**
	 * 
	 * Rotates each enemy based on the player's jet location and moves them towards
	 * the player's jet.
	 * 
	 */
	public void enemyMove() {
		if (gameState == playState) {
			for (int i = 0; i < enemy.size(); i++) { // for each enemy
				// Similarly to the playerMove method, set the angle of their direction towards
				// the player.
				enemy.get(i).radAngle = Math.atan2((player.y + Player.height / 2) - (enemy.get(i).y + Enemy.height / 2),
						(enemy.get(i).x + Enemy.width / 2) - (player.x + Player.width / 2));
				// Reverse the direction of the angle because the direction of graphics rotation
				// and trigonometry are opposites. Also, subtract half pi to make the enemy jet
				// image start facing the bottom, which is negative half pi in the trigonometry
				// quadrant.
				enemy.get(i).radAngle = -enemy.get(i).radAngle - Math.PI / 2;
				enemy.get(i).move(); // move method from the Enemy class
			}
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // paintComponent method with parent class (JPanel)
		if (gameState == menuState) // if the user is at the menu screen
			drawMenu(g);
		else if (gameState == playState) // if the game is being played
			drawGame(g);
		else if (gameState == endState) // if the game is over
			drawOver(g);
	}

	/**
	 * 
	 * Draws the menu screen with request for name, play button and controls
	 * description.
	 * 
	 * @param g Graphics object
	 * 
	 */
	public void drawMenu(Graphics g) {
		if (diffScreen == true) { // if on difficulty choosing screen
			// Game title
			g.setColor(Color.WHITE); // set Colour
			g.setFont(pixelFont.deriveFont(180f)); // set font size
			// FontMetrics object for displaying font at a location
			// relative to the font size and String length
			FontMetrics m = getFontMetrics(g.getFont());
			g.drawString("Jet Fighters", (PANEL_WIDTH - m.stringWidth("Jet Fighters")) / 2, 4 * PANEL_HEIGHT / 20);
			// Difficulty Buttons
			g.setColor(Color.LIGHT_GRAY);
			g.setFont(pixelFont.deriveFont(50f)); // set font size
			m = getFontMetrics(g.getFont());
			// Easy Button
			g.fill3DRect((PANEL_WIDTH - m.stringWidth("EASY") - 300) / 2, 8 * PANEL_HEIGHT / 20 - 50,
					m.stringWidth("EASY") + 300, 60, true);
			g.setColor(Color.BLACK);
			g.drawString("EASY", (PANEL_WIDTH - m.stringWidth("EASY")) / 2, 8 * PANEL_HEIGHT / 20);
			// Medium Button
			g.setColor(Color.LIGHT_GRAY);
			g.fill3DRect((PANEL_WIDTH - m.stringWidth("MEDIUM") - 250) / 2, 11 * PANEL_HEIGHT / 20 - 50,
					m.stringWidth("MEDIUM") + 250, 60, true);
			g.setColor(Color.BLACK);
			g.drawString("MEDIUM", (PANEL_WIDTH - m.stringWidth("MEDIUM")) / 2, 11 * PANEL_HEIGHT / 20);
			// Hard Button
			g.setColor(Color.LIGHT_GRAY);
			g.fill3DRect((PANEL_WIDTH - m.stringWidth("HARD") - 300) / 2, 14 * PANEL_HEIGHT / 20 - 50,
					m.stringWidth("HARD") + 300, 60, true);
			g.setColor(Color.BLACK);
			g.drawString("HARD", (PANEL_WIDTH - m.stringWidth("HARD")) / 2, 14 * PANEL_HEIGHT / 20);
		} else { // if on inputing name screen
			// Game title
			g.setColor(Color.WHITE); // set Colour
			g.setFont(pixelFont.deriveFont(180f)); // set font size
			FontMetrics m = getFontMetrics(g.getFont());
			g.drawString("Jet Fighters", (PANEL_WIDTH - m.stringWidth("Jet Fighters")) / 2, 4 * PANEL_HEIGHT / 20);
			// Request for the user's name
			g.setColor(Color.WHITE);
			g.setFont(pixelFont.deriveFont(20f));
			m = getFontMetrics(g.getFont());
			g.drawString("ENTER YOUR NAME:", (PANEL_WIDTH - m.stringWidth("ENTER YOUR NAME:")) / 2,
					6 * PANEL_HEIGHT / 20);
			// Play Button
			g.setColor(Color.LIGHT_GRAY);
			g.setFont(pixelFont.deriveFont(50f)); // set font size
			m = getFontMetrics(g.getFont());
			g.fill3DRect((PANEL_WIDTH - m.stringWidth("PLAY") - 300) / 2, PANEL_HEIGHT / 2 - 50,
					m.stringWidth("PLAY") + 300, 60, true);
			g.setColor(Color.BLACK);
			g.drawString("PLAY", (PANEL_WIDTH - m.stringWidth("PLAY")) / 2, PANEL_HEIGHT / 2);
			// Controls Description
			g.setColor(Color.WHITE);
			g.drawString("Controls", (PANEL_WIDTH - m.stringWidth("Controls")) / 2, 13 * PANEL_HEIGHT / 20);
			g.setFont(pixelFont.deriveFont(30f)); // set font size
			m = getFontMetrics(g.getFont());
			g.drawString("W - Go Up", (PANEL_WIDTH - m.stringWidth("W - Go Up")) / 2, 14 * PANEL_HEIGHT / 20);
			g.drawString("S - Go Down", (PANEL_WIDTH - m.stringWidth("S - Go Down")) / 2, 15 * PANEL_HEIGHT / 20);
			g.drawString("A - Go Left", (PANEL_WIDTH - m.stringWidth("A - Go Left")) / 2, 16 * PANEL_HEIGHT / 20);
			g.drawString("D - Go Right", (PANEL_WIDTH - m.stringWidth("D - Go Right")) / 2, 17 * PANEL_HEIGHT / 20);
			g.drawString("Space - Shoot", (PANEL_WIDTH - m.stringWidth("Space - Shoot")) / 2, 18 * PANEL_HEIGHT / 20);
		}
	}

	/**
	 * Draws the game play screen.
	 * 
	 * @param g Graphics object
	 * 
	 */
	public void drawGame(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // cast Graphics to Graphics2D
		AffineTransform old = g2.getTransform(); // copy the current rotation angle before any rotation
		g.drawImage(background, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null); // draw background image
		g.setColor(Color.RED);
		g.fillOval((int) player.bulletX, (int) player.bulletY, Player.bulletWidth, Player.bulletHeight); // draw
																											// bullet
		g2.rotate(player.radAngle, player.x + Player.width / 2, player.y + Player.height / 2); // rotate player
		g.drawImage(jet, (int) player.x, (int) player.y, Player.width, Player.height, null); // draw player
		g2.setTransform(old); // undo rotation
		if (items.numb == 0) { // if speed boost item is selected by random
			g.drawImage(item1, items.x, items.y, Item.width, Item.height, null); // draw the speed boost item
		} else { // if shoot boost item is selected by random
			g.drawImage(item2, items.x, items.y, Item.width, Item.height, null); // draw the shoot boost item
		}
		for (int i = 0; i < enemy.size(); i++) { // for each enemy
			// rotate enemy
			g2.rotate(enemy.get(i).radAngle, enemy.get(i).x + Enemy.width / 2, enemy.get(i).y + Enemy.height / 2);
			// draw enemy
			g.drawImage(enemyJet, (int) enemy.get(i).x, (int) enemy.get(i).y, Enemy.width, Enemy.height, null);
			g2.setTransform(old); // undo rotation
		}
		g.setColor(Color.WHITE);
		g.setFont(pixelFont.deriveFont(30f)); // set font size
		FontMetrics m = getFontMetrics(g.getFont());
		// Display score
		g.drawString("Score: " + score, (PANEL_WIDTH - m.stringWidth("Score: " + score)), 30);
	}

	/**
	 * Draws the game over screen, which includes the user's score, the leaderboard,
	 * and how to restart.
	 * 
	 * @param g Graphics object
	 * 
	 */
	public void drawOver(Graphics g) {
		// Display Game Over
		g.setColor(Color.WHITE);
		g.setFont(pixelFont.deriveFont(150f)); // set font size
		FontMetrics m = getFontMetrics(g.getFont());
		g.drawString("Game Over", (PANEL_WIDTH - m.stringWidth("Game Over")) / 2, PANEL_HEIGHT / 5);
		// Display user's score
		g.setFont(pixelFont.deriveFont(50f));
		m = getFontMetrics(g.getFont());
		g.drawString("YOUR SCORE: " + playerName + " - " + score,
				(PANEL_WIDTH - m.stringWidth("YOUR SCORE: " + playerName + ": " + score)) / 2, 2 * PANEL_HEIGHT / 5);
		// Display player with the highest score and their score
		g.setColor(Color.YELLOW);
		g.drawString("HIGH SCORE: " + highScorePlayer + " - " + highScore,
				(PANEL_WIDTH - m.stringWidth("HIGH SCORE: " + highScorePlayer + " - " + highScore)) / 2,
				3 * PANEL_HEIGHT / 5);
		// Display how to restart
		g.setColor(Color.WHITE);
		g.drawString("Press R to Restart", (PANEL_WIDTH - m.stringWidth("Press R to Restart")) / 2,
				4 * PANEL_HEIGHT / 5);
		// Display how to return to menu
		g.drawString("Press M to Return to Main Menu",
				(PANEL_WIDTH - m.stringWidth("Press M to Return to Main Menu")) / 2, 9 * PANEL_HEIGHT / 10);
	}

	/**
	 * 
	 * Checks the collision between the player and each enemy, and between the
	 * player's bullet and each enemy.
	 * 
	 */
	public void checkCollision() {
		if (gameState == playState) {
			for (int i = 0; i < enemy.size(); i++) { // for each enemy
				// if the player collides with the enemy
				if (player.rec.intersects(enemy.get(i).rec)) {
					gameState = endState; // move to game over state
					player.leftOn = false;
					player.rightOn = false;
					player.upOn = false;
					player.downOn = false;
					// Reset variables for game restart
					player.x = PANEL_WIDTH / 2 - Player.width / 2; // set location for Player
					player.y = PANEL_HEIGHT / 2 - Player.height / 2;
					player.setPlayer();
					enemy.clear(); // clear the enemy array list
					for (int j = 0; j < enemyStartCount; j++) { // re-add and set enemies
						enemy.add(new Enemy());
						enemy.get(j).setEnemy();
					}
					items.setItem();
					checkHighScore(); // check if the player got the highest score
					break;
				}
				// if the player's bullet hit the enemy jet
				if (player.bulletRec.intersects(enemy.get(i).rec)) {
					enemy.get(i).setEnemy(); // the enemy respawns outside of the panel
					// the player's bullet is no longer alive, making the bullet return to the
					// player's jet
					player.bulletX = player.x + Player.width / 2 - Player.bulletWidth / 2;
					player.bulletY = player.y + Player.height / 2 - Player.bulletHeight / 2;
					player.bulletOn = false;
					score++;
					break;
				}
			}
			if (player.rec.intersects(items.rec)) { // if the player's jet touches the item
				items.setItem();
				if (items.numb == 0) { // if speed boost item
					Player.speed += 0.1; // increase the speed of the player
				} else if (items.numb == 1) { // if shoot boost item
					Player.bulletSpeed += 0.5; // increase the speed of the bullet
				}
			}
		}
	}

	/**
	 * 
	 * Adds a new computer enemy jet to the game every 10 seconds.
	 * 
	 */
	public void addEnemy() {
		if (timeCount > 0 && timeCount % 10 == 0) { // for every 10 seconds
			enemy.add(new Enemy()); // add a new enemy
			enemy.get(enemy.size() - 1).setEnemy(); // set the new enemy
		}
	}

	/**
	 * 
	 * Checks if the player got the highest score. If they did, it replaces the
	 * recorded player's name and their score in the highScore.txt file.
	 * 
	 */
	public void checkHighScore() {
		// if the player's score is higher or the same as the high score
		if (score >= highScore) {
			highScore = score; // their score becomes the high score
			highScorePlayer = playerName; // they are the highest scoring player
			try {
				fwriter = new BufferedWriter(new FileWriter(recordFile));
				fwriter.write(playerName); // write the player's name
				fwriter.newLine();
				fwriter.write(Integer.toString(score)); // write the player's score
				fwriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

// Inner class for key input that extends abstract KeyAdapter class to only
// use wanted methods from KeyListener interface.
	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if (gameState == playState) { // if the game is at play state
				// A - Going left
				if (e.getKeyCode() == KeyEvent.VK_A) {
					player.leftOn = true;
				}
				// D - Going Right
				if (e.getKeyCode() == KeyEvent.VK_D) {
					player.rightOn = true;
				}
				// W - Going Up
				if (e.getKeyCode() == KeyEvent.VK_W) {
					player.upOn = true;
				}
				// S - Going Down
				if (e.getKeyCode() == KeyEvent.VK_S) {
					player.downOn = true;
				}
				// Space - Shooting
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					player.bulletShot = true;
				}
			} else if (gameState == endState) { // if the game is over
				if (e.getKeyCode() == KeyEvent.VK_R) { // if r key is pressed
					frameCount = 0; // reset time counters
					timeCount = 0;
					score = 0; // reset score
					Player.speed = 1;
					Player.bulletSpeed = 10;
					gameState = playState; // return to play state
				}
				if (e.getKeyCode() == KeyEvent.VK_M) { // if m key is pressed
					frameCount = 0;
					timeCount = 0; // reset time counter
					score = 0; // reset score
					Player.speed = 1;
					Player.bulletSpeed = 10;
					gameState = menuState; // return to menu state
					PlayGame.cl.show(PlayGame.panel, "MenuPanel"); // move on to MenuPanel
					PlayGame.mp.requestFocusInWindow(); // request menu panel to get the input focus
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_A) {
				player.leftOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_D) {
				player.rightOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_W) {
				player.upOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_S) {
				player.downOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				player.bulletShot = false;
			}
		}
	}

// Inner class for mouse input that extends abstract MouseAdapter class to only
// use wanted methods from MouseListener interface.
	public class MyMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			pixelFont = pixelFont.deriveFont(50f); // set size of the font
			FontMetrics m = getFontMetrics(pixelFont);
			// if Easy button clicked
			if (diffScreen == true && gameState == menuState
					&& p.x - 8 > (PANEL_WIDTH - m.stringWidth("EASY") - 300) / 2
					&& p.x - 8 < (PANEL_WIDTH - m.stringWidth("EASY") - 300) / 2 + m.stringWidth("EASY") + 300
					&& p.y > 8 * PANEL_HEIGHT / 20 - 20 && p.y < 8 * PANEL_HEIGHT / 20 + 40) {
				Enemy.speed = 1.5; // set difficulty
				diffScreen = false;
				add(tf); // add textfield
				// if Medium button clicked
			} else if (diffScreen == true && gameState == menuState
					&& p.x - 8 > (PANEL_WIDTH - m.stringWidth("MEDIUM") - 250) / 2
					&& p.x - 8 < (PANEL_WIDTH - m.stringWidth("MEDIUM") - 250) / 2 + m.stringWidth("MEDIUM") + 250
					&& p.y > 11 * PANEL_HEIGHT / 20 - 20 && p.y < 11 * PANEL_HEIGHT / 20 + 40) {
				Enemy.speed = 2; // set difficulty
				diffScreen = false;
				add(tf);
				// if Hard button clicked
			} else if (diffScreen == true && gameState == menuState
					&& p.x - 8 > (PANEL_WIDTH - m.stringWidth("HARD") - 300) / 2
					&& p.x - 8 < (PANEL_WIDTH - m.stringWidth("HARD") - 300) / 2 + m.stringWidth("HARD") + 300
					&& p.y > 14 * PANEL_HEIGHT / 20 - 20 && p.y < 14 * PANEL_HEIGHT / 20 + 40) {
				Enemy.speed = 3; // set difficulty
				diffScreen = false;
				add(tf);
				// if the mouse is pressed, the game is at menu state,
				// and the mouse pointer is inside of the PLAY button,
			}
			if (diffScreen == false && gameState == menuState
					&& p.x - 8 > (PANEL_WIDTH - m.stringWidth("PLAY") - 300) / 2
					&& p.x - 8 < (PANEL_WIDTH - m.stringWidth("PLAY") - 300) / 2 + m.stringWidth("PLAY") + 300
					&& p.y > PANEL_HEIGHT / 2 - 20 && p.y < PANEL_HEIGHT / 2 + 40) {
				playerName = tf.getText().trim(); // save the user's name from the text field
				if (tf.getText().trim().length() > 10) // if the length of the name is longer than 10
					playerName = tf.getText().trim().substring(0, 10); // trim to 10 letters
				if (playerName.trim().equals("")) // if no name is entered
					playerName = "Unnamed";
				remove(tf); // remove the text field component
				Player.speed = 1; // set player speed
				Player.bulletSpeed = 10; // set bullet speed
				diffScreen = true;
				gameState = playState; // move on to play state
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// if the mouse has entered the panel
			mouseInPanel = true;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// if the mouse has exited the panel
			mouseInPanel = false;
		}
	}
}
