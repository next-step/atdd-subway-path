package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import nextstep.subway.exception.NonExistentLineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Line 일호선;
    private Station 잠실역;
    private Station 선릉역;
    private Station 강남역;
    private Station 교대역;
    private Station 논현역;
    private Station 고속터미널역;

    @Transactional
    @BeforeEach
    void init() {
        잠실역 = new Station("잠실역");
        강남역 = new Station("강남역");
        교대역 = new Station("교대역");
        선릉역 = new Station("선릉역");
        논현역 = new Station( "논현역");
        고속터미널역 = new Station("고속터미널역");

        stationRepository.saveAll(List.of(잠실역, 강남역, 교대역, 선릉역, 논현역, 고속터미널역));

        일호선 = new Line("1호선", "br-red-600");
    }

    @DisplayName("지하철 노선을 저장한다.")
    @Test
    void saveLine() {
        LineResponse 이호선 = lineService.saveLine(new LineRequest("2호선", "br-red-600", 잠실역.getId(), 교대역.getId(), 5));

        assertThat(lineService.showLines()).contains(이호선);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        LineResponse 이호선 = lineService.saveLine(new LineRequest("2호선", "br-red-600", 잠실역.getId(), 교대역.getId(), 5));
        LineResponse 삼호선 = lineService.saveLine(new LineRequest("3호선", "br-blue-600", 강남역.getId(), 논현역.getId(), 5));
        LineResponse 사호선 = lineService.saveLine(new LineRequest("4호선", "br-green-600", 교대역.getId(), 고속터미널역.getId(), 5));

        assertThat(lineService.showLines()).containsExactly(이호선, 삼호선, 사호선);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findById() {
        LineResponse 이호선 = lineService.saveLine(new LineRequest("2호선", "br-red-600", 잠실역.getId(), 교대역.getId(), 5));

        assertThat(lineService.findById(이호선.getId())).isEqualTo(이호선);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findByIdNonExistentLine() {
        assertThatThrownBy(() -> lineService.findById(1000L))
                .isInstanceOf(NonExistentLineException.class);
    }

    @DisplayName("지하철 노선 정보를 업데이트한다.")
    @Test
    void updateLine() {
        LineResponse 이호선 = lineService.saveLine(new LineRequest("2호선", "br-red-600", 잠실역.getId(), 교대역.getId(), 5));

        lineService.updateLine(이호선.getId(), new LineRequest("3호선", "green", null, null, 0));

        LineResponse 변경된_이호선 = lineService.findById(이호선.getId());
        assertThat(변경된_이호선).isEqualTo(new LineResponse(이호선.getId(), "3호선", "green", 이호선.getStations()));
    }

    @DisplayName("존재하지 않는 지하철 노선의 정보를 업데이트할 수 없다.")
    @Test
    void updateNonExistentLine() {
        assertThatThrownBy(() -> lineService.updateLine(1000L, new LineRequest("3호선", "green", null, null, 0)))
                .isInstanceOf(NonExistentLineException.class);
    }

    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        LineResponse 이호선 = lineService.saveLine(new LineRequest("2호선", "br-red-600", 잠실역.getId(), 교대역.getId(), 5));
        LineResponse 삼호선 = lineService.saveLine(new LineRequest("3호선", "br-blue-600", 강남역.getId(), 논현역.getId(), 5));
        LineResponse 사호선 = lineService.saveLine(new LineRequest("4호선", "br-green-600", 교대역.getId(), 고속터미널역.getId(), 5));

        lineService.deleteLine(이호선.getId());

        assertThat(lineService.showLines()).doesNotContain(이호선);
    }

    @DisplayName("지하철 노선에 구간을 추가한다.")
    @Test
    void addSection() {
        lineRepository.save(일호선);

        lineService.addSection(일호선.getId(), new SectionRequest(강남역.getId(), 교대역.getId(), 20));

        Section 새로운_구간 = new Section(extractSectionId(일호선), 일호선, 강남역, 교대역, 20);
        assertThat(일호선.getSections()).contains(새로운_구간);
    }

    @DisplayName("존재하지 않는 지하철 노선에 구간을 추가할 수 없다.")
    @Test
    void addSectionAtNonExistentLine() {
        assertThatThrownBy(() -> lineService.addSection(1000L, new SectionRequest(강남역.getId(), 교대역.getId(), 10)))
                .isInstanceOf(NonExistentLineException.class);
    }

    @DisplayName("지하철 노선에서 구간을 삭제한다.")
    @Test
    void deleteSection() {
        lineRepository.save(일호선);
        lineService.addSection(일호선.getId(), new SectionRequest(강남역.getId(), 교대역.getId(), 20));
        lineService.addSection(일호선.getId(), new SectionRequest(교대역.getId(), 선릉역.getId(), 20));

        lineService.deleteSection(일호선.getId(), 교대역.getId());

        assertThat(일호선.getSections()).containsExactly(new Section(일호선, 강남역, 선릉역, 40));
    }

    @DisplayName("존재하지 않는 지하철 노선에서 구간을 삭제할 수 없다.")
    @Test
    void deleteSectionAtNonExistentLine() {
        assertThatThrownBy(() -> lineService.deleteSection(1000L, 1000L))
                .isInstanceOf(NonExistentLineException.class);
    }

    private Long extractSectionId(Line line) {
        return line.getSections().get(0).getId();
    }
}
