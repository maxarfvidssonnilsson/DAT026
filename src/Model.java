import java.util.Random;

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

	final double GRAVITY = 0.0005;
	int numberOfBalls = 500;
	double averageSpeed = 0.01;
	
	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;
		
		// Initialize the model with a few balls

		balls = getBalls(numberOfBalls, averageSpeed, areaHeight, areaWidth);
		//balls = new Ball[2];
		//balls[0] = new Ball(width / 3, height * 0.9, 2.2, 1.6, 0.2);
		//balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3);
	}

	Ball[] getBalls(int numberOfBalls, double averageSpeed, double height, double width){
		Ball[] balls = new Ball[numberOfBalls];
		Random random = new Random();
		for (int i = 0; i < numberOfBalls; i++){
			Ball b = new Ball(0,0,0,0,0);
			b.v = new Vector(((double)random.nextInt((int) (averageSpeed*200)))/100, ((double)random.nextInt((int) (averageSpeed*200)))/100);
			b.x = ((double)random.nextInt((int)(width *100)))/150;
			b.y = ((double)random.nextInt((int)(height *100)))/150;
			b.radius = ((double)random.nextInt((int)(1000/Math.pow(numberOfBalls,0.5))))/1000;
			balls[i] = b;
		}
		return balls;
	}

	boolean collidesWithBorder (double radius, double position, double border, double velocity) {
		if ((position < radius && velocity < 0) || (position > border - radius && velocity > 0)) {
			return true;
		}
		return false;
	}

	/*
	boolean checkBallCollision(Ball b1, Ball b2, double step){
		double b1X = b1.x + step * b1.v.vx;
		double b1Y = b1.y + step * b1.v.vy;
		double b2X = b2.x + step * b2.v.vx;
		double b2Y = b2.y + step * b2.v.vy;

		double distance = Math.sqrt(Math.pow(b1X - b2X,2)+Math.pow(b1Y -b2Y,2));
		if (distance <= b1.radius + b2.radius)
			return true;
		else
			return false;
	}
	*/

	void step(double deltaT) {
		for (Ball b1 : balls) {
			// detect collision with the border

			if (collidesWithBorder(b1.radius, b1.x, areaWidth, b1.v.vx)) {
				b1.v.vx *= -1; // change direction of ball
			}
			if (collidesWithBorder(b1.radius, b1.y, areaHeight, b1.v.vy)) {
				b1.v.vy *= -1;
			}
			else {
				b1.v.vy -= GRAVITY;
			}
			for (Ball b2 : balls){
				double a = checkBallCollision(b1,b2, deltaT);
				System.out.println(a);
				if ((a>=0 && a <= 1) && b1 != b2){
					b1.move(deltaT, a);
					//b2.move(deltaT, a);
					handleBallCollision(b1, b2);
					b1.move(deltaT, (1-a));
					//b2.move(deltaT,(1-a));
				} else {
					b1.move(deltaT, 1);
				}
			}
		}
	}
				void slowBall (Ball ball){
					ball.v = ball.v.subtract(new Vector(ball.v.vx * 0.05,ball.v.vy * 0.05));
				}
	double checkBallCollision(Ball b1, Ball b2, double deltaT) {

		return (b1.radius + b2.radius) /
				Math.sqrt(
						Math.pow((b1.v.vx - b2.v.vx) * deltaT, 2) + 
						Math.pow((b1.v.vy - b2.v.vy) * deltaT, 2)
						);
	}

	void handleBallCollision(Ball b1, Ball b2){
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

		void move (double step, double a) {
			this.x += a * step * this.v.vx;
			this.y += a * step * this.v.vy;
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
