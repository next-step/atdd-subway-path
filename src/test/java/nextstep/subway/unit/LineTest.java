package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {

    Station 강남역;
    Station 양재역;
    Station 광교역;
    Station 수지구청역;
    Station 왕십리역;
    Line 신분당선;
    Line 경춘선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        양재역 = new Station("양재역");
        광교역 = new Station("광교역");
        수지구청역 = new Station("수지구청역");
        왕십리역 = new Station("왕십리역");
        신분당선 = new Line("신분당선", "bg-red-600");
        경춘선 = new Line("경춘선", "bg-green-600");

        ReflectionTestUtils.setField(강남역,"id",1L);
        ReflectionTestUtils.setField(양재역,"id",2L);
        ReflectionTestUtils.setField(광교역,"id",3L);
        ReflectionTestUtils.setField(수지구청역,"id",4L);
        ReflectionTestUtils.setField(신분당선,"id",1L);
        ReflectionTestUtils.setField(경춘선,"id",2L);
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

    @DisplayName("기존 구간에 새로운 구간을 추가 (A-C + A-B = A+B+C)")
    @Test
    void addSectionCase1_기존구간에_새로운구간() {
        //when
        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(강남역, 광교역, 5);

        //then
        assertThat(신분당선.getSections()).hasSize(2);
        assertThat(신분당선.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(신분당선.getStations().get(1).getName()).isEqualTo("광교역");
        assertThat(신분당선.getStations().get(2).getName()).isEqualTo("양재역");
        assertThat(신분당선.getSections().get(0).getDistance()).isEqualTo(5);
        assertThat(신분당선.getSections().get(1).getDistance()).isEqualTo(5);
    }

    @DisplayName("처음 구간에 새로운 구간을 추가 (B-C + A-B = A+B+C)")
    @Test
    void addSectionCase2_새로운_역이_상행종점() {
        //when
        신분당선.addSection(양재역, 광교역, 10);
        신분당선.addSection(강남역, 양재역, 5);

        //then
        assertThat(신분당선.getSections()).hasSize(2);
        assertThat(신분당선.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(신분당선.getStations().get(1).getName()).isEqualTo("양재역");
        assertThat(신분당선.getStations().get(2).getName()).isEqualTo("광교역");
    }

    @DisplayName("마지막 구간에 새로운 구간을 추가 (A-B + B-C = A+B+C)")
    @Test
    void addSectionCase3_새로운_역이_하행종점() {
        //when
        신분당선.addSection(강남역, 양재역, 10);
        신분당선.addSection(양재역, 광교역, 5);

        //then
        assertThat(신분당선.getSections()).hasSize(2);
        assertThat(신분당선.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(신분당선.getStations().get(1).getName()).isEqualTo("양재역");
        assertThat(신분당선.getStations().get(2).getName()).isEqualTo("광교역");
    }

    @DisplayName("기존 역 사이 길이보다 크거나 같음 - 실패")
    @Test
    void addSectionException_기존_역_사이_길이보다_크거나_같음_상행역() {
        //when
        //then
        assertThrows(RuntimeException.class, () -> {
            신분당선.addSection(강남역, 양재역, 10);
            신분당선.addSection(강남역, 수지구청역, 10);
        });
    }

    @DisplayName("기존 역 사이 길이보다 크거나 같음 - 실패")
    @Test
    void addSectionException_기존_역_사이_길이보다_크거나_같음_하행역() {
        //when
        //then
        assertThrows(RuntimeException.class, () -> {
            신분당선.addSection(강남역, 양재역, 10);
            신분당선.addSection(수지구청역, 양재역, 10);
        });
    }

    @DisplayName("이미 모두 노선에 등록되어있음 - 실패")
    @Test
    void addSectionException_이미_모두_노선에_등록되어있음() {
        //when
        //then
        assertThrows(RuntimeException.class, () -> {
            신분당선.addSection(강남역, 양재역, 10);
            신분당선.addSection(강남역, 양재역, 5);
        });
    }

    @DisplayName("어떤 역에도 포함되어있지 않음 - 실패")
    @Test
    void addSectionException_역이_포함되어있지않음() {
        //when
        //then
        assertThrows(RuntimeException.class, () -> {
            신분당선.addSection(강남역, 양재역, 10);
            신분당선.addSection(수지구청역, 광교역, 5);
        });
    }

    @DisplayName("노선 삭제 실패 - 구간이 1개 남아있음")
    @Test
    void deleteSectionExceptionOneSection() {
        assertThrows(RuntimeException.class, () -> {
            신분당선.addSection(강남역, 양재역, 10);
            신분당선.removeSection(강남역);
        });
    }

    @DisplayName("노선 삭제 실패 - 라인에 요청한 역이 존재하지 않음")
    @Test
    void deleteSectionExceptionNoStation() {
        assertThrows(RuntimeException.class, () -> {
            신분당선.addSection(강남역, 양재역, 10);
            신분당선.addSection(양재역, 광교역, 10);
            경춘선.addSection(왕십리역, 수지구청역, 10);
            신분당선.removeSection(수지구청역);
        });
    }
}
