package recepteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;


/**
 * Recepteur d'information. Convertit les signaux analogiques en logiques.
 * 
 * @author Groupe 3
 *
 * @param <R> - type de l'information en reception
 * @param <E> - type de l'information en émission
 */
public class RecepteurCanal<R,E> extends Recepteur<Float,Boolean>  {


    /**
     * Constructeur classique
     *
     * @param ampMax - amplitude max du signal attendu
     * @param ampMin - amplitude min du signal attendu
     * @param pasEchantillonnage - pas d'échantillonage pour le signal attendu
     */
    public RecepteurCanal(Float ampMax, Float ampMin, int pasEchantillonnage) {
        super(ampMax,ampMin,pasEchantillonnage);

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
        decoder(information);

        this.emettre();

        }

    
    public void decoder(Information<Float> information)throws InformationNonConforme{
    	 informationRecue = information;
         informationEmise = new Information<Boolean>();

         for (int i = pasEchantillonnage/2 ;i<=informationRecue.nbElements();i+= 3*pasEchantillonnage){
         	try{
             if (informationRecue.iemeElement(i) ==null){
                 break;
             }
            if (informationRecue.iemeElement(i)<=(ampMax+ampMin)/2f){
            	if (informationRecue.iemeElement(i+pasEchantillonnage)<=(ampMax+ampMin)/2f) {
                	if (informationRecue.iemeElement(i+2*pasEchantillonnage)<=(ampMax+ampMin)/2f) {
                		informationEmise.add(Boolean.FALSE);
                	}
                	else {
                		informationEmise.add(Boolean.TRUE);
                	}
            	}
            	else {
                	informationEmise.add(Boolean.FALSE);
            	}
            }
            else {
            	if (informationRecue.iemeElement(i+pasEchantillonnage)<=(ampMax+ampMin)/2f) {
                		informationEmise.add(Boolean.TRUE);
            	}
            	else {
                	if (informationRecue.iemeElement(i+2*pasEchantillonnage)<=(ampMax+ampMin)/2f) {
                		informationEmise.add(Boolean.FALSE);
                	}
                	else {
                		informationEmise.add(Boolean.TRUE);
                	}
            	}
            	
            }
         }
         	catch (IndexOutOfBoundsException ignored){}
         }

    }
}
