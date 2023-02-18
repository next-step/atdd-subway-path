package nextstep.subway.unit;

import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

class LineTest {

    private static final String LINE_ONE = "1호선";
    private static final String LINE_TWO = "2호선";
    private static final String BACKGROUND_COLOR_BLUE = "bg-color-blue";
    private static final String BACKGROUND_COLOR_RED = "bg-color-red";
    private static final String SEOUL_STATION = "서울역";
    private static final String YONGSAN_STATION = "용산역";
    private static final String HONGDAE_STATION = "홍대역";
    private static final int DISTANCE_FIVE = 5;
    private Line lineOne;
    private Station seoulStation;
    private Station yongSanStation;

    @BeforeEach
    void setUp() {
        lineOne = new Line(LINE_ONE, BACKGROUND_COLOR_BLUE);
        seoulStation = new Station(SEOUL_STATION);
        yongSanStation = new Station(YONGSAN_STATION);
    }

    @DisplayName("노선에 구간을 추가 하면 노선의 포함 여부를 알 수 있다")
    @Test
    void addSection() {
        //given
        Station hongDaeStation = new Station(HONGDAE_STATION);
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DISTANCE_FIVE);
        Section sectionTwo = new Section(lineOne, yongSanStation, hongDaeStation, DISTANCE_FIVE);

        //when
        lineOne.addSection(sectionOne.getUpStation(), sectionOne.getDownStation(), sectionOne.getDistance());
        lineOne.addSection(sectionTwo.getUpStation(), sectionTwo.getDownStation(), sectionTwo.getDistance());

        //then
        Assertions.assertThat(lineOne.getSections().getSections().contains(sectionOne)).isTrue();
        Assertions.assertThat(lineOne.getSections().getSections().contains(sectionTwo)).isTrue();
    }

    @DisplayName("노선에 존재 하는 구간을 삭제 하면 노선의 포함 여부를 알 수 없다")
    @Test
    void removeSection() {
        //given
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DISTANCE_FIVE);
        lineOne.addSection(sectionOne.getUpStation(), sectionOne.getDownStation(), sectionOne.getDistance());

        //when
        lineOne.removeSection(yongSanStation);

        //then
        Assertions.assertThat(lineOne.getSections().getSections()).doesNotContain(sectionOne);
        Assertions.assertThat(lineOne.getStations()).isEmpty();
    }

    @DisplayName("노선의 이름이나 색깔을 변경할 수 있다")
    @Test
    void updateNameOrColor() {
        //given
        String lineTwo = LINE_TWO;
        String red = BACKGROUND_COLOR_RED;
        LineRequest lineRequest = LineRequest.builder()
                .name(lineTwo)
                .color(red)
                .build();

        //when
        lineOne.updateNameOrColor(lineRequest);

        //then
        Assertions.assertThat(lineOne.getName()).isEqualTo(lineTwo);
        Assertions.assertThat(lineOne.getColor()).isEqualTo(red);
    }

    @DisplayName("노선에 구간들을 추가하고 노선을 조회하면 구간에 포함된 지하철역을 알 수 있다")
    @Test
    void getStations() {
        //given
        Section sectionOne = new Section(lineOne, seoulStation, yongSanStation, DISTANCE_FIVE);
        lineOne.addSection(sectionOne.getUpStation(), sectionOne.getDownStation(), sectionOne.getDistance());

        //when
        List<Station> stations = lineOne.getStations();

        //then
        Assertions.assertThat(stations).containsExactly(sectionOne.getUpStation(), sectionOne.getDownStation());
    }
}
