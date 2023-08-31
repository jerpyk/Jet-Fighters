// Jerome Kim
// May 13, 2022
// PlayGame.java
// Class with a main method for the actual game play. It adds all 
// the panels to one main panel and adds that to a frame.
package finalProject;

import java.awt.*;
import javax.swing.*;

public class PlayGame {
	// Create JFrame object.
	static JFrame frame = new JFrame("Final Project - Jerome Kim");
	// Create MenuPanel object for menu screen.
	static MenuPanel mp = new MenuPanel();
	// Create GamePanel1 object for single player game screen.
	static GamePanel1 gp1 = new GamePanel1();
	// Create GamePanel2 object for PvP game screen.
	static GamePanel2 gp2 = new GamePanel2();
	// Create JPanel to add all the panels to one main panel.
	static JPanel panel = new JPanel();
	// Create card layout object to organize the panels
	// of the main panels like cards.
	static CardLayout cl = new CardLayout();

	public static void main(String[] args) {
		// Set up main panel
		panel.setLayout(cl); // set card layout as the layout manager
		panel.add(mp, "MenuPanel"); // add menu panel first, so that it is shown first
		panel.add(gp1, "GamePanel1"); // add game panel 1
		panel.add(gp2, "GamePanel2"); // add game panel 2
		// Set up the Game Frame
		frame.add(panel); // add panel to the frame
		frame.pack(); // pack frame to the preferred size of the panelsAA
		frame.setLocation(-8, 0); // centralize the frame location
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
