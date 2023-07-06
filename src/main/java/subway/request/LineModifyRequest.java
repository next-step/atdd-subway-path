package subway.request;


import lombok.Builder;
import lombok.Getter;

@Getter
public class LineModifyRequest {
    private String name;
    private String color;

    @Builder
    public LineModifyRequest(String name, String color) {
        this.name = name;
        this.color = color;
    }
}
