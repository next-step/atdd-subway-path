package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    @DisplayName("구간을 추가하면 해당 구간의 역을 조회할 수 있다.")
    void addSection() {
        // given
        Station gangnam = createStation("강남역");
        Station yeoksam = createStation("역삼역");

        Line line = createLine("2호선", "bg-green-600");

        // when
        SectionRequest sectionRequest = new SectionRequest(gangnam.getId(), yeoksam.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);

        // then
        assertThat(line.getStations()).containsOnly(gangnam, yeoksam);
    }

    @Test
    @DisplayName("구간을 삭제하면 해당 구간의 하행역을 조회할 수 없다.")
    void deleteSection() {
        // given
        Station gangnam = createStation("강남역");
        Station yeoksam = createStation("역삼역");
        Station seolleung = createStation("선릉역");

        Line line = createLine("2호선", "bg-green-600");

        lineService.addSection(line.getId(), new SectionRequest(gangnam.getId(), yeoksam.getId(), 10));
        lineService.addSection(line.getId(), new SectionRequest(yeoksam.getId(), seolleung.getId(), 10));

        // when
        lineService.deleteSection(line.getId(), seolleung.getId());

        // then
        assertThat(line.getStations()).doesNotContain(seolleung);
    }

    @Test
    @DisplayName("삭제하려는 구간은 마지막 구간만 가능하다.")
    void deleteSection_invalid_station() {
        // given
        Station gangnam = createStation("강남역");
        Station yeoksam = createStation("역삼역");
        Station seolleung = createStation("선릉역");

        Line line = createLine("2호선", "bg-green-600");

        lineService.addSection(line.getId(), new SectionRequest(gangnam.getId(), yeoksam.getId(), 10));
        lineService.addSection(line.getId(), new SectionRequest(yeoksam.getId(), seolleung.getId(), 10));

        // when
        assertThatThrownBy(() -> lineService.deleteSection(line.getId(), yeoksam.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private Station createStation(String stationName) {
        Station station = new Station(stationName);
        stationRepository.save(station);
        return station;
    }

    private Line createLine(String name, String color) {
        Line line = new Line(name, color);
        lineRepository.save(line);
        return line;
    }
}
