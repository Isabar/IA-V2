import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class ConvexPolygon extends Polygon {

	//static final int maxNumPoints = 3;
	static Random gen = new Random();
	static int max_X, max_Y;
	NumberFormat nf = new DecimalFormat("##.00");

	// randomly generates a polygon
	public ConvexPolygon(int numPoints) {
		super();
		genRandomConvexPolygone(numPoints);
		int r = gen.nextInt(256);
		int g = gen.nextInt(256);
		int b = gen.nextInt(256);
		this.setFill(Color.rgb(r, g, b));
		this.setOpacity(gen.nextDouble());
	}

	public ConvexPolygon() {
		super();
	}

	/*public ConvexPolygon(ConvexPolygon cp) {
		this.nf = cp.nf;
		for(int i = 0; i < cp.getPoints().size()-1; i+=2){
			this.addPoint(cp.getPoints().get(i), cp.getPoints().get(i+1));
}
		Color c = (Color) cp.getFill();
		this.setFill(Color.rgb((int) (c.getRed() * 255), (int) (c.getGreen() * 255), (int) (c.getBlue() * 255)));
		this.setOpacity(cp.getOpacity());
	}*/

	public String toString() {
		String res = super.toString();
		res += " " + this.getFill() + " opacity " + this.getOpacity();
		return res;
	}

	public void addPoint(double x, double y) {
		getPoints().add(x);
		getPoints().add(y);
	}

	// http://cglab.ca/~sander/misc/ConvexGeneration/convex.html
	public void genRandomConvexPolygone(int n) {
		List<Point> points = new LinkedList<Point>();
		List<Integer> abs = new ArrayList<>();
		List<Integer> ord = new ArrayList<>();

		for (int i = 0; i < n; i++) {
			abs.add(gen.nextInt(max_X));
			ord.add(gen.nextInt(max_Y));
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

			if (gen.nextBoolean()) {
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

			if (gen.nextBoolean()) {
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

		int xshift = gen.nextInt(max_X - (maxX - minX));
		int yshift = gen.nextInt(max_Y - (maxY - minY));
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
			x = gen.nextInt(max_X);
			y = gen.nextInt(max_Y);
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
			return "(" + x + "," + y + ")"; // + nf.format(Math.atan2(y, x))+")";
		}

	}

	public ConvexPolygon TranslationMutation() {
		System.out.println("translation");
		ConvexPolygon P = new ConvexPolygon();
		Random random = new Random();
		int vx = random.nextInt(max_X);
		int vy = random.nextInt(max_Y);
		Translate translate = new Translate(vx, vy);
		this.getTransforms().add(translate);
		return P;
	}
// probleme ici 
	/*public ConvexPolygon RotationMutation() {
		System.out.println("rotation");
		Random random = new Random();
		List<Double> point = this.getPoints();
		if (point.size() != 0) {

			Rotate rotate = new Rotate(1 + random.nextInt(360), point.get(0), point.get(1));
			this.getTransforms().addAll(rotate);
		}

		// a continuer
		return this;
	}*/

	public ConvexPolygon PointMutation() {
		// ObservableList<Double> l =this.getPoints();
		//System.out.println("point mutation ");
		//assert this.getPoints().size()>=3;
		Random random = new Random();
		//System.out.println(this.getPoints().size());
		
		int i = random.nextInt(this.getPoints().size() - 1);
		if (i<0) {
			i=0;
		}
		if (i % 2 == 0) {
			this.getPoints().remove(i);
			this.getPoints().remove(i);
		}
		if (i % 2 == 1) {
			this.getPoints().remove(i);
			this.getPoints().remove(i - 1);
		}

		// this.getPoints().setAll(l);
		// ModifList(l,this);// fonction remet la liste modifiée dans le polygon

		int x = random.nextInt(ConvexPolygon.max_X);
		int y = random.nextInt(ConvexPolygon.max_Y);
		this.addPoint(x, y);
		//System.out.println(this.getPoints().size());
		return this;
	}
	/*
	 * public void ModifList(List<Double> l, ConvexPolygon P){
	 * //System.out.println("avant modif" +P.getPoints()); P.getPoints().setAll(l);
	 * //System.out.println("liste"+l); //System.out.println("apres modif"
	 * +P.getPoints()); }
	 */

	public ConvexPolygon CouleurMutation() {
		ConvexPolygon P = this;
		//System.out.println("couleur mutation");
		Random x = new Random();
		int r = x.nextInt(256);
		int g = x.nextInt(256);
		int b = x.nextInt(256);
		P.setFill(Color.rgb(r, g, b));
		return P;
	}

	public ConvexPolygon OpaciteMutation() {
	//	System.out.println("opacite mutation");
		ConvexPolygon P = this;
		Random r = new Random();
		this.setOpacity(r.nextDouble());
		return P;
	}

	public ConvexPolygon TypeMutation() {
//		assert this.getPoints().size()>=3;
		Random r = new Random();
		int i = r.nextInt(4);
//		System.out.println( " i muatation "+i);
		ConvexPolygon p;
		if (i == 0) {
			p = this.PointMutation();
		} else if (i == 1) {
			p = this.CouleurMutation();
		} else if (i == 2) {
			p = this.OpaciteMutation();
		} /*else if (i == 3) {
			p = this.RotationMutation();
		} else if (i == 4) {
			p = this.TranslationMutation();
		}*/ else {
			p = new ConvexPolygon(Population.numPoint);
		}
		//assert p.getPoints().size()>=3;
		//System.out.println("point après mut "+ p.getPoints().size());
		return p;
	}

}