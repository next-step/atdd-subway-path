package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.handler.exception.SectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static nextstep.subway.domain.factory.EntityFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineService lineService;

    private Station 강남역;
    private Station 선릉역;
    private Station 역삼역;
    private Line 이호선;

    @BeforeEach
    void init() {
        강남역 = createStation("강남역");
        stationRepository.save(강남역);
        선릉역 = createStation("선릉역");
        stationRepository.save(선릉역);
        역삼역 = createStation("역삼역");
        stationRepository.save(역삼역);

        이호선 = createLine("2호선", "green", 강남역, 선릉역, 10);
        lineRepository.save(이호선);
    }

    @DisplayName("구간 삭제를 처리한다. - 종점을 삭제하는 경우")
    @Test
    void deleteSection() {
        // given
        이호선.addSection(createSection(이호선, 선릉역, 역삼역, 7));
        lineRepository.save(이호선);

        // when
        lineService.deleteSection(이호선.getId(), 역삼역.getId());

        // then
        Line line = lineRepository.findById(이호선.getId()).get();
        assertThat(line.getDownStationName()).isEqualTo("선릉역");
    }

    @DisplayName("구간 삭제를 처리한다. - 최상행역을 삭제하는 경우")
    @Test
    void deleteSection2() {
        // given
        이호선.addSection(createSection(이호선, 선릉역, 역삼역, 7));
        lineRepository.save(이호선);

        // when
        lineService.deleteSection(이호선.getId(), 강남역.getId());

        // then
        Line line = lineRepository.findById(이호선.getId()).get();
        assertThat(line.getUpStationName()).isEqualTo("선릉역");
    }

    @DisplayName("구간 삭제를 처리한다. - 중간역을 삭제하는 경우")
    @Test
    void deleteSection3() {
        // given
        이호선.addSection(createSection(이호선, 선릉역, 역삼역, 7));
        lineRepository.save(이호선);

        // when
        lineService.deleteSection(이호선.getId(), 선릉역.getId());

        // then
        Line line = lineRepository.findById(이호선.getId()).get();
        assertThat(line.getSectionSize()).isEqualTo(1);
    }

    @DisplayName("구간 삭제를 처리한다. - 노선에 구간이 1개인 경우")
    @Test
    void deleteSection4() {
        // when/then
        assertThatThrownBy(() -> lineService.deleteSection(이호선.getId(), 선릉역.getId()))
                .isInstanceOf(SectionException.class);
    }
}
