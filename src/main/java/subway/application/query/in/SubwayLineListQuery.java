package subway.application.query.in;

import subway.application.response.SubwayLineResponse;

import java.util.List;

public interface SubwayLineListQuery {

    List<SubwayLineResponse> findAll();
}
