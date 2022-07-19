package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("실제 객체를 활용한 LineServiceTest")
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
        Station 광교역 = stationRepository.save(new Station("광교역"));
        Station 광교중앙역 = stationRepository.save(new Station("광교중앙역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(광교역.getId(), 광교중앙역.getId(), 10));

        // then
        assertAll(
                () -> assertThat(신분당선.getSections()).hasSize(1),
                () -> assertThat(신분당선.getSections()).containsExactly(new Section(신분당선, 광교역, 광교중앙역, 10))
        );
    }
}
