package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LinesTest {


    private Line 일호선;
    private Line 이호선;
    private Line 삼호선;

    private Station 대방역;
    private Station 노량진역;
    private Station 용산역;

    private Station 강남역;
    private Station 역삼역;
    private Station 선릉역;

    private Station 교대역;
    private Station 고속버스터미널역;
    private Station 양재역;

    @BeforeEach
    public void setup(){
        일호선 = new Line("일호선", "green");
        대방역 = new Station("대방역");
        노량진역 = new Station("노량진역");
        용산역 = new Station("용산역");
        일호선.addSection(new Section(일호선, 대방역, 노량진역, 10));
        일호선.addSection(new Section(일호선, 노량진역, 용산역, 3));

        이호선 = new Line("이호선", "green");
        강남역 = new Station("강남역");
        역삼역 = new Station("역삼역");
        선릉역 = new Station("선릉역");
        이호선.addSection(new Section(이호선, 강남역, 역삼역, 7));
        이호선.addSection(new Section(이호선, 역삼역, 선릉역, 3));

        삼호선 = new Line("삼호선", "green");
        교대역 = new Station("교대역");
        고속버스터미널역 = new Station("고속버스터미널역");
        양재역 = new Station("양재역");
        삼호선.addSection(new Section(삼호선, 교대역, 고속버스터미널역, 7));
        삼호선.addSection(new Section(삼호선, 고속버스터미널역, 양재역, 3));
    }

    @Test
    public void findAllSections(){
        //Given
        Lines lines = new Lines(Arrays.asList(이호선, 일호선, 삼호선));

        //When
        List<Section> sections = lines.findAllSections();

        //Then
        assertThat(sections).hasSize(6);
    }

    @Test
    public void findAllStations(){
        //Given
        Lines lines = new Lines(Arrays.asList(이호선, 일호선, 삼호선));

        //When
        List<Station> stations = lines.findAllStations();

        //Then
        assertThat(stations).hasSize(9);
    }
}
