package atdd.station.web.dto;

public class SubwaySectionCreateRequestDto {
    private Long sourceStationId;
    private Long targetStationId;

    public SubwaySectionCreateRequestDto() {
    }

    public SubwaySectionCreateRequestDto(Long sourceStationId,
                                         Long targetStationId) {

        this.sourceStationId = sourceStationId;
        this.targetStationId = targetStationId;
    }

    public static SubwaySectionCreateRequestDto of(Long sourceStationId,
                                                   Long targetStationId) {

        return new SubwaySectionCreateRequestDto(sourceStationId, targetStationId);
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
        return "SubwaySectionCreateRequestDto{" +
                "sourceStationId=" + sourceStationId +
                ", targetStationId=" + targetStationId +
                '}';
    }

}
