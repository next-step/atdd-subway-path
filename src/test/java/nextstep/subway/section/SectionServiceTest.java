package nextstep.subway.section;

import static common.Constants.강남역;
import static common.Constants.신논현역;
import static common.Constants.신분당선;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Optional;
import nextstep.subway.line.Line;
import nextstep.subway.line.LineRepository;
import nextstep.subway.line.LineService;
import nextstep.subway.station.Station;
import nextstep.subway.station.StationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationRepository stationRepository;

    private SectionService sectionService;

    @BeforeEach
    void init() {
        sectionService = new SectionService(lineRepository, stationRepository);
    }

    /*
    어려운 점들이 존재한다
    1. 우선 SectionRepository를 사용해서 Section을 저장하는 게 아니라, cascade를 사용해 저장하기 때문에 SectionRepository를 모킹할 수 없다
    2. Section과 Line이 양방향 관계이기 때문에, Line을 최초 생성할 때 Section을 생성할 수 없고, 이후에 주입하는 형식으로 만들어야 하는데, thenReturn으로 Sections가 null인 상태로 생성한 이후, 그 이후의 동작을 제어하기 어렵다
    3. db 저장 상태에 의해 로직이 좌우되는 경우, Service 만으로는 테스트하기 어렵다. 이 경우는 인수테스트에서 다루어야 하나?
     */
//    @Test
//    void createSection() {
//        final Long lineId = 1L;
//        SectionRequest request = new SectionRequest(1L, 2L, 10);
//        when(lineRepository.findById(lineId)).thenReturn(Optional.of(new Line(1L, 신분당선, "bg-red-600", null)));
//        when(stationRepository.findById(request.getUpStationId())).thenReturn(Optional.of(new Station(1L, 강남역)));
//        when(stationRepository.findById(request.getDownStationId())).thenReturn(Optional.of(new Station(2L, 신논현역)));
//
//        sectionService.createSection(lineId, request);
//    }

}