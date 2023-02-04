package nextstep.subway.applicaion.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    @JsonProperty("stations")
    private List<StationResponse> stationResponses;

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stationResponses) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stationResponses = stationResponses;
    }

    public static LineResponse createResponse(final Line saveLine) {
        final List<StationResponse> stationResponses = convertToStationResponse(saveLine.convertToStation());
        return new LineResponse(saveLine.getId(), saveLine.getName(), saveLine.getColor(), stationResponses);
    }

    private static List<StationResponse> convertToStationResponse(final List<Station> stations) {
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

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }
}

