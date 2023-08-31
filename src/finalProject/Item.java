// Jerome Kim
// June 17, 2022
// Item.java
// Class with variables and methods to create items for the game.
package finalProject;

import java.awt.Rectangle;
import java.util.Random;

public class Item {

	int x;
	int y;
	static int width = 50;
	static int height = 50;
	int numb;
	Rectangle rec;
	Random rng = new Random();

	/**
	 * 
	 * Generates speed boost or shoot boost item. The spawning location of the item
	 * is random, but it has a boundary to not spawn around the edges to avoid being
	 * instantly hit by the enemy jets.
	 * 
	 */
	public void setItem() {
		this.numb = rng.nextInt(2); // random item
		this.x = rng.nextInt(GamePanel1.PANEL_WIDTH - 500 - width) + 250; // random location of the item
		this.y = rng.nextInt(GamePanel1.PANEL_HEIGHT - 500 - height) + 250;
		this.rec = new Rectangle(this.x, this.y, width, height);
	}
}
