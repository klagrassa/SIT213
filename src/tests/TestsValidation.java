package tests;

import app.ArgumentsException;
import app.Simulateur;

/**
 * Classe qui va permettre l'automatisation d'une partie des test.
 * Les tests sans ajout de bruit doivent avoir un TEB de 0, sinon ils échouent
 * Dans le cas des tests avec bruit, l'utilisateur doit statuer visuellement si le
 * test est correct ou non
 * 
 * @author Groupe A3
 *
 */
public class TestsValidation {

	public static void main(String[] args) throws ArgumentsException {
		int testsRealises = 0;
		int testsPositifs = 0;
		boolean testAvecBruit = false;
		
		try {
			Simulateur sim = new Simulateur(args);
			for (String arguments:args)
			{
				// On veut savoir si c'est un test avec bruit ou non
				if (arguments.equals("-snrpb"))
				{
					testAvecBruit = true;
				}
				
			}
			sim.execute();
			float TEB = sim.calculTauxErreurBinaire();
			testsRealises++;
			if (!testAvecBruit)
			{
				if (TEB == 0f)
					testsPositifs++;
				System.out.println(testsPositifs + "/" + testsRealises +" tests réussis, TEB = "+TEB);
			}
			else 
				System.out.println("TEB = "+ TEB + " Vérifiez visuellement le résultat");
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
