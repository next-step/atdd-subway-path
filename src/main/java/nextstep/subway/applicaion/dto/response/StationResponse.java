package nextstep.subway.applicaion.dto.response;

import nextstep.subway.domain.Station;

public class StationResponse {
    private final Long id;
    private final String name;

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
