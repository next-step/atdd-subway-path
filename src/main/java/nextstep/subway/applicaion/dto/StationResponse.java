package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StationResponse {
    private Long id;
    private String name;

    public StationResponse() {
    }

    public StationResponse(final Long id, final String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse createStationResponse(final Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }

    public static List<StationResponse> createStationResponses(final Line line) {
        if (line.getSections().getSections().isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getSections().getSections().stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        stations.add(0, line.getSections().getSections().get(0).getUpStation());

        return stations.stream()
                .map(StationResponse::createStationResponse)
                .collect(Collectors.toList());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
