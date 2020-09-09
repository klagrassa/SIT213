package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;

/**
 * Transmetteur parfait, il se contente de retransmettre l'information
 * qui arrive en réception.
 * 
 * @author Vincent Tritarelli et La Grassa Karl
 *
 * @param <R> : récepteur 
 * @param <E> : émetteur 
 */
public class TransmetteurParfait extends Transmetteur<Boolean, Boolean> {

	public TransmetteurParfait() {
		
	}

	@Override
	public void recevoir(Information information) throws InformationNonConforme {
		this.informationRecue = information;
		this.emettre();
	}

	@Override
	public void emettre() throws InformationNonConforme {
		this.informationEmise = this.informationRecue;
		// émission vers les composants connectés
		for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
			destinationConnectee.recevoir(informationEmise);
		}
	}
}
