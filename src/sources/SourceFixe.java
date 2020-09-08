package sources;

import information.Information;

/**
 * 
 * @author Tritarelli Vincent et La Grassa Karl
 * 
 * Source qui génère un signal correspondant au message entré en paramètre.
 *
 * @param <T> : type de l'information à manipuler
 */
public class SourceFixe<T> extends Source<Boolean> {

	/**
	 * Constructeur de la classe SourceFixe
	 * 
	 * @param mess - String, message à envoyer à la chaîne de transmission
	 */
	public SourceFixe(String mess) {
		informationGeneree = new Information<Boolean>();
		
		// Encodage pour envoyer
		for (int i = 0; i < mess.length(); i++)
		{
			if (mess.charAt(i) == '1')
				informationGeneree.add(Boolean.TRUE);
			else informationGeneree.add(Boolean.FALSE);
		}
		informationEmise = informationGeneree;
		
	}

}
