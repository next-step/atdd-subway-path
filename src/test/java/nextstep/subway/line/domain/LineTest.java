package nextstep.subway.line.domain;

import nextstep.subway.line.exception.DownStationExistedException;
import nextstep.subway.line.exception.NotValidUpStationException;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class LineTest {
    private Station 강남역;
    private Station 역삼역;
    private Station 삼성역;
    private Line 이호선;
    
    @BeforeEach
    void setUp() {
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        삼성역 = new Station("삼성역");
        이호선 = new Line("2호선", "green", 강남역, 역삼역, 10);
    }

    @DisplayName("노선의 역들을 조회할 수 있어야 한다.")
    @Test
    void getStations() {
        // given
        이호선.addSection(new Section(이호선, 역삼역, 삼성역, 10));
        List<Station> expected = Arrays.asList(강남역, 역삼역, 삼성역);

        // when
        List<Station> actual = 이호선.getStations();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("노선에 구간을 추가하면, 노선의 크기가 증가한다.")
    @Test
    void addSection() {
        // given
        int expected = 이호선.size() + 1;

        // when
        이호선.addSection(new Section(이호선, 역삼역, 삼성역, 10));

        // then
        assertThat(이호선.size()).isEqualTo(expected);
    }

    @DisplayName("목록 중간에 추가할 경우 에러 발생")
    @Test
    void addSectionInMiddle() {
        assertThatExceptionOfType(NotValidUpStationException.class)
                .isThrownBy(() -> 이호선.addSection(new Section(이호선, 강남역, 삼성역, 10)));
    }

    @DisplayName("이미 존재하는 역 추가 시 에러 발생")
    @Test
    void addSectionAlreadyIncluded() {
        assertThatExceptionOfType(DownStationExistedException.class)
                .isThrownBy(() -> 이호선.addSection(new Section(이호선, 역삼역, 강남역, 10)));
    }

    @Test
    void removeSection() {
    }

    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    @Test
    void removeSectionNotEndOfList() {
    }
}
