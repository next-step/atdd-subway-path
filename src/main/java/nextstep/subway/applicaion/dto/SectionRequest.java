package nextstep.subway.applicaion.dto;

public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }

    /*
        Q. test 위해서 불필요한 Request Constructor가 생성된 것이 아닌가?
        쓰지 않고 addLine(long, sectionRequest)를 테스트 할 방법이 있는가?
     */


    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
