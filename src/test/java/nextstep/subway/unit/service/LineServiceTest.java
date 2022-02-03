package nextstep.subway.unit.service;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import nextstep.subway.exception.NotFoundLineException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static nextstep.subway.unit.LineUnitTestHelper.강남_TO_역삼_구간_만들기;
import static nextstep.subway.unit.LineUnitTestHelper.교대_TO_역삼_구간_만들기;
import static nextstep.subway.unit.LineUnitTestHelper.신분당선_요청_객체_만들기;
import static nextstep.subway.unit.LineUnitTestHelper.이호선_요청_객체_만들기;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    private Station 교대역;
    private Station 강남역;
    private Station 역삼역;
    private Station 정자역;
    private Station 판교역;


    @BeforeEach
    void setFixtures() {
        교대역 = stationRepository.save(new Station("교대역"));
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        정자역 = stationRepository.save(new Station("정자역"));
        판교역 = stationRepository.save(new Station("판교역"));
    }

    @Test
    void saveLine() {
        // given
        LineRequest lineRequest = 이호선_요청_객체_만들기(교대역, 역삼역);

        // when
        LineResponse lineResponse = lineService.saveLine(lineRequest);

        // then
        assertThat(lineResponse.getStations()).hasSize(2);
    }

    @Test
    void addSection() {
        // given
        SectionRequest sectionRequest = 교대_TO_역삼_구간_만들기(교대역, 역삼역);
        Line 이호선 = lineRepository.save(new Line("2호선", "bg-green-600"));

        // when
        lineService.addSection(sectionRequest, 이호선.getId());

        // then
        Sections sections = 이호선.getSections();
        Station station = sections.getDownStationEndPoint();
        assertThat(station).isEqualTo(역삼역);
    }

    @Test
    void findLineById() {
        // given
        LineRequest lineRequest = 이호선_요청_객체_만들기(교대역, 역삼역);
        LineResponse newLine = lineService.saveLine(lineRequest);

        // when
        LineResponse findLine = lineService.findLineById(newLine.getId());

        // then
        assertThat(findLine.getStations()).hasSize(2);
        assertThat(findLine.getStations().get(0).getName()).isEqualTo(교대역.getName());
    }

    @Test
    void findAllLines() {
        // given
        lineRepository.save(new Line("2호선", "bg-green-600"));
        lineRepository.save(new Line("신분당선", "bg-red-600"));

        // when
        List<LineResponse> responses = lineService.findAllLines();

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    void updateLine() {
        // given
        LineResponse 이호선 = lineService.saveLine(이호선_요청_객체_만들기(교대역, 역삼역));

        // when
        LineRequest 신분당선 = 신분당선_요청_객체_만들기(정자역, 판교역);
        lineService.updateLine(이호선.getId(), 신분당선);
        LineResponse findLine = lineService.findLineById(이호선.getId());

        // then
        assertThat(findLine.getName()).isEqualTo(신분당선.getName());
    }

    @Test
    void deleteLine() {
        // given
        LineResponse 이호선 = lineService.saveLine(이호선_요청_객체_만들기(교대역, 역삼역));

        // when
        lineService.deleteLineById(이호선.getId());

        // then
        assertThatThrownBy(() -> {
            lineService.findLineById(이호선.getId());
        }).isInstanceOf(NotFoundLineException.class);
    }

    @Test
    void deleteSection() {
        // given
        LineResponse 이호선 = lineService.saveLine(이호선_요청_객체_만들기(교대역, 역삼역));
        SectionRequest sectionRequest = 강남_TO_역삼_구간_만들기(강남역, 역삼역);
        lineService.addSection(sectionRequest, 이호선.getId());

        // when
        lineService.deleteSection(이호선.getId(), 강남역.getId());
        LineResponse findLine = lineService.findLineById(이호선.getId());

        // then
        assertThat(findLine.getStations().get(1).getName()).isEqualTo(역삼역.getName());
    }
}
