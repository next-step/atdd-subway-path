package atdd.station.dto;

public class StationResponseDto {

    private Long id;
    private String name;

    private StationResponseDto() { }

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
