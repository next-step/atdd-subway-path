package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.PathFinderException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class PathServiceTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineService lineService;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private PathService pathService;

    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Station 청명역;
    private Station 영통역;
    
    private Line 강남_2호선;
    private Line 수인_분당선;
    private Line 강남_청명선;
    private Line 삼성_영통선;

    /**
     *          *강남_2호선*
     * 강남역  --- 역삼역      ---    삼성역
     * |                                 |
     * *강남_청명선*                *삼성_영통선*
     * |                                 |
     * 청명역  --- *수인_분당선* ---   영통역
     */
    @BeforeEach
    void setUp() {
        강남_2호선 = lineRepository.save(new Line("강남_2호선", "green"));
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        삼성역 = stationRepository.save(new Station("삼성역"));
        lineService.addSection(강남_2호선.getId(), new SectionRequest(강남역.getId(), 역삼역.getId(), 10));
        lineService.addSection(강남_2호선.getId(), new SectionRequest(역삼역.getId(), 삼성역.getId(), 15));

        수인_분당선 = lineRepository.save(new Line("수인_분당선", "yellow"));
        청명역 = stationRepository.save(new Station("청명역"));
        영통역 = stationRepository.save(new Station("영통역"));
        lineService.addSection(수인_분당선.getId(), new SectionRequest(청명역.getId(), 영통역.getId(), 20));

        강남_청명선 = lineRepository.save(new Line("강남_청명선", "blue"));
        lineService.addSection(강남_청명선.getId(), new SectionRequest(강남역.getId(), 청명역.getId(), 7));

        삼성_영통선 = lineRepository.save(new Line("삼성_영통선", "black"));
        lineService.addSection(삼성_영통선.getId(), new SectionRequest(삼성역.getId(), 영통역.getId(), 3));
    }

    @Test
    void 경로를_조회한다() {
        PathResponse response = pathService.findPath(강남역.getId(), 삼성역.getId());
        List<String> stationNames = response.getStations().stream().map(station -> station.getName()).collect(Collectors.toList());

        assertThat(stationNames).containsExactly(강남역.getName(), 역삼역.getName(), 삼성역.getName());
        assertThat(response.getDistance()).isEqualTo(25);
    }

    @Test
    void 출발역과_도착역이_같은_경우_예외가_발생한다() {
        assertThatThrownBy(() -> pathService.findPath(강남역.getId(), 강남역.getId()))
                .isInstanceOf(PathFinderException.class)
                .hasMessage("출발역과 도착역은 같을 수 없습니다.");
    }

    @Test
    void 출발역과_도착역이_연결_되어_있지_않은_경우_예외가_발생한다() {
        Station 사당역 = stationRepository.save(new Station("사당역"));

        assertThatThrownBy(() -> pathService.findPath(강남역.getId(), 사당역.getId()))
                .isInstanceOf(PathFinderException.class)
                .hasMessage("출발역과 도착역이 연결되어 있지 않습니다.");
    }

    @Test
    void 존재하지_않은_출발역을_조회_할_경우_예외가_발생한다() {
        assertThatThrownBy(() -> pathService.findPath(강남역.getId(), 100L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지_않은_도착역을_조회_할_경우_예외가_발생한다() {
        assertThatThrownBy(() -> pathService.findPath(100L, 역삼역.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
