
/**
 * The physics model.
 * 
 * This class is where you should implement your bouncing balls model.
 * 
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 * 
 * @author Simon Robillard
 *
 */
public class Model {

	double areaWidth, areaHeight;

	final double GRAVITY = 0.5;
	
	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;
		
		// Initialize the model with a few balls
		balls = new Ball[2];
		balls[0] = new Ball(width / 3, height * 0.9, 2.2, 1.6, 0.2);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3);
	}

	boolean collidesWithBorder (double radius, double position, double border) {
		if (position < radius || position > border - radius) {
			return true;
		}
		return false;
	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT
		for (Ball b : balls) {
			// detect collision with the border
			if (collidesWithBorder(b.radius, b.x, areaWidth)) {
				b.v.vx *= -1; // change direction of ball
			}
			if (collidesWithBorder(b.radius, b.y, areaHeight)) {
				b.v.vy *= -1;
			}
			else {
				b.v.vy -= GRAVITY;
			}
			for (Ball b2 : balls){
				if (b != b2 && b.checkBallCollsion(b2)){
					ballCollision(b, b2);
				}
			}
			
			// compute new position according to the speed of the ball
			b.x += deltaT * b.v.vx;
			b.y += deltaT * b.v.vy;
		}
	}



	void ballCollision ( Ball b1, Ball b2){

		Vector t = new Vector(b1.x - b2.x, b1.y - b2.y);

		Vector relV = b1.v.subtract(b2.v);

		Vector force = relV.projectOnVector(t);

		b1.v = b1.v.subtract(force);

		b2.v = b2.v.plus(force);

	}
	
	/**
	 * Simple inner class describing balls.
	 */
	class Ball {

		double x, y, radius;

		Vector v;

		Ball(double x, double y, double vx, double vy, double r) {
			this.x = x;
			this.y = y;
			this.v = new Vector(vx, vy);
			this.radius = r;
		}


		boolean checkBallCollsion (Ball b2){
			double distance = Math.sqrt(Math.pow(this.x - b2.x,2)+Math.pow(this.y - b2.y,2));
			if (distance <= this.radius + b2.radius)
				return true;
			else
				return false;
		}
	}

	class Vector {
		double vx, vy;

		Vector(double vx, double vy) {
			this.vx = vx;
			this.vy = vy;
		}

		double length () {
			return Math.sqrt( Math.pow(this.vx, 2) + Math.pow(this.vy, 2) );
		}

		double dotproduct (Vector v) {
			return this.vx * v.vx + this.vy * v.vy;
		}

		Vector projectOnVector (Vector tangent) {
			double scalar = this.dotproduct(tangent) / tangent.dotproduct(tangent);

			tangent.vx *= scalar;
			tangent.vy *= scalar;

			return tangent.copy();
		}

		Vector copy() {
			return new Vector(this.vx, this.vy);
		}

		Vector subtract (Vector v) {
			return new Vector(this.vx - v.vx, this.vy - v.vy);
		}

		Vector plus (Vector v) {
			return new Vector(this.vx + v.vx, this.vy + v.vy);
		}
	}
}
