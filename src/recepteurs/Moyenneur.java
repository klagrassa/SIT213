package recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import transmetteurs.Transmetteur;

public abstract class Moyenneur extends Transmetteur<Float,Float> {

    protected int pasEchantillonnage;



    /**
     * Constructeur classique
     *
     * @param pasEchantillonnage - pas d'échantillonage pour le signal attendu
     */
    public Moyenneur(int pasEchantillonnage) {
        super();
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

}
