package subway.db.h2.mapper;

import subway.db.h2.entity.StationJpa;
import subway.db.common.Mapper;
import subway.application.query.response.StationResponse;

@Mapper
public class StationResponseMapper {

    public StationResponse from(StationJpa station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }
}
