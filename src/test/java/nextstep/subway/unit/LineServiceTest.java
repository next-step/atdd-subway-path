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

    private final int DEFAULT_DISTANCE = 10;

    @Test
    @DisplayName("구간을 생성한다.")
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 개봉역 = stationRepository.save(new Station("개봉역"));
        Station 구일역 = stationRepository.save(new Station("구일역"));

        Line 일호선 = lineRepository.save(new Line("일호선", "blue"));

        // when
        // lineService.addSection 호출
        lineService.addSection(일호선.getId(),
                new SectionRequest(개봉역.getId(), 구일역.getId(), DEFAULT_DISTANCE));

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(
                () -> assertThat(일호선.getSections()).hasSize(1),
                () -> assertThat(일호선.getSections().get(0).getUpStation().getName()).containsAnyOf("개봉역"),
                () -> assertThat(일호선.getSections().get(0).getDownStation().getName()).containsAnyOf("구일역")
        );

    }
}
