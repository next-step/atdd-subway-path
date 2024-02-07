package nextstep.subway.controller.dto;

import nextstep.subway.service.dto.StationDto;

import java.util.List;
import java.util.stream.Collectors;

public class StationResponseBody {
    private Long id;
    private String name;

    public StationResponseBody(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static List<StationResponseBody> create (List<StationDto> stationDtoList) {
        return stationDtoList.stream()
                .map(stationDto -> new StationResponseBody(stationDto.getId(), stationDto.getName()))
                .collect(Collectors.toList());
    }
}
