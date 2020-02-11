package atdd.station;

import java.net.URI;

public class StationUri {

    public static final String ROOT = "/stations";

    public static URI getCreatedUrl(Long id) {
        return URI.create(ROOT + "/" + id);
    }
}
