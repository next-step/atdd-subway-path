package nextstep.subway.line.domain;

import nextstep.subway.common.exception.ApplicationException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LineTest {

    private Station 을지로3가역;
    private Station 을지로입구역;
    private Station 시청역;
    private Station 충정로역;

    private Line 이호선;

    private final static int DEFAULT_LINE_DISTANCE = 10;
    @BeforeEach
    public void setUp() {
        // given
        을지로3가역 = new Station(1L,"을지로3가역");
        을지로입구역 = new Station(2L, "을지로입구역");
        시청역 = new Station(3L,"시청역");
        충정로역 = new Station(4L, "충정로역");

        이호선 = new Line("이호선", "green", 을지로3가역, 시청역, DEFAULT_LINE_DISTANCE);
    }

    @Test
    @DisplayName("역 목록을 조회")
    void getSortedStations() {
        List<Station> stations = 이호선.getSortedStations();

        assertThat(stations.size()).isEqualTo(2);
        assertThat(stations).containsExactlyElementsOf(Arrays.asList(을지로3가역, 시청역));
    }

    @Test
    @DisplayName("라인에 구간을 추가")
    void addSection() {
        // when
        이호선.addSection(시청역, 을지로입구역, 5);

        // then
        assertThat(이호선.getSections().getSections().size()).isEqualTo(2);
        assertThat(이호선.getSortedStations()).containsExactlyElementsOf(Arrays.asList(을지로3가역, 시청역, 을지로입구역));
    }

    @DisplayName("구간 중간에 추가할 경우 이전 distance보다 짧거나 상행역이 같으면 성공")
    @Test
    void addSectionInMiddle() {
        //when
        이호선.addSection(을지로3가역, 을지로입구역, 5);

        //then
        assertThat(이호선.getSortedStations().size()).isEqualTo(3);
        assertThat(이호선.getSections().getSections().size()).isEqualTo(2);
        assertThat(이호선.getSortedStations()).containsExactlyElementsOf(Arrays.asList(을지로3가역, 을지로입구역, 시청역));
        assertThat(이호선.getLineDistance()).isEqualTo(10);
    }

    @DisplayName("구간 중간에 추가할 경우 이전 distance보다 짧거나 상행역이 같으면 성공")
    @Test
    void addSectionInMiddle2() {
        //when
        이호선.addSection(을지로3가역, 을지로입구역, 5);
        이호선.addSection(을지로3가역, 충정로역, 2);

        //then
        assertThat(이호선.getSortedStations().size()).isEqualTo(4);
        assertThat(이호선.getSections().getSections().size()).isEqualTo(3);
        assertThat(이호선.getSortedStations()).containsExactlyElementsOf(Arrays.asList(을지로3가역, 충정로역, 을지로입구역, 시청역));
        assertThat(이호선.getLineDistance()).isEqualTo(10);
    }


    @DisplayName("이미 존재하는 상행&하행역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        //when
        Exception exception = assertThrows(ApplicationException.class, () -> {
            이호선.addSection(을지로3가역, 시청역, 5);
        });

        //then
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("상/하행역 모두 이미 등록되어있는 역입니다."));
    }

    @DisplayName("하나도 등록되지 않은 상행&하행역 추가 시 에러 발생")
    @Test
    void addSectionNotIncludeBothUpstationAndDownStation() {
        //when
        Exception exception = assertThrows(ApplicationException.class, () -> {
            이호선.addSection(을지로입구역, 충정로역, 5);
        });

        //then
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains("상/하행역 중 하나는 노선에 등록되어 있어야 합니다."));
    }


    @DisplayName("마지막 구간 삭제시 성공")
    @Test
    void removeSection() {
        //given
        이호선.addSection(을지로3가역, 을지로입구역, 5);

        //when
        이호선.removeSection(시청역);

        //then
        assertThat(이호선.getLineDistance()).isEqualTo(5);
        assertThat(이호선.getSections().getSections().size()).isEqualTo(1);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionWhenMinSectionSize() {
        //when/then
        assertThatThrownBy(() -> 이호선.removeSection(시청역))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("구간은 최소하나는 등록되어있어야 합니다.");
    }

    @DisplayName("등록되지 않은 역을 삭제시 에러 발생")
    @Test
    void removeSectionNotRegistered() {
        //given
        이호선.addSection(시청역, 을지로입구역, 10);

        //when/then
        assertThatThrownBy(() -> 이호선.removeSection(충정로역))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("구간에 등록되어있지 않은 역입니다.");
    }

    @DisplayName("중간 역 삭제시 성공")
    @Test
    void removeSectionInMiddle() {
        //given
        이호선.addSection(시청역, 을지로입구역, 5);

        //when
        이호선.removeSection(시청역);

        //then
        assertThat(이호선.getLineDistance()).isEqualTo(DEFAULT_LINE_DISTANCE+5);
        assertThat(이호선.getSections().getSections().size()).isEqualTo(1);
    }

    @DisplayName("상행 역 삭제시 성공")
    @Test
    void removeSectionInUpStation() {
        //given
        이호선.addSection(시청역, 을지로입구역, 5);

        //when
        이호선.removeSection(을지로3가역);

        //then
        assertThat(이호선.getLineDistance()).isEqualTo(5);
        assertThat(이호선.getSections().getSections().size()).isEqualTo(1);
    }
}
