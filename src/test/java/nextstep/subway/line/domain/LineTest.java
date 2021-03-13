package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.exception.AlreadyExistDownStationException;
import nextstep.subway.line.exception.NotLastStationException;
import nextstep.subway.line.exception.NotMatchedUpStationException;
import nextstep.subway.line.exception.TooLowLengthSectionsException;
import nextstep.subway.station.domain.Station;

public class LineTest {

    private Station 강남역;
    private Station 역삼역;
    private Line 신분당선;

    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        신분당선 = new Line("신분당선", "red", 강남역, 역삼역, 5);
    }

    @Test
    void getStations() {
        // given
        Station 판교역 = new Station("판교역");
        신분당선.addSection(Section.of(신분당선, 역삼역, 판교역, 3));

        // when
        final List<Station> stations = 신분당선.getStations();

        // then
        assertThat(stations.size()).isEqualTo(3);
        assertThat(stations)
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(Arrays.asList(강남역, 역삼역, 판교역));
    }

    @Test
    void addSection() {
        // given
        Station 판교역 = new Station("판교역");
        int expectedSize = 신분당선.getStations().size() + 1;

        // when
        신분당선.addSection(Section.of(신분당선, 역삼역, 판교역, 3));

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(expectedSize);
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
        // given
        Station 판교역 = new Station("판교역");

        // when, then
        assertThatThrownBy(() -> 신분당선.addSection(Section.of(신분당선, 강남역, 판교역, 3)))
            .isInstanceOf(NotMatchedUpStationException.class);
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        // when, then
        assertThatThrownBy(() -> 신분당선.addSection(Section.of(신분당선, 역삼역, 강남역, 4)))
            .isInstanceOf(AlreadyExistDownStationException.class);
    }

    @Test
    void removeSection() {
        // given
        Station 판교역 = new Station("판교역");
        신분당선.addSection(Section.of(신분당선, 역삼역, 판교역, 3));

        // when
        신분당선.removeSection(판교역);

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(2);
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // when, then
        assertThatThrownBy(() -> 신분당선.removeSection(역삼역))
            .isInstanceOf(TooLowLengthSectionsException.class);
    }

    @DisplayName("삭제하는 역이 마지막 역이 아니면 에러 발생")
    @Test
    void removeSectionNotMatchedLastStation() {
        // given
        Station 판교역 = new Station("판교역");
        신분당선.addSection(Section.of(신분당선, 역삼역, 판교역, 3));

        // when, then
        assertThatThrownBy(() -> 신분당선.removeSection(강남역))
            .isInstanceOf(NotLastStationException.class);
    }
}
