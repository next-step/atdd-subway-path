package atdd.station.dto;

import atdd.station.domain.Station;

public class StationResponseDto {

    private Long id;
    private String name;

    private StationResponseDto() { }

    public static StationResponseDto from(Station station) {
        StationResponseDto view = new StationResponseDto();
        view.id = station.getId();
        view.name = station.getName();
        return view;
    }

    public StationResponseDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
