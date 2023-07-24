package nextstep.subway.line;

public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Integer distance;

    private LineRequest() {
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

    public Integer getDistance() {
        return distance;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LineRequest lineRequest;

        private Builder() {
            lineRequest = new LineRequest();
        }

        public Builder name(String name) {
            lineRequest.name = name;
            return this;
        }

        public Builder color(String color) {
            lineRequest.color = color;
            return this;
        }

        public Builder upStationId(Long upStationId) {
            lineRequest.upStationId = upStationId;
            return this;
        }

        public Builder downStationId(Long downStationId) {
            lineRequest.downStationId = downStationId;
            return this;
        }

        public Builder distance(Integer distance) {
            lineRequest.distance = distance;
            return this;
        }

        public LineRequest build() {
            return lineRequest;
        }
    }
}
