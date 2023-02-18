package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {

    private Station 강남역;
    private Station 분당역;

    private Line 신분당선;

    private SectionRequest 구간_요청;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        분당역 = stationRepository.save(new Station("분당역"));
        신분당선 = lineRepository.save(new Line("신분당선", "green"));
        구간_요청 = new SectionRequest(강남역.getId(), 분당역.getId(), 10);
    }

    @DisplayName("노선 생성 후 첫 구간 추가")
    @Test
    void addSection() {
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

    @DisplayName("노선 생성 후, 새로운 역 추가")
    @Test
    void addSection2() {
        // when
        // 첫 구간 추가
        lineService.addSection(신분당선.getId(), 구간_요청);

        //when
        //강남역과 분당역 사이에 정자역 추가
        Station 정자역 = stationRepository.save(new Station("정자역"));
        lineService.addSection(신분당선.getId(),
                new SectionRequest(강남역.getId(), 정자역.getId(), 4));

        //then
        assertThat(신분당선.getFirstSection().getDistance()).isEqualTo(4);
        assertThat(신분당선.getLastSection().getDistance()).isEqualTo(6);
        assertThat(신분당선.getDownStation()).isEqualTo(분당역);
        assertThat(신분당선.getSections()).hasSize(2);
    }
}
