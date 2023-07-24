package subway.application.query.in;

import subway.application.query.response.SubwayLineResponse;

import java.util.List;

public interface SubwayLineListQuery {

    List<SubwayLineResponse> findAll();
}
