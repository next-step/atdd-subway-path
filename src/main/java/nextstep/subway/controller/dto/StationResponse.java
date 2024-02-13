package nextstep.subway.controller.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class StationResponse {
    private Long id;
    private String name;

    @Builder
    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse stationToStationResponse(Station station) {
        return StationResponse.builder()
                .id(station.getId())
                .name(station.getName())
                .build();
    }

    public static List<StationResponse> sectionsToStationResponses(Sections sections) {
        Set<StationResponse> stationResponses = new HashSet<>();
        for (Section section : sections.getSections()) {
            StationResponse upStationResponse = StationResponse.builder()
                    .id(section.getUpStation().getId())
                    .name(section.getUpStation().getName())
                    .build();

            StationResponse downStationResponse = StationResponse.builder()
                    .id(section.getDownStation().getId())
                    .name(section.getDownStation().getName())
                    .build();

            stationResponses.add(upStationResponse);
            stationResponses.add(downStationResponse);
        }
        return new ArrayList<>(stationResponses);
    }
}
