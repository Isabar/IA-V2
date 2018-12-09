import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
	public static final double epsilon = 0.000001; 
	public static double probaMutation = 0.3;
	public static int nbPolygons = 50;
	public static Color[][] image;
	public static Random random;

	public static void main(String[] args) {
		random = new Random(1); 
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		long startChrono = System.currentTimeMillis();
		loadImage();

		Population p1 = new Population();

		// pour le calcul de la variance
		double s1 = 0;
		double s2 = 0;
		double V = 1; 
		
		// nombre de générations
		int n = 0;
		
		// test d'arret si score semble convenable 
		double scoreFinal = 25;
		double testScore = 200;

		while (n<3200) {
			p1.l.sort(p1); 

			ArrayList<List50Polygons> generationNouvelle = p1.selection(p1.l, Population.effectifSSPop);
			ArrayList<List50Polygons> crossOver = p1.crossover(generationNouvelle);

			ArrayList<List50Polygons> mutation;
			if (testScore > 35) {
				mutation = p1.mutationPolygon(crossOver);
			} else {
				mutation = p1.mutationCaracteristique(crossOver);
			}
			
			mutation.sort(p1);
			
			// garde le meilleur à chaque itération 
			if (Population.best.score < mutation.get(mutation.size() - 1).score) {
				mutation.remove(0);
				mutation.add(Population.best);
			} else {
				Population.best = mutation.get(mutation.size() - 1);
			}
			p1.l = mutation;
			p1.l.sort(p1);

			// calcul de la variance
			s1 = p1.l.get(p1.l.size() - 1).score; 
			s2 = (s2 + p1.l.get(p1.l.size() - 1).score) / 2; 
			V = Math.pow(s1 - s2, 2.0) / n;

			System.out.println("Variance =" + V);

			testScore = p1.l.get(p1.l.size() - 1).score;
			System.out.println("test score" + testScore);
			
			if (n % 100 == 0) {
				enregistrementImage(p1.l, n);
			}
			n++;
		}

		Group picture = enregistrementImage(p1.l,n);
		// affichage de l'image dans l'interface graphique
		Scene scene = new Scene(picture, ConvexPolygon.max_X, ConvexPolygon.max_Y);
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		int minutes = (int) ((System.currentTimeMillis() - startChrono / (1000 * 60)) % 60);
		System.out.println(minutes);
		
		System.out.println("score final" + p1.l.get(p1.l.size() - 1).score);

	}

	public static void loadImage() {
		String targetImage = "monaLisa-100.jpg";
		int maxX = 0;
		int maxY = 0;

		try {
			BufferedImage bi = ImageIO.read(new File(targetImage));
			maxX = bi.getWidth();
			maxY = bi.getHeight();
			ConvexPolygon.max_X = maxX;
			ConvexPolygon.max_Y = maxY;
			Main.image = new Color[maxX][maxY];
			for (int i = 0; i < maxX; i++) {
				for (int j = 0; j < maxY; j++) {
					int argb = bi.getRGB(i, j);
					int b = (argb) & 0xFF;
					int g = (argb >> 8) & 0xFF;
					int r = (argb >> 16) & 0xFF;
					int a = (argb >> 24) & 0xFF;
					Main.image[i][j] = Color.rgb(r, g, b);
				}
			}
		} catch (IOException e) {
			System.err.println(e);
			System.exit(9);
		}
	}

	public Group enregistrementImage(ArrayList<List50Polygons> l, int n) {
		Group picture = new Group();
		for (int i = 0; i < 50; i++) {
			ConvexPolygon p = l.get(l.size() - 1).list.get(i);
			picture.getChildren().add(p);
		}
		System.out.println(" création image");
		
		WritableImage wimg = new WritableImage(ConvexPolygon.max_X, ConvexPolygon.max_Y);
		picture.snapshot(null, wimg);
	
		// Stockage de l'image dans un fichier .png
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null);
		try {
			ImageIO.write(renderedImage, "png", new File("test" + n + ".png"));
			System.out.println("wrote image in " + "test.png");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return picture;
	}
}
