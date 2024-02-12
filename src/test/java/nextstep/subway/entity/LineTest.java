package nextstep.subway.entity;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.Section;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LineTest {

    @Mock
    private Station upStation;

    @Mock
    private Station downStation;

    @Mock
    private Section section;

    @DisplayName("Line 엔티티를 생성한다.")
    @Test
    void 지하철_노선_엔티티_생성() {
        // when
        final Line newLine = new Line("신분당선", "bg-red-600", upStation, downStation, 10);

        // then
        assertThat(newLine.getName()).isEqualTo("신분당선");
        assertThat(newLine.getColor()).isEqualTo("bg-red-600");
        assertThat(newLine.getSections()).isNotNull();
        assertThat(newLine.getSections().getSections().size()).isEqualTo(1);
        assertThat(newLine.getSections().getSections().get(0).getUpStation()).isEqualTo(upStation);
        assertThat(newLine.getSections().getSections().get(0).getDownStation()).isEqualTo(downStation);
        assertThat(newLine.getSections().getSections().get(0).getDistance()).isEqualTo(10);
    }

    @DisplayName("Line 엔티티 생성 시 이름이 null 혹은 공백이라면 오류가 발생한다.")
    @Test
    void 지하철_노선_엔티티_생성_시_이름이_null_혹은_공백이라면_생성_불가() {
        // then
        assertThatThrownBy(() -> new Line(null, "bg-red-600", upStation, downStation, 10))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Line("", "bg-red-600", upStation, downStation, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Line 엔티티 생성 시 색상이 null 혹은 공백이라면 오류가 발생한다.")
    @Test
    void 지하철_노선_엔티티_생성_시_색상이_null_혹은_공백이라면_생성_불가() {
        // then
        assertThatThrownBy(() -> new Line("신분당선", null, upStation, downStation, 10))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new Line("신분당선", "", upStation, downStation, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Line 엔티티 생성 시 상행역이 null이라면 오류가 발생한다.")
    @Test
    void 지하철_노선_엔티티_생성_시_상행역이_null이라면_생성_불가() {
        // then
        assertThatThrownBy(() -> new Line("신분당선", "bg-red-600", null, downStation, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Line 엔티티 생성 시 하행역이 null이라면 오류가 발생한다.")
    @Test
    void 지하철_노선_엔티티_생성_시_하행역이_null이라면_생성_불가() {
        // then
        assertThatThrownBy(() -> new Line("신분당선", "bg-red-600", upStation, null, 10))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("Line 엔티티 생성 시 거리가 0보다 작다면 오류가 발생한다.")
    @Test
    void 지하철_노선_엔티티_생성_시_거리가_0보다_작다면_생성_불가() {
        // then
        assertThatThrownBy(() -> new Line("신분당선", "bg-red-600", upStation, downStation, -1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선명과 색상을 변경한다.")
    @Test
    void 지하철_노선_노선명과_색상_변경() {
        // given
        final Line newLine = new Line("신분당선", "bg-red-600", upStation, downStation, 10);

        // when
        newLine.updateDetails("지하철노선", "색상");

        // then
        assertThat(newLine.getName()).isEqualTo("지하철노선");
        assertThat(newLine.getColor()).isEqualTo("색상");
    }

    @DisplayName("노선명과 색상을 변경 시 노선명이 null 혹은 공백이라면 오류가 발생한다.")
    @Test
    void 지하철_노선_노선명_변경_시_노선명이_null_혹은_공백이라면_변경_불가() {
        // given
        final Line newLine = new Line("신분당선", "bg-red-600", upStation, downStation, 10);

        // then
        assertThatThrownBy(() -> newLine.updateDetails(null, "색상"))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> newLine.updateDetails("", "색상"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선명과 색상을 변경 시 색상이 null 혹은 공백이라면 오류가 발생한다.")
    @Test
    void 지하철_노선_노선명_변경_시_색상이_null_혹은_공백이라면_변경_불가() {
        // given
        final Line newLine = new Line("신분당선", "bg-red-600", upStation, downStation, 10);

        // then
        assertThatThrownBy(() -> newLine.updateDetails("지하철노선", null))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> newLine.updateDetails("지하철노선", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노선에 구간을 추가한다.")
    @Test
    void 지하철_노선_구간_추가() {
        // given
        final Line newLine = new Line("신분당선", "bg-red-600", upStation, downStation, 10);

        final Station newDownStation = mock(Station.class);
        when(section.getDownStation()).thenReturn(newDownStation);
        when(section.getDownStation().getId()).thenReturn(2L);

        // when
        assertThat(newLine.getSections().getSections().size()).isEqualTo(1);
        newLine.addSection(section);

        // then
        assertThat(newLine.getSections().getSections().size()).isEqualTo(2);
        assertThat(newLine.getSections().getSections()).contains(section);
    }

    @DisplayName("노선에 구간을 추가 시 등록되어있는 역이 새로운 구간의 하행역이 된다면 오류가 발생한다.")
    @Test
    void 지하철_노선_구간_추가_시_이미_등록되어있는_역이라면_새로운_구간의_하행역으로_등록_불가() {
        // given
        final Line newLine = new Line("신분당선", "bg-red-600", upStation, downStation, 10);
        when(upStation.getId()).thenReturn(2L);

        final Station newDownStation = mock(Station.class);
        when(section.getDownStation()).thenReturn(newDownStation);
        when(section.getDownStation().getId()).thenReturn(2L);

        // when
        assertThatThrownBy(() -> newLine.addSection(section))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
