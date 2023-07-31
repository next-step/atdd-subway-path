package nextstep.subway.station.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.entity.Station;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SaveStationRequest {

    @NotBlank(message = "지하철역 이름은 빈칸일 수 없습니다.")
    @Size(max = 20, message = "지하철역 이름은 20자 이내여야 합니다.")
    private String name;

    public Station toEntity() {
        return Station.builder()
                .name(name)
                .build();
    }

}
