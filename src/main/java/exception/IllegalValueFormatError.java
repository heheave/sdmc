package exception;

import java.util.IllegalFormatException;

/**
 * Created by xiaoke on 17-5-6.
 */
public class IllegalValueFormatError extends RuntimeException{

    public final String  value;

    public final Class<? extends Number> clz;

    public IllegalValueFormatError(String value, Class<? extends Number> clz) {
        this.value = value;
        this.clz = clz;
    }

    @Override
    public String toString() {
        return value + " is not instance of " + clz.getName();
    }
}
