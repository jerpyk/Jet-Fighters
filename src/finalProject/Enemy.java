// Jerome Kim
// June 3, 2022
// Enemy.java
// Class that has the variables and methods for each Enemy of the game.
package finalProject;

import java.awt.Rectangle;
import java.util.Random;

public class Enemy {

	double x, y;
	static int width = 50;
	static int height = 50;
	static double speed = 2;
	double radAngle; // angle in radians
	Rectangle rec;
	Random rng = new Random(); // create random object

	/**
	 * 
	 * Sets rectangle boundary for the enemy a little smaller than its real boundary
	 * because when rotated, it becomes a diamond, so it reduces the chance of
	 * collision happening at the empty spaces of the rectangle.
	 * 
	 */
	public void updateRec() {
		this.rec = new Rectangle((int) this.x + 5, (int) this.y + 5, width - 10, height - 10);
	}

	/**
	 * 
	 * Randomly sets the spawn location of the enemy jets outside the border of the
	 * panel.
	 * 
	 */
	public void setEnemy() {
		int randChoice = rng.nextInt(4);
		if (randChoice == 0) {
			// top border
			this.x = rng.nextInt(GamePanel1.PANEL_WIDTH + 5 * width) - 5 * width;
			this.y = -5 * height;
		} else if (randChoice == 1) {
			// bottom border
			this.x = rng.nextInt(GamePanel1.PANEL_WIDTH + 5 * width) - 5 * width;
			this.y = GamePanel1.PANEL_HEIGHT + 4 * height;
		} else if (randChoice == 2) {
			// left border
			this.x = -5 * width;
			this.y = rng.nextInt(GamePanel1.PANEL_HEIGHT + 5 * height) - 5 * height;
		} else if (randChoice == 3) {
			// right border
			this.x = GamePanel1.PANEL_WIDTH + 4 * width;
			this.y = rng.nextInt(GamePanel1.PANEL_HEIGHT + 5 * height) - 5 * height;
		}
		this.updateRec();
	}

	/**
	 * 
	 * Moves the enemy jet based on the angle of the direction they are heading.
	 * 
	 */
	public void move() {
		this.x += speed * Math.cos(Math.PI / 2 - this.radAngle);
		this.y -= speed * Math.sin(Math.PI / 2 - this.radAngle);
		this.updateRec();
	}
}
