package nextstep.subway.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public final class SubwayLineUpdateRequest {
    private final String name;
    private final String color;

    @Builder
    public SubwayLineUpdateRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static class SubwayLineUpdateRequestBuilder {
        public SubwayLineUpdateRequestBuilder() {
        }
    }
}
