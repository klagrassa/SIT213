package app;

import emetteurs.Emetteur;
import emetteurs.EmetteurNRZ;
import emetteurs.EmetteurNRZT;
import emetteurs.EmetteurRZ;
import recepteurs.Decodeur;
import recepteurs.DecodeurNRZ;
import recepteurs.DecodeurRZ;
import recepteurs.Recepteur;
import sources.*;
import destinations.*;
import transmetteurs.*;

import visualisations.*;

import java.util.*;

/**
 * La classe Simulateur permet de construire et simuler une chaÃ®ne de
 * transmission composÃ©e d'une Source, d'un nombre variable de Transmetteur(s)
 * et d'une Destination.
 *
 * @author cousin
 * @author prou
 *
 */
public class Simulateur {

	/** indique si le Simulateur utilise des sondes d'affichage */
	private boolean affichage = false;
	/**
	 * indique si le Simulateur utilise un message gÃ©nÃ©rÃ© de maniÃ¨re alÃ©atoire
	 */
	private boolean messageAleatoire = true;


	/**
	 * indique si le Simulateur utilise un germe pour initialiser les gÃ©nÃ©rateurs
	 * alÃ©atoires
	 */
	private boolean aleatoireAvecGerme = false;
	/** la valeur de la semence utilisÃ©e pour les gÃ©nÃ©rateurs alÃ©atoires */
	private Integer seed = null;
	/**
	 * la longueur du message alÃ©atoire Ã  transmettre si un message n'est pas
	 * impose
	 */
	private int nbBitsMess = 100;
	/** la chaÃ®ne de caractÃ¨res correspondant Ã  m dans l'argument -mess m */
	private String messageString = "10011111";
	/** indique si le Simulateur utilise un codage de l'information Ã  Ã©mettre */
	private Boolean form = false;


	private Float snrPb = null;

	/** le composant Source de la chaine de transmission */
	private Source<Boolean> source = null;
	/** Le composant Ã©metteur de la chaine de transmission */
	private Emetteur<Boolean,Float> emetteur = null;

	private Decodeur decodeur = null;
	/** Le composant rÃ©cepteur de la chaine de transmission */
	private Recepteur<Float,Boolean> recepteur = null;
	/** le composant Transmetteur parfait logique de la chaine de transmission */
	private Transmetteur<Boolean,Boolean> transmetteurLogique = null;
	private Transmetteur<Float,Float> transmetteurParfait = null;
	private TransmetteurAvecBruit<Float,Float> transmetteurAvecBruit = null;
	/** le composant Destination de la chaine de transmission */
	private Destination<Boolean> destination = null;
	/** la composante Sonde pour la source de la chaine de transmission */
	private Sonde sondeSource = null;
	/** la composante Sonde pour l'Ã©metteur de la chaine de transmission */
	private SondeAnalogique sondeEmetteur = null;

	private SondeAnalogique sondeTransmetteur = null;
	/** la composante Sonde pour le rÃ©cepteur de la chaine de transmission */
	private SondeAnalogique sondeRecepteur = null;
	/** la composante Sonde pour la destination de la chaine de transmission */
	private Sonde sondeDestination = null;

	/**
	 * Le constructeur de Simulateur construit une chaÃ®ne de transmission composÃ©e
	 * d'une Source <Boolean>, d'une Destination <Boolean> et de Transmetteur(s)
	 * [voir la mÃ©thode analyseArguments]... <br>
	 * Les diffÃ©rents composants de la chaÃ®ne de transmission (Source,
	 * Transmetteur(s), Destination, Sonde(s) de visualisation) sont crÃ©Ã©s et
	 * connectÃ©s.
	 *
	 * @param args le tableau des diffÃ©rents arguments.
	 *
	 * @throws ArgumentsException si un des arguments est incorrect
	 *
	 */
	public Simulateur(String[] args) throws ArgumentsException {

		// analyser et rÃ©cupÃ©rer les arguments
		analyseArguments(args);

		// Instancier la source
		if (messageAleatoire)
		{
			if (aleatoireAvecGerme)
				source = new SourceAleatoire<Boolean>(seed, nbBitsMess);
			else source = new SourceAleatoire<Boolean>(nbBitsMess);
		}
		else source = new SourceFixe<Boolean>(messageString);

		// Instancier la destination
		destination = new DestinationFinale<Boolean>();

		// Instancier le transmetteur
		transmetteurParfait = new TransmetteurParfait<Float, Float>();
		transmetteurLogique = new TransmetteurParfait<Boolean, Boolean>();

		// Instancier les sondes
		if (affichage){

			this.sondeSource = new SondeLogique("Source", 200);
			this.sondeDestination = new SondeLogique("Destination", 200);
			this.source.connecter(this.sondeSource);
			// Connecter les sondes Ã©metteur et rÃ©cepteur si option -s
			if (this.form) {
				sondeEmetteur = new SondeAnalogique("Sortie emetteur");
				sondeTransmetteur = new SondeAnalogique("Sortie Transmetteur");
				sondeRecepteur = new SondeAnalogique("Sortie Transmetteur filtré");
				if (snrPb == null){

					this.transmetteurParfait.connecter(sondeTransmetteur);}
				else{
					sondeTransmetteur = new SondeAnalogique("Sortie Transmetteur avec bruit");
					this.transmetteurAvecBruit.connecter(sondeTransmetteur);
					decodeur.connecter(sondeRecepteur);
				}
				this.emetteur.connecter(sondeEmetteur);
				this.recepteur.connecter(this.sondeDestination);
			}
			else {
				this.transmetteurLogique.connecter(this.sondeDestination);
			}
		}
		// Connecter Ã©metteur et rÃ©cepteur Ã  la chaÃ®ne de transmission si option -form
		if (this.form) {
			this.source.connecter(this.emetteur);
			if (snrPb == null){
				this.emetteur.connecter(this.transmetteurParfait);
				this.transmetteurParfait.connecter(this.recepteur);}
			else{
				this.emetteur.connecter(this.transmetteurAvecBruit);
				this.transmetteurAvecBruit.connecter(this.decodeur);
				this.decodeur.connecter(this.recepteur);
			}

			this.recepteur.connecter(this.destination);
		}
		// Sinon une chaÃ®ne de transmission logique
		else {
			this.source.connecter(this.transmetteurLogique);
			this.transmetteurLogique.connecter(this.destination);
		}

	}

