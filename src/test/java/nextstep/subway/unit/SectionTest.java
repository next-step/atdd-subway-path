package nextstep.subway.unit;

import java.util.List;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionTest {

    Line 이호선;

    Station 강남역;
    Station 역삼역;
    Station 선릉역;
    Station 서울역;
    Station 삼성역;

    @BeforeEach
    void setup() {
        이호선 = new Line("2호선", "bg-green-600");

        강남역 = new Station(1L, "강남역");
        역삼역 = new Station(2L, "역삼역");
        선릉역 = new Station(3L, "선릉역");
        서울역 = new Station(4L, "서울역");
        삼성역 = new Station(5L ,"삼성역");
    }

    @Test
    @DisplayName("지하철구간을 생성한다.")
    void createSection() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        assertAll(() -> {
            assertThat(강남_역삼_구간.getLine()).isEqualTo(이호선);
            assertThat(강남_역삼_구간.getUpStation()).isEqualTo(강남역);
            assertThat(강남_역삼_구간.getDownStation()).isEqualTo(역삼역);
        });
    }

    @Test
    @DisplayName("지하철역을 조회한다.")
    void getStations() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);
        List<Station> 지하철역 = 강남_역삼_구간.getStations();

        assertAll(() -> {
            assertThat(지하철역.get(0)).isEqualTo(강남역);
            assertThat(지하철역.get(1)).isEqualTo(역삼역);
        });
    }

    @Test
    @DisplayName("중복되는 Section이 있는지 확인합니다.")
    void isEqualStationTest() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);
        Section 선릉_서울_구간 = new Section(이호선, 선릉역, 서울역, 200);

        boolean 비교값 = 강남_역삼_구간.isEqualsUpStationAndDownStation(선릉_서울_구간);

        assertThat(비교값).isFalse();
    }

    @Test
    @DisplayName("지하열역 상행선 변경 및 거리 수정 합니다.")
    void changeUpStationTest() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        강남_역삼_구간.changeUpStation(선릉역, 3);
        Section 비교값 = new Section(이호선, 선릉역, 역삼역, 3);

        assertThat(강남_역삼_구간).isEqualTo(비교값);
    }

    @Test
    @DisplayName("지하철역 하행선 변경 및 거리수정합니다.")
    void changeDownStationTest() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        강남_역삼_구간.changeDownStation(선릉역, 3);
        Section 비교값 = new Section(이호선, 강남역, 선릉역, 3);

        assertThat(강남_역삼_구간).isEqualTo(비교값);
    }

    @Test
    @DisplayName("구간 사이에 들어가는 거리가 더크면 에러를 반환합니다.")
    void isOverLengthTest() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);

        assertThatExceptionOfType(SectionException.class).isThrownBy(() -> {
            강남_역삼_구간.changeUpStation(선릉역, 10);
        })
            .withMessage("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없습니다.");
    }

    @Test
    @DisplayName("연결 될 수 없는 구간 일때 false를 반환합니다.")
    void isNotConnectionTest() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);
        Section 선릉_삼성_구간 = new Section(이호선, 선릉역, 삼성역, 7);

        boolean 연결될수_있는지 = 강남_역삼_구간.isConnection(선릉_삼성_구간);

        assertThat(연결될수_있는지).isFalse();
    }

    @Test
    @DisplayName("연결될 수 있는 구간일때 true를 반환합니다.")
    void isConnectionTest() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);
        Section 선릉_삼성_구간 = new Section(이호선, 역삼역, 삼성역, 7);

        boolean 연결될수_있는지 = 강남_역삼_구간.isConnection(선릉_삼성_구간);

        assertThat(연결될수_있는지).isTrue();
    }

    @Test
    @DisplayName("상행선 기준으로 연결될 수 있을 때 true를 반환합니다.")
    void isUpStationConnectionTest() {
        Section 역삼_선릉_구간 = new Section(이호선, 역삼역, 선릉역, 6);
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 7);

        boolean 연결될_수_있는지 = 역삼_선릉_구간.isUpStationConnection(강남_역삼_구간);

        assertThat(연결될_수_있는지).isTrue();
    }

    @Test
    @DisplayName("하행선 기준으로 연결될 수 있을 때 true를 반환합니다.")
    void isDownStationConnectionTest() {
        Section 강남_역삼_구간 = new Section(이호선, 강남역, 역삼역, 6);
        Section 역삼_삼성_구간 = new Section(이호선, 역삼역, 삼성역, 7);

        boolean 연결될_수_있는지 = 강남_역삼_구간.isDownStationConnection(역삼_삼성_구간);

        assertThat(연결될_수_있는지).isTrue();
    }

}
