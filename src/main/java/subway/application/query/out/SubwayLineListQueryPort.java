package subway.application.query.out;

import subway.application.response.SubwayLineResponse;

import java.util.List;

public interface SubwayLineListQueryPort {

    List<SubwayLineResponse> findAll();


}
