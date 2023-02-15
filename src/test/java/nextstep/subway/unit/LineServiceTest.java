package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {

    private static final int CREATE_LINE_DISTANCE = 10;
    private static final int ADD_SECTION_DISTANCE = 5;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @DisplayName("지하철 노선에 새로운 구간 추가하기")
    @Test
    void addSection() {
        // given
        Station 서울역 = stationRepository.save(new Station("서울역"));
        Station 시청역 = stationRepository.save(new Station("시청역"));
        Line line = lineRepository.save(new Line("1호선", "남색"));

        // when
        lineService.addSection(line.getId(), new SectionRequest(서울역.getId(), 시청역.getId(), CREATE_LINE_DISTANCE));

        // then
        assertThat(line.getStations()).containsExactly(서울역, 시청역);
    }

    @DisplayName("지하철 노선에 구간 제거하기")
    @Test
    void removeSection() {
        // given
        Station 서울역 = stationRepository.save(new Station("서울역"));
        Station 시청역 = stationRepository.save(new Station("시청역"));
        Station 종각역 = stationRepository.save(new Station("종각역"));

        Line line = lineRepository.save(new Line("1호선", "남색"));

        lineService.addSection(line.getId(), new SectionRequest(서울역.getId(), 시청역.getId(), CREATE_LINE_DISTANCE));
        lineService.addSection(line.getId(), new SectionRequest(시청역.getId(), 종각역.getId(), ADD_SECTION_DISTANCE));

        // when
        lineService.deleteSection(line.getId(), 시청역.getId());

        // then
        assertThat(line.getStations()).containsExactly(서울역, 종각역);
    }
}
