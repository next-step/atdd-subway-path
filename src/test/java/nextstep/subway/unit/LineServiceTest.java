package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.DeleteSectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional()
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;

    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    private Station 선릉역;

    private Station 삼성역;

    private Station 종합운동장역;

    private Line _2호선;

    @BeforeEach
    void setUp() {
        선릉역 = stationRepository.save(new Station("선릉역"));
        삼성역 = stationRepository.save(new Station("삼성역"));
        종합운동장역 = stationRepository.save(new Station("종합운동장역"));
        _2호선 = lineRepository.save(new Line("2호선", "bg-green-600"));
    }

    @DisplayName("구간을 추가할 수 있다.")
    @Test
    void addSection() {
        // given
        final var sectionRequest = new SectionRequest(선릉역.getId(), 삼성역.getId(), 10);

        // when
        lineService.addSection(_2호선.getId(), sectionRequest);

        // then
        assertThat(_2호선.findAllStations()).containsExactly(선릉역, 삼성역);
    }

    @DisplayName("하행 종착역 구간을 삭제할 수 있다.")
    @Test
    void deleteSection() {
        // given
        lineService.addSection(_2호선.getId(), new SectionRequest(선릉역.getId(), 삼성역.getId(), 10));
        lineService.addSection(_2호선.getId(), new SectionRequest(삼성역.getId(), 종합운동장역.getId(), 6));

        // when
        lineService.deleteSection(_2호선.getId(), 종합운동장역.getId());

        // then
        assertThat(showAllStationNames()).containsExactly("선릉역", "삼성역");
    }

    private List<String> showAllStationNames() {
        return lineService.showLines().stream()
                .map(LineResponse::getStations)
                .flatMap(List::stream)
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }

    @DisplayName("삭제하고자 하는 구간이 상행 종착역과 하행 종착역만이 있다면 삭제 시 에러가 발생한다.")
    @Test
    void deleteExceptionWhenOnlyOneSectionExist() {
        // given
        lineService.addSection(_2호선.getId(), new SectionRequest(선릉역.getId(), 삼성역.getId(), 10));

        // when
        assertThatThrownBy(() -> lineService.deleteSection(_2호선.getId(), 삼성역.getId()))
                .isInstanceOf(DeleteSectionException.class)
                .hasMessage("상행역과 하행역만 존재하기 때문에 삭제할 수 없습니다.");
    }

    @DisplayName("삭제하고자 하는 역이 하행 종착역이 아니면 에러가 발생한다.")
    @Test
    void deleteExceptionWhenStationIsNotDownStation() {
        // given
        lineService.addSection(_2호선.getId(), new SectionRequest(선릉역.getId(), 삼성역.getId(), 10));
        lineService.addSection(_2호선.getId(), new SectionRequest(삼성역.getId(), 종합운동장역.getId(), 6));

        // when, then
        assertThatThrownBy(() -> lineService.deleteSection(1L, 0L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
