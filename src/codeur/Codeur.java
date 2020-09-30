package codeurs;

import java.util.LinkedList;

import app.ArgumentsException;
import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import transmetteurs.Transmetteur;


public class Codeur<R,E> extends Transmetteur<Boolean, Boolean> {
	
    public Codeur() {
    	super();
    }
	
	public void recevoir(Information<Boolean> informationRecue) throws InformationNonConforme {
        for (Boolean aBoolean : informationRecue) {
            if (aBoolean == Boolean.TRUE) {
            	informationEmise.add(Boolean.TRUE);
            	informationEmise.add(Boolean.FALSE);
            	informationEmise.add(Boolean.TRUE);
            } 
            else {
            	informationEmise.add(Boolean.FALSE);
            	informationEmise.add(Boolean.TRUE);
            	informationEmise.add(Boolean.FALSE);
            }
        }
        this.emettre();
	}

	public void emettre() throws InformationNonConforme {
		// Ã©mission vers les composants connectÃ©s
		for (DestinationInterface<E> destinationConnectee : destinationsConnectees) {
			destinationConnectee.recevoir(informationEmise);
		}
	}
}
