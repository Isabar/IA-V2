import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Translate;

public class ConvexPolygon extends Polygon {

	static final int maxNumPoints = 3;

	static int max_X = 100, max_Y = 149;
	NumberFormat nf = new DecimalFormat("##.00");

	// randomly generates a polygon
	public ConvexPolygon(int numPoints) {
		super();
		genConvexPolygone(numPoints);
		int r = Main.random.nextInt(256);
		int g = Main.random.nextInt(256);
		int b = Main.random.nextInt(256);
		this.setFill(Color.rgb(r, g, b));
		this.setOpacity(Main.random.nextDouble());
	}

	public ConvexPolygon() {
		super();
	}

	public String toString() {
		String res = super.toString();
		res += " " + this.getFill() + " opacity " + this.getOpacity();
		return res;
	}

	public void addPoint(double x, double y) {
		getPoints().add(x);
		getPoints().add(y);
	}

	// http://cglab.ca/~sander/misc/ConvexMain.randomeration/convex.html
	public void genConvexPolygone(int n) {
		List<Point> points = new LinkedList<Point>();
		List<Integer> abs = new ArrayList<>();
		List<Integer> ord = new ArrayList<>();

		for (int i = 0; i < n; i++) {
			abs.add(Main.random.nextInt(max_X));
			ord.add(Main.random.nextInt(max_Y));
		}
		Collections.sort(abs);
		Collections.sort(ord);
		// System.out.println(abs + "\n" + ord);
		int minX = abs.get(0);
		int maxX = abs.get(n - 1);
		int minY = ord.get(0);
		int maxY = ord.get(n - 1);

		List<Integer> xVec = new ArrayList<>();
		List<Integer> yVec = new ArrayList<>();

		int top = minX, bot = minX;
		for (int i = 1; i < n - 1; i++) {
			int x = abs.get(i);

			if (Main.random.nextBoolean()) {
				xVec.add(x - top);
				top = x;
			} else {
				xVec.add(bot - x);
				bot = x;
			}
		}
		xVec.add(maxX - top);
		xVec.add(bot - maxX);

		int left = minY, right = minY;
		for (int i = 1; i < n - 1; i++) {
			int y = ord.get(i);

			if (Main.random.nextBoolean()) {
				yVec.add(y - left);
				left = y;
			} else {
				yVec.add(right - y);
				right = y;
			}
		}
		yVec.add(maxY - left);
		yVec.add(right - maxY);

		Collections.shuffle(yVec);

		List<Point> lpAux = new ArrayList<>();
		for (int i = 0; i < n; i++)
			lpAux.add(new Point(xVec.get(i), yVec.get(i)));

		// sort in order by angle
		Collections.sort(lpAux, (x, y) -> Math.atan2(x.getY(), x.getX()) < Math.atan2(y.getY(), y.getX()) ? -1
				: Math.atan2(x.getY(), x.getX()) == Math.atan2(y.getY(), y.getX()) ? 0 : 1);

		int x = 0, y = 0;
		int minPolX = 0, minPolY = 0;

		for (int i = 0; i < n; i++) {
			points.add(new Point(x, y));
			x += lpAux.get(i).getX();
			y += lpAux.get(i).getY();

			if (x < minPolX)
				minPolX = x;
			if (y < minPolY)
				minPolY = y;
		}

		int xshift = Main.random.nextInt(max_X - (maxX - minX));
		int yshift = Main.random.nextInt(max_Y - (maxY - minY));
		xshift -= minPolX;
		yshift -= minPolY;
		for (int i = 0; i < n; i++) {
			Point p = points.get(i);
			p.translate(xshift, yshift);
		}
		for (Point p : points)
			addPoint(p.getX(), p.getY());

	}

	public class Point {

		int x, y;

		// generate a random point
		public Point() {
			x = Main.random.nextInt(max_X);
			y = Main.random.nextInt(max_Y);
		}

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void translate(int vx, int vy) {
			x += vx;
			y += vy;
		}

		public boolean equals(Object o) {
			if (o == null)
				return false;
			else if (o == this)
				return true;
			else if (o instanceof Point)
				return ((Point) o).x == this.x && ((Point) o).y == this.y;
			else
				return false;
		}

		public String toString() {
			NumberFormat nf = new DecimalFormat("#.00");
			return "(" + x + "," + y + ")" + nf.format(Math.atan2(y, x))+")";
		}

	}

	public ConvexPolygon TranslationMutation() {

		int vx = Main.random.nextInt(max_X);
		int vy = Main.random.nextInt(max_Y);
		Translate translate = new Translate(vx, vy);
		this.getTransforms().add(translate);

		return this;
	}

	public ConvexPolygon RotationMutation() {

		Random r = new Random();

		this.setRotate(r.nextDouble());
		return this;
	}

	public ConvexPolygon PointMutation() {
		int i = Main.random.nextInt(this.getPoints().size());

		if (i % 2 == 1) {
			i = i - 1;
		}
		this.getPoints().remove(i);
		this.getPoints().remove(i);

		int x = 1 + Main.random.nextInt(ConvexPolygon.max_X);
		int y = 1 + Main.random.nextInt(ConvexPolygon.max_Y);
		this.addPoint(x, y);
		return this;
	}

	public ConvexPolygon CouleurMutation() {
// couleur seulement augmenter ou diminuer un peu pas changer totalement ? 
		int r = Main.random.nextInt(256);
		int g = Main.random.nextInt(256);
		int b = Main.random.nextInt(256);
		this.setFill(Color.rgb(r, g, b));
		return this;
	}

	public ConvexPolygon OpaciteMutation() {

		this.setOpacity(Main.random.nextDouble());
		return this;
	}

	public ConvexPolygon TypeMutation() {

		int i = Main.random.nextInt(6);
		ConvexPolygon p;
		if (i == 0) {
			p = this.CouleurMutation();
		} else if (i == 1) {
			p = this.PointMutation();
		} else if (i == 2) {
			p = this.OpaciteMutation();
		} else if (i == 3) {
			p = this.RotationMutation();

		} else if (i == 4) {
			p = this.TranslationMutation();

		} else {
			p = new ConvexPolygon(Population.numPoint);
		}
		return p;
	}

}