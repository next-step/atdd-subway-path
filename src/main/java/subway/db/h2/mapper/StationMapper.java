package subway.db.h2.mapper;

import subway.db.h2.entity.StationJpa;
import subway.db.common.Mapper;
import subway.domain.Station;

@Mapper
public class StationMapper {

    public Station from(StationJpa station) {
        return Station.of(
                new Station.Id(station.getId()),
                station.getName()
        );
    }
}
