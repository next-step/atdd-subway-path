package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.utils.LineFixture.라인_생성_2호선;
import static nextstep.subway.utils.LineFixture.라인_생성_신분당선;
import static nextstep.subway.utils.SectionFixture.구간생성;
import static nextstep.subway.utils.StationFixture.역생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LineTest {
    @Test
    void addSection() {
        final Line line = new Line();
        final Section section = 구간생성(line, 역생성("강남역"), 역생성("역삼역"), 2);

        line.addSection(section);

        assertThat(line.getSections()).contains(section);
    }

    @DisplayName("새로운 역을 상행 종점으로 등록할 수 있다")
    @Test
    void addSectionByUpStation() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성("역삼역");
        final Station 선릉역 = 역생성("선릉역");

        final Section 역삼역_선릉역_구간 = 구간생성(line, 역삼역, 선릉역, 3);
        line.addSection(역삼역_선릉역_구간);

        final Section 강남역_역삼역_구간 = 구간생성(line, 강남역, 역삼역, 2);
        line.addSection(강남역_역삼역_구간);

        assertThat(line.getSections()).containsSequence(강남역_역삼역_구간, 역삼역_선릉역_구간);
        assertThat(line.getDistance()).isEqualTo(역삼역_선릉역_구간.getDistance() + 강남역_역삼역_구간.getDistance());
    }

    @DisplayName("새로운 역을 하행 좀정으로 등록 할 수 있다")
    @Test
    void addSectionByDownStation() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성( "역삼역");
        final Station 선릉역 = 역생성( "선릉역");

        final Section 강남역_역삼역_구간 = 구간생성(line, 강남역, 역삼역, 2);
        line.addSection(강남역_역삼역_구간);

        final Section 역삼역_선릉역_구간 = 구간생성(line, 역삼역, 선릉역, 3);
        line.addSection(역삼역_선릉역_구간);

        assertThat(line.getSections()).containsSequence(강남역_역삼역_구간, 역삼역_선릉역_구간);
        assertThat(line.getDistance()).isEqualTo(역삼역_선릉역_구간.getDistance() + 강남역_역삼역_구간.getDistance());
    }

    @DisplayName("역 사이에 새로운 역을 등록 할 수 있다")
    @Test
    void addSectionBetweenStations() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성("역삼역");
        final Station 선릉역 = 역생성("선릉역");

        final Section 강남역_선릉역_구간 = 구간생성(line, 강남역, 선릉역, 5);
        line.addSection(강남역_선릉역_구간);

        final Section 강남역_역삼역_구간 = 구간생성(line, 강남역, 역삼역, 3);
        line.addSection(강남역_역삼역_구간);

        final int 역삼역_선릉역_구간_거리 = 강남역_선릉역_구간.getDistance() - 강남역_역삼역_구간.getDistance();
        final Section 역삼역_선릉역_구간 = 구간생성(line, 역삼역, 선릉역, 역삼역_선릉역_구간_거리);

        assertThat(line.getSections()).containsSequence(강남역_역삼역_구간, 역삼역_선릉역_구간);
        assertThat(line.getDistance()).isEqualTo(강남역_선릉역_구간.getDistance());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없다")
    @Test
    void addSectionBetweenStationsFail() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성("역삼역");
        final Station 선릉역 = 역생성("선릉역");

        final Section 역삼역_선릉역_구간 = 구간생성(line, 역삼역, 선릉역, 2);
        line.addSection(역삼역_선릉역_구간);

        final Section 강남역_선릉역_구간 = 구간생성(line, 강남역, 선릉역, 5);

        assertThatThrownBy(() -> line.addSection(강남역_선릉역_구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다")
    @Test
    void addSectionBetweenStationsFail2() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성("역삼역");

        final Section 강남역_역삼역_구간 = 구간생성(line, 강남역, 역삼역, 3);
        line.addSection(강남역_역삼역_구간);

        assertThatThrownBy(() -> line.addSection(강남역_역삼역_구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없습니다.");
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다")
    @Test
    void addSectionBetweenStationsFail3() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성("역삼역");
        final Station 선릉역 = 역생성("선릉역");
        final Station 삼성역 = 역생성("삼성역");

        final Section 강남역_역삼역_구간 = 구간생성(line, 강남역, 역삼역, 3);
        line.addSection(강남역_역삼역_구간);

        final Section 선릉역_삼성역_구간 = 구간생성(line, 선릉역, 삼성역, 2);

        assertThatThrownBy(() -> line.addSection(선릉역_삼성역_구간))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없습니다.");
    }

    @Test
    void getStations() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성("역삼역");
        final Station 선릉역 = 역생성("선릉역");

        final Section 강남_역삼_구간 = 구간생성(line, 강남역, 역삼역, 2);
        final Section 역삼_선릉_구간 = 구간생성(line, 역삼역, 선릉역, 3);

        line.addSection(강남_역삼_구간);
        line.addSection(역삼_선릉_구간);

        assertThat(line.getSections()).contains(강남_역삼_구간, 역삼_선릉_구간);
    }

    @DisplayName("구간이 하나뿐인 노선에서 구건을 제거할 수 없다")
    @Test
    void removeSectionFail() {
        final Line line = 라인_생성_신분당선();
        final Station 강남역 = 역생성( "강남역");
        final Station 양재역 = 역생성( "양재역");
        final Section 강남_양재_구간 = 구간생성(line, 강남역, 양재역, 3);
        line.addSection(강남_양재_구간);

        assertThatThrownBy(() -> line.deleteSection(양재역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("구간이 하나뿐인 노선에서 구간을 제거할 수 없습니다.");
    }

    @DisplayName("노선에 존재하지 않는 역은 삭제할 수 없다")
    @Test
    void failDeleteUnKnownSection() {
        final Line line = 라인_생성_신분당선();
        final Station 논현역 = 역생성( "논현역");
        final Station 강남역 = 역생성( "강남역");
        final Station 양재역 = 역생성( "양재역");

        final Section 논현_강남_구간 = 구간생성(line, 논현역, 강남역, 2);
        final Section 강남_양재_구간 = 구간생성(line, 강남역, 양재역, 3);
        line.addSection(논현_강남_구간);
        line.addSection(강남_양재_구간);

        final Station 판교역 = 역생성( "판교역");
        assertThatThrownBy(() -> line.deleteSection(판교역))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("노선에 등록되어있지 않은 역은 제거 할 수 없습니다.");
    }

    @DisplayName("지하철 구간에 마지막 역으로 구간 제거 요청")
    @Test
    void removeSection() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성("역삼역");
        final Station 선릉역 = 역생성("선릉역");

        final Section 강남_역삼_구간 = 구간생성(line, 강남역, 역삼역, 2);
        final Section 역삼_선릉_구간 = 구간생성(line, 역삼역, 선릉역, 3);
        line.addSection(강남_역삼_구간);
        line.addSection(역삼_선릉_구간);

        line.deleteSection(선릉역);

        assertThat(line.getSections()).doesNotContain(역삼_선릉_구간);
    }

    @DisplayName("지하철 노선에 첫번째 역으로 구간 제거 요청")
    @Test
    void removeFirstSection() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성("역삼역");
        final Station 선릉역 = 역생성("선릉역");

        final Section 강남_역삼_구간 = 구간생성(line, 강남역, 역삼역, 2);
        final Section 역삼_선릉_구간 = 구간생성(line, 역삼역, 선릉역, 3);
        line.addSection(강남_역삼_구간);
        line.addSection(역삼_선릉_구간);

        line.deleteSection(강남역);

        assertThat(line.getSections()).contains(역삼_선릉_구간);
    }

    @DisplayName("지하철 노선에 중간 구간을 제거")
    @Test
    void removeMiddleSection() {
        final Line line = 라인_생성_2호선();
        final Station 강남역 = 역생성("강남역");
        final Station 역삼역 = 역생성("역삼역");
        final Station 선릉역 = 역생성("선릉역");

        final Section 강남_역삼_구간 = 구간생성(line, 강남역, 역삼역, 2);
        final Section 역삼_선릉_구간 = 구간생성(line, 역삼역, 선릉역, 3);
        line.addSection(강남_역삼_구간);
        line.addSection(역삼_선릉_구간);

        line.deleteSection(역삼역);

        final Section 강남_선릉_구간 = 구간생성(line, 강남역, 선릉역, 강남_역삼_구간.getDistance() + 역삼_선릉_구간.getDistance());
        assertThat(line.getSections()).contains(강남_선릉_구간);
    }

}
