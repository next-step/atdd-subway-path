package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LineTest {
    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    Station 강남역;
    Station 양재역;
    Station 광교역;
    Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        양재역 = stationRepository.save(new Station("양재역"));
        광교역 = stationRepository.save(new Station("광교역"));
        신분당선 = lineRepository.save(new Line("신분당선", "bg-red-600"));
    }

    @DisplayName("구간 목록 마지막에 새로운 구간을 추가할 경우")
    @Test
    void addSection() {

        //when
        신분당선.addSection(강남역, 양재역, 10);

        //then
        assertThat(신분당선.getSections()).hasSize(1);
        assertThat(신분당선.getSections().get(0).getUpStation().getName()).isEqualTo("강남역");
        assertThat(신분당선.getSections().get(0).getDownStation().getName()).isEqualTo("양재역");
    }

    @DisplayName("노선에 속해있는 역 목록 조회")
    @Test
    void getStations() {
        //given
        신분당선.addSection(강남역, 양재역, 10);

        //when
        List<Station> stations = 신분당선.getStations();

        //then
        assertThat(stations).hasSize(2);
    }

    @DisplayName("구간이 목록에서 마지막 역 삭제")
    @Test
    void removeSection() {
        //given
        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(양재역, 광교역, 10);

        //when
        신분당선.removeSection(광교역);

        //then
        assertThat(신분당선.getStations()).hasSize(2);
    }
}
