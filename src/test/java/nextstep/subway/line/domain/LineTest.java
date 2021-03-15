package nextstep.subway.line.domain;

import nextstep.subway.line.exception.InvalidDistanceException;
import nextstep.subway.line.exception.OnlyOneSectionRemainingException;
import nextstep.subway.line.exception.StationAlreadyExistsException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LineTest {
    private Station 강남역 = new Station("강남역");
    private Station 양재역 = new Station("양재역");
    private Station 청계산입구역 = new Station("청계산입구역");
    private Station 정자역 = new Station("정자역");
    private Station 미금역 = new Station("미금역");
    private Line 신분당선 = new Line("신분당선", "bg-red-600", 강남역, 정자역, 10);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(강남역, "id", 1L);
        ReflectionTestUtils.setField(정자역, "id", 2L);
        ReflectionTestUtils.setField(미금역, "id", 3L);
    }

    @Test
    void getStations() {
        assertThat(신분당선.getStations()).isEqualTo(Arrays.asList(강남역, 정자역));
    }

    @Test
    void addSection() {
        int before = 신분당선.getSections().size();

        Section section = new Section(신분당선, 정자역, 미금역,10);
        신분당선.addSection(section);

        int after = 신분당선.getSections().size();

        assertThat(after).isEqualTo(before + 1);
    }

    @DisplayName("역과 역 사이에 새로운 역 추가")
    @Test
    void addSectionInMiddle() {
        //given
        //강남----------정자

        //when
        //강남-양재----청계산입구-----정자
        신분당선.addSection(new Section(신분당선, 양재역, 정자역, 9));
        신분당선.addSection(new Section(신분당선, 양재역, 청계산입구역,4));

        //then
        //신분당선 역 이름 확인
        assertThat(신분당선.getStations()).isEqualTo(Arrays.asList(강남역, 양재역, 청계산입구역, 정자역));
    }

    @DisplayName("새로운 역을 상행 종점으로 추가")
    @Test
    void addStationAsFirst() {
        //given
        //강남----------정자

        //when
        //고속터미널-----강남
        Station 고속터미널역 = new Station("고속터미널역");
        신분당선.addSection(new Section(신분당선, 고속터미널역, 강남역, 5));

        //then
        //신분당선 역 이름 확인
        assertThat(신분당선.getStations()).isEqualTo(Arrays.asList(고속터미널역, 강남역, 정자역));
    }


    @DisplayName("역과 역 사이에 길이가 더 긴 새로운 역 추가")
    @Test
    void addSectionInMiddle_WithBiggerDistance() {
        //given
        //강남----------정자

        //when
        //강남------------청계산입구 추가
        //then
        //InvalidDistanceException
        assertThatThrownBy(() -> {
            신분당선.addSection(new Section(신분당선, 강남역, 청계산입구역,12));
        }).isInstanceOf(InvalidDistanceException.class);
    }

    @Test
    void removeSection() {
        신분당선.addSection(new Section(신분당선, 정자역, 미금역, 7));

        int before = 신분당선.getStations().size();
        신분당선.removeSection(미금역.getId());

        int after = 신분당선.getStations().size();

        assertThat(신분당선.getStations()).doesNotContain(미금역);
        assertThat(after).isEqualTo(before - 1);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        assertThatThrownBy( () -> 신분당선.removeSection(정자역.getId())).isInstanceOf(OnlyOneSectionRemainingException.class);
    }
}
