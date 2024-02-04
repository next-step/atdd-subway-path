package nextstep.subway.station;

import nextstep.subway.line.Line;
import nextstep.subway.line.section.Sections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StationResponseFactory {

    private StationResponseFactory() {
    }

    public static List<StationResponse> createStationResponses(Line line) {
        Sections sections = line.getSections();
        List<StationResponse> stationResponses = new ArrayList<>(createStationResponses(sections.startStations()));
        stationResponses.add(createStationResponse(sections.lastStation()));
        return stationResponses;
    }

    public static List<StationResponse> createStationResponses(List<Station> stations) {
        return stations.stream()
                .map(StationResponseFactory::createStationResponse)
                .collect(Collectors.toList());
    }

    public static StationResponse createStationResponse(Station station) {
        return new StationResponse(station.getId(),
                station.getName());
    }

}
