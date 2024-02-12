package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static StationResponse createStationResponse(Station station) {
        return new StationResponse(
            station.getId(),
            station.getName()
        );
    }

    public static List<StationResponse> createStationsResponse(Sections sections) {
        return sections.getSections().stream()
                       .flatMap(section ->
                                    Stream.of(section.getUpStation(), section.getDownStation()))
                       .distinct()
                       .map(StationResponse::createStationResponse)
                       .collect(Collectors.toList());
    }
}
