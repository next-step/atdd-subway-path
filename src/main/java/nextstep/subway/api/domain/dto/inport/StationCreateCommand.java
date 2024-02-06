package nextstep.subway.api.domain.dto.inport;

import lombok.Getter;
import lombok.Setter;
import nextstep.subway.api.interfaces.dto.request.StationCreateRequest;
import nextstep.subway.common.mapper.ModelMapperBasedObjectMapper;

@Getter
@Setter
public class StationCreateCommand {
    private String name;

    public static StationCreateCommand from(StationCreateRequest stationCreateRequest) {
        return ModelMapperBasedObjectMapper.convert(stationCreateRequest, StationCreateCommand.class);
    }
}
