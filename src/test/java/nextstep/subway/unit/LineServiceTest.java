package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @AfterEach
    void clean() {
        stationRepository.deleteAll();
        lineRepository.deleteAll();
    }

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line 사호선 = lineRepository.save(new Line("4호선", "sky-blue"));
        Station 서울역 = stationRepository.save(new Station("서울역"));
        Station 숙대입구역 = stationRepository.save(new Station("숙대입구역"));

        // when
        // lineService.addSection 호출
        addSection(사호선, 서울역, 숙대입구역);

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(
                () -> assertThat(사호선.getSections().isEmpty()).isFalse(),
                () -> assertThat(사호선.getAllStations()).contains(서울역),
                () -> assertThat(사호선.getAllStations()).contains(숙대입구역)
        );
    }

    @Test
    @DisplayName("구간 삭제")
    void deleteSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line 사호선 = lineRepository.save(new Line("4호선", "sky-blue"));
        Station 서울역 = stationRepository.save(new Station("서울역"));
        Station 숙대입구역 = stationRepository.save(new Station("숙대입구역"));
        addSection(사호선, 서울역, 숙대입구역);

        Station 삼각지역 = stationRepository.save(new Station("삼각지역"));
        addSection(사호선, 숙대입구역, 삼각지역);

        // when
        // lineService.deleteSection 호출
        lineService.deleteSection(사호선.getId(), 삼각지역.getId());

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(
                () -> assertThat(사호선.getSections().isEmpty()).isFalse(),
                () -> assertThat(사호선.getAllStations()).contains(서울역),
                () -> assertThat(사호선.getAllStations()).contains(숙대입구역),
                () -> assertThat(사호선.getAllStations()).doesNotContain(삼각지역)
        );
    }

    private void addSection(Line 사호선, Station 서울역, Station 숙대입구역) {
        SectionRequest sectionRequest = new SectionRequest();
        sectionRequest.setUpStationId(서울역.getId());
        sectionRequest.setDownStationId(숙대입구역.getId());
        sectionRequest.setDistance(10);

        lineService.addSection(사호선.getId(), sectionRequest);
    }
}
