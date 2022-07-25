package nextstep.subway.applicaion.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StationRequest {
    private String name;

    @Builder
    public StationRequest(String name) {
        this.name = name;
    }
}
