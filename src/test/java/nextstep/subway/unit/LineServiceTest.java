package nextstep.subway.unit;

import static nextstep.subway.common.LineSomething.DISTANCE_VALID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
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
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Long 노선;
    private Long 일번역1;
    private Long 이번역2;
    private Long 삼번역3;
    private Long 사번역4;
    private Long 신규역;

    private final String 노선이름 = "노선1";
    private final String 노선색상 = "노선색상1";
    private final Station 상행역 = Station.of("상행역");
    private final Station 하행역 = Station.of("하행역");
    private final int 역간거리 = 10;
    private Long 상행역Id;
    private Long 하행역Id;

    @BeforeEach
    void setUp() {
        // given
        상행역Id = stationRepository.save(상행역).getId();
        하행역Id = stationRepository.save(하행역).getId();
    }

    @DisplayName("노선을 등록하는데 성공한다.")
    @Test
    void saveLine_1() {
        // when
        LineRequest request = LineRequest.of(노선이름, 노선색상, 상행역.getId(), 하행역.getId(), 역간거리);
        LineResponse response = lineService.saveLine(request);

        // then
        assertThat(response.getName()).isEqualTo(노선이름);
        assertThat(response.getColor()).isEqualTo(노선색상);
        assertThat(response.getStations().size()).isEqualTo(2);
    }

    @DisplayName("노선을 등록하는데 등록된 역이 아니면 실패한다.")
    @Test
    void saveLine_2() {
        // when // then
        assertThatThrownBy(() -> {
            lineService.saveLine(LineRequest.of(노선이름, 노선색상, 상행역Id, 하행역Id+99L, 역간거리));
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void showLines() {
        // given
        lineService.saveLine(LineRequest.of(노선이름, 노선색상, 상행역.getId(), 하행역.getId(), 역간거리));

        // when
        List<LineResponse> lineResponseList = lineService.showLines();

        // then
        assertThat(lineResponseList.size()).isEqualTo(1);
        assertThat(lineResponseList.get(0).getName()).isEqualTo(노선이름);
    }

    @DisplayName("노선을 아이디를 사용하여 찾는데 성공한다.")
    @Test
    void findById_Test_1() {
        // given
        노선 = lineService.saveLine(
            LineRequest.of(노선이름, 노선색상, 상행역.getId(), 하행역.getId(), 역간거리)).getId();

        // when
        LineResponse 저장된노선 = lineService.findById(노선);

        // then
        assertThat(저장된노선.getId()).isEqualTo(노선);
        assertThat(저장된노선.getName()).isEqualTo(노선이름);
        assertThat(저장된노선.getColor()).isEqualTo(노선색상);
    }

    @DisplayName("노선을 아이디를 사용하여 찾는데 아이디가 없으면 실패한다.")
    @Test
    void findById_Test_2() {
        // given
        노선 = lineService.saveLine(
            LineRequest.of(노선이름, 노선색상, 상행역.getId(), 하행역.getId(), 역간거리)).getId();

        // when // then
        assertThatThrownBy(() -> {
            LineResponse 저장된노선 = lineService.findById(노선+99L);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateLine() {
        // given
        상행역Id = stationRepository.save(상행역).getId();
        하행역Id = stationRepository.save(하행역).getId();
        노선 = lineService.saveLine(
            LineRequest.of(노선이름, 노선색상, 상행역.getId(), 하행역.getId(), 역간거리)).getId();
    }

    @Test
    void deleteLine() {

    }

    @Test
    void addSection() {
        // given
        // stationRepository와 lineRepository를 활용하여 초기값 셋팅
        Station station1 = stationRepository.save(Station.of("station1"));
        Station station2 = stationRepository.save(Station.of("station2"));
        Line line = lineRepository.save(Line.of("line1", "color"));

        // when
        // lineService.addSection 호출
        lineService.addSection(line.getId(), SectionRequest.of(station1.getId(), station2.getId(), DISTANCE_VALID));

        // then
        // line.getSections 메서드를 통해 검증
        assertThat(line.getSections().size()).isEqualTo(1);
    }
}
