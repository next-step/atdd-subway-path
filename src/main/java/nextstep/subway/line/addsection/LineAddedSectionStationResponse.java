package nextstep.subway.line.addsection;

public class LineAddedSectionStationResponse {

    private final Long id;
    private final String name;

    public LineAddedSectionStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }
}
