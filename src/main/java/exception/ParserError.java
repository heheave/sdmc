package exception;

/**
 * Created by xiaoke on 17-5-6.
 */
public class ParserError extends Exception{

    private final String type;

    private final String errorToken;

    public ParserError(String type, String errorToken) {
        this.type = type;
        this.errorToken = errorToken;
    }

    @Override
    public String toString() {
        return this.type + " parser error near: " + this.errorToken;
    }

}
