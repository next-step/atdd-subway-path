package nextstep.subway.applicaion.dto;

public class PathRequest {
    long sourceId;
    long targetId;

    public PathRequest() {
    }

    public PathRequest(long sourceId, long targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public long getSourceId() {
        return sourceId;
    }

    public long getTargetId() {
        return targetId;
    }
}
