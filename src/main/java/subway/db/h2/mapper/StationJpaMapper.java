package subway.db.h2.mapper;

import subway.db.h2.entity.StationJpa;
import subway.db.common.Mapper;
import subway.domain.Station;

@Mapper
public class StationJpaMapper {

    public StationJpa from(Station station) {
        return new StationJpa(station.getName());
    }

}
