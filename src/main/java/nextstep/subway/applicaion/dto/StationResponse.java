package nextstep.subway.applicaion.dto;

import lombok.Data;

@Data
public class StationResponse {
    private Long id;
    private String name;

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
