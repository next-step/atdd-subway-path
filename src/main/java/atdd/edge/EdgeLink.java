package atdd.edge;

import java.net.URI;

public class EdgeLink {

    public static final String ROOT = "/edges";

    public static URI getCreatedUrl(Long id) {
        return URI.create(ROOT + "/" + id);
    }
}
