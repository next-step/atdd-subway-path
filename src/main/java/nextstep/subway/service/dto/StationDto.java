package nextstep.subway.service.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class StationDto {
    private Long id;
    private String name;

    public StationDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static StationDto from(Station station) {
        return new StationDto(station.getId(), station.getName());
    }

    public static List<StationDto> from(List<Station> stations) {
        return stations.stream()
                .map(station -> new StationDto(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }
}
