package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Station 판교역 = stationRepository.save(new Station("판교역"));
        Station 정자역 = stationRepository.save(new Station("정자역"));
        Station 미금역 = stationRepository.save(new Station("미금역"));
        Line 신분당선 = lineRepository.save(new Line("신분당선", "red"));
        Section 판교_정자_구간 = new Section(신분당선, 판교역, 정자역, 10);
        신분당선.getSections().add(판교_정자_구간);

        Section 정자_미금_구간 = new Section(신분당선, 정자역, 미금역, 10);
        
        // when
        // lineService.addSection 호출
        lineService.addSection(1L, new SectionRequest(2L, 3L, 10));

        // then
        // lineService.findLineById 메서드를 통해 검증
        assertThat(lineRepository.findById(1L).get().getSections()).contains(정자_미금_구간);
    }
}
