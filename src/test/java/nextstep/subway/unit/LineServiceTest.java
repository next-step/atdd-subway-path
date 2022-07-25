package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station upStation;
    private Station downStation;
    private Line line;

    @BeforeEach
    void setUp() {
        upStation = new Station("삼성역");
        downStation = new Station("강남역");
        line = new Line("분당선", "bg-yellow-600");
    }

    @DisplayName("구간 추가")
    @Test
    void addSection() {
        // given
        stationRepository.save(upStation);
        stationRepository.save(downStation);
        lineRepository.save(line);

        // when
        lineService.addSection(line.getId(), new SectionRequest(upStation.getId(), downStation.getId(), 10));

        // then
        assertThat(line.getSections()).hasSize(1);
    }

    @DisplayName("구간 삭제")
    @Test
    void deleteSection(){
        //given
        stationRepository.save(upStation);
        stationRepository.save(downStation);
        lineRepository.save(line);
        lineService.addSection(line.getId(), new SectionRequest(upStation.getId(), downStation.getId(), 10));

        //when
        lineService.deleteSection(line.getId(), downStation.getId());

        //then
        assertThat(line.getSections()).isEmpty();
    }
}
