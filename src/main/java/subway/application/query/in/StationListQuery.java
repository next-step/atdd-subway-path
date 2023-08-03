package subway.application.query.in;

import subway.application.response.StationResponse;

import java.util.List;

public interface StationListQuery {
    List<StationResponse> findAll();
}
