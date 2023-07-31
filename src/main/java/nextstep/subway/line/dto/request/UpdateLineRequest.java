package nextstep.subway.line.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.line.entity.Line;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateLineRequest {

    @NotBlank(message = "지하철 노선 이름은 빈칸일 수 없습니다.")
    @Size(max = 20, message = "지하철역 이름은 20자 이내여야 합니다.")
    private String name;

    @NotBlank(message = "지하철 노선 색깔은 빈칸일 수 없습니다.")
    @Size(max = 7, message = "지하철 노선 색깔은 7자 이내여야 합니다.")
    private String color;

    public Line toEntity() {
        return Line.builder()
                .name(name)
                .color(color)
                .build();
    }

}
