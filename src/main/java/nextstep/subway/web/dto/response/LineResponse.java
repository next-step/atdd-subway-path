package nextstep.subway.web.dto.response;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import nextstep.subway.domians.domain.Line;
import nextstep.subway.domians.domain.Station;

@Getter
public class LineResponse {


    @Getter
    static class StationDto {

        private final Long id;

        private final String name;

        public StationDto(Station station) {
            this.id = station.getId();
            this.name = station.getName();
        }
    }


    private final Long id;

    private final String name;

    private final String color;

    private final List<StationDto> stations;

    public LineResponse(Line line) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = line.getAllStations().stream()
            .map(StationDto::new)
            .collect(Collectors.toList());
    }


}
