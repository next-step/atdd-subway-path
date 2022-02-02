package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StationResponse {
    private Long id;
    private String name;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public StationResponse(Long id, String name, LocalDateTime createdDate, LocalDateTime modifiedDate) {
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

    public static StationResponse from(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCreatedDate(),
                station.getModifiedDate()
        );
    }

    public static List<StationResponse> from(Line line) {
        if (line.getSections()
                .isEmpty()) {
            return Collections.emptyList();
        }

        List<Station> stations = line.getSections()
                                     .stream()
                                     .map(Section::getDownStation)
                                     .collect(Collectors.toList());

        stations.add(
                0,
                line.getSections()
                    .get(0)
                    .getUpStation()
        );

        return stations.stream()
                       .map(StationResponse::from)
                       .collect(Collectors.toList());
    }
}
