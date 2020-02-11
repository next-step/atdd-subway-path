package atdd.util;

import org.springframework.http.HttpHeaders;

import java.net.URI;
import java.util.Objects;

public class TestUtils {

    private TestUtils() {}

    public static String getLocationPath(HttpHeaders responseHeaders) {
        URI location = responseHeaders.getLocation();
        return Objects.nonNull(location) ? location.getPath() : "noLocation";
    }

}
