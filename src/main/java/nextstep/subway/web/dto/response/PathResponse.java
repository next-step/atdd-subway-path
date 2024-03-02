package nextstep.subway.web.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import nextstep.subway.domain.entity.Path;
import nextstep.subway.domain.entity.Station;

@Getter
public class PathResponse {

    @Getter
    public static class StationDto {

        private final Long id;

        private final String name;

        public StationDto(Station station) {
            this.id = station.getId();
            this.name = station.getName();
        }

    }

    private final List<StationDto> stations;
    private final long distance;

    public PathResponse(List<StationDto> stations, long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public PathResponse(Path shortestPath) {
        this.stations = shortestPath.getStations().stream()
            .map(StationDto::new)
            .collect(Collectors.toList());
        this.distance = (long) shortestPath.getDistance();

    }


}
