package subway.db.h2.mapper;

import subway.db.common.Mapper;
import subway.db.h2.entity.StationJpa;
import subway.domain.PathStation;

@Mapper
public class PathStationMapper {

    public PathStation from(StationJpa stationJpa) {
        if(stationJpa.getId() == null)
            throw new IllegalArgumentException(stationJpa.getName() + "은 아직 저장되지 않았습니다.");
        return PathStation.of(PathStation.Id.of(stationJpa.getId()), stationJpa.getName());
    }
}
