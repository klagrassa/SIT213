package emetteurs;

import information.Information;
import information.InformationNonConforme;

/**
 * Implémentation de l'émetteur NRZT. Les contraintes et la forme de ce signal
 * sont disponibles dans les consignes de l'étape 2.
 * 
 * @author Groupe 3
 *
 * @param <R> : Type d'info en entrée (recepteur)
 * @param <E> : Type d'info en sortie (émetteur)
 */
public class EmetteurNRZT<R, E> extends Emetteur<R, E> {

	private float transition;
	private float penteTransition;

	/**
	 * @param ampMax            Amplitude correspondant à un "1" logique
	 * @param ampMin            Amplitude correspondant à un "0" logique
	 * @param pasEchantillonage nombre d'échantillons pour représenter un symbole
	 */
	public EmetteurNRZT(Float ampMax, Float ampMin, int pasEchantillonage) {
		super(ampMax, ampMin, pasEchantillonage);
	}

	/**
	 * Reçoit l'information de l'interface désignée en reception, puis convertit le
	 * signal logique en signal de type NRZT
	 * 
	 * @param information - information à recevoir
	 */
	public void recevoir(Information<Boolean> information) throws InformationNonConforme {
		this.informationRecue = information;
		this.informationGenere = new Information<Float>();

		Boolean front = null;
		this.penteTransition = ((this.ampMax - ampMin) / 2) / ((float) this.pasEchantillonage / 3.0F);

		for (Boolean bitLogique : this.informationRecue) {
			if (front == null) {
				front = bitLogique;
				if (bitLogique == Boolean.TRUE) {
					int balise = 0;
					for (int i = 0; i < (2 * this.pasEchantillonage / 3f)
							* (ampMax / (ampMax + Math.abs(ampMin))); ++i) {
						this.informationGenere.add(0f - this.transition);
						this.transition += -penteTransition;
						balise++;
					}
					while (balise < this.pasEchantillonage / 3) {
						this.informationGenere.add(this.ampMax);
						balise++;
					}
					this.ajouterSymbole(this.ampMax);
				} else {
					// this.ajouterTransition(((this.ampMax + ampMin)/2), this.penteTransition);
					int balise = 0;
					for (int i = 0; i < (2 * this.pasEchantillonage / 3f)
							* (Math.abs(ampMin) / (ampMax + Math.abs(ampMin))); ++i) {
						this.informationGenere.add(0f - this.transition);
						this.transition += penteTransition;
						balise++;
					}
					while (balise < this.pasEchantillonage / 3) {
						this.informationGenere.add(this.ampMin);
						balise++;
					}

					this.ajouterSymbole(ampMin);

				}
			} else if (front != bitLogique) {
				front = bitLogique;
				if (bitLogique == Boolean.TRUE) {
					this.transition = 0f;
					this.ajouterTransition(this.ampMin, -this.penteTransition);
					this.ajouterTransition(this.ampMin, -this.penteTransition);
					this.ajouterSymbole(ampMax);

				} else {
					this.transition = 0f;
					this.ajouterTransition(this.ampMax, this.penteTransition);
					this.ajouterTransition(this.ampMax, this.penteTransition);
					this.ajouterSymbole(ampMin);

				}
			} else if (bitLogique == Boolean.TRUE) {
				for (int i = 0; i < 3; i++)
					this.ajouterSymbole(this.ampMax);
			} else {
				for (int i = 0; i < 3; i++)
					this.ajouterSymbole(this.ampMin);
			}
		}
		// Bastien idea
		int padding = informationRecue.nbElements() * this.pasEchantillonage;
		while (informationGenere.nbElements() < padding) {
			this.informationGenere.add(this.informationGenere.iemeElement(informationGenere.nbElements() - 1));
		}

		this.emettre();
	}

	/**
	 * Ajoute le "plateau" de valeur amp au signal
	 * 
	 * @param amp - amplitude du "plateau"
	 */
	@Override
	protected void ajouterSymbole(Float amp) {
		for (int i = 0; i < pasEchantillonage / 3; i++) {
			informationGenere.add(amp);
		}
	}

	/**
	 * Ajoute le motif de transition (haute ou basse)
	 * 
	 * @param amp             - amplitude désirée (vers quelle valeur la transition
	 *                        doit s'effectuer)
	 * @param penteTransition - pente de la transition
	 */
	private void ajouterTransition(Float amp, Float penteTransition) {
		for (int i = 0; i < this.pasEchantillonage / 3; ++i) {
			this.informationGenere.add(amp - this.transition);
			this.transition += penteTransition;
		}

	}
}
