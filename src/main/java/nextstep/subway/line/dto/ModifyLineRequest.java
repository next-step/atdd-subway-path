package nextstep.subway.line.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ModifyLineRequest {
    private String name;
    private String color;

    @Builder
    public ModifyLineRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
