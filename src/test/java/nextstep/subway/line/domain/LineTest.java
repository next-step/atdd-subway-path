package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.subway.line.exception.AlreadyIncludedAllStationException;
import nextstep.subway.line.exception.NotExistStationException;
import nextstep.subway.line.exception.TooLongDistanceException;
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

    @DisplayName("지하철 목록을 조회")
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

    @DisplayName("지하철 노선 마지막 하행에 역을 추가")
    @Test
    void addSectionLastDownStation() {
        // given
        Station 판교역 = new Station("판교역");
        int expectedSize = 신분당선.getStations().size() + 1;

        // when
        신분당선.addSection(Section.of(신분당선, 역삼역, 판교역, 3));

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(expectedSize);
        assertThat(신분당선.getStations())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(Arrays.asList(강남역, 역삼역, 판교역));
    }

    @DisplayName("지하철 노선 제일 앞 상행에 역을 추가")
    @Test
    void addSectionFirstUpStation() {
        // given
        Station 판교역 = new Station("판교역");
        int expectedSize = 신분당선.getStations().size() + 1;

        // when
        신분당선.addSection(Section.of(신분당선, 판교역, 강남역, 5));

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(expectedSize);
        assertThat(신분당선.getStations())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(Arrays.asList(판교역, 강남역, 역삼역));
    }

    @DisplayName("지하철 노선 중간에 역을 추가")
    @Test
    void addSectionInMiddle() {
        // given
        Station 판교역 = new Station("판교역");
        Station 삼성역 = new Station("삼성역");
        int expectedSize = 신분당선.getStations().size() + 2;

        // when
        신분당선.addSection(Section.of(신분당선, 강남역, 판교역, 3));
        신분당선.addSection(Section.of(신분당선, 삼성역, 역삼역, 1));

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(expectedSize);
        assertThat(신분당선.getStations())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(Arrays.asList(강남역, 판교역, 삼성역, 역삼역));
    }

    @DisplayName("지하철 노선 중간에 역 추가 시 구간의 길이가 기존의 길이보다 크거나 같으면 에러 발생")
    @Test
    void addSectionInMiddleTooLongDistance() {
        // given
        Station 판교역 = new Station("판교역");

        // when, then
        assertThatThrownBy(() -> 신분당선.addSection(Section.of(신분당선, 판교역, 역삼역, 5)))
            .isInstanceOf(TooLongDistanceException.class);
    }

    @DisplayName("구간 등록 시 상행역과 하행역이 모두 등록되어 있으면 에러 발생")
    @Test
    void addSectionAlreadyIncludedAllStation() {
        // when, then
        assertThatThrownBy(() -> 신분당선.addSection(Section.of(신분당선, 강남역, 역삼역, 3)))
            .isInstanceOf(AlreadyIncludedAllStationException.class);
    }

    @DisplayName("구간 등록 시 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 에러 발생")
    @Test
    void addSectionNotIncludedAllStation() {
        // given
        Station 판교역 = new Station("판교역");
        Station 삼성역 = new Station("삼성역");

        // when, then
        assertThatThrownBy(() -> 신분당선.addSection(Section.of(신분당선, 삼성역, 판교역, 3)))
            .isInstanceOf(NotExistStationException.class);
    }

    @DisplayName("지하철 노선의 제일 앞 상행역을 삭제")
    @Test
    void removeSectionFirstUpStation() {
        // given
        Station 판교역 = new Station("판교역");
        신분당선.addSection(Section.of(신분당선, 역삼역, 판교역, 3));

        // when
        신분당선.removeSection(강남역);

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선의 종착역을 삭제")
    @Test
    void removeSectionLastDownStation() {
        // given
        Station 판교역 = new Station("판교역");
        신분당선.addSection(Section.of(신분당선, 역삼역, 판교역, 3));

        // when
        신분당선.removeSection(판교역);

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선의 중간에 위치한 역을 삭제")
    @Test
    void removeSectionInMiddle() {
        // given
        Station 판교역 = new Station("판교역");
        Station 삼성역 = new Station("삼성역");
        int expectedSize = 신분당선.getStations().size() + 1;

        신분당선.addSection(Section.of(신분당선, 강남역, 판교역, 3));
        신분당선.addSection(Section.of(신분당선, 삼성역, 역삼역, 1));

        // when
        신분당선.removeSection(판교역);

        // then
        assertThat(신분당선.getStations().size()).isEqualTo(expectedSize);
        assertThat(신분당선.getStations())
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactlyElementsOf(Arrays.asList(강남역, 삼성역, 역삼역));
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
        // when, then
        assertThatThrownBy(() -> 신분당선.removeSection(역삼역))
            .isInstanceOf(TooLowLengthSectionsException.class);
    }
}
