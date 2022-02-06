package nextstep.subway.acceptance.dto;

public class LineTestRequest {

    private final String color;
    private final String name;
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    private LineTestRequest(String color, String name, Long upStationId, Long downStationId, int distance) {
        this.color = color;
        this.name = name;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public static LineTestRequestBuilder builder() {
        return new LineTestRequestBuilder();
    }

    public String getColor() {
        return color;
    }

    public String getName() {
        return name;
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

    public static class LineTestRequestBuilder {
        private String color;
        private String name;
        private Long upStationId;
        private Long downStationId;
        private int distance;

        LineTestRequestBuilder() {}

        public LineTestRequest build() {
            return new LineTestRequest(
                    this.color, this. name, this.upStationId, this.downStationId, this.distance);
        }

        public LineTestRequestBuilder color(String color) {
            this.color = color;
            return this;
        }

        public LineTestRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public LineTestRequestBuilder upStationId(Long upStationId) {
            this.upStationId = upStationId;
            return this;
        }

        public LineTestRequestBuilder downStationId(Long downStationId) {
            this.downStationId = downStationId;
            return this;
        }

        public LineTestRequestBuilder distance(int distance) {
            this.distance = distance;
            return this;
        }
    }
}
