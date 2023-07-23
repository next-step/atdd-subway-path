package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@Sql(scripts = {"classpath:/script/01.InsertStation.sql", "classpath:/script/02.InsertLine.sql", "classpath:/script/03.InsertSection.sql",})
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    static Long LINE_ID_1 = 1L;
    static String 분당선 = "분당선";
    static String 노란색 = "Yellow";
    static String 신분당선 = "신분당선";
    static String 빨간색 = "RED";
    static Long LINE_ID_1000 = 1000L;
    static Long STATION_ID_1 = 1L;
    static Long STATION_ID_2 = 2L;
    static Long STATION_ID_3 = 3L;
    static int DISTANCE_10 = 10;

    @Test
    @DisplayName("새로운 노선을 저장")
    void saveLine() {
        // given
        LineRequest request = LineRequest.builder()
                .name(분당선).color(노란색).upStationId(STATION_ID_1).downStationId(STATION_ID_2).distance(DISTANCE_10).build();

        // when
        LineResponse response = lineService.saveLine(request);

        // then
        assertThat(response.getName()).isNotNull();
        assertThat(response.getName()).isEqualTo(분당선);
    }

    @Test
    @DisplayName("기존 노선을 수정")
    void updateLine() {
        // given
        LineRequest request = LineRequest.builder()
                .name(신분당선).color(빨간색).build();

        // when
        lineService.updateLine(LINE_ID_1000, request);

        // then
        Line line = lineRepository.findById(LINE_ID_1000).orElseThrow();

        assertThat(line.getName()).isEqualTo(신분당선);
        assertThat(line.getColor()).isEqualTo(빨간색);
    }

    @Test
    @DisplayName("기존 노선을 삭제")
    void deleteLine() {
        // when
        lineService.deleteLine(LINE_ID_1000);

        // then
        assertThatThrownBy(() -> lineRepository.findById(LINE_ID_1000).orElseThrow()).isInstanceOf(NoSuchElementException.class);
    }


    @Test
    @DisplayName("기조 노선에 구간을 추가")
    void addSection() {
        // given
        SectionRequest request = new SectionRequest(STATION_ID_2, STATION_ID_3, DISTANCE_10);

        // when
        lineService.addSection(LINE_ID_1000, request);

        // then
        Line line = lineRepository.findById(LINE_ID_1000).orElseThrow();
        // then
        assertThat(line).isNotNull();
        Section lastSection = line.getSections().get(line.getSections().size() - 1);
        assertThat(lastSection.getUpStation().getId()).isEqualTo(STATION_ID_2);
        assertThat(lastSection.getDownStation().getId()).isEqualTo(STATION_ID_3);
        assertThat(lastSection.getDistance()).isEqualTo(DISTANCE_10);
    }
}
