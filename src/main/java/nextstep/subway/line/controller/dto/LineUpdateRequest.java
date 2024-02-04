package nextstep.subway.line.controller.dto;

/** 지하철 노선 수정 요청 데이터 */
public class LineUpdateRequest {

    private String name;
    private String color;

    public LineUpdateRequest() {}

    public LineUpdateRequest(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
