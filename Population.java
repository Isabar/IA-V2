import java.util.Comparator;
import java.util.ArrayList;

public class Population implements Comparator<List50Polygons> {
	ArrayList<List50Polygons> l;
	public static int effectif = 200;
	public static int effectifSSPop = 50;
	public static int meilleurSSeffectif = 25;
	public static int numPoint = 3; // nb de sommets max pour les polygons
	public static List50Polygons best;

	public Population() {
		super();
		l = new ArrayList<List50Polygons>(effectif);
		for (int i = 0; i < effectif; i++) {
			l.add(new List50Polygons(numPoint));
		}
		best = new List50Polygons(numPoint);
		best.setScore();
	}

	public ArrayList<List50Polygons> selection(ArrayList<List50Polygons> l, int nbIndividus) {

		ArrayList<List50Polygons> popSelectionnee = new ArrayList<List50Polygons>(effectifSSPop);
		// on garde un certain nombre de meilleurs
		for (int k = 0; k < meilleurSSeffectif; k++) {
			popSelectionnee.add(l.remove(l.size() - 1));
		}

		// les autres sont selectionnes au hasard
		double scoreMin = l.get(l.size() - 1).score;
		double scoreMax = l.get(0).score;

		while (popSelectionnee.size() < nbIndividus) {
			int i = (int) (Main.random.nextInt(l.size() - 1));
			List50Polygons indSelectionne = l.get(i);
			double r = scoreMin + (double) (Main.random.nextDouble() * scoreMax - scoreMin + 1);

			if (r >= indSelectionne.score) {
				popSelectionnee.add(indSelectionne);

			} else if (scoreMin == scoreMax) {
				popSelectionnee.add(indSelectionne);

			}
		}
		return popSelectionnee;
	}

	public ArrayList<List50Polygons> selectionElitiste(ArrayList<List50Polygons> l, int nbIndividus) {
		ArrayList<List50Polygons> l2 = new ArrayList<List50Polygons>(effectifSSPop);

		for (int i = 0; i < nbIndividus; i++) {
			l2.add(l.get(l.size() - i - 1));
		}
		return l2;
	}

	public ArrayList<List50Polygons> crossover(ArrayList<List50Polygons> l) {
		int nbCouples = effectif / 2;

		ArrayList<List50Polygons> progeniture = new ArrayList<List50Polygons>(effectif);
		int crossoverPoint = -1;
		for (int i = 0; i < nbCouples; i++) {
			int indiceparent1 = (int) (Main.random.nextInt((l.size())));
			int indiceparent2 = (int) (Main.random.nextInt(l.size()));
			while (crossoverPoint <= 0 || crossoverPoint >= Main.nbPolygons) {
				crossoverPoint = (int) (Math.random() * Main.nbPolygons);
			}

			List50Polygons parent1 = l.get(indiceparent1);
			List50Polygons parent2 = l.get(indiceparent2);
			List50Polygons enfant1 = new List50Polygons();
			List50Polygons enfant2 = new List50Polygons();

			for (int j = 0; j < crossoverPoint; j++) {
				enfant1.list.add(parent1.list.get(j));
				enfant2.list.add(parent2.list.get(j));
			}

			for (int k = crossoverPoint; k < Main.nbPolygons; k++) {
				enfant1.list.add(parent2.list.get(k));
				enfant2.list.add(parent1.list.get(k));

			}

			enfant1.setScore();
			enfant2.setScore();
			progeniture.add(enfant1);
			progeniture.add(enfant2);
		}
		return progeniture;
	}

	public ArrayList<List50Polygons> Doublecrossover(ArrayList<List50Polygons> l) {
		int nbCouples = effectif / 2;

		ArrayList<List50Polygons> progeniture = new ArrayList<List50Polygons>();
		int crossoverPoint1 = -1;
		int crossoverPoint2 = -1;
		for (int i = 0; i < nbCouples; i++) {
			int indiceparent1 = (int) (Math.random() * (l.size() - 1));
			int indiceparent2 = (int) (Math.random() * (l.size() - 1));
			
			while ((crossoverPoint1 <=0 || crossoverPoint1 >= Main.nbPolygons)
					&& (crossoverPoint2 <=0 || crossoverPoint2>= Main.nbPolygons)) { 
				crossoverPoint1 = (int) (Math.random() * 50);
				crossoverPoint2 = (int) (Math.random() * 50);
			}

			List50Polygons parent1 = l.get(indiceparent1);
			List50Polygons parent2 = l.get(indiceparent2);

			List50Polygons enfant1 = new List50Polygons();
			List50Polygons enfant2 = new List50Polygons();

			if (crossoverPoint1 > crossoverPoint2) {
				int tmp = crossoverPoint1;
				crossoverPoint1 = crossoverPoint2;
				crossoverPoint2 = tmp;
			}

			for (int j = 0; j < crossoverPoint1; j++) { 
				enfant1.list.add(parent1.list.get(j));
				enfant2.list.add(parent2.list.get(j));
			}

			for (int k = crossoverPoint1; k < crossoverPoint2; k++) { 
				enfant1.list.add(parent2.list.get(k));
				enfant2.list.add(parent1.list.get(k));

			}
			for (int k = crossoverPoint2; k < Main.nbPolygons; k++) { 
				enfant1.list.add(parent2.list.get(k));
				enfant2.list.add(parent1.list.get(k));
			}
			enfant1.setScore();
			enfant2.setScore();
			progeniture.add(enfant1);
			progeniture.add(enfant2);
		}
		return progeniture;

	}

	public ArrayList<List50Polygons> mutationPolygon(ArrayList<List50Polygons> l) {
		for (int i = 0; i < l.size(); i++) {
			double proba = Math.random();
			if (proba < Main.probaMutation) {
				int mutationPoint = (int) (Math.random() * Main.nbPolygons);
				ConvexPolygon CP = new ConvexPolygon(numPoint);
				l.get(i).list.set(mutationPoint, CP);
				l.get(i).setScore();
			}
		}
		return l;
	}

	public ArrayList<List50Polygons> mutationCaracteristique(ArrayList<List50Polygons> l) {
		for (int i = 0; i < l.size(); i++) {
			double proba = Math.random();
			if (proba < Main.probaMutation) {
				int mutationPoint = (int) (Math.random() * Main.nbPolygons);
				ConvexPolygon CP = l.get(i).list.get(mutationPoint);
				CP = CP.TypeMutation();
				l.get(i).list.set(mutationPoint, CP);
				l.get(i).setScore();
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