package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

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
    private Station 가천대역;

    @BeforeEach
    void setup() {
        분당선 = new Line( "분당선", "yellow");
        수서역 = new Station( "수서역");
        복정역 = new Station( "복정역");
        가천대역 = new Station( "가천대역");

        stationRepository.save(수서역);
        stationRepository.save(복정역);
        stationRepository.save(가천대역);
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

    @DisplayName("지하철 구간 등록 시, 상행역과 하행역이 같으면 예외가 발생한다.")
    @Test
    void identicalStations() {
        // given
        SectionRequest request = new SectionRequest(수서역.getId(), 수서역.getId(), 5);

        // when & then
        assertThatThrownBy(() -> lineService.addSection(분당선.getId(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 등록 시, 구간의 길이는 최소 1 이상이어야 한다.")
    @ValueSource(ints = {-1, 0})
    @ParameterizedTest
    void invalidDistance(int distance) {
        // given
        SectionRequest request = new SectionRequest(수서역.getId(), 복정역.getId(), distance);

        // when & then
        assertThatThrownBy(() -> lineService.addSection(분당선.getId(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간을 제거한다.")
    @Test
    void deleteSection() {
        // given
        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));
        lineService.addSection(분당선.getId(), new SectionRequest(복정역.getId(), 가천대역.getId(), 5));

        // when
        lineService.deleteSection(분당선.getId(), 가천대역.getId());

        // then
        assertThat(분당선.getSections()).hasSize(1);
    }

    @DisplayName("지하철 구간 제거 시, 노선에 등록된 구간이 하나라면 예외가 발생한다.")
    @Test
    void cannotDeleteSectionWhenSingleSection() {
        // given
        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));

        // when & then
        assertThatThrownBy(() -> lineService.deleteSection(분당선.getId(), 복정역.getId()))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("지하철 구간 제거 시, 전달한 역이 하행 종점역이 아니라면 예외가 발생한다.")
    @Test
    void cannotDeleteSectionWhenNonDownStation() {
        // given
        lineService.addSection(분당선.getId(), new SectionRequest(수서역.getId(), 복정역.getId(), 5));
        lineService.addSection(분당선.getId(), new SectionRequest(복정역.getId(), 가천대역.getId(), 5));

        // when & then
        assertThatThrownBy(() -> lineService.deleteSection(분당선.getId(), 복정역.getId()))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