	/**
	 * La mÃ©thode analyseArguments extrait d'un tableau de chaÃ®nes de caractÃ¨res
	 * les diffÃ©rentes options de la simulation. Elle met Ã  jour les attributs du
	 * Simulateur.
	 *
	 * @param args le tableau des diffÃ©rents arguments. <br>
	 *             <br>
	 *             Les arguments autorisÃ©s sont : <br>
	 *             <dl>
	 *             <dt>-mess m</dt>
	 *             <dd>m (String) constituÃ© de 7 ou plus digits Ã  0 | 1, le
	 *             message Ã  transmettre</dd>
	 *             <dt>-mess m</dt>
	 *             <dd>m (int) constituÃ© de 1 Ã  6 digits, le nombre de bits du
	 *             message "alÃ©atoire" Ã Â  transmettre</dd>
	 *             <dt>-s</dt>
	 *             <dd>utilisation des sondes d'affichage</dd>
	 *             <dt>-seed v</dt>
	 *             <dd>v (int) d'initialisation pour les gÃ©nÃ©rateurs
	 *             alÃ©atoires</dd>
	 *             </dl>
	 *
	 * @throws ArgumentsException si un des arguments est incorrect.
	 *
	 */

	public void analyseArguments(String[] args) throws ArgumentsException {
		Float ampMax = 1f;
		Float ampMin = 0f;

		int pasEch = 30;
		int messLength = 100;


		messageAleatoire = true;
		nbBitsMess = messLength;
		emetteur = new EmetteurRZ<Boolean, Float>(ampMax,ampMin,pasEch);
		recepteur = new Recepteur<Float, Boolean>(ampMax,ampMin,pasEch);
		transmetteurAvecBruit = new TransmetteurAvecBruit<Float,Float>(snrPb,pasEch);
		decodeur =new DecodeurRZ(ampMax,ampMin,pasEch);

		for (int i = 0; i < args.length; i++) {

			if (args[i].matches("-s")) {
				affichage = true;
			}
			else if (args[i].matches("-seed")) {
				aleatoireAvecGerme = true;
				i++;
				// traiter la valeur associee
				try {
					seed = Integer.valueOf(args[i]);
				} catch (Exception e) {
					throw new ArgumentsException("Valeur du parametre -seed  invalide :" + args[i]);
				}
			}

			else if (args[i].matches("-mess")) {
				if (i < args.length-1){
					i++;
					messageString = args[i];
					if (args[i].matches("[0,1]{7,}")) {
						messageAleatoire = false;
						nbBitsMess = args[i].length();
					}
					else if (args[i].matches("[0-9]{1,6}")) {
						messageAleatoire = true;
						nbBitsMess = Integer.parseInt(args[i]);
						if (nbBitsMess < 1)
							throw new ArgumentsException("Valeur du parametre -mess invalide : " + nbBitsMess);
					}
					else if (args[i].matches("^-\\w*$")) {
						i--;
					}
					else
						throw new ArgumentsException("Valeur du parametre -mess invalide : " + args[i]);
				}
			}

			else if (args[i].matches("-form")){
				this.form = true;
				if (i < args.length-1){
					i++;
					if (args[i].matches("^NRZT$|^N?RZ$")) {
						switch (args[i]){
							case "NRZ" :
								emetteur = new EmetteurNRZ<Boolean, Float>(ampMax,ampMin,pasEch);
								decodeur = new DecodeurNRZ(ampMax,ampMin,pasEch);
								break;

							case "NRZT" :
								emetteur = new EmetteurNRZT<Boolean, Float>(ampMax,ampMin,pasEch);
								decodeur = new DecodeurNRZ(ampMax,ampMin,pasEch);
								break;
						}
					}
					else if (args[i].matches("^-\\w*$")){
						i--;

					}
					else
						throw new ArgumentsException("Valeur du parametre -form invalide : " + args[i]);
				}
			}

			else if (args[i].matches("-nbEch")){
				this.form =true;
				if (i < args.length-1){
					i++;
					if (args[i].matches("^\\d+$")){
						if (Integer.parseInt(args[i])>=3) {
							pasEch = Integer.parseInt(args[i]);
							emetteur.setPasEchantillonage(pasEch);
							recepteur.setPasEchantillonnage(pasEch);
							transmetteurAvecBruit.setNbEch(pasEch);
							decodeur.setPasEchantillonnage(pasEch);
						}
						else throw new ArgumentsException("pas echantillonnage incorrect il doit être supérieur ou égal à 3: "+args[i]);

					}
					else if (args[i].matches("^-\\w*$")) {
						i--;
					}
					else throw new ArgumentsException("pas echantillonnage incorrect : "+args[i]);
				}
			}

			else if (args[i].matches("-ampl")){
				this.form =true;
				if (i < args.length-2){
					i++;
					if (args[i].matches("^-?\\d+.?\\d*$")&&args[i+1].matches("^-?\\d+.?\\d*$")){
						ampMin = Float.parseFloat(args[i]);
						ampMax = Float.parseFloat(args[++i]);
						emetteur.setAmpMax(ampMax);
						emetteur.setAmpMin(ampMin);
						recepteur.setAmpMax(ampMax);
						recepteur.setAmpMin(ampMin);
						decodeur.setAmpMax(ampMax);
						decodeur.setAmpMin(ampMin);

					}
					else if (args[i].matches("^-\\w*$")) {
						i--;
					}
					else {
						throw new ArgumentsException("Valeur amplitude -ampl impossible : amplitude max : " + ampMax +" amplitude min : "+ampMin);
					}
				}
			}
			else if (args[i].matches("-snrpb")) {
				this.form=true;
				if (i < args.length-1){
					i++;
					if (args[i].matches("^-?\\d+.?\\d*$")){
						snrPb = Float.parseFloat(args[i]);
						transmetteurAvecBruit.setSnrPb(snrPb);
					}
					else if (args[i].matches("^-\\w*$")) {
						i--;
					}
					else {
						throw new ArgumentsException("Valeur de bruit -snrpb impossible : " + ampMax);
					}
				}
			}
		}
	}
	/**
	 * La mÃ©thode execute effectue un envoi de message par la source de la chaÃ®ne
	 * de transmission du Simulateur.
	 *
	 * @throws Exception si un problÃ¨me survient lors de l'exÃ©cution
	 *
	 */
	public void execute() throws Exception {

		source.emettre();
	}

