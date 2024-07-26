package nextstep.subway.path.payload;

public class SearchPathRequest {
    private Long source;
    private Long target;

    public SearchPathRequest(final Long source, final Long target) {
        this.source = source;
        this.target = target;
    }

    public void setSource(final Long source) {
        this.source = source;
    }

    public void setTarget(final Long target) {
        this.target = target;
    }

    public Long getSource() {
        return source;
    }

    public Long getTarget() {
        return target;
    }

    public boolean isStationSame() {
        return source.equals(target);
    }
}
