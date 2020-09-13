package recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import transmetteurs.Transmetteur;

public class Recepteur<R,E> extends Transmetteur<Float,Boolean> {
    private int pasEchantillonnage;

    public Recepteur(int pasEchantillonnage) {
        this.pasEchantillonnage = pasEchantillonnage;
    }

    @Override
    public void recevoir(Information<Float> information) throws InformationNonConforme {
        informationRecue = information;
        informationEmise = new Information<Boolean>();
        System.out.println(informationRecue.nbElements());
        for (int i = pasEchantillonnage/2 ;i<=informationRecue.nbElements();i+=pasEchantillonnage){
            if (informationRecue.iemeElement(i)<=2.5f){
                informationEmise.add(Boolean.FALSE);}
            else informationEmise.add(Boolean.TRUE);
        }
        this.emettre();

    }

    @Override
    public void emettre() throws InformationNonConforme {
        // émission vers les composants connectés
        for (DestinationInterface<Boolean> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }

    public void setPasEchantillonnage(int pasEchantillonnage) {
        this.pasEchantillonnage = pasEchantillonnage;
    }
}
