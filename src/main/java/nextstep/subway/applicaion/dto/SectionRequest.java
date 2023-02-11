package nextstep.subway.applicaion.dto;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
	private int distance;

	public SectionRequest(Long downStationId, Long upStationId, int distance) {
		this.downStationId = downStationId;
		this.upStationId = upStationId;
		this.distance = distance;
	}


	public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
