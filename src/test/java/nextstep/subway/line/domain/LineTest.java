package nextstep.subway.line.domain;

import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@DisplayName("노선 Domain 테스트")
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
        이호선 = new Line("2호선", "bg-green-600", 강남역, 역삼역, 10);
    }

    @Test
    @DisplayName("노선의 구간에 있는 역들을 가져오기")
    void getStations() {
        List<Station> stations = 이호선.getStations();
        assertThat(stations).hasSize(2);
    }

    @Test
    @DisplayName("노선에 구간 추가하기")
    void addSection() {
        SectionRequest sectionRequest = new SectionRequest(역삼역.getId(), 삼성역.getId(), 6);
        이호선.addSection(sectionRequest);
        assertThat(이호선.getStations()).containsExactlyElementsOf(Arrays.asList(강남역, 역삼역, 삼성역));
    }

    @Test
    @DisplayName("노선 중간에 구간을 추가할 경우 에러 발생")
    void addSectionInMiddle() {
        assertThatExceptionOfType(CannotAddSectionException.class)
                .isThrownBy(() -> {
                    SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 선릉역.getId(), 20);
                    이호선.addSection(sectionRequest);
                }).withMessage("노선 중간에 구간을 추가할 수 없습니다.");
    }

    @Test
    @DisplayName("노선에 존재하는 구간 추가 시 에러 발생")
    void addSectionAlreadyIncluded() {
        assertThatExceptionOfType(CannotAddSectionException.class)
                .isThrownBy(() -> {
                    SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 역삼역.getId(), 10);
                    이호선.addSection(sectionRequest);
                }).withMessage("이미 존재하는 구간입니다.");
    }

    @Test
    @DisplayName("노선에 있는 하행 종점역 구간 삭제")
    void removeSection() {
        이호선.removeSection(1L);
        assertThat(이호선.getSections()).isEmpty();
    }

    @Test
    @DisplayName("구간이 하나인 노선에서 역 삭제 시 에러 발생")
    void removeSectionNotEndOfList() {
        assertThatExceptionOfType(CannotRemoveSectionException.class)
                .isThrownBy(() -> {
                    이호선.removeSection(1L);
                });
    }
}
