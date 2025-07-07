public class NotEnoughMoneyException extends Exception {

    public NotEnoughMoneyException() {
        super("Запрашиваемая сумма не может быть выдана имеющимися купюрами");
    }

    public NotEnoughMoneyException(String msg) {
        super(msg);
    }
}
