package nextstep.subway.applicaion.dto;

public class LineRequest {

    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public static LineRequest of(String name, String color,
                                 long upStationId, long downStationId,
                                 int distance) {
        LineRequest request = new LineRequest();
        request.name = name;
        request.color = color;
        request.upStationId = upStationId;
        request.downStationId = downStationId;
        request.distance = distance;

        return request;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

}
