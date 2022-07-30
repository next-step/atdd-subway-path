package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.fake.FakeLineRepository;
import nextstep.subway.fake.FakeStationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PathServiceTest {

    LineRepository lineRepository = new FakeLineRepository();
    StationRepository stationRepository = new FakeStationRepository();
    private PathService pathService = new PathService(lineRepository, stationRepository);

    Station 강남역;
    Station 역삼역;
    Station 잠실역;
    Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        잠실역 = stationRepository.save(new Station("잠실역"));
        Section 강남역_역삼역_구간 = new Section(강남역, 역삼역, 10);
        Section 역삼역_잠실역_구간 = new Section(역삼역, 잠실역, 10);
        신분당선 = lineRepository.save(new Line("신분당선", "red", 강남역_역삼역_구간));
        신분당선.getSections().addSection(역삼역_잠실역_구간);
    }

    @Test
    @DisplayName("최적의 경로와 길이를 출력합니다.")
    void test1() {
        PathResponse response = pathService.findPath(강남역.getId(), 잠실역.getId());

        assertAll(
            () -> assertThat(response.getStations()).hasSize(3),
            () -> assertThat(response.getDistance()).isEqualTo(20)
        );
    }
}
