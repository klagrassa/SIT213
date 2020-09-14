package emetteurs;

/**
 * Implémentation de l'émetteur NRZ.
 * Les contraintes et la forme de ce signal sont disponibles dans 
 * les consignes de l'étape 2.
 * 
 * @author Groupe 3
 *
 * @param <R> - type de l'information en reception
 * @param <E> - type de l'information en emission
 */
public class EmetteurNRZ<R,E> extends Emetteur<R,E> {
    /**
     * @param ampMax            Amplitude correspondant à un "1" logique
     * @param ampMin            Amplitude correspondant à un "0" logique
     * @param pasEchantillonage nombre d'échantillons pour représenter un symbole
     */
    public EmetteurNRZ(Float ampMax, Float ampMin, int pasEchantillonage) {
        super(ampMax, ampMin, pasEchantillonage);
    }

    /**
     * Implémentation d'ajouterSymbole pour les signaux NRZ
     * Le symbole est un "plateau" de valeur amp.
     * 
     * @param amp - amplitude du symbole
     */
    @Override
    protected void ajouterSymbole(Float amp) {
        for (int i = 0; i<pasEchantillonage; i++){
            informationGenere.add(amp);}
    }
}
