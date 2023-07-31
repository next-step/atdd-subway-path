package nextstep.subway.line.dto.request;

import lombok.*;
import nextstep.subway.line.entity.Line;
import nextstep.subway.station.entity.Station;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SaveLineRequest {

    @NotBlank(message = "지하철 노선 이름은 빈칸일 수 없습니다.")
    @Size(max = 20, message = "지하철역 이름은 20자 이내여야 합니다.")
    private String name;

    @NotBlank(message = "지하철 노선 색깔은 빈칸일 수 없습니다.")
    @Size(max = 7, message = "지하철 노선 색깔은 7자 이내여야 합니다.")
    private String color;

    @NotNull
    private Long upStationId;

    @NotNull
    private Long downStationId;

    @NotNull
    @Min(value = 1, message = "구간 거리는 1 이상이어야 합니다.")
    private Integer distance;

    public Line toEntity(Station upStation, Station downStation) {
        return Line.builder()
                .name(name)
                .color(color)
                .upStation(upStation)
                .downStation(downStation)
                .distance(distance)
                .build();
    }

}
