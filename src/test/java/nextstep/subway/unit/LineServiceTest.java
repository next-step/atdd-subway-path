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
    public static String 강남역_이름 = "강남역";
    public static String 분당역_이름 = "분당역";
    public static String 신분당선_이름 = "신분당선";
    public static String 신분당선_색 = "Green";
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
        Station 강남역 = stationRepository.save(new Station(LineServiceTest.강남역_이름));
        Station 분당역 = stationRepository.save(new Station(LineServiceTest.분당역_이름));
        Line 신분당선 = lineRepository.save(new Line(신분당선_이름, 신분당선_색));
        SectionRequest 구간_요청 = new SectionRequest(강남역.getId(), 분당역.getId(), 10);

        // when
        // lineService.addSection 호출
        lineService.addSection(신분당선.getId(), 구간_요청);

        // then
        // line.getSections 메서드를 통해 검증
        Section 새로운_구간 = 신분당선.getFirstSection();
        assertThat(새로운_구간.getLine()).isEqualTo(신분당선);
        assertThat(새로운_구간.getUpStation()).isEqualTo(강남역);
        assertThat(새로운_구간.getDownStation()).isEqualTo(분당역);
        assertThat(새로운_구간.getDistance()).isEqualTo(10);
    }
}
