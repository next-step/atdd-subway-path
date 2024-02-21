package nextstep.subway.application.converter;

import nextstep.subway.dto.StationResponse;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class StationConverter {
    public static List<StationResponse> convertToStationResponses(List<Section> sections) {
        Set<StationResponse> stationResponses = new LinkedHashSet<>();

        sections.forEach(section -> {
            stationResponses.add(convertToStationResponse(section.getUpStation()));
            stationResponses.add(convertToStationResponse(section.getDownStation()));
        });

        return new ArrayList<>(stationResponses);
    }

    public static StationResponse convertToStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName()
        );
    }
}
