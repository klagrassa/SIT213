package recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import transmetteurs.Transmetteur;

public abstract class Decodeur extends Transmetteur<Float,Float> {

    protected int pasEchantillonnage;
    protected Float ampMax;
    protected Float ampMin;


    /**
     * Constructeur classique
     *
     * @param ampMax             - amplitude max du signal attendu
     * @param ampMin             - amplitude min du signal attendu
     * @param pasEchantillonnage - pas d'échantillonage pour le signal attendu
     */
    public Decodeur(Float ampMax, Float ampMin, int pasEchantillonnage) {
        super();
        this.ampMax = ampMax;
        this.ampMin =ampMin;
        this.pasEchantillonnage=pasEchantillonnage;
    }

    @Override
    public void recevoir(Information<Float> information) throws InformationNonConforme {
        this.informationRecue = information;
        this.informationEmise = new Information<Float>();

        decode();
        this.emettre();
    }

    @Override
    public void emettre() throws InformationNonConforme {
        // émission vers les composants connectés
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }

    protected abstract void decode();

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
