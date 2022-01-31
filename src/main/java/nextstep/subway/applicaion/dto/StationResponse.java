package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public static StationResponse of(final Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCreatedDate(),
                station.getModifiedDate()
        );
    }

    public static List<StationResponse> toStations(final Sections sections) {
        List<Station> stations = Stream.concat(
                sections.getSections().stream().map(Section::getUpStation),
                sections.getSections().stream().map(Section::getDownStation)
        ).distinct().collect(Collectors.toList());

        return stations.stream()
                .map(StationResponse::of)
                .sorted(Comparator.comparing(StationResponse::getId))
                .collect(Collectors.toList());
    }

    private StationResponse(final Long id, final String name, final LocalDateTime createdDate, final LocalDateTime modifiedDate) {
        this.id = id;
        this.name = name;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
