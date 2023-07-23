package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;
    @Autowired
    private LineService lineService;

    Station 양재역;
    Station 강남역;
    Line 신분당선;
    final Integer distance = 10;

    @BeforeEach
    void setUp(){
        양재역 = stationRepository.save(new Station("양재역"));
        강남역 = stationRepository.save(new Station("강남역"));
        신분당선 = lineRepository.save(new Line("신분당선", "yellow"));
    }

    @AfterEach
    void clean(){
        lineRepository.deleteAll();
        stationRepository.deleteAll();
    }

    @Test
    void addSection() {
        // given
        SectionRequest sectionRequest = new SectionRequest(양재역.getId(), 강남역.getId(), distance);

        // when
        lineService.addSection(신분당선.getId(), sectionRequest);

        // then
        Line savedLine = lineRepository.findById(신분당선.getId()).get();
        assertThat(savedLine.getSections().size()).isEqualTo(1);
        assertThat(savedLine.getSections().get(0).getUpStation()).isEqualTo(양재역);
        assertThat(savedLine.getSections().get(0).getDownStation()).isEqualTo(강남역);
    }

    @Test
    void saveLine(){
        //given
        final String 분당선 = "분당선";
        final String 노란색 = "yellow";
        LineRequest lineRequest = new LineRequest(분당선, 노란색, 양재역.getId(), 강남역.getId(), 10);

        //when
        LineResponse response = lineService.saveLine(lineRequest);

        //then
        Line savedLine = lineRepository.findById(response.getId()).get();
        assertThat(savedLine.getName()).isEqualTo(분당선);
        assertThat(savedLine.getColor()).isEqualTo(노란색);
        assertThat(savedLine.getSections().size()).isEqualTo(1);
        assertThat(savedLine.getSections().get(0).getUpStation()).isEqualTo(양재역);
        assertThat(savedLine.getSections().get(0).getDownStation()).isEqualTo(강남역);
    }

    @Test
    void updateLine(){
        //given
        final String 분당선 = "분당선";
        final String 노란색 = "yellow";
        Station 청계산입구역 = stationRepository.save(new Station("청계산입구역"));
        LineRequest lineRequest = new LineRequest("분당선", "yellow", 양재역.getId(), 청계산입구역.getId(), 10);

        //when
        lineService.updateLine(신분당선.getId(), lineRequest);

        //then
        Line savedLine = lineRepository.findById(신분당선.getId()).get();
        assertThat(savedLine.getName()).isEqualTo(분당선);
        assertThat(savedLine.getColor()).isEqualTo(노란색);
    }

    @Test
    void deleteLine(){
        //when
        lineService.deleteLine(신분당선.getId());

        //then
        assertThatThrownBy(() -> {
            lineService.findByLineId(신분당선.getId());
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteSection(){
        //given
        SectionRequest sectionRequest = new SectionRequest(양재역.getId(), 강남역.getId(), distance);
        lineService.addSection(신분당선.getId(), sectionRequest);

        //when
        lineService.deleteSection(신분당선.getId(), 강남역.getId());

        //then
        Line savedLine = lineRepository.findById(신분당선.getId()).get();
        assertThat(savedLine.getSections().size()).isEqualTo(0);
    }
}
