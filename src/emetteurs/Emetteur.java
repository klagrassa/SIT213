package emetteurs;

import app.ArgumentsException;
import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import transmetteurs.Transmetteur;

/**
 * Classe mère des emetteurs, elle contient la méthode ajouterSymbole qui 
 * sera implémentée dans les différentes classes filles.
 * Les emetteurs sont des types de transmetteurs qui permettent la conversion
 * d'un signal logique booléen classique en un signal respectant différents protocoles
 * (RZ, NRZ, NRZT,...)
 * 
 * @author Groupe 3
 *
 * @param <R> - type de l'information en réception
 * @param <E> - type de l'information en émission
 */
public abstract class Emetteur<R,E> extends Transmetteur<Boolean,Float> {
    protected Float ampMax;
    protected Float ampMin;
    protected Information<Float> informationGenere= null;
    protected int pasEchantillonage;


    /**
     * @param ampMax Amplitude correspondant à un "1" logique
     * @param ampMin Amplitude correspondant à un "0" logique
     * @param pasEchantillonage nombre d'échantillons pour représenter un symbole
     */
    public Emetteur(Float ampMax, Float ampMin, int pasEchantillonage) {
        this.ampMax = ampMax;
        this.ampMin = ampMin;
        this.pasEchantillonage = pasEchantillonage;
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
    protected abstract void ajouterSymbole(Float amp);
    @Override
    public void emettre() throws InformationNonConforme {
        this.informationEmise = this.informationGenere;
        // émission vers les composants connectés
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }

    public void setAmpMax(Float ampMax) throws ArgumentsException {

        if (ampMax<0f)
            throw new ArgumentsException("Amplitude max doit être supérieur ou égale à 0, ici amp : "+ampMax);
        if (ampMin>=ampMax)
            throw new ArgumentsException("Amplitude max doit être supérieur à ampMin, ici ampMin : "+ampMin +" et ampMax :"+ampMax);
        this.ampMax = ampMax;
    }

    public void setAmpMin(Float ampMin) throws ArgumentsException {
        if (ampMin>0f)
            throw new ArgumentsException("Amplitude min doit être inférieur ou égale à 0, ici amp : "+ampMin);
        if (ampMin>=ampMax)
            throw new ArgumentsException("Amplitude min doit être inférieur à ampMax, ici ampMin : "+ampMin +" et ampMax :"+ampMax);
        this.ampMin = ampMin;
    }

    public void setPasEchantillonage(int pasEchantillonage)  {
        this.pasEchantillonage = pasEchantillonage;
    }
}
