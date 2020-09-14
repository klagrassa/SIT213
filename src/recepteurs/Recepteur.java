package recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import transmetteurs.Transmetteur;

public class Recepteur<R,E> extends Transmetteur<Float,Boolean> {
    private int pasEchantillonnage;
    private Float ampMax;
    private Float ampMin;

    public Recepteur(Float ampMax, Float ampMin, int pasEchantillonnage) {
        this.pasEchantillonnage = pasEchantillonnage;
        this.ampMin =ampMin;
        this.ampMax =ampMax;

    }

    @Override
    public void recevoir(Information<Float> information) throws InformationNonConforme {
        informationRecue = information;
        informationEmise = new Information<Boolean>();

        for (int i = pasEchantillonnage/2 ;i<=informationRecue.nbElements();i+=pasEchantillonnage){
            if (informationRecue.iemeElement(i)<=(ampMax+ampMin)/2f){
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

    public void setAmpMax(Float ampMax) {
        this.ampMax = ampMax;
    }

    public void setAmpMin(Float ampMin) {
        this.ampMin = ampMin;
    }
}
