package recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import transmetteurs.Transmetteur;

/**
 * Recepteur d'information. Convertit les signaux analogiques en logiques.
 * 
 * @author Groupe 3
 *
 * @param <R> - type de l'information en reception
 * @param <E> - type de l'information en émission
 */
public class Recepteur<R,E> extends Transmetteur<Float,Boolean> {
    private int pasEchantillonnage;
    private Float ampMax;
    private Float ampMin;

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
}
