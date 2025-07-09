public enum Denomination {
    HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500),
    THOUSAND(1000),
    TWO_THOUSAND(2000),
    FIVE_THOUSAND(5000);

    private int val;

    private Denomination(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }
}
