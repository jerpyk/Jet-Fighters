// Jerome Kim
// May 16, 2022
// GamePanel2.java
// Class with JPanel as its parent class for player versus player 
// game screen. Each player avoids being hit by the other player's bullet 
// or crashing the other player's jet
package finalProject;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel2 extends JPanel implements Runnable {

	// Set the dimensions of the panel as the size of the screen
	static final Dimension panelSize = Toolkit.getDefaultToolkit().getScreenSize();
	static final int PANEL_WIDTH = (int) panelSize.getWidth();
	// Set the panel height relative to the screen size minus 30 because
	// The height of the title bar is 30.
	static final int PANEL_HEIGHT = (int) panelSize.getHeight() - 30;
	static final int fps = 144; // 144 frames per seconds for the Thread
	int winner;
	int gameState; // the state in which the program is at
	int menuState = 1;
	int playState = 2;
	int endState = 3;
	boolean running;
	String result;
	Player p1 = new Player(); // Player 1
	Player p2 = new Player(); // Player 2
	Thread gameThread; // create Thread
	BufferedImage jet1; // Images
	BufferedImage jet2;
	BufferedImage background;
	Font pixelFont; // Font

	/**
	 * 
	 * Create a new GamePanel2 with JPanel as the parent class.
	 * 
	 */
	public GamePanel2() {
		this.setPreferredSize(panelSize); // set preferred size to the screen size
		this.setMaximumSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setBackground(Color.BLACK);
		this.addKeyListener(new MyKeyAdapter()); // add KeyLisetener for key input
		this.addMouseListener(new MyMouseAdapter()); // add MouseListener for mouse input
		this.setFocusable(true);
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
		// Initialize instance variables for Player 1
		p1.x = (PANEL_WIDTH / 2 - Player.width / 2) / 2; // set location for Player 1
		p1.y = PANEL_HEIGHT / 2 - Player.height / 2;
		p1.setPlayer(); // use set player method for player 1
		// Initialize instance variables for Player 2
		p2.x = (PANEL_WIDTH / 2 - Player.width / 2) * 1.5; // set location for Player 2
		p2.y = PANEL_HEIGHT / 2 - Player.height / 2;
		p2.setPlayer(); // use set player method for player 2
		try { // error handling with file
				// Images
			jet1 = ImageIO.read(new File("src\\files\\spaceJet1.png"));
			jet2 = ImageIO.read(new File("src\\files\\spaceJet2.png"));
			background = ImageIO.read(new File("src\\files\\background.png"));
			// Create and register font
			pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("src\\files\\PixelFont.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(pixelFont);
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Method to create and start the Thread of the program.
	 * 
	 */
	public void startThread() {
		gameThread = new Thread(this); // target for the Thread is the GamePanel2 object
		gameThread.start(); // start Thread
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

		while (running) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / nanoSeconds;
			lastTime = currentTime;
			if (delta >= 1) { // for every time frame becomes over 1,
				playerMove(); // update player locations
				playerShoot(); // update bullet locations
				checkCollision(); // check collision
				repaint(); // after updating, repaint the visual components onto the screen
				delta--;
			}
		}
	}

	/**
	 * 
	 * Updates the x and y values, and the angle of the direction the players based
	 * on the keys pressed by the user.
	 * 
	 */
	public void playerMove() {
		p1.move2(); // move2 method from the Player class for player 1
		p2.move2(); // move2 method from the Player class for player 2
	}

	/**
	 *
	 * Updates the x and y values of the bullets based on the user's key input and
	 * the direction of their shooting.
	 * 
	 */
	public void playerShoot() {
		p1.shootBullet(); // shootBullet method from the Player class for player 1
		p2.shootBullet(); // shootBullet method from the Player class for player 2
	}

	/**
	 * 
	 * Checks the collision between the two players and between their opponent's
	 * bullet.
	 * 
	 */
	public void checkCollision() {
		if (gameState == playState) {
			// If Player 1 hits Player 2
			if (p1.bulletRec.intersects(p2.rec)) {
				result = "Player 1 Wins!";
				winner = 1; // Player 1 is the winner
				gameState = endState; // move to end state
			}
			// If Player 2 hits Player 1
			if (p2.bulletRec.intersects(p1.rec)) {
				result = "Player 2 Wins!";
				winner = 2; // Player 2 is the winner
				gameState = endState;
			}
			// if the boundaries of Player 1 and Player 2 collide
			if (p1.rec.intersects(p2.rec)) {
				winner = 0; // It's a tie
				result = "Both Jets Exploded...";
				gameState = endState;
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
	 * Draws the menu screen with controls description and play button.
	 * 
	 * @param g Graphics object
	 * 
	 */
	public void drawMenu(Graphics g) {
		// Title
		g.setColor(Color.WHITE); // set Colour
		g.setFont(pixelFont.deriveFont(180f)); // set font size
		// FontMetrics object for displaying font at a location
		// relative to the font size and String length
		FontMetrics m = getFontMetrics(g.getFont());
		// Centralize the text for any screen size
		g.drawString("Jet Fighters", (PANEL_WIDTH - m.stringWidth("Jet Fighters")) / 2, 4 * PANEL_HEIGHT / 20);
		// Play Button
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(pixelFont.deriveFont(50f)); // set font size
		m = getFontMetrics(g.getFont());
		g.fill3DRect((PANEL_WIDTH - m.stringWidth("PLAY") - 300) / 2, 8 * PANEL_HEIGHT / 20 - 50,
				m.stringWidth("PLAY") + 300, 60, true);
		g.setColor(Color.BLACK);
		g.drawString("PLAY", (PANEL_WIDTH - m.stringWidth("PLAY")) / 2, 8 * PANEL_HEIGHT / 20);
		// Controls Description
		g.setColor(Color.WHITE);
		g.drawString("Controls", (PANEL_WIDTH - m.stringWidth("Controls")) / 2, 11 * PANEL_HEIGHT / 20);
		g.drawString("Player 1        Player 2", (PANEL_WIDTH - m.stringWidth("Player 1        Player 2")) / 2,
				13 * PANEL_HEIGHT / 20);
		g.setFont(pixelFont.deriveFont(30f)); // set font size
		m = getFontMetrics(g.getFont());
		g.drawString("W - Go Forwards          Up - Go Forwards",
				(PANEL_WIDTH - m.stringWidth("W - Go Forwards          Up - Go Forwards")) / 2, 14 * PANEL_HEIGHT / 20);
		g.drawString("S - Go Backwards      Down - Go BackWards",
				(PANEL_WIDTH - m.stringWidth("S - Go Backwards      Down - Go BackWards")) / 2, 15 * PANEL_HEIGHT / 20);
		g.drawString("A - Turn Left            Left - Turn Left",
				(PANEL_WIDTH - m.stringWidth("A - Turn Left            Left - Turn Left")) / 2, 16 * PANEL_HEIGHT / 20);
		g.drawString("D - Turn Right         Right - Turn Right",
				(PANEL_WIDTH - m.stringWidth("D - Turn Right         Right - Turn Right")) / 2, 17 * PANEL_HEIGHT / 20);
		g.drawString("Space - Shoot               Enter - Shoot",
				(PANEL_WIDTH - m.stringWidth("Space - Shoot               Enter - Shoot")) / 2, 18 * PANEL_HEIGHT / 20);
	}

	/**
	 * 
	 * Draws the game play screen.
	 * 
	 * @param g Graphics object
	 */
	public void drawGame(Graphics g) {
		Graphics2D g2 = (Graphics2D) g; // cast Graphics to Graphics2D
		AffineTransform old = g2.getTransform(); // copy the current rotation angle before any rotation
		g.drawImage(background, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, null); // draw background image
		g.setColor(Color.RED);
		// draw player 1 bullet
		g.fillOval((int) p1.bulletX, (int) p1.bulletY, Player.bulletWidth, Player.bulletHeight);
		g.setColor(Color.BLUE);
		// draw player 2 bullet
		g.fillOval((int) p2.bulletX, (int) p2.bulletY, Player.bulletWidth, Player.bulletHeight);
		g2.rotate(p1.radAngle, p1.x + Player.width / 2, p1.y + Player.height / 2); // rotate player 1
		g.drawImage(jet1, (int) p1.x, (int) p1.y, Player.width, Player.height, null); // draw player 1
		g2.setTransform(old); // undo rotation
		g2.rotate(p2.radAngle, p2.x + Player.width / 2, p2.y + Player.height / 2); // rotate player 2
		g.drawImage(jet2, (int) p2.x, (int) p2.y, Player.width, Player.height, null); // draw player 2
	}

	/**
	 * Draws the game over screen.
	 * 
	 * @param g Graphics object
	 * 
	 */
	public void drawOver(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(pixelFont.deriveFont(150f)); // set font size
		FontMetrics m = getFontMetrics(g.getFont());
		// Display Game Over
		g.drawString("Game Over", (PANEL_WIDTH - m.stringWidth("Game Over")) / 2, PANEL_HEIGHT / 5);
		g.setFont(pixelFont.deriveFont(100f)); // set font size
		m = getFontMetrics(g.getFont());
		if (winner == 1) // if Player 1 won
			g.setColor(Color.RED);
		else if (winner == 2) // if Player 2 won
			g.setColor(Color.BLUE);
		else if (winner == 0) // if tied
			g.setColor(new Color(127, 0, 255)); // purple RGB value
		// Display result
		g.drawString(result, (PANEL_WIDTH - m.stringWidth(result)) / 2, PANEL_HEIGHT / 2);
		g.setColor(Color.WHITE);
		g.setFont(pixelFont.deriveFont(50f));
		m = getFontMetrics(g.getFont());
		// Display how to restart
		g.drawString("Press R to Restart", (PANEL_WIDTH - m.stringWidth("Press R to Restart")) / 2,
				4 * PANEL_HEIGHT / 5);
		// Display how to return to menu
		g.drawString("Press M to Return to Main Menu",
				(PANEL_WIDTH - m.stringWidth("Press M to Return to Main Menu")) / 2, 9 * PANEL_HEIGHT / 10);
	}

	// Inner class for key input that extends abstract KeyAdapter class to only
	// use wanted methods from KeyListener interface.
	public class MyKeyAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			// PLAYER 1 KEYS
			if (gameState == playState) { // if the game is at play state
				// A - Turning Left
				if (e.getKeyCode() == KeyEvent.VK_A) {
					p1.leftOn = true;
				}
				// D - Turning Right
				if (e.getKeyCode() == KeyEvent.VK_D) {
					p1.rightOn = true;
				}
				// W - Going Forwards
				if (e.getKeyCode() == KeyEvent.VK_W) {
					p1.upOn = true;
				}
				// S - Going Backwards
				if (e.getKeyCode() == KeyEvent.VK_S) {
					p1.downOn = true;
				}
				// Space - Shooting
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					p1.bulletShot = true;
				}
				// PLAYER 2 KEYS
				// Left - Turning Left
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					p2.leftOn = true;
				}
				// Right - Turning Right
				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					p2.rightOn = true;
				}
				// Up - Going Forwards
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					p2.upOn = true;
				}
				// Down - Going Backwards
				if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					p2.downOn = true;
				}
				// Enter - Shooting
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					p2.bulletShot = true;
				}
			} else if (gameState == endState) { // if the game is over
				if (e.getKeyCode() == KeyEvent.VK_R) { // if r key is pressed
					// Reset variables for game restart
					p1.x = (PANEL_WIDTH / 2 - Player.width / 2) / 2; // set location for Player 1
					p1.y = PANEL_HEIGHT / 2 - Player.height / 2;
					p1.setPlayer();
					p2.x = (PANEL_WIDTH / 2 - Player.width / 2) * 1.5; // set location for Player 2
					p2.y = PANEL_HEIGHT / 2 - Player.height / 2;
					p2.setPlayer();
					gameState = playState; // return to play state
				}
				if (e.getKeyCode() == KeyEvent.VK_M) { // if m key is pressed
					p1.x = (PANEL_WIDTH / 2 - Player.width / 2) / 2;
					p1.y = PANEL_HEIGHT / 2 - Player.height / 2;
					p1.setPlayer();
					p2.x = (PANEL_WIDTH / 2 - Player.width / 2) * 1.5;
					p2.y = PANEL_HEIGHT / 2 - Player.height / 2;
					p2.setPlayer();
					gameState = menuState; // return to menu state
					PlayGame.cl.show(PlayGame.panel, "MenuPanel"); // move on to MenuPanel
					PlayGame.mp.requestFocusInWindow(); // request menu panel to get the input focus

				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// PLAYER 1 KEYS
			if (e.getKeyCode() == KeyEvent.VK_A) {
				p1.leftOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_D) {
				p1.rightOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_W) {
				p1.upOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_S) {
				p1.downOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_SPACE) {
				p1.bulletShot = false;
			}
			// PLAYER 2 KEYS
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				p2.leftOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				p2.rightOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_UP) {
				p2.upOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				p2.downOn = false;
			}
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				p2.bulletShot = false;
			}
		}
	}

	// Inner class for mouse input that extends abstract MouseAdapter class to only
	// use wanted methods from MouseListener interface.
	public class MyMouseAdapter extends MouseAdapter {
		@Override
		public void mousePressed(MouseEvent e) {
			Point p = PlayGame.frame.getMousePosition(); // get mouse location relative to the frame
			pixelFont = pixelFont.deriveFont(50f); // set size of the font
			FontMetrics m = getFontMetrics(pixelFont);
			// if mouse is pressed, the game is at menu state,
			// and the mouse pointer is inside of the PLAY button
			if (gameState == menuState && p.x - 8 > (PANEL_WIDTH - m.stringWidth("PLAY") - 300) / 2
					&& p.x - 8 < (PANEL_WIDTH - m.stringWidth("PLAY") - 300) / 2 + m.stringWidth("PLAY") + 300
					&& p.y > 8 * PANEL_HEIGHT / 20 - 20 && p.y < 8 * PANEL_HEIGHT / 20 + 40) {
				Player.speed = 3; // set player speed
				Player.bulletSpeed = 10; // set bullet speed
				gameState = playState; // move on to play state
			}
		}
	}

}
