package atdd.global.util;

import lombok.experimental.UtilityClass;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@UtilityClass
public class LocalTimeUtils {

    public static Optional<LocalTime> localTimeOf(String localTime) {
        try {
            return Optional.of(LocalTime.parse(localTime));
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

}
