package atdd.station.web.dto;

public class SubwaySectionCreateRequestDto {
    private Long sourceStationId;
    private Long targetStationId;

    public SubwaySectionCreateRequestDto() {
    }

    public Long getSourceStationId() {
        return sourceStationId;
    }

    public Long getTargetStationId() {
        return targetStationId;
    }

    public void setSourceStationId(Long sourceStationId) {
        this.sourceStationId = sourceStationId;
    }

    public void setTargetStationId(Long targetStationId) {
        this.targetStationId = targetStationId;
    }

    @Override
    public String toString() {
        return "SubwaySectionCreateRequest{" +
                "sourceStationId=" + sourceStationId +
                ", targetStationId=" + targetStationId +
                '}';
    }
}
