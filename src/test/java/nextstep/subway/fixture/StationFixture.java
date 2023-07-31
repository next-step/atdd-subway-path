package nextstep.subway.fixture;

import nextstep.subway.station.dto.request.SaveStationRequest;
import nextstep.subway.station.entity.Station;

public class StationFixture {

    public static final SaveStationRequest 신사역 =
            SaveStationRequest.builder()
                    .name("신사역")
                    .build();

    public static final SaveStationRequest 강남역 =
            SaveStationRequest.builder()
                    .name("강남역")
                    .build();

    public static final SaveStationRequest 판교역 =
            SaveStationRequest.builder()
                    .name("판교역")
                    .build();

    public static final SaveStationRequest 광교역 =
            SaveStationRequest.builder()
                    .name("광교역")
                    .build();

    public static final SaveStationRequest 청량리역 =
            SaveStationRequest.builder()
                    .name("청량리역")
                    .build();

    public static final SaveStationRequest 춘천역 =
            SaveStationRequest.builder()
                    .name("춘천역")
                    .build();

    public static final SaveStationRequest 교대역 =
            SaveStationRequest.builder()
                    .name("교대역")
                    .build();

    public static final SaveStationRequest 양재역 =
            SaveStationRequest.builder()
                    .name("양재역")
                    .build();

    public static final SaveStationRequest 남부터미널역 =
            SaveStationRequest.builder()
                    .name("남부터미널역")
                    .build();

    public static final Station 까치산역 = new Station(1L, "까치산역");

    public static final Station 신도림역 = new Station(2L, "신도림역");

    public static final Station 신촌역 = new Station(3L, "신촌역");

    public static final Station 잠실역 = new Station(4L, "잠실역");

}
