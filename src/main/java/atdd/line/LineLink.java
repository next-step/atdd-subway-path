package atdd.line;

import java.net.URI;

public abstract class LineLink {

    public static final String ROOT = "/lines";

    public static URI getCreatedUrl(Long id) {
        return URI.create(ROOT + "/" + id);
    }
}
