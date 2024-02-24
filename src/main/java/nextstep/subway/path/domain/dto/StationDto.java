package nextstep.subway.path.domain.dto;

public class StationDto {

    private long id;
    private String name;

    public StationDto() {
    }

    public StationDto(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
