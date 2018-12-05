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
			// System.out.println (" fin tri");
			ArrayList<List50Polygons> generationNouvelle = new ArrayList<List50Polygons>(
					p1.selection(p1.l, Population.effectifSSPop));
			/*
			 * for (int i = 0; i < generationNouvelle.size(); i++) {
			 * System.out.println("liste " + i + " "+generationNouvelle.get(i).score); }
			 */
			ArrayList<List50Polygons> CrossOver = new ArrayList<List50Polygons>(p1.crossover(generationNouvelle));
			// System.out.println("cross over ");
			/*
			 * for (int i = 0; i < CrossOver.size(); i++) { System.out.println("liste " + i
			 * + " "+CrossOver.get(i).score); }
			 */
			// System.out.println(" fin crossover");
			// System.out.println("fin mutation");
			ArrayList<List50Polygons> Mutation = new ArrayList<List50Polygons>(p1.mutation(CrossOver));
			Mutation.sort(p1);
			// System.out.println("mutation");
			// System.out.println(Mutation.get(Mutation.size()-1).score );
			if (Population.best.score < Mutation.get(Mutation.size() - 1).score) {
				// System.out.println(Mutation.get(0));
				Mutation.remove(0);
				// System.out.println(Mutation.get(0));
				Mutation.add(Population.best);
				// System.out.println(Population.best.score);
				// System.out.println(Mutation.get(0));
				// System.out.println("best ");
			} else {
				// garder plus de meilleurs ?
				Population.best = Mutation.get(Mutation.size() - 1);
			}
			// System.out.println(Mutation.get(Mutation.size()-1).score );
			// calcul de la variance
			/*
			 * for (int i=0; i<Mutation.size();i++) { p1.l.set(i,Mutation.get(i));}
			 */
			p1.l = Mutation;
			p1.l.sort(p1);

			s1 = p1.l.get(p1.l.size() - 1).score; // somme des meilleurs scores le dernier individu est le meilleur
			s2 = (s2 + p1.l.get(p1.l.size() - 1).score) / 2; // somme des carrés des meilleurs scores
			V = Math.pow(s1 - s2, 2.0) / n;

			System.out.println("Variance =" + V);

			testScore = p1.l.get(p1.l.size() - 1).score;
			System.out.println("test score" + testScore);
			System.out.println(n);
			if (n % 100 == 0) {
				enregistrementImage(p1.l, n);
			}
		}
		/*
		 * for (int i = 0; i < p1.l.size(); i++) { System.out.println("liste " + i +
		 * " "+ p1.l.get(i).score); }
		 */
		// affichage
		// Color[][] target = null;

		System.out.println("end ");
		// formation de l'image par superposition des polygones
		Group picture = new Group();
		for (int i = 0; i < 50; i++) {
			ConvexPolygon p = p1.l.get(p1.l.size() - 1).list.get(i);
			picture.getChildren().add(p);
		}
		System.out.println(" création image");
		// pb stockage image -> l'image est vide

		WritableImage wimg = new WritableImage(ConvexPolygon.max_X, ConvexPolygon.max_Y);
		picture.snapshot(null, wimg);
		PixelReader pr = wimg.getPixelReader();
		// On utilise le PixelReader pour lire chaque couleur
		// ici, on calcule la somme de la distance euclidienne entre le vecteur (R,G,B)
		// de la couleur du pixel cible et celui du pixel de l'image générée
		double res = 0;
		for (int i = 0; i < ConvexPolygon.max_X; i++) {
			for (int j = 0; j < ConvexPolygon.max_Y; j++) {
				Color c = pr.getColor(i, j);
				res += Math.pow(c.getBlue() - image[i][j].getBlue(), 2) + Math.pow(c.getRed() - image[i][j].getRed(), 2)
						+ Math.pow(c.getGreen() - image[i][j].getGreen(), 2);
			}
		}
		System.out.println(Math.sqrt(res));

		// Stockage de l'image dans un fichier .png
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null);
		try {
			ImageIO.write(renderedImage, "png", new File("test.png"));
			System.out.println("wrote image in " + "test.png");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// affichage de l'image dans l'interface graphique
		System.out.println("----------------------");

		System.out.println("n" + n);
		System.out.println(ConvexPolygon.max_X + " *" + ConvexPolygon.max_Y);
		Scene scene = new Scene(picture, ConvexPolygon.max_X, ConvexPolygon.max_Y);// Stockage de l'image dans un
																					// fichier .png
		primaryStage.setScene(scene);

		primaryStage.show();
		System.out.print("chrono  ");
		int minutes = (int) ((System.currentTimeMillis() - startChrono / (1000 * 60)) % 60);
		System.out.println(minutes);
		p1.l.sort(p1);
		System.out.println("score final" + p1.l.get(p1.l.size() - 1).score);

	}

	// il faut que la séléction donne un sous-ensemble de la population qu'on
	// utilise ensuite dans le crossover

	public static void loadImage() {
		// formation de l'image par superposition des polygones
		String targetImage = "monaLisa-100.jpg";
		// initialisation de l'image à tout blanc ?
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
		double res = 0;
		/*
		 * for (int i=0;i<ConvexPolygon.max_X;i++){ for (int
		 * j=0;j<ConvexPolygon.max_Y;j++){ Color c = pr.getColor(i, j); res +=
		 * Math.pow(c.getBlue()-image[i][j].getBlue(),2)
		 * +Math.pow(c.getRed()-image[i][j].getRed(),2)
		 * +Math.pow(c.getGreen()-image[i][j].getGreen(),2); } }
		 * System.out.println(Math.sqrt(res));
		 */

		// Stockage de l'image dans un fichier .png
		RenderedImage renderedImage = SwingFXUtils.fromFXImage(wimg, null);
		try {
			ImageIO.write(renderedImage, "png", new File("test" + n + ".png"));
			System.out.println("wrote image in " + "test.png");
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
}
