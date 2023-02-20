package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Test
    void addSection() {
        // given
        Station 강남구청역 = stationRepository.save(new Station("강남구청역"));
        Station 압구정로데오역 = stationRepository.save(new Station("압구정로데오역"));
        Line 수인분당선 = lineRepository.save(new Line("수인분당선", "bg-yellow-600"));
        int distance = 10;

        // when
        lineService.addSection(수인분당선.getId(),
            new SectionRequest(강남구청역.getId(), 압구정로데오역.getId(), distance));

        // then
        수인분당선 = lineRepository.findById(수인분당선.getId()).get();
        Sections sections = 수인분당선.getSections();
        assertThat(sections.get(0).getUpStation().getId()).isEqualTo(강남구청역.getId());
        assertThat(sections.get(0).getDownStation().getId()).isEqualTo(압구정로데오역.getId());
        assertThat(sections.get(0).getDistance()).isEqualTo(distance);
    }
}
