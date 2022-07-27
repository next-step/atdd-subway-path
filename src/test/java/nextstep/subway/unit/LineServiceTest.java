package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.request.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
        int defaultDistance = 4;
        Station 구의역 = new Station("구의역");
        Station 건대입구역 = new Station("건대입구역");

        Station 구의역_생성 = stationRepository.save(구의역);
        Station 건대입구역_생성 = stationRepository.save(건대입구역);

        Line 초록색_라인 = new Line("초록색_라인", "green-bg-20");
        Line 초록색_라인_생성 = lineRepository.save(초록색_라인);

        // when
        // lineService.addSection 호출
        lineService.addSection(초록색_라인_생성.getId(), new SectionRequest(구의역_생성.getId(), 건대입구역.getId(), defaultDistance));

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(() -> {
            assertThat(초록색_라인_생성.getSections()).hasSize(1);
            assertThat(초록색_라인_생성.getSections()).filteredOn(section -> section.equals(new Section(초록색_라인_생성, 구의역_생성, 건대입구역_생성, defaultDistance)));
        });
    }
}
