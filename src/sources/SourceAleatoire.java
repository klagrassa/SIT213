package sources;

import java.util.Random;

import information.Information;

/**
 * 
 * @author Tritarelli Vincent et La Grassa Karl
 *
 * Source qui génère un signal en fonction de la graine donnée
 *
 * @param <T> : type de l'information à manipuler
 */
public class SourceAleatoire<T> extends Source<Boolean> {
	
	/**
	 * Constructeur par défaut, utilisé si la seed n'est pas précisée
	 * 
	 * @param tailleMessage taille du message à générer
	 */
	public SourceAleatoire(int tailleMessage) {
		informationGeneree = new Information<Boolean>();
		Random rd = new Random();
		for (int i = 0; i < tailleMessage; i++)
		{
			informationGeneree.add(rd.nextBoolean());
		}
		informationEmise = informationGeneree;
	}	
	
	/**
	 * Constructeur de la source aléatoire, donner une semance de type int
	 * 
	 * @param seed - int, semance utilisée pour la génération
	 * @param tailleMessage - int, taille du message à générer
	 */
	public SourceAleatoire(Integer seed, int tailleMessage) {
		informationGeneree = new Information<Boolean>();
		Random rd = new Random(seed);
		for (int i = 0; i < tailleMessage; i++)
		{
			informationGeneree.add(rd.nextBoolean());
		}
		informationEmise = informationGeneree;
	}
	
	
}
