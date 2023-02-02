package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Line 분당선;
    private Station 수서역;
    private Station 복정역;
    private Station 판교역;
    private Station 광교역;

    @BeforeEach
    void setup() {
        분당선 = new Line( "분당선", "yellow");
        수서역 = new Station( "수서역");
        복정역 = new Station( "복정역");
        판교역 = new Station( "판교역");
        광교역 = new Station( "광교역");

        stationRepository.save(수서역);
        stationRepository.save(복정역);
        stationRepository.save(판교역);
        stationRepository.save(광교역);
        lineRepository.save(분당선);
    }

    @DisplayName("새로운 지하철 구간을 추가한다.")
    @Test
    void addSection() {
        // when
        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));

        // then
        assertThat(분당선.getSections()).hasSize(1);
    }

    @DisplayName("지하철 구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));

        // when
        lineService.deleteSection(분당선.getId(), 복정역.getId());

        // then
        assertThat(분당선.getSections()).isEmpty();
    }

    @DisplayName("지하철 구간 제거 시, 전달한 역이 하행 종점역이 아니라면 예외가 발생한다.")
    @Test
    void cannotDeleteSection() {
        // when
        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));

        // then
        assertThatThrownBy(() -> lineService.deleteSection(분당선.getId(), 수서역.getId()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("새로운 지하철 노선을 등록한다.")
    @Test
    void saveLine() {
        // when
        LineResponse response = lineService.saveLine(new LineRequest("신분당선", "red", 판교역.getId(), 광교역.getId(), 20));

        // then
        assertAll(
            () -> assertThat(response.getName()).isEqualTo("신분당선"),
            () -> assertThat(response.getColor()).isEqualTo("red"),
            () -> assertThat(response.getStations()).hasSize(2)
        );
    }
}
