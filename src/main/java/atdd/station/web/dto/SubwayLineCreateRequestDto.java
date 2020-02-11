package atdd.station.web.dto;

public class SubwayLineCreateRequestDto {
    private String name;

    public SubwayLineCreateRequestDto() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "SubwayLineCreateRequest{" +
                "name='" + name + '\'' +
                '}';
    }
}
