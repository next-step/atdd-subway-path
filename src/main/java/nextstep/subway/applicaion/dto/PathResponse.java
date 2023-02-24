package nextstep.subway.applicaion.dto;

public class PathResponse {

    private String sourceStationName;
    private String targetStationName;
    private Integer distance;

    public PathResponse(String sourceStationName, String targetStationName, Integer distance) {
        this.sourceStationName = sourceStationName;
        this.targetStationName = targetStationName;
        this.distance = distance;
    }

    public String getSourceStationName() {
        return sourceStationName;
    }

    public String getTargetStationName() {
        return targetStationName;
    }

    public Integer getDistance() {
        return distance;
    }
}

