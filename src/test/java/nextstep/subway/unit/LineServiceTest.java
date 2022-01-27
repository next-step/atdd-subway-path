package nextstep.subway.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.dto.LineRequest;
import nextstep.subway.applicaion.dto.LineResponse;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
public class LineServiceTest {
    @Autowired
    private StationRepository stationRepository;
    @Autowired
    private LineRepository lineRepository;

    @Autowired
    private LineService lineService;

    @Nested
    @DisplayName("노선 저장시")
    class SaveLineATest {
        @Test
        @DisplayName("구간의 길이가 0인 경우는 구간을 추가하지 않는다.")
        void vhcgrlrzv() {
            //given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));
            LineRequest request = new LineRequest("2호선", "갈매색", 강남역.getId(), 삼성역.getId(), 0);

            //when
            LineResponse lineResponse = lineService.saveLine(request);

            //then
            Line line = lineRepository.findById(lineResponse.getId()).orElseThrow(IllegalArgumentException::new);
            assertThat(line.getSections()).hasSize(0);
        }

        @Test
        @DisplayName("상행역 또는 하행역이 존재하지 않는 경우 구간을 추가하지 않는다.")
        void vhcgr2lrzv() {
            //given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            LineRequest request = new LineRequest("2호선", "갈매색", 강남역.getId(), null, 100);

            //when
            LineResponse lineResponse = lineService.saveLine(request);

            //then
            Line line = lineRepository.findById(lineResponse.getId()).orElseThrow(IllegalArgumentException::new);
            assertThat(line.getSections()).hasSize(0);
        }

        @Test
        @DisplayName("구간과 함께 저장된다.")
        void vhcgr2l2rzv() {
            //given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));

            LineRequest request = new LineRequest("2호선", "갈매색", 강남역.getId(), 삼성역.getId(), 100);

            //when
            LineResponse lineResponse = lineService.saveLine(request);

            //then
            Line line = lineRepository.findById(lineResponse.getId()).orElseThrow(IllegalArgumentException::new);
            assertAll(
                    () -> assertThat(line.getSections()).hasSize(1),
                    () -> assertThat(line.getSections().get(0).getUpStation()).isEqualTo(강남역),
                    () -> assertThat(line.getSections().get(0).getDownStation()).isEqualTo(삼성역),
                    () -> assertThat(line.getSections().get(0).getDistance()).isEqualTo(100)
            );
        }
    }

    @Nested
    @DisplayName("구간 추가시")
    class AddSection {

        @Test
        @DisplayName("정상적으로 저장된다.")
        void addSection() {
            // given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));
            Line line = lineRepository.save(new Line("2호선", "갈매색"));
            SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 삼성역.getId(), 100);

            // when
            lineService.addSection(line.getId(), sectionRequest);

            // then
            List<Section> sections = line.getSections();
            assertAll(
                    () -> assertThat(sections.size()).isEqualTo(1),
                    () -> assertThat(sections.get(0).getUpStation()).isEqualTo(강남역),
                    () -> assertThat(sections.get(0).getDownStation()).isEqualTo(삼성역),
                    () -> assertThat(sections.get(0).getDistance()).isEqualTo(100)
            );
        }
    }

    @Nested
    @DisplayName("구간 삭제시")
    class DeleteSection {
        @Test
        @DisplayName("노선에 구간이 2개이상일 때만 가능하다")
        void deleteSection1() {
            // given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));
            Station 선릉역 = stationRepository.save(new Station("선릉역"));
            Line line = lineRepository.save(new Line("2호선", "갈매색"));
            line.addSection(강남역, 삼성역, 100);
            line.addSection(삼성역, 선릉역, 100);

            // when
            lineService.deleteSection(line.getId(), 선릉역.getId());

            // then
            List<Section> sections = line.getSections();
            assertAll(
                    () -> assertThat(sections.size()).isEqualTo(1),
                    () -> assertThat(sections.get(0).getUpStation()).isEqualTo(강남역),
                    () -> assertThat(sections.get(0).getDownStation()).isEqualTo(삼성역),
                    () -> assertThat(sections.get(0).getDistance()).isEqualTo(100)
            );
        }

        @Test
        @DisplayName("하행 종점역이 아니면 불가능하다.")
        void deleteSection2() {
            // given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));
            Station 선릉역 = stationRepository.save(new Station("선릉역"));
            Line line = lineRepository.save(new Line("2호선", "갈매색"));
            line.addSection(강남역, 삼성역, 100);
            line.addSection(삼성역, 선릉역, 100);

            // when, then
            assertThrows(IllegalArgumentException.class, () -> lineService.deleteSection(line.getId(), 삼성역.getId()));
        }
    }
}
