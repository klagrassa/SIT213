package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;

/**
 * Transmetteur parfait, il se contente de retransmettre l'information
 * qui arrive en réception.
 * 
 * @author Groupe 3
 *
 * @param <R> - type d'information en reception
 * @param <E> - type d'information en emission
 */
public class TransmetteurParfait <R,E> extends Transmetteur<R,E> {

	public TransmetteurParfait() {
		super();
	}

	/**
	 * Recoit et renvoie l'information vers la destination
	 * 
	 * @param information - information reçue
	 */
	@Override
	public void recevoir(Information<R> information) throws InformationNonConforme {
		this.informationRecue = information;
		this.emettre();
	}

	
	/**
	 * Emet l'informationEmise
	 */
	@Override
	public void emettre() throws InformationNonConforme {
		this.informationEmise = (Information<E>) this.informationRecue;
		// émission vers les composants connectés
		for (DestinationInterface<E> destinationConnectee : destinationsConnectees) {
			destinationConnectee.recevoir(informationEmise);
		}
	}
}
