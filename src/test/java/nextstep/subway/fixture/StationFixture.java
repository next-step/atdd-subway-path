package nextstep.subway.fixture;

import nextstep.subway.station.dto.request.SaveStationRequestDto;
import nextstep.subway.station.entity.Station;

public class StationFixture {

    public static final SaveStationRequestDto 신사역 =
            SaveStationRequestDto.builder()
                    .name("신사역")
                    .build();

    public static final SaveStationRequestDto 강남역 =
            SaveStationRequestDto.builder()
                    .name("강남역")
                    .build();

    public static final SaveStationRequestDto 판교역 =
            SaveStationRequestDto.builder()
                    .name("판교역")
                    .build();

    public static final SaveStationRequestDto 광교역 =
            SaveStationRequestDto.builder()
                    .name("광교역")
                    .build();

    public static final SaveStationRequestDto 청량리역 =
            SaveStationRequestDto.builder()
                    .name("청량리역")
                    .build();

    public static final SaveStationRequestDto 춘천역 =
            SaveStationRequestDto.builder()
                    .name("춘천역")
                    .build();

    public static final Station 까치산역 = new Station(1L, "까치산역");

    public static final Station 신도림역 = new Station(2L, "신도림역");

    public static final Station 신촌역 = new Station(3L, "신촌역");

    public static final Station 잠실역 = new Station(4L, "잠실역");

}
