package emetteur;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import transmetteurs.Transmetteur;

import java.util.Iterator;

public class Emetteur extends Transmetteur<Boolean,Float> {
    private Float ampMax;
    private Float ampMin;
    private String mode;
    private Information<Float> informationGenere= null;
    private int pasEchantillonage;


    /**
     * @param ampMax Amplitude correspondant à un "1" logique
     * @param ampMin
     * @param pasEchantillonage
     */
    public Emetteur(Float ampMax, Float ampMin, int pasEchantillonage) {
        this.ampMax = ampMax;
        this.ampMin = ampMin;
        this.mode ="NRZ";
        this.pasEchantillonage=pasEchantillonage;
    }

    @Override
    public void recevoir(Information<Boolean> information) throws InformationNonConforme {
        informationRecue = information;
        informationGenere = new Information<Float>();
        Iterator it = informationRecue.iterator();
        while (it.hasNext()) {
            if (it.next() == Boolean.TRUE) {
                ajouterSymbole(ampMax);
            } else ajouterSymbole(ampMin);

        }
        this.emettre();


    }
    protected void ajouterSymbole(Float amp){
        for (int i = 0; i<pasEchantillonage; i++){
            informationGenere.add(amp);}
    }

    @Override
    public void emettre() throws InformationNonConforme {
        this.informationEmise = this.informationGenere;
        // émission vers les composants connectés
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }


}
