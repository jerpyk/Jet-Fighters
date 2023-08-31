// Jerome Kim
// May 13, 2022
// Player.java
// Class that has the variables and methods for each Player of the game.
package finalProject;

import java.awt.Rectangle;

public class Player {
	// Variables for the Player's Jet
	double x, y;
	static int width = 50;
	static int height = 50;
	static double speed = 3;
	double angle; // change in angle in degrees
	double radAngle; // change in angle in radians
	Rectangle rec;
	// Variables for the Player's Bullet
	double bulletX, bulletY;
	static int bulletWidth = 10;
	static int bulletHeight = 10;
	static double bulletSpeed = 10;
	double bulletAngle;
	Rectangle bulletRec;
	// Variables for the Player's key input
	boolean leftOn, rightOn, upOn, downOn;
	boolean bulletShot;
	boolean bulletOn;

	/**
	 * 
	 * Sets rectangle boundary for the player a little smaller than its real
	 * boundary because when rotated, it becomes a diamond, so it reduces the chance
	 * of collision happening at the empty spaces of the rectangle.
	 * 
	 */
	public void updateRec() {
		this.rec = new Rectangle((int) this.x + 5, (int) this.y + 5, width - 10, height - 10);
	}

	/**
	 * Set rectangle boundary for bullet collision detection
	 * 
	 */
	public void updateBulletRec() {
		this.bulletRec = new Rectangle((int) this.bulletX, (int) this.bulletY, bulletWidth, bulletHeight);
	}

	/**
	 * 
	 * Set variables for each Player
	 * 
	 */
	public void setPlayer() {
		this.angle = 0; // start angle in degrees
		this.radAngle = Math.toRadians(this.angle); // angle in radians
		this.updateRec();
		this.bulletX = this.x + width / 2 - bulletWidth / 2;
		this.bulletY = this.y + height / 2 - bulletHeight / 2;
		this.updateBulletRec();
		this.bulletOn = false;
	}

	/**
	 * 
	 * Method for player movement based on key input and for Single Player game
	 * 
	 */
	public void move() {
		// if the player is inside the left edge
		if (this.leftOn == true && this.x > 0) {
			this.x -= speed; // go left
			this.updateRec(); // update Rectangle
		}
		// if the player is inside the right edge
		if (this.rightOn == true && this.x < GamePanel1.PANEL_WIDTH - width) {
			this.x += speed; // go right
			this.updateRec();
		}
		// if the player is inside the top edge
		if (this.upOn == true && this.y > 0) {
			this.y -= speed; // go up
			this.updateRec();
		}
		// if the player is inside the bottom edge
		if (this.downOn == true && this.y < GamePanel1.PANEL_HEIGHT - height) {
			this.y += speed; // go down
			this.updateRec();
		}
	}

	/**
	 * 
	 * Method for player movement based on key input and direction for Multiplayer
	 * Game
	 * 
	 */
	public void move2() {
		if (this.leftOn == true) {
			this.angle -= 2; // turn left
			this.radAngle = Math.toRadians(this.angle);
		}
		if (this.rightOn == true) {
			this.angle += 2; // turn right
			this.radAngle = Math.toRadians(this.angle);
		}
		if (this.upOn == true) {
			// The radAngle is the change in direction of the player in radians, which
			// starts half PI greater than the actual radian angle in trigonometry
			// quadrant. Also, the direction of rotation for image rotation and
			// the rotation in trigonometry quadrant are the opposite, so the radAngle
			// has to be subtracted from the angle on trigonometry quadrant
			// to aim for the real direction.
			this.x += speed * Math.cos(Math.PI / 2 - this.radAngle);
			this.y -= speed * Math.sin(Math.PI / 2 - this.radAngle);
			// The ratio of base to height of a right triangle is equal to the ratio
			// of cos(angle) to sin(angle). Here, the change in x of the player is the base,
			// and the change in y of the player is the height. Therefore, to aim towards
			// the angle, the x value and y are changed proportionally depending on the
			// angle to move straight to the target angle.

			// If the player's position after their movement is off screen
			if (this.x < 0 || this.x > GamePanel1.PANEL_WIDTH - width || this.y < 0
					|| this.y > GamePanel1.PANEL_HEIGHT - height) {
				// reverse their x and y movement at the same angle;
				this.x -= speed * Math.cos(Math.PI / 2 - this.radAngle); //
				this.y += speed * Math.sin(Math.PI / 2 - this.radAngle);
			}
			// Set rectangle for Player
			this.updateRec();
		}
		if (this.downOn == true) {
			this.x -= speed * Math.cos(Math.PI / 2 - this.radAngle);
			this.y += speed * Math.sin(Math.PI / 2 - this.radAngle);
			if (this.x < 0 || this.x > GamePanel1.PANEL_WIDTH - width || this.y < 0
					|| this.y > GamePanel1.PANEL_HEIGHT - height) {
				this.x += speed * Math.cos(Math.PI / 2 - this.radAngle);
				this.y -= speed * Math.sin(Math.PI / 2 - this.radAngle);
			}
			// Set rectangle for Player
			this.updateRec();
		}
	}

	/**
	 * 
	 * Updates the bullet location for each player based on key input and the
	 * direction.
	 * 
	 * 
	 */
	public void shootBullet() {
		// if bullet shooting key is pressed while the bullet is not alive
		if (this.bulletShot == true && this.bulletOn == false) {
			this.bulletShot = false;
			this.bulletAngle = this.radAngle; // set the angle at which the bullet is shot at
			this.bulletOn = true;
		}

		// If bullet goes out of the screen
		if (this.bulletX < 0 || this.bulletX > GamePanel1.PANEL_WIDTH || this.bulletY < 0
				|| this.bulletY > GamePanel1.PANEL_HEIGHT) {
			this.bulletOn = false; // The bullet is no longer alive
		}

		if (this.bulletOn == true) { // if the bullet is alive
			// move the bullet towards the bullet's set angle
			this.bulletX += bulletSpeed * Math.cos(Math.PI / 2 - this.bulletAngle);
			this.bulletY -= bulletSpeed * Math.sin(Math.PI / 2 - this.bulletAngle);
			// Set the rectangle
			this.updateBulletRec();
		} else { // if the bullet is not alive
			// The bullet is kept at the center of the player's jet
			this.bulletX = this.x + width / 2 - bulletWidth / 2;
			this.bulletY = this.y + height / 2 - bulletHeight / 2;
			this.updateBulletRec();
		}
	}
}
