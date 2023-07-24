package subway.application.query.in;

import subway.application.query.response.StationResponse;

import java.util.List;

public interface StationListQuery {
    List<StationResponse> findAll();
}
