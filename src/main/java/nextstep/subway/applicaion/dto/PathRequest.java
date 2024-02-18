package nextstep.subway.applicaion.dto;

public class PathRequest {

    private String source;
    private String target;

    public PathRequest() {

    }

    public PathRequest(String source, String target) {
        this.source = source;
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
