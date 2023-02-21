package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    Long 이호선;
    Long 강남역;
    Long 삼성역;
    Long 잠실역;
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;
    @InjectMocks
    private LineService lineService;

    @BeforeEach
    void setUp() {
        이호선 = 1L;
        강남역 = 1L;
        삼성역 = 2L;
        잠실역 = 3L;
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청한다.
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("성공: 구간 추가")
    @Test
    void add_Section() {

        given(stationService.findById(강남역)).willReturn(new Station("강남역"));
        given(stationService.findById(삼성역)).willReturn(new Station("삼성역"));
        given(lineRepository.findById(이호선)).willReturn(Optional.of(new Line("이호선", "green")));

        SectionRequest sectionRequest = new SectionRequest(강남역, 삼성역, 5);

        // when
        lineService.addSection(이호선, sectionRequest);
        // then
        assertSoftly(softly -> {
            softly.assertThat(lineService.findLineById(이호선).getSections()).hasSize(1);
            softly.assertThat(lineService.findLineById(이호선).getStations())
                    .extracting(Station::getName)
                    .containsExactlyInAnyOrder("강남역", "삼성역");
        });
    }

    /**
     * Given 지하철 노선에 새로운 구간 2개 추가를 요청한다.
     * When 지하철 노선의 가운데 역 삭제 요청 하면
     * Then 노선에 구간이 재배치 된다.
     */
    @DisplayName("성공: 구간사이 역 삭제")
    @Test
    void delete_Section() {
        given(stationService.findById(강남역)).willReturn(new Station("강남역"));
        given(stationService.findById(삼성역)).willReturn(new Station("삼성역"));
        given(stationService.findById(잠실역)).willReturn(new Station("잠실역"));
        given(lineRepository.findById(이호선)).willReturn(Optional.of(new Line("이호선", "green")));

        // given
        lineService.addSection(이호선, new SectionRequest(강남역, 삼성역, 5));
        lineService.addSection(이호선, new SectionRequest(삼성역, 잠실역, 10));
        // when
        lineService.deleteSection(이호선, 삼성역);
        // then
        assertSoftly(softly -> {
            softly.assertThat(lineService.findLineById(이호선).getSections()).hasSize(1);
            softly.assertThat(lineService.findLineById(이호선).getStations())
                    .extracting(Station::getName)
                    .containsExactlyInAnyOrder("강남역", "잠실역");
        });
    }
}
