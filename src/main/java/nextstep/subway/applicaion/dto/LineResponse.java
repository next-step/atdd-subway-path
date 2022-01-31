package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse(Long id, String name, String color, List<StationResponse> stations, LocalDateTime createdDate, LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
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

    public List<StationResponse> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public static LineResponse from(Line line) {
        var sections = line.getSections();

        var stationResponses = sections.stream()
                .map(Section::getUpStation)
                .map(StationResponse::from)
                .collect(Collectors.toList());
        stationResponses.add(StationResponse.from(sections.get(sections.size() - 1).getDownStation()));

        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                stationResponses,
                line.getCreatedDate(),
                line.getModifiedDate()
        );
    }
}

