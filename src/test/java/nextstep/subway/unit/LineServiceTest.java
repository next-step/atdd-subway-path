package nextstep.subway.unit;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import nextstep.subway.utils.DataBaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
public class LineServiceTest {

    @Autowired
    private DataBaseCleaner dataBaseCleaner;

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station 강남역;
    private Station 역삼역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        dataBaseCleaner.execute();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성() {
        // given
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        final LineRequest lineRequest = this.신분당선_생성_요청();

        // when
        final LineResponse response = lineService.createSubwayLine(lineRequest);

        // then
        assertThat(response.getName()).isEqualTo(lineRequest.getName());
        assertThat(response.getColor()).isEqualTo(lineRequest.getColor());
    }

    @DisplayName("지하철 노선 생성 시 존재하지 않는 역으로 조회할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_생성_시_존재하지_않는_역으로_조회_불가() {
        // given
        final LineRequest lineRequest = this.신분당선_생성_요청();

        // then
        assertThatThrownBy(() -> lineService.createSubwayLine(lineRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }

    private LineRequest 신분당선_생성_요청() {
        return new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
    }
}
