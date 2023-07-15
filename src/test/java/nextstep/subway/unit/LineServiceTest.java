package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;
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

@SpringBootTest
@Transactional
@Sql(scripts = {
        "classpath:/script/01.InsertStation.sql",
        "classpath:/script/02.InsertLine.sql",
        "classpath:/script/03.InsertSection.sql",
})
public class LineServiceTest {

    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    static Long LINE_ID_1 = 1L;
    static String LINE_NAME_1 = "분당선";
    static String LINE_COLOR_1 = "Yellow";
    static String LINE_NAME_UPDATE = "신분당선";
    static String LINE_COLOR_UPDATE = "RED";
    static Long LINE_ID_1000 = 1000L;
    static Long STATION_ID_1 = 1L;
    static Long STATION_ID_2 = 2L;
    static Long STATION_ID_3 = 3L;
    static int DISTANCE_10 = 10;

    @Test
    @DisplayName("새로운 노선을 저장")
    void saveLine() {
        // given
        LineRequest request = new LineRequest(LINE_NAME_1, LINE_COLOR_1, STATION_ID_1, STATION_ID_2,
                DISTANCE_10);

        // when
        LineResponse response = lineService.saveLine(request);

        // then
        assertThat(response.getName()).isNotNull();
        assertThat(response.getName()).isEqualTo(LINE_NAME_1);
    }

    @Test
    @DisplayName("기존 노선을 수정")
    void updateLine() {
        // given
        LineRequest request = new LineRequest(LINE_NAME_UPDATE, LINE_COLOR_UPDATE);

        // when
        lineService.updateLine(LINE_ID_1000, request);

        // then
        Line line = lineRepository.findById(LINE_ID_1000).orElseThrow();

        assertThat(line.getName()).isEqualTo(LINE_NAME_UPDATE);
        assertThat(line.getColor()).isEqualTo(LINE_COLOR_UPDATE);
    }

    @Test
    @DisplayName("기존 노선을 삭제")
    void deleteLine() {
        // when
        lineService.deleteLine(LINE_ID_1000);

        // then
        assertThatThrownBy(() -> lineRepository.findById(LINE_ID_1000).orElseThrow())
                .isInstanceOf(NoSuchElementException.class);
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
