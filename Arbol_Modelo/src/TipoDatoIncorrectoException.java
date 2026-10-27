/** Una propiedad tiene un tipo distinto del exigido. */
public final class TipoDatoIncorrectoException extends Exception {
    public TipoDatoIncorrectoException(String mensaje) {
        super(mensaje);
    }
}