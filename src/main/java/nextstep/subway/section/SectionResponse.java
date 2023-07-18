package nextstep.subway.section;

public class SectionResponse {
    private Long id;
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int sequence;

    public SectionResponse() {}

    public SectionResponse(final Section section) {
        this(section.getId(), section.getLine().getId(), section.getUpStation().getId(),
            section.getDownStation().getId(), section.getSequence());
    }

    public SectionResponse(final Long id, final Long lineId, final Long upStationId, final Long downStationId,
        int sequence) {
        this.id = id;
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.sequence = sequence;
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

    public int getSequence() {
        return sequence;
    }
}
