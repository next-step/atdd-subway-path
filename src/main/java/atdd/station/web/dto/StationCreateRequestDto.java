package atdd.station.web.dto;

public class StationCreateRequestDto {
    private String name;

    public StationCreateRequestDto() {
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
