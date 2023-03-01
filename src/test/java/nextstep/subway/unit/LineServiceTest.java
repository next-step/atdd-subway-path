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

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Line 강남_2호선 = lineRepository.save(new Line("강남 2호선", "green"));
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        int distance = 10;

        // when
        // lineService.addSection 호출
        lineService.addSection(강남_2호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), distance));

        // then
        // line.getSections 메서드를 통해 검증
        List<Section> 검증하려는_구간_목록 = lineRepository.findById(강남_2호선.getId()).get().getSections();
        List<Section> 입력했던_구간_목록 = 강남_2호선.getSections();

        assertAll(
                () -> assertThat(검증하려는_구간_목록).isEqualTo(입력했던_구간_목록),
                () -> assertThat(검증하려는_구간_목록.get(0).getUpStation().getId()).isEqualTo(입력했던_구간_목록.get(0).getUpStation().getId()),
                () -> assertThat(검증하려는_구간_목록.get(0).getDownStation().getId()).isEqualTo(입력했던_구간_목록.get(0).getDownStation().getId()),
                () -> assertThat(검증하려는_구간_목록.get(0).getDistance()).isEqualTo(distance)
        );
    }
}
