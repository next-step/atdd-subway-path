package nextstep.subway.applicaion.dto.response;

import nextstep.subway.domain.Station;

public class StationResponse {
    private Long id;
    private String name;

    private StationResponse() {
    }

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationResponse of(Station entity) {
        return new StationResponse(
                entity.getId(),
                entity.getName()
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
