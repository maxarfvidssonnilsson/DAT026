import java.util.Random;

public class Model {

	double areaWidth, areaHeight;

	final double GRAVITY = 0.5;
	int numberOfBalls = 20;
	double averageSpeed = 0.5;
	
	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		//balls = getBalls(numberOfBalls, averageSpeed, areaHeight, areaWidth);
		balls = new Ball[2];
		balls[0] = new Ball(width / 3, height * 0.9, 2.2, 1.6, 0.2);
		balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3);
	}

	/*
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
	*/



	void step(double deltaT) {
		for (Ball b1 : balls) {

			// detect collision with the border
			if (collidesWithBorder(b1.radius, b1.x, areaWidth, b1.v.vx)) {
				b1.v.vx *= -1; // change direction of ball
			}
			if (collidesWithBorder(b.radius, b.y, areaHeight, b.v.vy)) {
				b.v.vy *= -1;
			} else {
				b.v.vy -= GRAVITY;
			}

			for (Ball b2 : balls){
				if (b1 != b2 && b1.checkBallCollsion(b2)){
					double distance = Math.sqrt(Math.pow(b1.x - b2.x,2)+Math.pow(b1.y - b2.y,2));
					while (distance <= b1.radius + b2.radius) {

						b1.x -= b1.v.vx * 0.1 * deltaT;
						b2.y -= b2.v.vy * 0.1 * deltaT;
						b1.x -= b1.v.vx * 0.1 * deltaT;
						b2.y -= b2.v.vy * 0.1 * deltaT;

						distance = Math.sqrt(Math.pow(b1.x - b2.x, 2) + Math.pow(b1.y - b2.y, 2));
					}
					ballCollision(b1, b2,deltaT);
				}
			}

			b1.x += deltaT * b1.v.vx;
			b1.y += deltaT * b1.v.vy;
		}
	}

	boolean collidesWithBorder (double radius, double position, double border, double velocity) {
		if ((position < radius && velocity < 0) || (position > border - radius && velocity > 0)) {
			return true;
		}
		return false;
	}

	void ballCollision ( Ball b1, Ball b2, double deltaT){
		Vector t = new Vector(b1.x - b2.x, b1.y - b2.y);
		Vector relV = b1.v.subtract(b2.v);
		Vector force = relV.projectOnVector(t);
		b1.v = b1.v.subtract(force);
		b2.v = b2.v.plus(force);

		double ditance = (b1.radius + b2.radius) - Math.sqrt(Math.pow((b1.x - b2.x),2) + Math.pow((b2.y - b2.y),2));

		Vector unitVector = new Vector(force.vx/force.length(),force.vy / force.length());
		Vector z = new Vector(unitVector.vx *ditance/2, unitVector.vy * ditance/2);
		b2.y += z.vy * deltaT;
		b2.x += z.vx * deltaT;

		b1.y -= z.vy * deltaT;
		b1.x -= z.vx * deltaT;


	}

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
