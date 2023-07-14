package nextstep.subway.station.dto;


import nextstep.subway.station.entity.Station;

public class StationDto {

    private Long id;
    private String name;

    public StationDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static StationDto from(Station station) {
        return new StationDto(station.getId(), station.getName());
    }

    public Station toEntity() {
        return new Station(name);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
