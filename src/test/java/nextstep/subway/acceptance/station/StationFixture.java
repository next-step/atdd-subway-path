package nextstep.subway.acceptance.station;

import nextstep.subway.station.dto.request.SaveStationRequestDto;

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
}
