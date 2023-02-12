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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    private LineService lineService;

    @BeforeEach
    void setUp() {
        lineService = new LineService(lineRepository, stationService);
    }

    /**
     * Given 지하철 노선에 새로운 구간 추가를 요청한다.
     * When 지하철 노선의 마지막 구간 제거를 요청 하면
     * Then 노선에 구간이 제거된다
     */
    @DisplayName("성공: 구간 추가")
    @Test
    void add_Section() {
        Station upStation = new Station("삼성역");
        Station downStation = new Station("강남역");
        Line line = new Line("이호선", "green");

        // given
        given(stationService.findById(any())).willReturn(upStation, downStation);
        given(lineRepository.findById(any())).willReturn(Optional.of(line));

        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), 3);

        // when
        lineService.addSection(1L, sectionRequest);

        // then
        assertSoftly(softly -> {
            softly.assertThat(lineService.findLineById(line.getId()).getSections()).hasSize(1);
            softly.assertThat(lineService.findLineById(line.getId()).getStations())
                            .extracting(Station::getName)
                            .containsExactlyInAnyOrder("삼성역", "강남역");
        });
    }
}
