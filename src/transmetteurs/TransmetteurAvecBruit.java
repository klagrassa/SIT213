package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Logger;


public class TransmetteurAvecBruit<R,E> extends Transmetteur<Float,Float> {
    private java.lang.Float snrPb;
    private int nbEch;

    public TransmetteurAvecBruit(java.lang.Float snrPb, int nbEch) {
        super();
        this.snrPb = snrPb;
        this.nbEch =nbEch;
    }

    @Override
    public void recevoir(Information<Float> information) throws InformationNonConforme {
        this.informationRecue = information;
        this.informationEmise = new Information<Float>();
        for (Float amp:informationRecue){
            Double bruit = calculBruit();
//            ajouterValeurFichier(bruit);
            informationEmise.add((float) (amp+bruit));
        }
        this.emettre();
    }

    @Override
    public void emettre() throws InformationNonConforme {
        // émission vers les composants connectés
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }

    public Float calculPuissance(){
        Float puissance= 0f;
        for (Float amp : informationRecue ){
            puissance+= (Float) amp* (Float) amp;
        }
        if (informationRecue.nbElements() !=0)
            return puissance/informationRecue.nbElements();
        else return 0f;
    }

    public Double calculBruit(){
        Float ps = calculPuissance();
        Double sigmaB =Math.sqrt((ps*nbEch)/(2*Math.pow(10,(snrPb/10))));
        Random generateur = new Random();
        Float a1 = generateur.nextFloat();
        Float a2 = generateur.nextFloat();
        return sigmaB*Math.sqrt((-2*Math.log10(1-a1)))*Math.cos(2*Math.PI*a2);
    }

 public void ajouterValeurFichier(double bruit) {
     BufferedWriter bufWriter = null;
     FileWriter fileWriter = null;
     try{

         fileWriter = new FileWriter("C:\\Users\\Elia\\Documents\\Cour IMT\\SIT200\\SIT213\\Git\\src\\transmetteurs\\HistogrammeBruit", true);
         bufWriter = new BufferedWriter(fileWriter);
         //Insérer un saut de ligne
         bufWriter.newLine();
         bufWriter.write((String.valueOf(bruit)));
         bufWriter.close();
         bufWriter.close();
         fileWriter.close();
 }catch (IOException e){
         System.out.println("erreur");
     }

 }
}
