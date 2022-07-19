package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("실제 객체를 활용한 LineServiceTest")
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
    void addSection() {
        // given
        Station 광교역 = 역을_등록한다("광교역");
        Station 광교중앙역 = 역을_등록한다("광교중앙역");
        Line 신분당선 = 노선을_등록한다("신분당선", "red");

        // when
        lineService.addSection(신분당선.getId(), new SectionRequest(광교역.getId(), 광교중앙역.getId(), 10));

        // then
        assertAll(
                () -> assertThat(신분당선.getSections()).hasSize(1),
                () -> assertThat(신분당선.getSections()).containsExactly(new Section(신분당선, 광교역, 광교중앙역, 10))
        );
    }

    @Test
    void deleteSection() {
        //given
        Station 광교역 = 역을_등록한다("광교역");
        Station 광교중앙역 = 역을_등록한다("광교중앙역");
        Line 신분당선 = 노선을_등록한다("신분당선", "red");

        //when
        lineService.addSection(신분당선.getId(), new SectionRequest(광교역.getId(), 광교중앙역.getId(), 10));
        lineService.deleteSection(신분당선.getId(), 광교중앙역.getId());

        //then
        LineResponse 신분당선_응답 = lineService.findById(신분당선.getId());
        assertThat(신분당선_응답.getStations()).isEmpty();
    }

    @Test
    void saveLine() {
        //given
        Station 광교역 = 역을_등록한다("광교역");
        Station 광교중앙역 = 역을_등록한다("광교중앙역");
        Line 신분당선 = 노선을_등록한다("신분당선", "red");

        //when
        LineResponse 신분당선_응답 = lineService.saveLine(new LineRequest(신분당선.getName(), 신분당선.getColor(), 광교역.getId(), 광교중앙역.getId(), 10));

        //then
        LineResponse response = lineService.findById(신분당선.getId());
        assertAll(
                () -> assertThat(신분당선_응답.getName()).isEqualTo("신분당선"),
                () -> assertThat(신분당선_응답.getStations()).hasSize(2)
        );
    }

    @Test
    void showLines() {
        //given
        노선을_등록한다("신분당선", "red");

        //when
        List<LineResponse> 노선_응답 = lineService.showLines();

        //then
        assertAll(
                () -> assertThat(노선_응답).hasSize(1),
                () -> assertThat(노선_응답.get(0).getName()).isEqualTo("신분당선")
        );
    }

    private Line 노선을_등록한다(String lineName, String color) {
        return lineRepository.save(new Line(lineName, color));
    }

    private Station 역을_등록한다(String stationName) {
        return stationRepository.save(new Station(stationName));
    }
}
