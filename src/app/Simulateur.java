package app;

import emetteur.Emetteur;
import emetteur.EmetteurNRZ;
import emetteur.EmetteurNRZT;
import emetteur.EmetteurRZ;
import sources.*;
import destinations.*;
import transmetteurs.*;

import information.*;

import visualisations.*;

import java.util.regex.*;
import java.util.*;
import java.lang.Math;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

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
	 * la longueur du message aléatoire à transmettre si un message n'est pas
	 * impose
	 */
	private int nbBitsMess = 100;
	/** la chaîne de caractères correspondant à m dans l'argument -mess m */
	private String messageString = "10011111";

	private String formString = "RZ";

	/** le composant Source de la chaine de transmission */
	private Source<Boolean> source = null;

	private Emetteur<Boolean,Float> emetteur = new EmetteurRZ<Boolean,Float>(1f,0f,30);
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
		transmetteurLogique = new TransmetteurParfait<Boolean, Boolean>();

//		Emetteur emetteur = new Emetteur(5f,-5f,30);

		// Instancier les sondes
		if (affichage){

		sondeSource = new SondeLogique("Sonde source", 200);
		sondeDestination = new SondeLogique("Sonde destination", 200);
		SondeAnalogique sondeEmetteur = new SondeAnalogique("Sonde emetteur");
		// Connecter la source é une sonde
		source.connecter(sondeSource);
		// Connecter le transmetteur é une sond
		transmetteurLogique.connecter(emetteur);
		transmetteurLogique.connecter(sondeDestination);
		emetteur.connecter(sondeEmetteur);

		}
		// Connecter la source au transmetteur
		source.connecter(transmetteurLogique);

		// Connecter le transmetteur é la destination
		transmetteurLogique.connecter(destination);
	}

	/**
	 * La méthode analyseArguments extrait d'un tableau de chaînes de caractères
	 * les différentes options de la simulation. Elle met à jour les attributs du
	 * Simulateur.
	 *
	 * @param args le tableau des différents arguments. <br>
	 *             <br>
	 *             Les arguments autorisés sont : <br>
	 *             <dl>
	 *             <dt>-mess m</dt>
	 *             <dd>m (String) constitué de 7 ou plus digits à 0 | 1, le
	 *             message à transmettre</dd>
	 *             <dt>-mess m</dt>
	 *             <dd>m (int) constitué de 1 à 6 digits, le nombre de bits du
	 *             message "aléatoire" à  transmettre</dd>
	 *             <dt>-s</dt>
	 *             <dd>utilisation des sondes d'affichage</dd>
	 *             <dt>-seed v</dt>
	 *             <dd>v (int) d'initialisation pour les générateurs
	 *             aléatoires</dd>
	 *             </dl>
	 *
	 * @throws ArgumentsException si un des arguments est incorrect.
	 *
	 */
	public void analyseArguments(String[] args) throws ArgumentsException {
		Float ampMax = 1f;

		Float ampMin = 0f;

		int pasEch = 30;

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
				i++;
				// traiter la valeur associee
				messageString = args[i];
				if (args[i].matches("[0,1]{7,}")) {
					messageAleatoire = false;
					nbBitsMess = args[i].length();
				} else if (args[i].matches("[0-9]{1,6}")) {
					messageAleatoire = true;
					nbBitsMess = Integer.parseInt(args[i]);
					if (nbBitsMess < 1)
						throw new ArgumentsException("Valeur du parametre -mess invalide : " + nbBitsMess);
				} else
					throw new ArgumentsException("Valeur du parametre -mess invalide : " + args[i]);
			}

			else if (args[i].matches("-form")){
				i++;
				if (args[i].matches("^N?RZT?$")) {
					formString = args[i];
					switch (args[i]){

						case "NRZ" :
							emetteur = new EmetteurNRZ<Boolean, Float>(ampMax,ampMin,pasEch);

						case "NRZT" :
							emetteur = new EmetteurNRZT<Boolean, Float>(ampMax,ampMin,pasEch);
					}
				}  else
					throw new ArgumentsException("Valeur du parametre -form invalide : " + args[i]);
			}
			else if (args[i].matches("-nbEch")){
				i++;
				if (args[i].matches("^\\d+$")){
					emetteur.setPasEchantillonage(Integer.parseInt(args[i]));
				}
				else throw new ArgumentsException("pas echantillonnage incorect : "+args[i]);
			}
			else if (args[i].matches("-ampl")){
				i++;

				if (args[i].matches("^-?\\d+.?\\d*$")&&args[i+1].matches("^-?\\d+.?\\d*$")){

					ampMin = Float.parseFloat(args[i]);
					ampMax = Float.parseFloat(args[++i]);

					if (ampMax<ampMin){
						throw new ArgumentsException("Valeur amplitude -ampl impossible : amplitude max : " + ampMax +" amplitude min : "+ampMin);
					}
					else {
						emetteur.setAmpMax(ampMax);
						emetteur.setAmpMin(ampMin);
					}
				}
				else
					throw new ArgumentsException("Valeur du parametre -ampl invalide : " + args[i]);

			}
			else
				throw new ArgumentsException("Option invalide :" + args[i]);
		}

	}

	/**
	 * La méthode execute effectue un envoi de message par la source de la chaîne
	 * de transmission du Simulateur.
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
		float nbErreur=0;
		Iterator signalSource = source.getInformationEmise().iterator();
		Iterator signalDestination = destination.getInformationRecue().iterator();

		while (signalDestination.hasNext()){

			if (signalSource.next() != signalDestination.next()) nbErreur++;
		}

		return nbErreur/nbBitsMess;
	}

	/**
	 * La fonction main instancie un Simulateur à l'aide des arguments paramètres
	 * et affiche le résultat de l'exécution d'une transmission.
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
