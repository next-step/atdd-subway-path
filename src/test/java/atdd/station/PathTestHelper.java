package atdd.station;

import atdd.HttpTestHelper;
import atdd.station.model.dto.PathResponseView;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.EntityExchangeResult;

public class PathTestHelper {
    final String PATH_URL = "/paths";
    private HttpTestHelper httpTestHelper;

    public PathTestHelper(HttpTestHelper httpTestHelper) {
        this.httpTestHelper = httpTestHelper;
    }

    public EntityExchangeResult findPath(long startStationId, long endStationId, String eTag) {
        return httpTestHelper.getRequestWithETag(PATH_URL + "/short-path" + "?startId=" + startStationId + "&endId=" + endStationId, eTag, new ParameterizedTypeReference<PathResponseView>() {
        });
    }

    public EntityExchangeResult findPath(long startStationId, long endStationId) {
        return httpTestHelper.getRequest(PATH_URL + "/short-path" + "?startId=" + startStationId + "&endId=" + endStationId, new ParameterizedTypeReference<PathResponseView>() {
        });
    }

    public EntityExchangeResult findShortTimePath(long startStationId, long endStationId) {
        return httpTestHelper.getRequest(PATH_URL + "/short-time" + "?startId=" + startStationId + "&endId=" + endStationId, new ParameterizedTypeReference<PathResponseView>() {
        });
    }
}
