package emetteur;

import information.Information;
import information.InformationNonConforme;

import java.util.Iterator;
import java.util.LinkedList;

public class EmetteurNRZT<R,E> extends Emetteur<R,E> {

    private float transition;
    private float delta;
    /**
     * @param ampMax            Amplitude correspondant à un "1" logique
     * @param ampMin            Amplitude correspondant à un "0" logique
     * @param pasEchantillonage nombre d'échantillons pour représenter un symbole
     */
    public EmetteurNRZT(Float ampMax, Float ampMin, int pasEchantillonage) {
        super(ampMax, ampMin, pasEchantillonage);
    }


    public void recevoir(Information<Boolean> information) throws InformationNonConforme {
        this.informationRecue = information;
        this.informationGenere = new Information<Float>();
        Boolean front = null;
        this.delta = ((this.ampMax - ampMin)/2) / ((float)this.pasEchantillonage / 3.0F);

        for (Boolean bitLogique : this.informationRecue) {
            if (front == null) {
                front = bitLogique;
                if (bitLogique == Boolean.TRUE) {
                    //this.ajouterTransition(((this.ampMax + ampMin)/2), -this.delta);
                    for(int i = 0; i < (2*this.pasEchantillonage / 3)*(ampMax/(ampMax+Math.abs(ampMin))); ++i) {
                        this.informationGenere.add( 0f - this.transition);
                        this.transition += -delta;
                    }
                    this.ajouterSymbole(this.ampMax);
                } else {
                    //this.ajouterTransition(((this.ampMax + ampMin)/2), this.delta);
                    for(int i = 0; i < (2*this.pasEchantillonage / 3)*(Math.abs(ampMin)/(ampMax+Math.abs(ampMin))); ++i) {
                        this.informationGenere.add( 0f - this.transition);
                        this.transition += delta;
                    }
                    this.ajouterSymbole(ampMin);
            }} else if (front != bitLogique) {
                front = bitLogique;
                if (bitLogique == Boolean.TRUE) {
                    this.transition = 0f;
                    this.ajouterTransition(this.ampMin, -this.delta);
                    this.ajouterTransition(this.ampMin, -this.delta);
                    ajouterSymbole(ampMax);

                } else {
                    this.transition = 0f;
                    this.ajouterTransition(this.ampMax, this.delta);
                    this.ajouterTransition(this.ampMax, this.delta);
                    ajouterSymbole(ampMin);

                }
            } else if (bitLogique == Boolean.TRUE) {
                this.ajouterSymbole(this.ampMax);
            } else {
                this.ajouterSymbole(this.ampMin);
            }
        }

        this.emettre();
    }

    @Override
    protected void ajouterSymbole(Float amp) {
        for (int i = 0; i<pasEchantillonage/3; i++){
            informationGenere.add(amp);}
    }

    private void ajouterTransition(Float amp, Float delta) {
        for(int i = 0; i < this.pasEchantillonage / 3; ++i) {
            this.informationGenere.add(amp - this.transition);
            this.transition += delta;
        }

    }
}
