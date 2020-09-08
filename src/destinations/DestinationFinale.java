package destinations;

import information.Information;
import information.InformationNonConforme;

/**
 * 
 * @author Tritarelli Vincent et La Grassa Karl
 *
 * @param <T> : type de l'information que manipule la destination.
 */
public class DestinationFinale<T> extends Destination <T>{

	/** 
	 * Constructeur par défaut
	 */
	public DestinationFinale() {
		super();
	}
	
	@Override
	public void recevoir(Information information) throws InformationNonConforme {
		this.informationRecue = information;
	}

}
