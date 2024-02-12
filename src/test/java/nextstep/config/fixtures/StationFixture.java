package nextstep.config.fixtures;

import nextstep.subway.dto.StationRequest;
import nextstep.subway.entity.Station;

import java.util.List;

public class StationFixture {
    public static final Station 강남 = new Station("강남");
    public static final Station 양재 = new Station("양재");
    public static final Station 삼성 = new Station("삼성");
    public static final Station 선릉 = new Station("선릉");
    public static final Station 역삼 = new Station("역삼");
    public static final Station 신천 = new Station("신천");

    public static final StationRequest 가산디지털단지 = new StationRequest("가산디지털단지");
    public static final StationRequest 구로디지털단지 = new StationRequest("구로디지털단지");
    public static final StationRequest 독산 = new StationRequest("독산역");
    public static final StationRequest 신도림 = new StationRequest("신도림");
    public static final StationRequest 홍대입구 = new StationRequest("홍대입구");
    public static final StationRequest 종각 = new StationRequest("종각");
    public static final StationRequest 신림 = new StationRequest("신림");
    public static final StationRequest 잠실 = new StationRequest("잠실");
    public static final StationRequest 교대 = new StationRequest("교대");
    public static final StationRequest 서울역 = new StationRequest("서울역");
    public static final List<StationRequest> 역_10개 =
            List.of(가산디지털단지, 구로디지털단지, 독산, 신도림, 홍대입구, 종각, 신림, 잠실, 교대, 서울역);
}
