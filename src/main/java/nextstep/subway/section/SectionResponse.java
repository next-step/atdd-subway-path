package nextstep.subway.section;

public class SectionResponse {
    private Long id;
    private int distance;

    public SectionResponse() {
    }

    public SectionResponse(final Long id, final int distance) {
        this.id = id;
        this.distance = distance;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }
}
