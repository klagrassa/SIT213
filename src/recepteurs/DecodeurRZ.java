package recepteurs;

public class DecodeurRZ extends Decodeur {
    /**
     * Constructeur classique
     *
     * @param pasEchantillonnage - pas d'échantillonage pour le signal attendu
     */
    public DecodeurRZ( int pasEchantillonnage) {
        super( pasEchantillonnage);
    }

    @Override
    protected void decode() {
        Float mean;
        for(int i=0;i<informationRecue.nbElements();i+=pasEchantillonnage) {
            mean = 0F;
            for (int j=((int)pasEchantillonnage/3);j<(2*pasEchantillonnage)/3F;j++) {
                try{
                    mean += informationRecue.iemeElement(i+j);}
                catch (IndexOutOfBoundsException ignored){
                }
            }
            mean = (mean*3)/pasEchantillonnage;

            for (int j=0;j<pasEchantillonnage;j++) {
                informationEmise.add(mean);
            }
        }
    }
}
