// Jerome Kim
// May 16, 2022
// MenuPanel.java
// Class with JPanel as its parent class for a menu screen.
package finalProject;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.JPanel;

public class MenuPanel extends JPanel {

	// The dimensions of the panel is the size of the screen
	static final Dimension panelSize = Toolkit.getDefaultToolkit().getScreenSize();
	static final int PANEL_WIDTH = (int) panelSize.getWidth();
	// set panel height relative to the screen size minus 20 because
	// the height of the title bar is 30.
	static final int PANEL_HEIGHT = (int) panelSize.getHeight() - 30;
	Font pixelFont; // create Font

	/**
	 * 
	 * Create a new MenuPanel with JPanel as the parent class.
	 */
	public MenuPanel() {
		this.setPreferredSize(panelSize); // set preferred size to the screen size
		this.setMaximumSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setBackground(Color.BLACK);
		this.addMouseListener(new MyMouseAdapter()); // add MouseListener for mouse input
		this.setFocusable(true);
		this.setMenu(); // set up the menu
	}

	/**
	 * 
	 * Sets up the variables for the menu panel
	 * 
	 */
	public void setMenu() {
		try { // error handling with files
				// create and register font
			pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("src\\files\\PixelFont.ttf"));
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(pixelFont);
		} catch (IOException | FontFormatException e) {
			e.printStackTrace();
		}
	}

	// Drawing main menu screen with Single Player and PvP buttons
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g); // paintComponent method with parent class (JPanel)
		// Title
		g.setColor(Color.WHITE); // set Colour
		g.setFont(pixelFont.deriveFont(180f)); // set font size
		// FontMetrics object for displaying font at a location
		// relative to the screen size
		FontMetrics m = getFontMetrics(g.getFont());
		g.drawString("Jet Fighters", (PANEL_WIDTH - m.stringWidth("Jet Fighters")) / 2, 4 * PANEL_HEIGHT / 20);
		// Single Player Button
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(pixelFont.deriveFont(50f)); // set font size
		m = getFontMetrics(g.getFont());
		g.fill3DRect((PANEL_WIDTH - m.stringWidth("Single Player") - 300) / 2, 9 * PANEL_HEIGHT / 20 - 50,
				m.stringWidth("Single Player") + 300, 70, true);
		g.setColor(Color.BLACK);
		g.drawString("Single Player", (PANEL_WIDTH - m.stringWidth("Single Player")) / 2, 9 * PANEL_HEIGHT / 20);
		// Player vs Player Button
		g.setColor(Color.LIGHT_GRAY);
		g.fill3DRect((PANEL_WIDTH - m.stringWidth("Player vs Player") - 225) / 2, 13 * PANEL_HEIGHT / 20 - 50,
				m.stringWidth("Player vs Player") + 225, 70, true);
		g.setColor(Color.BLACK);
		g.drawString("Player vs Player", (PANEL_WIDTH - m.stringWidth("Player vs Player")) / 2, 13 * PANEL_HEIGHT / 20);
	}

	// Inner class for mouse input that extends abstract MouseAdapter class to only
	// use wanted methods from MouseListener interface.
	public class MyMouseAdapter extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			Point p = PlayGame.frame.getMousePosition(); // get mouse location relative to the frame
			pixelFont = pixelFont.deriveFont(50f); // set font size
			// FontMetrics object for displaying font at a location
			// relative to the font and screen size
			FontMetrics m = getFontMetrics(pixelFont);
			// if the mouse pointer is inside the Single Player
			// button and the mouse is pressed
			if (p.x - 8 > (PANEL_WIDTH - m.stringWidth("Single Player") - 300) / 2
					&& p.x - 8 < (PANEL_WIDTH - m.stringWidth("Single Player") - 300) / 2
							+ m.stringWidth("Single Player") + 300
					&& p.y > 9 * PANEL_HEIGHT / 20 - 20 && p.y < 9 * PANEL_HEIGHT / 20 + 50) {
				PlayGame.cl.show(PlayGame.panel, "GamePanel1"); // move on to GamePanel1
				PlayGame.gp1.requestFocusInWindow(); // request GamePanel1 to get the input focus
			}
			// if the mouse pointer is inside the Player vs Player
			// button and the mouse is pressed
			if (p.x - 8 > (PANEL_WIDTH - m.stringWidth("Player vs Player") - 225) / 2
					&& p.x - 8 < (PANEL_WIDTH - m.stringWidth("Player vs Player") - 225) / 2
							+ m.stringWidth("Player vs Player") + 225
					&& p.y > 13 * PANEL_HEIGHT / 20 - 20 && p.y < 13 * PANEL_HEIGHT / 20 + 50) {
				PlayGame.cl.show(PlayGame.panel, "GamePanel2"); // move on to GamePanel2
				PlayGame.gp2.requestFocusInWindow(); // request GamePanel2 to get the input focus
			}
		}
	}
}
