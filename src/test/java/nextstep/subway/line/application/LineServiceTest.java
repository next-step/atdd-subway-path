package nextstep.subway.line.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.exception.BothStationAlreadyEnrolledException;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.NoSuchElementException;

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

    Station station1;
    Station station2;
    Station station3;
    Line line;

    @BeforeEach
    void setUp() {
        station1 = stationRepository.save(new Station("강남역1"));
        station2 = stationRepository.save(new Station("선릉역2"));
        station3 = stationRepository.save(new Station("역삼역3"));
        line = lineRepository.save(new Line("2호선", "green", station1, station2, 10));
    }

    @Test
    @DisplayName("섹션을 노선에 추가(append)")
    void 섹션을_노선_마지막에_추가() {
        // when
        lineService.addSection(line.getId(), new SectionRequest(station2.getId(), station3.getId(), 10));
        // then
        assertThat(line.getSections().getSectionList().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("섹션을 노선에 추가(insert)")
    void 섹션을_노선_중간에_추가() {
        //given
        Station station4 = stationRepository.save(new Station("대청역4"));
        lineService.addSection(line.getId(), new SectionRequest(station2.getId(), station3.getId(), 10));
        //when
        lineService.addSection(line.getId(), new SectionRequest(station4.getId(), station3.getId(), 5));
        //then
        LineResponse response = lineService.findLineResponseById(line.getId());
        assertThat(response.getStations()).extracting("name")
                .containsExactlyElementsOf(Arrays.asList("강남역1", "선릉역2", "대청역4", "역삼역3"));
    }

    @Test
    @DisplayName("섹션을 노선에서 제거")
    void 섹션을_노선에서_제거() {
        //given
        Station station4 = stationRepository.save(new Station("대청역4"));
        lineService.addSection(line.getId(), new SectionRequest(station2.getId(), station3.getId(), 10));
        lineService.addSection(line.getId(), new SectionRequest(station3.getId(), station4.getId(), 5));
        //when
        lineService.removeSection(line.getId(), station2.getId());
        //then
        LineResponse response = lineService.findLineResponseById(line.getId());
        assertThat(response.getStations()).extracting("name")
                .containsExactlyElementsOf(Arrays.asList("강남역1", "역삼역3", "대청역4"));
    }

    @Test
    @DisplayName("추가하고자 하는 두 역 모두 등록되어있음")
    void 추가하는_두_역_모두_등록됨() {
        //given
        lineService.addSection(line.getId(), new SectionRequest(station2.getId(), station3.getId(), 10));
        //when

        //then
        assertThatThrownBy(() ->
                lineService.addSection(line.getId(),
                        new SectionRequest(station3.getId(), station1.getId(), 5)
                )).isInstanceOf(BothStationAlreadyEnrolledException.class);
    }

    @Test
    @DisplayName("등록되지 않은 역 불러올 때 에러 발생")
    void 등록되지_않은_역_불러오기() {
        //given
        assertThatThrownBy(() -> lineService.findLineById(2L))
                .isInstanceOf(NoSuchElementException.class);
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        Line persistLine = lineRepository.findById(id).orElseThrow(RuntimeException::new);
        persistLine.update(new Line(lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }
}
