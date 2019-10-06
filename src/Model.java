import java.util.Random;

public class Model {

	double areaWidth, areaHeight;

	final double GRAVITY = 0.2;
	int numberOfBalls = 3;
	double averageSpeed = 0.3;
	double errorMargin = 0.1;
	
	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		balls = getBalls(numberOfBalls, averageSpeed, areaHeight, areaWidth);
		//balls = new Ball[2];
		//balls[0] = new Ball(width / 3, height * 0.9, 1.2, 1.6, 0.2);
		//balls[1] = new Ball(2 * width / 3, height * 0.7, -0.6, 0.6, 0.3);
	}

	Ball[] getBalls(int numberOfBalls, double averageSpeed, double height, double width){
		Ball[] balls = new Ball[numberOfBalls];
		Random random = new Random();
		for (int i = 0; i < numberOfBalls; i++){
			Ball b = new Ball(0,0,0,0,0);
			b.radius = ((double)random.nextInt((int)(1000/Math.pow(numberOfBalls,0.5))))/1000;
			b.v = new Vector(((double)random.nextInt((int) (averageSpeed*200)))/100, ((double)random.nextInt((int) (averageSpeed*200)))/100);

			boolean hasFound = false;
			while (!hasFound) {
				b.x = ((double) random.nextInt((int) (width * 100))) / 150;
				b.y = ((double) random.nextInt((int) (height * 100))) / 150;
				hasFound = true;
				for (int j = 0; j < balls.length; j++){
					if (balls[j] != null && Math.sqrt( Math.pow(b.x - balls[j].x,2) + Math.pow(b.y - balls[j].y, 2) ) < b.radius + balls[j].radius){

						hasFound = false;
						j = balls.length;
					}
					if(b.x < b.radius || b.y < b.radius) {
						hasFound = false;
						j = balls.length;
					}
				}
			}
			balls[i] = b;
		}
		return balls;
	}

	void step(double deltaT) {
		for (Ball b : balls) {
			b.handleCollisionBorder();
		}

		for (int i = 0; i < balls.length ; i++) {
			for (int m = 0; m < i; m++) {
				if (balls[i].checkBallCollsion(balls[m])) {
					minimiseCollisionError(balls[i], balls[m], deltaT);
					ballCollision(balls[i], balls[m]);
				}
			}
		}

		for (Ball b : balls) {
			b.tick(deltaT);
		}
	}

	void minimiseCollisionError( Ball b1, Ball b2, double deltaT) {
		double distance = Math.sqrt(Math.pow(b1.x - b2.x,2)+Math.pow(b1.y - b2.y,2));
		int k = 0;
		while ((distance <= b1.radius + b2.radius) && k != 10 ) {

			b1.x -= b1.v.vx * errorMargin * deltaT;
			b2.y -= b2.v.vy * errorMargin* deltaT;
			b1.y -= b1.v.vy * errorMargin * deltaT;
			b2.x -= b2.v.vx * errorMargin * deltaT;

			distance = Math.sqrt(Math.pow(b1.x - b2.x, 2) + Math.pow(b1.y - b2.y, 2));
			k++;
		}
	}

	void ballCollision ( Ball b1, Ball b2){

		Vector t = new Vector(b1.x - b2.x, b1.y - b2.y);
		Vector relV = b1.v.subtract(b2.v);
		Vector force = relV.projectOnVector(t);
		b1.v = b1.v.subtract(force);
		b2.v = b2.v.plus(force);

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

		void handleCollisionBorder () {
			if (		((this.x < this.radius && this.v.vx < 0) || (this.x > areaWidth - this.radius && this.v.vx > 0))) {
				this.v.vx *= -1; // change direction of ball
			} else  if(	((this.y < this.radius && this.v.vy < 0) || (this.y > areaHeight- this.radius && this.v.vy > 0))) {
				this.v.vy *= -1;
			}
		}

		void tick(double deltaT) {
			if (this.y > areaHeight- this.radius) {
				this.v.vy -= GRAVITY;
			}
			this.x += deltaT * this.v.vx;
			this.y += deltaT * this.v.vy;
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
