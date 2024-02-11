package nextstep.subway.api.interfaces.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nextstep.subway.api.domain.model.entity.Station;
import nextstep.subway.common.mapper.ModelMapperBasedObjectMapper;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationResponse {
    private Long id;
    private String name;

    public static StationResponse from(Station station) {
        return ModelMapperBasedObjectMapper.convert(station, StationResponse.class);
    }
}