	/**
	 * La mÃ©thode qui calcule le taux d'erreur binaire en comparant les bits du
	 * message Ã©mis avec ceux du message reÃ§u.
	 *
	 * @return La valeur du Taux dErreur Binaire.
	 */
	public float calculTauxErreurBinaire() {

		// on rÃ©cupÃ¨re d'abord l'information des sources
		// source
		float nbErreur=0;
		Iterator signalSource = source.getInformationEmise().iterator();
		Iterator signalDestination = destination.getInformationRecue().iterator();

		while (signalDestination.hasNext()){

			if (signalSource.next() != signalDestination.next()) nbErreur++;
		}

		return nbErreur/nbBitsMess;
	}

	/**
	 * La fonction main instancie un Simulateur Ã  l'aide des arguments paramÃ¨tres
	 * et affiche le rÃ©sultat de l'exÃ©cution d'une transmission.
	 *
	 * @param args les diffÃ©rents arguments qui serviront Ã  l'instanciation du
	 *             Simulateur.
	 */
	public static void main(String[] args) {

		Simulateur simulateur = null;

		try {
			simulateur = new Simulateur(args);
		} catch (Exception e) {
			System.out.println(e);
			System.exit(-1);
		}

		try {
			simulateur.execute();
			float tauxErreurBinaire = simulateur.calculTauxErreurBinaire();
			String s = "java  Simulateur  ";
			for (int i = 0; i < args.length; i++) {
				s += args[i] + "  ";
			}
			System.out.println(s + "  =>   TEB : " + tauxErreurBinaire);
		} catch (Exception e) {
			System.out.println(e);
			e.printStackTrace();
			System.exit(-2);
		}
	}
}
