package rebuilding.day2;

public class DealCalculator {
    /**
     * @param quantity
     * @return
     */
    public Double getDiscount(int quantity) {
        if (quantity > 3) {
            return 0.7D;
        }
        return 1D;
    }
}
