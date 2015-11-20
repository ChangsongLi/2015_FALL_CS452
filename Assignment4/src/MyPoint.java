
public class MyPoint {
	int x;
	int y;
	double points;

	public MyPoint(int x, int y, double points) {
		this.points = points;
		this.x = x;
		this.y = y;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public double getPoints() {
		return points;
	}

	public void setPoints(double points) {
		this.points = points;
	}

	public boolean equalsTo(MyPoint p) {
		if (getX() == p.getX() && getY() == p.getY()) {
			double dif = Math.abs(getPoints() - p.getPoints());
			if (dif < 0.2)
				return true;
		}
		return false;
	}
}
