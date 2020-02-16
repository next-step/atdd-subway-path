package atdd.station.web.dto;

public class StationCreateRequestDto {
    private String name;

    public StationCreateRequestDto() {
    }

    public StationCreateRequestDto(String name) {
        this.name = name;
    }

    public static StationCreateRequestDto of(String name) {
        return new StationCreateRequestDto(name);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "StationCreateRequestDto{" +
                "name='" + name + '\'' +
                '}';
    }

}
