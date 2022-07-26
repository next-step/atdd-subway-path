package nextstep.subway.applicaion.dto;

public class PathRequest {

    private Long source;
    private Long target;

    private PathRequest() { }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }
}
