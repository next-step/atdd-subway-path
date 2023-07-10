package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DisplayName("LineService 단위 테스트 (spring integration test)")
@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    /**
     * Given 노선이 있을 때
     * When 구간을 추가하면
     * Then 구간을 조회 할 수 있다.
     */
    @DisplayName("노선에 구간을 추가한다")
    @Test
    void addSection() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        final int distance = 10;

        // when
        SectionRequest request = new SectionRequest(강남역.getId(), 역삼역.getId(), distance);
        lineService.addSection(이호선.getId(), request);

        // then
        assertThat(이호선.getSections().size()).isEqualTo(1);
    }


    /**
     * Given 2개의 구간을 가진 노선이 있을때
     * When 노선을 1개 삭제하면
     * Then 1개의 구간을 가진 노선이 된다.
     */
    @DisplayName("노선의 구간을 삭제한다.")
    @Test
    void deleteSection() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Station 선릉역 = stationRepository.save(new Station("선릉역"));
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        final int distance = 10;

        SectionRequest 첫번째구간_요청 = new SectionRequest(강남역.getId(), 역삼역.getId(), distance);
        lineService.addSection(이호선.getId(), 첫번째구간_요청);
        SectionRequest 두번째구간_요청 = new SectionRequest(역삼역.getId(), 선릉역.getId(), distance);
        lineService.addSection(이호선.getId(), 두번째구간_요청);

        // when
        lineService.deleteSection(이호선.getId(), 선릉역.getId());

        // then
        List<Section> sections = lineService.findByLineId(이호선.getId()).getSections();
        assertThat(sections.size()).isEqualTo(1);

    }

    /**
     * Given 노선이 있을 때
     * When 노선의 정보를 변경하면
     * Then 노선의 정보가 변경 된 것을 죄회로 확인할 수 있다.
     */
    @DisplayName("노선의 정보를 변경한다")
    @Test
    void updateLine() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        final int distance = 10;
        SectionRequest request = new SectionRequest(강남역.getId(), 역삼역.getId(), distance);
        lineService.addSection(이호선.getId(), request);

        // when
        final String changeName = "3호선";
        final String changeColor = "bg-amber-600";
        LineRequest updateRequest = new LineRequest(changeName, changeColor, 강남역.getId(), 역삼역.getId(), distance);
        lineService.updateLine(이호선.getId(), updateRequest);

        // then
        Line line = lineService.findByLineId(이호선.getId());
        assertThat(line.getName()).isEqualTo(changeName);

    }

    /**
     * Given 노선이 있을 때
     * When 노선을 삭제 하면
     * Then 노선이 조회되지 않는다.
     */
    @DisplayName("노선을 삭제한다")
    @Test
    void deleteLine() {
        // given
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        final int distance = 10;
        SectionRequest request = new SectionRequest(강남역.getId(), 역삼역.getId(), distance);
        lineService.addSection(이호선.getId(), request);

        // when
        lineService.deleteLine(이호선.getId());

        // then
        assertThatThrownBy(() -> lineService.findByLineId(이호선.getId())).isInstanceOf(EntityNotFoundException.class);


    }

    /**
     * When 노선을 저장하면
     * Then 노선이 저장된 것을 조회로 확인할 수 있다.
     */
    @DisplayName("노선을 저장한다.")
    @Test
    void saveLine() {
        // when
        Station 강남역 = stationRepository.save(new Station("강남역"));
        Station 역삼역 = stationRepository.save(new Station("역삼역"));
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
        final int distance = 10;
        SectionRequest request = new SectionRequest(강남역.getId(), 역삼역.getId(), distance);
        lineService.addSection(이호선.getId(), request);

        // then
        Line foundLine = lineService.findByLineId(이호선.getId());
        assertThat(foundLine).isNotNull();
    }
}
