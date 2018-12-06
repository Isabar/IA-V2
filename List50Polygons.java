import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import javafx.scene.Group;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class List50Polygons {
	ArrayList<ConvexPolygon> list;
	double score;
	
	int generation;
	
	public List50Polygons(int generation ) {
		super();
		list=new ArrayList<ConvexPolygon>();
		this.generation=generation;
		}

	public List50Polygons(int n, int generation) {// création de liste avec 50 polygons avec n nb de sommets max des polygones
		list=new ArrayList<ConvexPolygon>();
		this.generation=generation;
		for ( int i=0; i<50; i++) {
			list.add(new ConvexPolygon(n)); // nb de sommet max des polygons 
		}
		//System.out.println("création liste ");
		score = score(list);
	}

	
	public static double score(List<ConvexPolygon> ls){
		
		//System.out.println("Read target image " + targetImage + " " + maxX + "x" + maxY);
		Group image = new Group();
		for (ConvexPolygon p : ls)
			image.getChildren().add(p);
		
		// Calcul de la couleur de chaque pixel.Pour cela, on passe par une instance de 
		// WritableImage, qui possède une méthode pour obtenir un PixelReader.
		WritableImage wimg = new WritableImage(ConvexPolygon.max_X,ConvexPolygon.max_Y);
		image.snapshot(null,wimg);
		PixelReader pr = wimg.getPixelReader();
		// On utilise le PixelReader pour lire chaque couleur
		// ici, on calcule la somme de la distance euclidienne entre le vecteur (R,G,B)
		// de la couleur du pixel cible et celui du pixel de l'image générée	
		double res=0;
		for (int i=0;i<ConvexPolygon.max_X;i++){
			for (int j=0;j<ConvexPolygon.max_Y;j++){
				Color c = pr.getColor(i, j);
				res += Math.pow(c.getBlue()-Main.image[i][j].getBlue(),2)
				+Math.pow(c.getRed()-Main.image[i][j].getRed(),2)
				+Math.pow(c.getGreen()-Main.image[i][j].getGreen(),2);
			}
		}
		//System.out.println("score score "+Math.sqrt(res));
		return Math.sqrt(res);
	}
	
	public String toString(){
		String result=new String();
		for(int i=0;i<list.size();i++) {
			result=result+list.get(i).toString();
		}
	   return  result + " " +generation;
	}

	public void setScore() {
		this.score = score(list);
		//System.out.println("set score"+score);
	}

}