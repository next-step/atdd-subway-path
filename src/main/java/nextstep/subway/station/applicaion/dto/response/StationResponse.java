package nextstep.subway.station.applicaion.dto.response;

import lombok.Getter;

@Getter
public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
