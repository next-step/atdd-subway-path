package nextstep.subway.path.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.global.error.code.ErrorCode;
import nextstep.subway.global.error.exception.InvalidPathException;
import nextstep.subway.line.adapters.persistence.LineJpaAdapter;
import nextstep.subway.line.entity.Line;
import nextstep.subway.path.dto.request.PathRequest;
import nextstep.subway.path.dto.response.PathResponse;
import nextstep.subway.path.utils.ShortestPathHelper;
import nextstep.subway.section.entity.Section;
import nextstep.subway.station.adapters.persistence.StationJpaAdapter;
import nextstep.subway.station.entity.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PathService {

    private final StationJpaAdapter stationJpaAdapter;

    private final LineJpaAdapter lineJpaAdapter;

    public PathResponse getShortestDistance(PathRequest pathRequest) {
        if (pathRequest.isSameSourceAndTarget()) {
            throw new InvalidPathException(ErrorCode.SAME_DEPARTURE_AND_ARRIVAL_STATIONS);
        }

        Station source = stationJpaAdapter.findById(pathRequest.getSource());
        Station target = stationJpaAdapter.findById(pathRequest.getTarget());

        List<Station> stations = stationJpaAdapter.findAll();
        List<Section> sections = lineJpaAdapter.findAll()
                .stream()
                .map(Line::getSections)
                .flatMap(lineSections -> lineSections.getSections().stream())
                .collect(Collectors.toList());

        ShortestPathHelper shortestPathHelper = ShortestPathHelper.builder()
                .stations(stations)
                .sections(sections)
                .source(source)
                .target(target)
                .build();

        return PathResponse.of(shortestPathHelper.getPath());
    }

}
