package nextstep.subway.unit;

import nextstep.subway.applicaion.SectionService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.fixture.StationFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.fixture.LineFixture.이호선;
import static nextstep.subway.fixture.SectionFixture.강남_역삼_구간;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("지하철 구간 서비스 단위 테스트 (실 객체)")
class SectionServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private SectionService sectionService;

    @DisplayName("지하철 구간 추가")
    @Test
    void addSection() {
        // given - stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 강남역 = stationRepository.save(StationFixture.강남역.엔티티_생성());
        Station 역삼역 = stationRepository.save(StationFixture.역삼역.엔티티_생성());
        Line line = lineRepository.save(이호선.엔티티_생성());

        // when - addSection 호출
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 강남_역삼_구간.노선_간_거리());
        sectionService.addSection(line.getId(), sectionRequest);

        // then - line.getSections 메서드를 통해 검증
        assertThat(line.getSections().getAllStations())
                .containsExactly(강남역, 역삼역);
    }

    @DisplayName("지하철 구간 삭제")
    @Test
    void deleteSection() {
        // given - stationRepository와 lineRepository를 활용하여 초기값 셋팅 후 구간 추가
        Station 강남역 = stationRepository.save(StationFixture.강남역.엔티티_생성());
        Station 역삼역 = stationRepository.save(StationFixture.역삼역.엔티티_생성());
        Line line = lineRepository.save(이호선.엔티티_생성());
        SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 강남_역삼_구간.노선_간_거리());
        sectionService.addSection(line.getId(), sectionRequest);

        // when - deleteSection 호출
        sectionService.deleteSection(line.getId(), 역삼역.getId());

        // then - line.getSections 메서드를 통해 검증
        assertThat(line.getSections().getAllStations())
                .doesNotContain(강남역, 역삼역);
    }
}
