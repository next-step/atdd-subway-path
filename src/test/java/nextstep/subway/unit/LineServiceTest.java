package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Long 여주역_Id = stationRepository.save(new Station("여주역")).getId();
        Long 부발역_Id = stationRepository.save(new Station("부발역")).getId();
        Line 경강선 = lineRepository.save(new Line("경강선", "blue"));

        // when
        // lineService.addSection 호출
        SectionRequest sectionRequest = new SectionRequest(여주역_Id, 부발역_Id, 10);
        lineService.addSection(경강선.getId(), sectionRequest);

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(경강선.getSections()).hasSize(1);
        assertThat(경강선.getSections().get(0).getUpStation().getName()).isEqualTo("여주역");
        assertThat(경강선.getSections().get(0).getDownStation().getName()).isEqualTo("부발역");
    }
}
