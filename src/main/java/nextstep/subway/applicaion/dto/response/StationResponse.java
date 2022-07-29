package nextstep.subway.applicaion.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.applicaion.dto.StationDto;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StationResponse {
    private Long id;
    private String name;

    public static StationResponse from(StationDto stationDto) {
        return new StationResponse(stationDto.getId(), stationDto.getName());
    }

    public static List<StationResponse> from(List<StationDto> stationDtos) {
        return  stationDtos.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

}
