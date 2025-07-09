public class NotEnoughMoneyException extends Exception {

    private int amount;

    public NotEnoughMoneyException(int amount) {
        super("Запрашиваемая сумма " + amount + " не может быть выдана имеющимися купюрами");
        this.amount = amount;
    }

    public NotEnoughMoneyException(String msg) {
        super(msg);
    }

    public int getAmount() {
        return amount;
    }
}
