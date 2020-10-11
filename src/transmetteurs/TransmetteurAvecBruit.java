package transmetteurs;

import destinations.DestinationInterface;
import information.Information;
import information.InformationNonConforme;
import visualisations.SondeAnalogique;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

/**
 * @param <R> Type d'information recue
 * @param <E> Type d'information Emise
 */
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


    /**
     * Traite l'information reçue
     * Si il y a des trajet multilple le programme calule les différentes trajectoires et en fait la somme pour obtenir le signal résultant
     * puis ajoute le bruit additif gaussien avant d'émettre le message
     * Sinon créer un signal simple avec le bruit additif gaussien 
     * @param information l'information reçue
     * @throws InformationNonConforme
     */
    @Override
    public void recevoir(Information<Float> information) throws InformationNonConforme {
        // Inintialise les variables contentant les informations recue et émise
        this.informationRecue = information;
        this.informationEmise = new Information<Float>();
        // On vérifie si le tableau contenant les décalages est instancié si pas vérifié alors pas de trajet multiple à générer
        if (dt!= null){
            //Initialisation d'une liste pouvant acceuillir les différent trajets
            LinkedList<Information<Float>> informationTrajetMultiple =new LinkedList<>();
            // On balaie le tableau des retard et ajoute un padding de zéro correspondant à la valeur de chaque retard
//          La liste informationTrajetMultiple contient touts les trajets retardés
            for (int i = 0; i < dt.size(); i++) {
                informationTrajetMultiple.add(addPadding(i));
            }
             // Initialisation d'un compteur globale pour balayer qu'une seule fois la liste d'information Recue
            int compteurBoucle = 0;
            for (Float amp:informationRecue) {
                // Inintialisation d'un compteur local afin d'affecter les différents coefficients d'amplitude aux trajets multiples
                // Compteur remis à zéros à chaque passage de boucle
                int compteur = 0;
                for (Information<Float> unTrajet: informationTrajetMultiple) {
                    // Ajout de l'amplitute de l'information directe de chaque trajet multiplié par leur coffiecient définit
                    unTrajet.add(amp*ar.get(compteur));
                    compteur++;
                }
                // Calcul de la somme des trajets multiples et du signal directe non retardé
                // Puis Ajout du résultat
                informationEmise.add(calculSommeAmp(informationTrajetMultiple,compteurBoucle,amp));
                compteurBoucle++;
            }
            // Determine le trajet le plus long
            int max =0;
            Information<Float> cheminsSecondaires = null;
            for (Information<Float> unTrajet: informationTrajetMultiple) {
               if (  max < unTrajet.nbElements()){
                    max = unTrajet.nbElements();
                    cheminsSecondaires = unTrajet;
               }
            }
            // Compplete la liste à émettre avec uniquement la somme des signaux retardés
            if (max !=0){
                for (int i = compteurBoucle; i <cheminsSecondaires.nbElements(); i++) {
                    informationEmise.add((calculSommeAmp(informationTrajetMultiple, i,0f)));
            }}
            // Calcul de la puissance à émettre
            ps = calculPuissance(informationEmise);
            // Calcul de l'écart type du bruit
            sigmaB =Math.sqrt((ps*nbEch)/(2*Math.pow(10,(snrPb/10))));
            compteurBoucle =0;

            // Ajout du bruit dans l'information à émettre
            for (Float amp :informationEmise) {
                Float bruit = (float)(0.f+calculBruit(sigmaB));
                puissancebruit.add(bruit);
                informationEmise.setIemeElement(compteurBoucle,(amp+bruit));
                compteurBoucle++;
            }
            // affiche les trajets multiples
//        max =1;
//        for (Information<Float> unTrajet: informationTrajetMultiple) {
//            SondeAnalogique sonde = new SondeAnalogique("trajet dans transmetteur" +max);
//            sonde.recevoir(unTrajet);
//            max++;
//        }
            // Affiche les perfomances du simulateur
            affichePerfTransmission();
            // On émet l'information
            this.emettre();
        }
        // Si pas de retard à générer
         else {
              // Calcul de la puissance à émettre
            ps = calculPuissance(informationRecue);
            // Calcul de l'écart type du bruit
            sigmaB =Math.sqrt((ps*nbEch)/(2*Math.pow(10,(snrPb/10))));
             // Ajout du bruit dans l'information à émettre
             for (Float amp:informationRecue){
            Float bruit = (float)(0.f+calculBruit(sigmaB));
            puissancebruit.add(bruit);
//            ajouterValeurFichier(bruit);
            informationEmise.add((float) (amp+bruit));
        }
        // Affiche les perfomances du simulateur
        affichePerfTransmission();
        this.emettre();
        }
    }

    /**
     * Envoi l'informationEmise à touts les composants auquels le transmetteur est connecté
     * @throws InformationNonConforme
     */
    @Override
    public void emettre() throws InformationNonConforme {
        // émission vers les composants connectés
        for (DestinationInterface<Float> destinationConnectee : destinationsConnectees) {
            destinationConnectee.recevoir(informationEmise);
        }
    }

    /**
     * @param information signal que l'on souhaite calculer la puissance
     * @return  La puissance du signal fournit en paramètre
     */
    public Float calculPuissance(Information<Float> information){
        Float puissance= 0f;
        for (Float amp : information ){
            puissance+= amp*amp;
        }

        if (information.nbElements() !=0)
            return puissance/(information.nbElements());
        else return 0f;
    }

    /**
     * @param sigmaB écart type du bruit à générer
     * @return valeur de bruit blanc gaussien
     */
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

    /**
     * Affecte un nouveau rapport eb/N0
     * @param snrPb Rapport eb/N0
     */
    public void setSnrPb(Float snrPb) {
        this.snrPb = snrPb;
    }

    /**
     * Affecte un nouveau pas échantillonnage
     * @param nbEch nombre d'échantillon porté sur un symbole
     */
    public void setNbEch(int nbEch) {
        this.nbEch = nbEch;
    }

    /**
     * @param seed Germe pertmettant de générer un bruit identique à chaque simulation
     */
    public void setSeed(int seed) {
        generateur = new Random(seed);
    }

    /**
     * Affiche les performances du simulateur
     */
    public void affichePerfTransmission(){
        Float pb = calculPuissance(puissancebruit);
        System.out.println("Puissance moyenne du signal émis : "+ps);
        System.out.println("Puissance du moyenne du signal reçu (signal + buit) : "+calculPuissance(informationEmise));
        System.out.println("Valeur de sigma (ecart-type du bruit) : "+sigmaB);
        System.out.println("Puissance moyenne du bruit : "+pb);
        double snr  = 10*Math.log10(ps/pb);
        double rapportEbN0 = 10*Math.log10((nbEch*ps)/(2*pb));
        System.out.println("Rapport Eb/N0 recalculé en dB : "+rapportEbN0);
        System.out.println("Rapport Signal Bruit en dB : "+snr);
    }


}
