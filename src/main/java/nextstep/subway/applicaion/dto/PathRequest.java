package nextstep.subway.applicaion.dto;

public class PathRequest {

    private Long source;
    private Long target;

    private PathRequest() { }

    private PathRequest(Long source, Long target) {
        this.source = source;
        this.target = target;
    }

    public static PathRequest of(Long source, Long target) {
        return new PathRequest(source, target);
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }

    public void setSource(Long source) {
        this.source = source;
    }

    public void setTarget(Long target) {
        this.target = target;
    }
}
