package emetteurs;

import app.ArgumentsException;

/**
 * Implémentation de l'émetteur RZ.
 * Les contraintes et la forme de ce signal sont disponibles dans 
 * les consignes de l'étape 2.
 *  
 * @author Groupe 3
 *
 * @param <R> - type de l'information en reception
 * @param <E> - type de l'information en emission
 */
public class EmetteurRZ<R,E> extends Emetteur<R,E> {

    /**
     * @param ampMax            Amplitude correspondant à un "1" logique
     * @param ampMin            Amplitude correspondant à un "0" logique
     * @param pasEchantillonage nombre d'échantillons pour représenter un symbole
     */
    public EmetteurRZ(Float ampMax, Float ampMin, int pasEchantillonage) {

        super(ampMax, ampMin, pasEchantillonage);
    }

    @Override
    protected void ajouterSymbole(Float amp) {

        for (int i = 0; i<pasEchantillonage; i++){
            if (i<pasEchantillonage/3){
                informationGenere.add(0f);}
            else if (i<(2*pasEchantillonage)/3){
                informationGenere.add(amp);}
            else informationGenere.add(0f);
        }

    }

    public void setAmpMin(Float ampMin) throws ArgumentsException {
        if (ampMin!=0f)
            throw new ArgumentsException("Amplitude min doit être égale à 0, ici amp : "+ampMin);
        this.ampMin = ampMin;
    }

}
