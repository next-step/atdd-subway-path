package atdd.station.model.dto;

import atdd.station.model.entity.Station;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class StationSimpleDto {
    private long id;
    private String name;

    public StationSimpleDto() {
    }

    public StationSimpleDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static List<StationSimpleDto> listOf(List<Station> stations) {
        return stations.stream()
                .map(it -> new StationSimpleDto(it.getId(), it.getName()))
                .collect(Collectors.toList());
    }
}
