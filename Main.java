import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
	public static final double epsilon = 0.000001; // seuil d'arret
	public static double probaMutation = 0.1;
	public static int nbPolygons = 50;
	public static Color[][] image;

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		loadImage();
		long startChrono = System.currentTimeMillis();
		Population p1 = new Population();

		// pour le calcul de la variance
		double s1 = 0;
		double s2 = 0;
		int n = 0;// nombre de générations

		double V = 1; // on initialise la variance a 1

		// deux possibilités pour la condition d'arrêt : elitiste ou quand le score
		// semble convenable
		double scoreFinal = 10;
		double testScore = 200;

		while (n < 100) {
			n++; // nombre d'iterations

			p1.l.sort(p1); // on la trie en fonction du score
			ArrayList<List50Polygons> generationNouvelle = new ArrayList<List50Polygons>(
					p1.selection(p1.l, Population.effectifSSPop));

			ArrayList<List50Polygons> CrossOver = new ArrayList<List50Polygons>(p1.crossover(generationNouvelle));

			ArrayList<List50Polygons> Mutation = new ArrayList<List50Polygons>(p1.mutation(CrossOver));
			Mutation.sort(p1);

			if (Population.best.score < Mutation.get(Mutation.size() - 1).score) {
// on garde le meilleur et on enlève le moins bon 
				Mutation.remove(0);
				Mutation.add(Population.best);
			} else {
				Population.best = Mutation.get(Mutation.size() - 1);
			}

			/*
			 * for (int i=0; i<Mutation.size();i++) { p1.l.set(i,Mutation.get(i));}
			 */
			p1.l = new ArrayList<List50Polygons>(Mutation);
			p1.l.sort(p1);

			// calcul de la variance
			s1 = p1.l.get(p1.l.size() - 1).score;
			s2 = (s2 + p1.l.get(p1.l.size() - 1).score) / 2;
			V = Math.pow(s1 - s2, 2.0) / n;

			System.out.println("Variance =" + V);

			testScore = p1.l.get(p1.l.size() - 1).score;
			System.out.println("test score" + testScore);
			System.out.println(n);
			if (n % 100 == 0) {
				enregistrementImage(p1.l, n);
			}
		}
		// affichage

		affichageImage(p1.l, primaryStage);
		System.out.println("end ");
		// formation de l'image par superposition des polygones
		System.out.print("chrono  ");
		int minutes = (int) ((System.currentTimeMillis() - startChrono / (1000 * 60)) % 60);
		System.out.println(minutes);


	}


	public static void loadImage() {
		// formation de l'image par superposition des polygones
		String targetImage = "monaLisa-100.jpg";
		
		int maxX = 0;
		int maxY = 0;

		try {
			BufferedImage bi = ImageIO.read(new File(targetImage));
			maxX = bi.getWidth();
			maxY = bi.getHeight();
			ConvexPolygon.max_X = maxX;
			ConvexPolygon.max_Y = maxY;
			image = new Color[maxX][maxY];
			for (int i = 0; i < maxX; i++) {
				for (int j = 0; j < maxY; j++) {
					int argb = bi.getRGB(i, j);
					int b = (argb) & 0xFF;
					int g = (argb >> 8) & 0xFF;
					int r = (argb >> 16) & 0xFF;
					int a = (argb >> 24) & 0xFF;
					image[i][j] = Color.rgb(r, g, b);
				}
			}
		} catch (IOException e) {
			System.err.println(e);
			System.exit(9);
		}
	}

	public void enregistrementImage(ArrayList<List50Polygons> l, int n) {
		Group picture = new Group();
		for (int i = 0; i < nbPolygons; i++) {
			ConvexPolygon p = l.get(l.size() - 1).list.get(i);
			picture.getChildren().add(p);
		}

		WritableImage wimg = new WritableImage(ConvexPolygon.max_X, ConvexPolygon.max_Y);
		picture.snapshot(null, wimg);
		// PixelReader pr = wimg.getPixelReader();
		// On utilise le PixelReader pour lire chaque couleur
		// ici, on calcule la somme de la distance euclidienne entre le vecteur (R,G,B)
		// de la couleur du pixel cible et celui du pixel de l'image générée

		// Stockage de l'image dans un fichier .png
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null);
		try {
			ImageIO.write(renderedImage, "png", new File("test" + n + ".png"));
			System.out.println("wrote image in " + "test.png");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public void affichageImage(ArrayList<List50Polygons> l, Stage stage) {

		Group picture = new Group();
		for (int i = 0; i < 50; i++) {
			ConvexPolygon p = l.get(l.size() - 1).list.get(i);
			picture.getChildren().add(p);
		}
		WritableImage wimg = new WritableImage(ConvexPolygon.max_X, ConvexPolygon.max_Y);
		picture.snapshot(null, wimg);
		PixelReader pr = wimg.getPixelReader();

		// Stockage de l'image dans un fichier .png
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null);
		try {
			ImageIO.write(renderedImage, "png", new File("testfinal.png"));
			System.out.println("wrote image in " + "test.png");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// affichage de l'image dans l'interface graphique
		System.out.println("----------------------");
		Scene scene = new Scene(picture, ConvexPolygon.max_X, ConvexPolygon.max_Y);
		stage.setScene(scene);

		stage.show();

	}
}
