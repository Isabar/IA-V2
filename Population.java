import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Population  implements Comparator<List50Polygons> {
	ArrayList<List50Polygons> l; // si trop long faire avec priorityQueue
	public static int effectif = 100;// effectif initial de la population
	public static int effectifSSPop = 20;// effectif de la sous-population suite à la séléction
	public static int numPoint = 7; // nb de sommets max pour les polygons
	public static List50Polygons best;
	
	// ???
	// vraiment nécessaire d'ajouter la génération à l'individu ? on fait le
	// décompte avec le n
	public static int nbgeneration;

	public Population() {
		super();
		l = new ArrayList<List50Polygons>();
		nbgeneration = 0;
		for (int i = 0; i < effectif; i++) {
			l.add(new List50Polygons(numPoint, nbgeneration));
		}
		best = new List50Polygons(numPoint);
		best.setScore();
	}



	// l'application javafx tourne dans le start suite à l'appel du main

		
	public ArrayList<List50Polygons> selection(ArrayList<List50Polygons> l, int nbIndividus) {
		int meilleurs = nbIndividus / 3;
		ArrayList<List50Polygons> popSelectionnee = new ArrayList<List50Polygons>();
		for (int k = 0; k < meilleurs; k++) {
			popSelectionnee.add(l.remove(l.size()-1));
		}
		double scoreMin = l.get(l.size() - 1).score;
		double scoreMax = l.get(0).score;

		while (popSelectionnee.size() < nbIndividus) {
			int i = (int) (Math.random() * (l.size() - 1));
			// System.out.println("i "+i);
			List50Polygons indSelectionne = l.get(i);
			double r = scoreMin + (double) (Math.random() * (scoreMax - scoreMin + 1));
			// System.out.println("scoreMin "+scoreMin);
			// System.out.println("scoreMax "+scoreMax);
			// System.out.println(l.get(i).score);
			// System.out.println("score "+indSelectionne.score);
			// System.out.println("random "+r);
			if (r >= indSelectionne.score) {
				popSelectionnee.add(indSelectionne);
				// System.out.println("ajout");
			} else if (scoreMin == scoreMax) {
				popSelectionnee.add(indSelectionne);
				// System.out.println("ajout");
			}
		}

		// int k = l.size() - nbIndividus;
		// System.out.println(k);
		/*
		 * for (int i = 0; i < k; i++) { l.remove(); }
	*/	 
		// System.out.println( "taille après selec"+l.size());
		return popSelectionnee;
	}
	
/*	public ArrayList<List50Polygons> selection(ArrayList<List50Polygons> l, int nbIndividus) {
		ArrayList<List50Polygons> l2= new ArrayList<List50Polygons>();
		// System.out.println(k);
		for (int i = 0; i < nbIndividus; i++) {
			//System.out.println("score séléction "+l.get(l.size()-i-1).score);
		
			l2.add(l.get(l.size()-i-1));
		}
		// System.out.println( "taille après selec"+l.size());
		return l2;
	}*/


	// retourne la liste des enfants créés par le crossover de même taille qu ela
	// poop initial
	// prend en paramètre la liste crée suite à la sélection
	public ArrayList<List50Polygons> crossover(ArrayList<List50Polygons> l) {
		int nbCouples = effectif / 2;
		// System.out.println(" nb couples : " + nbCouples);
		ArrayList<List50Polygons> progeniture = new ArrayList<List50Polygons>();
		int crossoverPoint = -1;
		for (int i = 0; i < nbCouples; i++) {
			int indiceparent1 = (int) (Math.random() * (l.size() - 1));// on choisit les coiuples de parents de façon
																		// totalement aléatoire
			int indiceparent2 = (int) (Math.random() * (l.size() - 1));
			while (crossoverPoint < 0 || crossoverPoint >= Main.nbPolygons) { // On garde le cas où =0 les parents restent
				// indentiques dans ce cas ou =50
				crossoverPoint = (int) (Math.random() * Main.nbPolygons);

			}
			// System.out.println("crossoverPoint " +crossoverPoint);

			List50Polygons parent1 = l.get(indiceparent1);
			List50Polygons parent2 = l.get(indiceparent2);
			// System.out.println("test1");
			List50Polygons enfant1 = new List50Polygons(nbgeneration);
			List50Polygons enfant2 = new List50Polygons(nbgeneration);
			// System.out.println("test2");
			for (int j = 0; j < crossoverPoint; j++) { // avant le point de crossover on effectue une copie
				enfant1.list.add(parent1.list.get(j));
				enfant2.list.add(parent2.list.get(j));

			}

			for (int k = crossoverPoint; k < Main.nbPolygons; k++) { // avant le point de crossover on effectue une copie
				enfant1.list.add(parent2.list.get(k));
				enfant2.list.add(parent1.list.get(k));

			}

			enfant1.setScore();
			enfant2.setScore();
			progeniture.add(enfant1);
			progeniture.add(enfant2);
		}
		// System.out.println("progeniture " + progeniture.size());
		return progeniture;

	}

	public ArrayList<List50Polygons> Doublecrossover(ArrayList<List50Polygons> l) {
		int nbCouples = effectif / 2;
		System.out.println(" nb couples : " + nbCouples);
		ArrayList<List50Polygons> progeniture = new ArrayList<List50Polygons>();
		int crossoverPoint1 = -1;
		int crossoverPoint2 = -1;
		for (int i = 0; i < nbCouples; i++) {
			int indiceparent1 = (int) (Math.random() * (l.size() - 1));// on choisit les coiuples de parents de façon //
																		// totalement aléatoire
			int indiceparent2 = (int) (Math.random() * (l.size() - 1));
			while ((crossoverPoint1 < 0 || crossoverPoint1 >= Main.nbPolygons)
					&& (crossoverPoint1 < 0 || crossoverPoint1 >= Main.nbPolygons)) { // On garde le cas où =0 les parents
																					// restent
				// indentiques dans ce cas ou =50
				crossoverPoint1 = (int) (Math.random() * 50);
				crossoverPoint2 = (int) (Math.random() * 50);
			}
			// System.out.println("crossoverPoint " +crossoverPoint);

			List50Polygons parent1 = l.get(indiceparent1);
			List50Polygons parent2 = l.get(indiceparent2);

			List50Polygons enfant1 = new List50Polygons(nbgeneration);
			List50Polygons enfant2 = new List50Polygons(nbgeneration);

			if (crossoverPoint1 > crossoverPoint2) {
				int tmp = crossoverPoint1;
				crossoverPoint1 = crossoverPoint2;
				crossoverPoint2 = tmp;
			}

			for (int j = 0; j < crossoverPoint1; j++) { // avant le point de crossover on effectue une copie
				enfant1.list.add(parent1.list.get(j));
				enfant2.list.add(parent2.list.get(j));

			}

			for (int k = crossoverPoint1; k < crossoverPoint2; k++) { // avant le point de crossover on effectue une
																		// copie
				enfant1.list.add(parent2.list.get(k));
				enfant2.list.add(parent1.list.get(k));

			}
			for (int k = crossoverPoint2; k < Main.nbPolygons; k++) { // avant le point de crossover on effectue une copie
				enfant1.list.add(parent2.list.get(k));
				enfant2.list.add(parent1.list.get(k));

			}

			enfant1.setScore();
			enfant2.setScore();
			progeniture.add(enfant1);
			progeniture.add(enfant2);
		}
		System.out.println("progeniture  " + progeniture.size());
		return progeniture;

	}

	public ArrayList<List50Polygons> mutation(ArrayList<List50Polygons> l) {
		for (int i = 0; i < l.size(); i++) {
			double proba = Math.random();
			if (proba < Main.probaMutation) {
				int mutationPoint = (int) (Math.random() * Main.nbPolygons);
				//ConvexPolygon CP = new ConvexPolygon(numPoint);
				ConvexPolygon CP= l.get(i).list.get(mutationPoint);
				//System.out.println(CP);
				//System.out.println("avant mut "+l.get(i).score );
				CP= CP.TypeMutation();
				//System.out.println(CP);
				System.out.println("score av mut" +l.get(i).score);
				l.get(i).list.set(mutationPoint, CP);
				l.get(i).setScore();
				System.out.println("score ap mut" +l.get(i).score);
				//System.out.println("apres mut "+l.get(i).score);
			}
		}
		return l;
	}

	@Override
	public int compare(List50Polygons l0, List50Polygons l1) {
		if (l0.score < l1.score) {
			return 1;
		} else if (l0.score == l1.score) {
			return 0;
		}
		return -1;
	}

}