package subway.application.query.out;

import subway.application.response.StationResponse;

import java.util.List;

public interface StationListQueryPort {
    List<StationResponse> findAll();
}
