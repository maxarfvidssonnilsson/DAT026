import java.util.Random;

public class Model {

	double areaWidth, areaHeight;

	final double GRAVITY = 0;
	int numberOfBalls = 4;
	double averageSpeed = 0.5;
	double errorMargin = 0.1;
	
	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		balls = getBalls(numberOfBalls, averageSpeed, areaHeight, areaWidth);
//		balls = new Ball[2];
//		balls[0] = new Ball(width / 4, height / 4, 1, 1, 0.2);
//		balls[1] = new Ball(3 * width / 4, height / 4, -1, 1, 0.2);
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

	void step(double deltaT) {
		for (Ball b1 : balls) {
			b1.x += deltaT * b1.v.vx;
			b1.y += deltaT * b1.v.vy;

			boolean isInGround = true;
			// detect collision with the border
			if (collidesWithBorder(b1.radius, b1.x, areaWidth, b1.v.vx)) {
				b1.v.vx *= -1; // change direction of ball
			}
			if (collidesWithBorder(b1.radius, b1.y, areaHeight, b1.v.vy)) {
				b1.v.vy *= -1;
			} else {
				isInGround = false;
			}

			for (Ball b2 : balls){
				if (b1 != b2 && b1.checkBallCollsion(b2)){
					double distance = Math.sqrt(Math.pow(b1.x - b2.x,2)+Math.pow(b1.y - b2.y,2));
					int i = 0;
					while ((distance <= b1.radius + b2.radius) && i != 10 ) {

						b1.x -= b1.v.vx * errorMargin * deltaT;
						b2.y -= b2.v.vy * errorMargin* deltaT;
						b1.y -= b1.v.vy * errorMargin * deltaT;
						b2.x -= b2.v.vx * errorMargin * deltaT;

						distance = Math.sqrt(Math.pow(b1.x - b2.x, 2) + Math.pow(b1.y - b2.y, 2));
						i++;
					}
					ballCollision(b1, b2,deltaT);
				}
			}
			if(!isInGround)
				b1.v.vy -= GRAVITY;
		}
	}

	boolean collidesWithBorder (double radius, double position, double border, double velocity) {
		if ((position < radius && velocity < 0) || (position > border - radius && velocity > 0)) {
			return true;
		}
		return false;
	}

	void ballCollision ( Ball b1, Ball b2, double deltaT){
		Vector tangent = new Vector(b1.x - b2.x, b1.y - b2.y);

		double före = b1.getMass()*b1.v.length()+b2.getMass()*b2.v.length();

		//Vector relV = b1.v.subtract(b2.v);
		//Vector force = relV.projectOnVector(tangent);
		Vector directedV1 = b1.v.projectOnVector(tangent);
		Vector directedV2 = b1.v.projectOnVector(tangent);
//
//		b1.v = b1.v.subtract(force);
//		b2.v = b2.v.plus(force);


		double velocity1 = 	((b1.getMass() - b2.getMass()) / (b1.getMass() + b2.getMass())) * directedV1.length() +
				((2 * b2.getMass())            / (b1.getMass() + b2.getMass())) * directedV2.length();


		double velocity2 = 	((2 * b1.getMass())            / (b1.getMass() + b2.getMass())) * directedV1.length()  +
				((b2.getMass() - b1.getMass()) / (b1.getMass() + b2.getMass())) * directedV2.length();

		b1.v = b1.v.subtract(tangent.withLenght(velocity1));
		b1.v = b1.v.subtract(directedV1);
		b2.v = b2.v.plus(tangent.withLenght(velocity2));
		b2.v = b2.v.subtract(directedV2);
		System.out.println("Efter-------------------------------");
		System.out.println( före - b1.getMass()*b1.v.length()+b2.getMass()*b2.v.length());
		System.out.println();

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

		double getMass() {
			return Math.PI * Math.pow(this.radius , 2);
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

		Vector withLenght(double len) {
			return new Vector(this.vx*len/this.length(),this.vy*len/this.length());
		}
	}
}
