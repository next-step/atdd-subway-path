package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station 군자역 = stationRepository.save(new Station("군자역"));
        Station 아차산역 = stationRepository.save(new Station("아차산역"));
        Station 광나루역 = stationRepository.save(new Station("광나루역"));
        Line _5호선 = new Line("5호선", "보라색");
        _5호선.getSections().add(Section.of(_5호선, 군자역, 아차산역, 10));
        lineRepository.save(_5호선);

        int distance = 10;
        SectionRequest request = new SectionRequest(아차산역.getId(), 광나루역.getId(), distance);

        // when
        // lineService.addSection 호출
        lineService.addSection(_5호선.getId(), request);

        // then
        // line.getSections 메서드를 통해 검증
        List<Section> sections = _5호선.getSections();
        Section lastSection = sections.get(sections.size() - 1);
        assertThat(lastSection.getUpStation()).isEqualTo(아차산역);
        assertThat(lastSection.getDownStation()).isEqualTo(광나루역);
        assertThat(lastSection.getDistance()).isEqualTo(distance);
    }
}
