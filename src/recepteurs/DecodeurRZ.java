package recepteurs;

public class DecodeurRZ extends Decodeur {
    /**
     * Constructeur classique
     *
     * @param ampMax             - amplitude max du signal attendu
     * @param ampMin             - amplitude min du signal attendu
     * @param pasEchantillonnage - pas d'échantillonage pour le signal attendu
     */
    public DecodeurRZ(Float ampMax, Float ampMin, int pasEchantillonnage) {
        super(ampMax, ampMin, pasEchantillonnage);
    }

    @Override
    protected void decode() {
        Float mean;
        for(int i=0;i<informationRecue.nbElements();i+=pasEchantillonnage) {
            mean = 0F;
            for (int j=((int)pasEchantillonnage/3);j<(2*pasEchantillonnage)/3F;j++) {
                mean += informationRecue.iemeElement(i+j);
            }
            mean = (mean*3)/pasEchantillonnage;
            for (int j=0;j<pasEchantillonnage;j++) {
                informationEmise.add(mean);
            }
        }
    }
}