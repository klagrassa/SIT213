package transmetteurs;

import com.sun.source.tree.NewArrayTree;
import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import visualisations.SondeAnalogique;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

public class TransmetteurAvecBruit<R,E> extends Transmetteur<Float,Float> {
    private Float snrPb;
    private int nbEch;
    private Float ps;
    private Double sigmaB;
    private Information<Float> puissancebruit= new Information<>();
    Random generateur = new Random();

    public TransmetteurAvecBruit(Float snrPb, int nbEch) {
        super();
        this.snrPb = snrPb;
        this.nbEch =nbEch;
    }


    @Override
    public void recevoir(Information<Float> information) throws InformationNonConforme {
        this.informationRecue = information;
        this.informationEmise = new Information<Float>();

        if (dt!= null){

            LinkedList<Information<Float>> informationTrajetMultiple =new LinkedList<>();
            for (int i = 0; i < dt.size(); i++) {
                informationTrajetMultiple.add(addPadding(i));
            }

            int compteurBoucle = 0;
            for (Float amp:informationRecue) {
                int compteur = 0;
                for (Information<Float> unTrajet: informationTrajetMultiple) {
                    unTrajet.add(amp*ar.get(compteur));
                    compteur++;
                }
                informationEmise.add(calculSommeAmp(informationTrajetMultiple,compteurBoucle,amp));
                compteurBoucle++;
            }
            int max =0;
            Information<Float> cheminsSecondaires = null;
            for (Information<Float> unTrajet: informationTrajetMultiple) {
               if (  max < unTrajet.nbElements()){
                    max = unTrajet.nbElements();
                    cheminsSecondaires = unTrajet;
               }
            }
            if (max !=0){
                for (int i = compteurBoucle; i <cheminsSecondaires.nbElements(); i++) {
                    informationEmise.add((calculSommeAmp(informationTrajetMultiple, i,0f)));
            }}
            ps = calculPuissance(informationEmise);

            sigmaB =Math.sqrt((ps*nbEch)/(2*Math.pow(10,(snrPb/10))));
            compteurBoucle =0;
            for (Float amp :informationEmise) {
                Float bruit = (float)(0.f+calculBruit(sigmaB));
                puissancebruit.add(bruit);
                informationEmise.setIemeElement(compteurBoucle,(amp+bruit));
                compteurBoucle++;
            }
//        max =1;
//        for (Information<Float> unTrajet: informationTrajetMultiple) {
//            SondeAnalogique sonde = new SondeAnalogique("trajet dans transmetteur" +max);
//            sonde.recevoir(unTrajet);
//            max++;
//        }
            affichePerfTransmission();
            this.emettre();
        }
         else {
            ps = calculPuissance(informationRecue);

            sigmaB =Math.sqrt((ps*nbEch)/(2*Math.pow(10,(snrPb/10))));
             for (Float amp:informationRecue){
            Float bruit = (float)(0.f+calculBruit(sigmaB));
            puissancebruit.add(bruit);
//            ajouterValeurFichier(bruit);
            informationEmise.add((float) (amp+bruit));
        }
        affichePerfTransmission();
        this.emettre();
        }
    }

    @Override
    public void emettre() throws InformationNonConforme {
        // émission vers les composants connectés
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }

    public Float calculPuissance(Information<Float> information){
        Float puissance= 0f;
        for (Float amp : information ){
            puissance+= amp*amp;
        }

        if (information.nbElements() !=0)
            return puissance/(information.nbElements());
        else return 0f;
    }

    public Double calculBruit(Double sigmaB){
        Float a1 = generateur.nextFloat();
        Float a2 = generateur.nextFloat();
        return sigmaB*Math.sqrt((-2*Math.log(1-a1)))*Math.cos(2*Math.PI*a2);
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
    }
     catch (IOException e){
         System.out.println("erreur");
     }

    }

    public void setSnrPb(Float snrPb) {
        this.snrPb = snrPb;
    }

    public void setNbEch(int nbEch) {
        this.nbEch = nbEch;
    }

    public void setSeed(int seed) {
        generateur = new Random(seed);
    }
    public void affichePerfTransmission(){
        Float pb = calculPuissance(puissancebruit);
        System.out.println("Puissance moyenne du signal émis : "+ps);
        System.out.println("Puissance du moyenne du signal reçu : "+calculPuissance(informationEmise));
        System.out.println("Valeur de sigma (ecart-type du bruit) : "+sigmaB);
        System.out.println("Puissance moyenne du bruit : "+pb);
        double snr  = 10*Math.log10(ps/pb);
        double rapportEbN0 = 10*Math.log10((nbEch*ps)/(2*pb));
        System.out.println("Rapport Eb/N0 recalculé en dB : "+rapportEbN0);
        System.out.println("Rapport Signal Bruit en dB : "+snr);
    }


}
