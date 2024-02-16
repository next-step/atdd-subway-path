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
        Set<Long> addedStationIds = new HashSet<>();
        List<StationResponse> stationResponses = new ArrayList<>();

        for (Section section : sections.getSections()) {
            addStationResponseIfNotExists(section.getUpStation(), addedStationIds, stationResponses);
            addStationResponseIfNotExists(section.getDownStation(), addedStationIds, stationResponses);
        }

        return stationResponses;
    }

    private static void addStationResponseIfNotExists(Station station, Set<Long> addedStationIds, List<StationResponse> stationResponses) {
        Long stationId = station.getId();
        if (!addedStationIds.contains(stationId)) {
            stationResponses.add(StationResponse.builder()
                    .id(stationId)
                    .name(station.getName())
                    .build());
            addedStationIds.add(stationId);
        }
    }
}
