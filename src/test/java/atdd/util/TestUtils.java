package atdd.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

public class TestUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private TestUtils() {}

    public static String getLocationPath(HttpHeaders responseHeaders) {
        URI location = responseHeaders.getLocation();
        return Objects.nonNull(location) ? location.getPath() : "noLocation";
    }

    public static String localTimeToString(LocalTime localTime) {
        return localTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String jsonOf(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }

}
