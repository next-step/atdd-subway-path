package nextstep.subway.web.dto.response;

import lombok.Getter;

@Getter
public class StationResponse {

    private final Long id;
    private final String name;

    public StationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
