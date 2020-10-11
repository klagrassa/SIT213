package recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import transmetteurs.Transmetteur;
import visualisations.SondeAnalogique;

import java.util.Arrays;

/**
 * Recepteur d'information. Convertit les signaux analogiques en logiques.
 * 
 * @author Groupe 3
 *
 * @param <R> - type de l'information en reception
 * @param <E> - type de l'information en émission
 */
public class Recepteur<R,E> extends Transmetteur<Float,Boolean> {
    protected int pasEchantillonnage;
    protected Float ampMax;
    protected Float ampMin;
    protected int retardMax = 0;
    protected Information<Float> informationRegenerer = new Information<>();

    /**
     * Constructeur classique
     * 
     * @param ampMax - amplitude max du signal attendu
     * @param ampMin - amplitude min du signal attendu
     * @param pasEchantillonnage - pas d'échantillonage pour le signal attendu
     */
    public Recepteur(Float ampMax, Float ampMin, int pasEchantillonnage) {
        this.pasEchantillonnage = pasEchantillonnage;
        this.ampMin =ampMin;
        this.ampMax =ampMax;
    }

    /**
     * Decode l'information analogique en numérique.
     * A partir du pas d'échantillonage, on essaie de déduire la place des "plateaux"
     * (partie utile du signal) et selon la tension on retrouve si l'information est un
     * 1 ou 0.
     *
     * @param information - information à décoder.
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConforme{
        informationRecue = information;
        informationEmise = new Information<Boolean>();

        for (int i = pasEchantillonnage/2 ;i<=informationRecue.nbElements();i+=pasEchantillonnage){
            try{
            if (informationRecue.iemeElement(i) ==null){
                break;
            }
            if (informationRecue.iemeElement(i)<=(ampMax+ampMin)/2f){
                informationEmise.add(Boolean.FALSE);
            }
            else informationEmise.add(Boolean.TRUE);
        }
        catch (IndexOutOfBoundsException ignored){}}

        this.emettre();
    }
    


    /**
     * Transmet le message décodé vers la destination.
     */
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

    protected Float calculPuissance(Float[] tabAmp, int fenetre){
        Float puissance = 0f;

        for (Float amp:tabAmp) {

            puissance+=amp;
        }
        return Math.abs(puissance)/fenetre;
    }

}
