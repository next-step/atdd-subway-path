package nextstep.subway.line.application.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
public class LineUpdateRequest {
    @NotBlank(message = "노선의 이름을 입력해주세요.")
    private String name;
    @NotBlank(message = "노선의 색 정보를 입력해주세요.")
    private String color;
}
