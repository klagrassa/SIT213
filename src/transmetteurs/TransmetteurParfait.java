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
public class TransmetteurParfait <R,E> extends Transmetteur<R,E> {

	public TransmetteurParfait() {
		super();
	}

	@Override
	public void recevoir(Information<R> information) throws InformationNonConforme {
		this.informationRecue = information;
		this.emettre();
	}

	@Override
	public void emettre() throws InformationNonConforme {
		this.informationEmise = (Information<E>) this.informationRecue;
		// émission vers les composants connectés
		for (DestinationInterface<E> destinationConnectee : destinationsConnectees) {
			destinationConnectee.recevoir(informationEmise);
		}
	}
}
