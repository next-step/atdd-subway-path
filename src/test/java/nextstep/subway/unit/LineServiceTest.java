package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static nextstep.subway.utils.EntityCreator.*;
import static nextstep.subway.utils.EntityCreator.createSection;
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
        // station
        Station 판교역 = createStation("판교역");
        Station 정자역 = createStation("정자역");
        Station 미금역 = createStation("미금역");
        판교역 = stationRepository.save(판교역);
        정자역 = stationRepository.save(정자역);
        미금역 = stationRepository.save(미금역);

        // line
        Line 신분당선 = createLine();
        Section 판교_정자 = createSection(신분당선, 판교역, 정자역);
        신분당선.addSection(판교_정자);
        신분당선 = lineRepository.save(신분당선);

        // when
         lineService.addSection(신분당선.getId(), new SectionRequest(정자역.getId(), 미금역.getId(), 10));

        // then
        List<Section> sections = 신분당선.getSections();
        assertThat(sections).hasSize(2);

        Section 정자_미금 = createSection(신분당선, 정자역, 미금역);
        assertThat(sections.stream().anyMatch(s -> sectionEquals(s, 정자_미금))).isTrue();
    }

    private boolean sectionEquals(Section one, Section two) {
        return Objects.equals(one.getLine(), two.getLine()) &&
                Objects.equals(one.getUpStation(), two.getUpStation()) && Objects.equals(one.getDownStation(), two.getDownStation());
    }
}
