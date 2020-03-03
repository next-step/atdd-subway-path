package atdd.global.util;

import lombok.experimental.UtilityClass;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@UtilityClass
public class LocalTimeUtils {

    public static LocalTime valueOf(String localTime) {
        try {
            return LocalTime.parse(localTime);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException();
        }
    }

}
