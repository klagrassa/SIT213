package emetteurs;


import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import transmetteurs.Transmetteur;


public class Codeur<R,E> extends Transmetteur<Boolean, Boolean> {
	
    public Codeur() {
    	super();
    }
	
	public void recevoir(Information<Boolean> information) throws InformationNonConforme {
    	informationRecue =information;
    	informationEmise = new Information<Boolean>();
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
		for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
			destinationConnectee.recevoir(informationEmise);
		}
	}
}
