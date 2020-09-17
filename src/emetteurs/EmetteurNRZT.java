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

		Float modulo = this.pasEchantillonage % 3.0F;

		float nbEchantillonsPente = 0;
		float nbEchantillonsPalier = 0;

		if (modulo==1) {
			nbEchantillonsPalier = (this.pasEchantillonage + 2) / 3.0F;
			nbEchantillonsPente = (this.pasEchantillonage - 1) / 3.0F;
		}
		else if (modulo == 2) {
			nbEchantillonsPalier = (this.pasEchantillonage - 2) / 3.0F;
			nbEchantillonsPente = (this.pasEchantillonage + 1) / 3.0F;
		}
		else {
			nbEchantillonsPalier = this.pasEchantillonage / 3.0F;
			nbEchantillonsPente = this.pasEchantillonage / 3.0F;
		}

		Boolean front = null;
		this.penteTransition = ((this.ampMax - ampMin) / 2) / nbEchantillonsPente;

		for (Boolean bitLogique : this.informationRecue) {
			if (front == null) {
				front = bitLogique;
				if (bitLogique == Boolean.TRUE) {
					for (int i = 0; i < nbEchantillonsPente; ++i) {
						this.informationGenere.add(0f - this.transition);
						this.transition -= (this.ampMax/ nbEchantillonsPente);
					}
					this.ajouterSymbole(this.ampMax, nbEchantillonsPalier);
				}
				else {
					for (int i = 0; i < nbEchantillonsPente; ++i) {
						this.informationGenere.add(0f - this.transition);
						this.transition += (Math.abs(this.ampMin)/ nbEchantillonsPente);
					}
					this.ajouterSymbole(this.ampMin,nbEchantillonsPalier );

				}
			} else if (front != bitLogique) {
				front = bitLogique;
				if (bitLogique == Boolean.TRUE) {
					this.transition = 0f;
					this.ajouterTransition(this.ampMin, -this.penteTransition, 2*nbEchantillonsPente);
					this.ajouterSymbole(ampMax, nbEchantillonsPalier);

				} else {
					this.transition = 0f;
					this.ajouterTransition(this.ampMax, this.penteTransition, 2*nbEchantillonsPente);
					this.ajouterSymbole(ampMin, nbEchantillonsPalier);

				}
			} else if (bitLogique == Boolean.TRUE) {
				this.ajouterSymbole(this.ampMax, (float) this.pasEchantillonage);
			} else {
				this.ajouterSymbole(this.ampMin, (float) this.pasEchantillonage);
			}
		}
		this.ajouterSymbole(this.informationGenere.iemeElement(informationGenere.nbElements() - 1), nbEchantillonsPente);
		this.emettre();
	}

	/**
	 * Ajoute le "plateau" de valeur amp au signal
	 *
	 * @param amp - amplitude du "plateau"
	 */
	protected void ajouterSymbole(Float amp, Float nbEchantillonsPalier) {
		for (int i = 0; i < nbEchantillonsPalier; i++) {
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
	private void ajouterTransition(Float amp, Float penteTransition, Float nbEchantillonsPente) {
		for (int i = 0; i < nbEchantillonsPente; ++i) {
			this.informationGenere.add(amp - this.transition);
			this.transition += penteTransition;
		}
	}

	@Override
	protected void ajouterSymbole(Float amp) {
		// TODO Auto-generated method stub

	}
}
