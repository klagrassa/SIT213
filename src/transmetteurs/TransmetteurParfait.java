package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import visualisations.SondeAnalogique;

import java.util.LinkedList;

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
		if (dt!= null){
			Information<Float> informationEmise = new Information<Float>();

			LinkedList<Information<Float>> informationTrajetMultiple =new LinkedList<>();
			for (int i = 0; i < dt.size(); i++) {
				informationTrajetMultiple.add(addPadding(i));
			}

			int compteurBoucle = 0;
			for (R amp:informationRecue) {
				int compteur = 0;
				for (Information<Float> unTrajet: informationTrajetMultiple) {
					unTrajet.add((Float) amp*ar.get(compteur));
					compteur++;
				}
				informationEmise.add(calculSommeAmp(informationTrajetMultiple,compteurBoucle,(float) amp));
				compteurBoucle++;
			}
			int max =0;
			Information<Float> cheminsSecondaires = null;
			for (Information<Float> unTrajet: informationTrajetMultiple) {
				if (  max < unTrajet.nbElements()){
					max = unTrajet.nbElements();
					cheminsSecondaires = unTrajet;
				}
			}
			if (max !=0){
				for (int i = compteurBoucle; i <cheminsSecondaires.nbElements(); i++) {
					informationEmise.add(calculSommeAmp(informationTrajetMultiple, i,0f));
				}}
//			max =1;
//			for (Information<Float> unTrajet: informationTrajetMultiple) {
//				SondeAnalogique sonde = new SondeAnalogique("trajet dans transmetteur" +max);
//				sonde.recevoir(unTrajet);
//				max++;
//			}
			this.informationEmise  = (Information<E>) informationEmise;

			this.emettre();
		}
		else {
			this.informationEmise = (Information<E>) this.informationRecue;
		this.emettre();}
	}

	
	/**
	 * Emet l'informationEmise
	 */
	@Override
	public void emettre() throws InformationNonConforme {

		// émission vers les composants connectés
		for (DestinationInterface<E> destinationConnectee : destinationsConnectees) {
			destinationConnectee.recevoir(informationEmise);
		}
	}
}
