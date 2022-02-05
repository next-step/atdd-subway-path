package nextstep.subway.unit;

import lombok.val;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.object.Distance;
import nextstep.subway.unit.test.utils.Stations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.unit.test.utils.Lines.GTXA_연신내_판교역;
import static nextstep.subway.unit.test.utils.Lines.노선_초기화;
import static nextstep.subway.unit.test.utils.Stations.역_초기화;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @BeforeEach
    public void init() {
        역_초기화();
        노선_초기화();
    }

    /**
     * Given 연신내역, 서울역, 삼성역, 판교역을 생성하고,
     * Given 연신내와 판교역을 연결하는 GTX-A 노선을 생성한다,
     * Given 연신내와 삼성역을 연결하는 노선을 추가하고,
     * Given 연신내와 서울역을 연결하는 노선을 추가한다,
     * When getAllStations() 메소드를 호출하면,
     * Then 연신내, 서울역, 삼성역, 판교역 순서로 list가 반환된다.
     * @see nextstep.subway.domain.Line#getAllStations
     */
    @DisplayName("지하철 노선의 역 순차적으로 불러오기 테스트")
    @Test
    void getAllStations() {
        // given
        val 연신내 = stationRepository.save(Stations.연신내);
        val 서울역 = stationRepository.save(Stations.서울역);
        val 삼성역 = stationRepository.save(Stations.삼성역);
        stationRepository.save(Stations.판교역);
        Line line = lineRepository.save(GTXA_연신내_판교역);
        line.addSection(연신내, 삼성역, new Distance(8));
        line.addSection(연신내, 서울역, new Distance(4));

        // when
        val stations = line.getAllStations();

        // then
        assertThat(stations.size()).isEqualTo(4);
        assertThat(stations.get(0).getName().equals("연신내")).isTrue();
        assertThat(stations.get(1).getName().equals("서울역")).isTrue();
        assertThat(stations.get(2).getName().equals("삼성역")).isTrue();
        assertThat(stations.get(3).getName().equals("판교역")).isTrue();
    }

    /**
     * Given 연신내역, 서울역, 삼성역, 판교역을 생성하고,
     * Given 연신내와 판교역을 연결하는 GTX-A 노선을 생성한다,
     * Given 연신내와 삼성역을 연결하는 노선을 추가하고,
     * Given 연신내와 서울역을 연결하는 노선을 추가한다,
     * When getAllStations() 메소드를 호출하면,
     * Then 연신내, 서울역, 삼성역, 판교역 순서로 list가 반환된다.
     * @see nextstep.subway.domain.Line#removeSection
     */
    @DisplayName("지하철 노선의 역 삭제 테스트")
    @Test
    void removeSection() {
        // given
        val 연신내 = stationRepository.save(Stations.연신내);
        val 서울역 = stationRepository.save(Stations.서울역);
        val 삼성역 = stationRepository.save(Stations.삼성역);
        stationRepository.save(Stations.판교역);

        Line line = lineRepository.save(GTXA_연신내_판교역);
        line.addSection(연신내, 삼성역, new Distance(8));
        line.addSection(연신내, 서울역, new Distance(4));

        // when
        line.removeSection(서울역.getId());

        // then
        val stations = line.getAllStations();
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations.get(0).getName().equals("연신내")).isTrue();
        assertThat(stations.get(1).getName().equals("삼성역")).isTrue();
        assertThat(stations.get(2).getName().equals("판교역")).isTrue();
    }
}
