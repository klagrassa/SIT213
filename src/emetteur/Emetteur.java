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
     * @param ampMin Amplitude correspondant à un "0" logique
     * @param pasEchantillonage nombre d'échantillons pour représenter un symbole
     */
    public Emetteur(Float ampMax, Float ampMin, int pasEchantillonage) {
        this.ampMax = ampMax;
        this.ampMin = ampMin;
        this.mode ="NRZ";
        this.pasEchantillonage=pasEchantillonage;
    }

    /**
     * @param information l'information reçue
     * @throws InformationNonConforme exeception en cas de non conformité
     */
    @Override
    public void recevoir(Information<Boolean> information) throws InformationNonConforme {
        informationRecue = information;
        informationGenere = new Information<Float>();
        for (Boolean aBoolean : informationRecue) {
            if (aBoolean == Boolean.TRUE) {
                ajouterSymbole(ampMax);
            } else ajouterSymbole(ampMin);

        }
        this.emettre();


    }

    /**
     * Ajoute un symbole dans la LinkedList informationGenere
     * @param amp valeur d'amplitude à ajouter
     */
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
