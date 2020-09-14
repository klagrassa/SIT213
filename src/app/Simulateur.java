package app;

import emetteurs.Emetteur;
import emetteurs.EmetteurNRZ;
import emetteurs.EmetteurNRZT;
import emetteurs.EmetteurRZ;
import recepteurs.Recepteur;
import sources.*;
import destinations.*;
import transmetteurs.*;

import visualisations.*;

import java.util.*;

/**
 * La classe Simulateur permet de construire et simuler une chaîne de
 * transmission composée d'une Source, d'un nombre variable de Transmetteur(s)
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
	 * indique si le Simulateur utilise un message généré de manière aléatoire
	 */
	private boolean messageAleatoire = true;

	/**
	 * indique si le Simulateur utilise un germe pour initialiser les générateurs
	 * aléatoires
	 */
	private boolean aleatoireAvecGerme = false;
	/** la valeur de la semence utilisée pour les générateurs aléatoires */
	private Integer seed = null;
	/**
	 * la longueur du message aléatoire à transmettre si un message n'est pas impose
	 */
	private int nbBitsMess = 100;
	/** la chaîne de caractères correspondant à m dans l'argument -mess m */
	private String messageString = "10011111";

	private Boolean formString = false;

	/** le composant Source de la chaine de transmission */
	private Source<Boolean> source = null;

	/** le composant emetteur de la chaine de transmission */
	private Emetteur<Boolean, Float> emetteur = null;

	private Recepteur<Float, Boolean> recepteur = null;
	/** le composant Transmetteur parfait logique de la chaine de transmission */
	private Transmetteur transmetteurLogique = null;
	/** le composant Destination de la chaine de transmission */
	private Destination<Boolean> destination = null;
	/** la composante Sonde pour la source de la chaine de transmission */
	private Sonde sondeSource = null;
	/** la composante Sonde pour le transmetteur de la chaine de transmission */
	private Sonde sondeDestination = null;

	/**
	 * Le constructeur de Simulateur construit une chaîne de transmission composée
	 * d'une Source <Boolean>, d'une Destination <Boolean> et de Transmetteur(s)
	 * [voir la méthode analyseArguments]... <br>
	 * Les différents composants de la chaîne de transmission (Source,
	 * Transmetteur(s), Destination, Sonde(s) de visualisation) sont créés et
	 * connectés.
	 * 
	 * @param args le tableau des différents arguments.
	 *
	 * @throws ArgumentsException si un des arguments est incorrect
	 *
	 */
	public Simulateur(String[] args) throws ArgumentsException {

		// analyser et récupérer les arguments
		analyseArguments(args);

		// Instancier la source
		if (messageAleatoire) {
			if (aleatoireAvecGerme)
				source = new SourceAleatoire<Boolean>(seed, nbBitsMess);
			else
				source = new SourceAleatoire<Boolean>(nbBitsMess);
		} else
			source = new SourceFixe<Boolean>(messageString);

		// Instancier la destination
		destination = new DestinationFinale<Boolean>();

		// Instancier le transmetteur
		transmetteurLogique = new TransmetteurParfait<Boolean, Boolean>();

		// Instancier les sondes
		if (affichage) {

			sondeSource = new SondeLogique("Sonde source", 200);
			sondeDestination = new SondeLogique("Sonde destination", 200);

			// Connecter la source é une sonde
			source.connecter(sondeSource);

			// Connecter le transmetteur é une sond
			SondeAnalogique sondeEmetteur = new SondeAnalogique("Sonde emetteur");
			SondeAnalogique sondeRecepteur = new SondeAnalogique("Sonde recepteur");
			transmetteurLogique.connecter(sondeRecepteur);
			emetteur.connecter(sondeEmetteur);
			recepteur.connecter(sondeDestination);

		}
		source.connecter(emetteur);
		emetteur.connecter(transmetteurLogique);
		transmetteurLogique.connecter(recepteur);
		recepteur.connecter(destination);

//		// Connecter la source au transmetteur
//		source.connecter(transmetteurLogique);

	}

	/**
	 * La méthode analyseArguments extrait d'un tableau de chaînes de caractères les
	 * différentes options de la simulation. Elle met à jour les attributs du
	 * Simulateur.
	 *
	 * @param args le tableau des différents arguments. <br>
	 *             <br>
	 *             Les arguments autorisés sont : <br>
	 *             <dl>
	 *             <dt>-mess m</dt>
	 *             <dd>m (String) constitué de 7 ou plus digits à 0 | 1, le message
	 *             à transmettre</dd>
	 *             <dt>-mess m</dt>
	 *             <dd>m (int) constitué de 1 à 6 digits, le nombre de bits du
	 *             message "aléatoire" à  transmettre</dd>
	 *             <dt>-s</dt>
	 *             <dd>utilisation des sondes d'affichage</dd>
	 *             <dt>-seed v</dt>
	 *             <dd>v (int) d'initialisation pour les générateurs aléatoires</dd>
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
		String form = "RZ";

		messageAleatoire = true;
		nbBitsMess = messLength;
		emetteur = new EmetteurRZ<Boolean, Float>(ampMax, ampMin, pasEch);
		recepteur = new Recepteur<Float, Boolean>(ampMax, ampMin, pasEch);

		for (int i = 0; i < args.length; i++) {

			if (args[i].matches("-s")) {
				affichage = true;
			} else if (args[i].matches("-seed")) {
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
				if (i < args.length - 1) {
					i++;
					messageString = args[i];
					if (args[i].matches("[0,1]{7,}")) {
						messageAleatoire = false;
						nbBitsMess = args[i].length();
					} else if (args[i].matches("[0-9]{1,6}")) {
						messageAleatoire = true;
						nbBitsMess = Integer.parseInt(args[i]);
						if (nbBitsMess < 1)
							throw new ArgumentsException("Valeur du parametre -mess invalide : " + nbBitsMess);
					} else if (args[i].matches("^-\\w*$")) {
						i--;
					} else
						throw new ArgumentsException("Valeur du parametre -mess invalide : " + args[i]);
				}
			}

			else if (args[i].matches("-form")) {
				formString = true;
				if (i < args.length - 1) {
					i++;
					if (args[i].matches("^NRZT$|^N?RZ$")) {
						switch (args[i]) {
						case "NRZ":
							emetteur = new EmetteurNRZ<Boolean, Float>(ampMax, ampMin, pasEch);
							break;

						case "NRZT":
							emetteur = new EmetteurNRZT<Boolean, Float>(ampMax, ampMin, pasEch);
							break;
						}
					} else if (args[i].matches("^-\\w*$")) {
						i--;

					} else
						throw new ArgumentsException("Valeur du parametre -form invalide : " + args[i]);
				}
			}

			else if (args[i].matches("-nbEch")) {
				if (i < args.length - 1) {
					i++;
					if (args[i].matches("^\\d+$")) {
						emetteur.setPasEchantillonage(Integer.parseInt(args[i]));
						recepteur.setPasEchantillonnage(Integer.parseInt(args[i]));
					} else if (args[i].matches("^-\\w*$")) {
						i--;
					} else
						throw new ArgumentsException("pas echantillonnage incorrect : " + args[i]);
				}
			}

			else if (args[i].matches("-ampl")) {
				if (i < args.length - 2) {
					i++;
					if (args[i].matches("^-?\\d+.?\\d*$") && args[i + 1].matches("^-?\\d+.?\\d*$")) {
						ampMin = Float.parseFloat(args[i]);
						ampMax = Float.parseFloat(args[++i]);
						emetteur.setAmpMax(ampMax);
						emetteur.setAmpMin(ampMin);

					} else if (args[i].matches("^-\\w*$")) {
						i--;
					} else {
						throw new ArgumentsException("Valeur amplitude -ampl impossible : amplitude max : " + ampMax
								+ " amplitude min : " + ampMin);
					}
				}
			}
		}
	}

	/**
	 * La méthode execute effectue un envoi de message par la source de la chaîne de
	 * transmission du Simulateur.
	 *
	 * @throws Exception si un problème survient lors de l'exécution
	 *
	 */
	public void execute() throws Exception {

		source.emettre();
	}

	/**
	 * La méthode qui calcule le taux d'erreur binaire en comparant les bits du
	 * message émis avec ceux du message reçu.
	 *
	 * @return La valeur du Taux dErreur Binaire.
	 */
	public float calculTauxErreurBinaire() {

		// on récupère d'abord l'information des sources
		// source
		float nbErreur = 0;
		Iterator signalSource = source.getInformationEmise().iterator();
		Iterator signalDestination = destination.getInformationRecue().iterator();

		while (signalDestination.hasNext()) {

			if (signalSource.next() != signalDestination.next())
				nbErreur++;
		}

		return nbErreur / nbBitsMess;
	}

	/**
	 * La fonction main instancie un Simulateur à l'aide des arguments paramètres et
	 * affiche le résultat de l'exécution d'une transmission.
	 * 
	 * @param args les différents arguments qui serviront à l'instanciation du
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
