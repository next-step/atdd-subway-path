package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.*;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class LineServiceTest {
    private Line 신분당선;
    private Station 강남역;
    private Station 양재역;
    private Section section;

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @BeforeEach
    void setUp() {
        신분당선 = new Line("신분당선", "red");

        강남역 = new Station("강남역");
        양재역 = new Station("양재역");

        section = new Section(신분당선, 강남역, 양재역, 10);

        신분당선.addSection(section);
        lineRepository.save(신분당선);
    }

    @DisplayName("노선 생성")
    @Test
    void saveLine() {
        // given 신분당선 강남-양재 구간 setUp

        // when 분당선 생성
        LineResponse lineResponse = lineService.saveLine(new LineRequest("분당선", "yellow", 강남역.getId(), 양재역.getId(), 10));

        // then 분당선에 강남역, 양재역이 존재하는지 조회
        Line 분당선 = lineRepository.findById(lineResponse.getId()).orElseThrow(IllegalArgumentException::new);
        assertThat(분당선.getStations()).containsExactly(강남역, 양재역);
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given 신분당선 강남-양재 구간 setUp
        // 양재시민의숲역 추가
        Station 양재시민의숲역 = new Station("양재시민의숲역");
        stationRepository.save(양재시민의숲역);

        // when 신분당선에 양재-양재시민의숲 구간 추가
        lineService.addSection(신분당선.getId(), new SectionRequest(양재역.getId(), 양재시민의숲역.getId(), 20));

        // then 강남역, 양재역, 양재시민의숲역이 신분당선에 추가되었는지 조회
        assertThat(신분당선.getStations()).containsExactly(강남역, 양재역, 양재시민의숲역);
    }

    @DisplayName("노선 수정")
    @Test
    void updateLine() {
        // given 신분당선 강남-양재 구간 setUp

        // when 신분당선의 이름과 색을 수정
        String newName = "2호선";
        String newColor = "green";
        lineService.updateLine(신분당선.getId(), new LineRequest(newName, newColor, 강남역.getId(), 양재역.getId(),10));

        // then 이름과 색이 변경되었는지 확인
        assertThat(신분당선.getName()).isEqualTo(newName);
        assertThat(신분당선.getColor()).isEqualTo(newColor);
    }

    @DisplayName("노선 제거")
    @Test
    void deleteLine() {
        // given 신분당선 강남-양재 구간 setUp

        // when 신분당선 제거
        lineService.deleteLine(신분당선.getId());

        // then 신분당선이 제거되어 조회시 에러 발생
        assertThatThrownBy(()->lineService.findLineResponse(신분당선.getId()))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
