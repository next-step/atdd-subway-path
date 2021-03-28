package nextstep.subway.path.application;

import nextstep.subway.common.exception.ApplicationException;
import nextstep.subway.common.exception.ApplicationType;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    private StationService stationService;

    public PathService (StationService stationService) {
        this.stationService = stationService;
    }

    public PathResponse findPath(Long source, Long target) {
        Station sourceStation = stationService.findStationById(source);
        Station targetStation = stationService.findStationById(target);

        if (sourceStation.equals(targetStation)) {
            throw new ApplicationException(ApplicationType.CANNOT_SAME_WITH_SOURCE_AND_TARGET_ID);
        }


        return null;
    }
}
