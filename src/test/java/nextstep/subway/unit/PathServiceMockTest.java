package nextstep.subway.unit;

import nextstep.subway.applicaion.PathService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayName("경로 조회 Service 테스트")
@SpringBootTest
@Transactional
public class PathServiceMockTest {


    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private StationService stationService;

    private Line 이호선;
    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;
    private Station 삼성역;
    private Section 강남_역삼_구간;
    private Section 역삼_선릉_구간;

    @BeforeEach
    public void setUp() {
        이호선 = new Line("이호선", "#29832");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        삼성역 = new Station("삼성역");
        강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 10);
        역삼_선릉_구간 = new Section(이호선, 역삼역, 선릉역, 5);
    }

    @Test
    @DisplayName("지하철 경로 조회")
    void searchPath(){
        // given
        강남역 = stationRepository.save(강남역);
        역삼역 = stationRepository.save(역삼역);
        선릉역 = stationRepository.save(선릉역);

        이호선 = lineRepository.save(이호선);
        이호선.addSection(강남_역삼_구간);
        이호선.addSection(역삼_선릉_구간);


        // when
        PathService pathService = new PathService(stationService, lineRepository);
        PathResponse pathResponse = pathService.searchPath(강남역.getId(), 선릉역.getId());

        // then
        assertThat(pathResponse.getDistance()).isEqualTo(15);
    }

}
