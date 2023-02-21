package nextstep.subway.applicaion.dto;

import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private Integer length;
    private List<StationResponse> stations;

    public static LineResponse of(Long id, String name, String color, Integer length, List<StationResponse> stations) {
        return new LineResponse(id, name, color, length, stations);
    }

    private LineResponse(Long id, String name, String color, Integer length, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.length = length;
        this.stations = stations;
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

    public Integer getLength() {
        return length;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}

