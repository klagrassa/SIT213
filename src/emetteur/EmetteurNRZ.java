package emetteur;

public class EmetteurNRZ<R,E> extends Emetteur<R,E> {
    /**
     * @param ampMax            Amplitude correspondant à un "1" logique
     * @param ampMin            Amplitude correspondant à un "0" logique
     * @param pasEchantillonage nombre d'échantillons pour représenter un symbole
     */
    public EmetteurNRZ(Float ampMax, Float ampMin, int pasEchantillonage) {
        super(ampMax, ampMin, pasEchantillonage);
    }

    @Override
    protected void ajouterSymbole(Float amp) {
        for (int i = 0; i<pasEchantillonage; i++){
            informationGenere.add(amp);}
    }
}
