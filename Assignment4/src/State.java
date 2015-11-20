import java.util.LinkedList;

public class State {

	int x;
	int y;
	int c;
	LinkedList<MyPoint> remain = new LinkedList<MyPoint>();

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getC() {
		return c;
	}

	public void setC(int c) {
		this.c = c;
	}

	public LinkedList<MyPoint> getRemain() {
		return remain;
	}

	public void setRemain(LinkedList<MyPoint> remain) {
		this.remain = remain;
	}
}
