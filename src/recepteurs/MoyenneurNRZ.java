package recepteurs;

public class MoyenneurNRZ extends Moyenneur {
    /**
     * Constructeur classique
     *
     * @param pasEchantillonnage - pas d'échantillonage pour le signal attendu
     */
    public MoyenneurNRZ(int pasEchantillonnage) {
        super(pasEchantillonnage);
    }

    /**
     * Moyenne un signal bruité avec une forme d'onde NRZ
     */
    @Override
    protected void decode() {
        Float mean;
        for(int i=0;i<informationRecue.nbElements();i+=pasEchantillonnage) {
            mean = 0f;
            for (int j=0;j<pasEchantillonnage;j++) {
                try{
                mean += informationRecue.iemeElement(i+j);
                }
                catch (IndexOutOfBoundsException ignored){
                }
            }
            mean = mean/pasEchantillonnage;
            for (int j=0;j<pasEchantillonnage;j++) {
                informationEmise.add(mean);
            }
        }
    }
}
