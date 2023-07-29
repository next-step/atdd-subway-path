package nextstep.subway.section;

public class SectionResponse {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;

    public SectionResponse() {}

    public SectionResponse(final Section section) {
        this(section.getId(),
            section.getLine().getId(),
            section.getUpStation().getId(),
            section.getDownStation().getId());
    }

    public SectionResponse(final Long id, final Long lineId, final Long upStationId, final Long downStationId) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

}
