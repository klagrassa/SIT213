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
    private int pasEchantillonnage;
    private Float ampMax;
    private Float ampMin;
    private int retardMax =0;
    private Information<Float> informationRegenerer = new Information<>();

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
//        resynchrSignal();
//        regenereSiganl();
//        informationRecue = informationRegenerer;
//        SondeAnalogique sonde = new SondeAnalogique("resynchro");
//        sonde.recevoir(informationRecue);
        for (int i = pasEchantillonnage/2 ;i<=informationRecue.nbElements();i+=pasEchantillonnage){
            if (informationRecue.iemeElement(i) ==null){
                break;
            }
           if (informationRecue.iemeElement(i)<=(ampMax+ampMin)/2f){
                informationEmise.add(Boolean.FALSE);
           }
           else informationEmise.add(Boolean.TRUE);
        }
        this.emettre();
    }
    
    public void decoder(Information<Float> information)throws InformationNonConforme{
    	 informationRecue = information;
         informationEmise = new Information<Boolean>();

         for (int i = pasEchantillonnage/2 ;i<=informationRecue.nbElements();i+= 3*pasEchantillonnage){
             if (informationRecue.iemeElement(i) ==null){
                 break;
             }
            if (informationRecue.iemeElement(i)<=(ampMax+ampMin)/2f){
            	if (informationRecue.iemeElement(i+pasEchantillonnage)<=(ampMax+ampMin)/2f) {
                	if (informationRecue.iemeElement(i+2*pasEchantillonnage)<=(ampMax+ampMin)/2f) {
                		informationEmise.add(Boolean.TRUE);
                	}
                	else {
                		informationEmise.add(Boolean.FALSE);
                	}
            	}
            	else {
                	informationEmise.add(Boolean.TRUE);
            	}
            }
            else {
            	if (informationRecue.iemeElement(i+pasEchantillonnage)<=(ampMax+ampMin)/2f) {
                		informationEmise.add(Boolean.FALSE);
            	}
            	else {
                	if (informationRecue.iemeElement(i+2*pasEchantillonnage)<=(ampMax+ampMin)/2f) {
                		informationEmise.add(Boolean.TRUE);
                	}
                	else {
                		informationEmise.add(Boolean.FALSE);
                	}
            	}
            	
            }
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

    public void setRetardMax(int retardMax) {
        if (retardMax%pasEchantillonnage != 0){
            retardMax = ((retardMax/pasEchantillonnage)+1) * pasEchantillonnage;
        }
        this.retardMax = retardMax;
    }
    protected Float calculPuissance(Float[] tabAmp, int fenetre){
        Float puissance = 0f;

        for (Float amp:tabAmp) {

            puissance+=amp;
        }
        return Math.abs(puissance)/fenetre;
    }
    protected void resynchrSignal(){
        int fenetre = pasEchantillonnage/2;
        int indice = 0;
        int compteur = 0;
        Float puissance = null;
        Float puissanceFenetre= null;
        Float[] tabAmp = new Float[fenetre];
        for (int i = 0; i <fenetre ; i++) {

            tabAmp[i] =informationRecue.iemeElement(i);
        }
        puissanceFenetre = calculPuissance(tabAmp,fenetre);

        for (Float amp:informationRecue) {

            if (indice/fenetre != 1){
                tabAmp[indice] = amp;
            }
            else {
                puissance = calculPuissance(tabAmp,fenetre);

                if(puissance<puissanceFenetre/5){
                    for (int i = compteur; i <informationRecue.nbElements() ; i++) {
                        informationRecue.setIemeElement(i,null);
                    }
                    break;
                }

                if (puissance > puissanceFenetre){

                    puissance = (puissanceFenetre/puissance);
                    for (int i = compteur; i <informationRecue.nbElements() ; i++) {
                      try{
                        informationRecue.setIemeElement(i,informationRecue.iemeElement(i)*puissance);}
                      catch (NullPointerException e){
                          informationRecue.setIemeElement(i,null);
                      }
                    }
                }
                indice = -1;
            }
            compteur++;
            indice++;
        }
    }
    protected void regenereSiganl(){
        for (Float amp:informationRecue) {
            if (amp !=null)
                informationRegenerer.add(amp);
        }
    }
}
