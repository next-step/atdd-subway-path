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
    class AddSectionTest {

        @Nested
        @DisplayName("기존의 역과 역 사이에 추가하는 경우")
        class BetweenExistStationsTest {

            @Test
            @DisplayName("상행역을 기준으로 추가가 가능하다.")
            void ygyspqklz() {
                //given
                Station 강남역 = stationRepository.save(new Station("강남역"));
                Station 삼성역 = stationRepository.save(new Station("삼성역"));
                Station 선릉역 = stationRepository.save(new Station("선릉역"));
                Line line = lineRepository.save(new Line("2호선", "갈매색"));
                SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 선릉역.getId(), 60);
                lineService.addSection(line.getId(), sectionRequest);

                //when
                lineService.addSection(line.getId(), new SectionRequest(강남역.getId(), 삼성역.getId(), 50));

                //then
                Line result = lineRepository.findById(line.getId()).orElseThrow(IllegalArgumentException::new);
                List<Section> sections = result.getSections();
                assertAll(
                        () -> assertThat(sections).hasSize(2),
                        () -> assertThat(sections.get(0).getUpStation()).isEqualTo(강남역),
                        () -> assertThat(sections.get(0).getDownStation()).isEqualTo(삼성역),
                        () -> assertThat(sections.get(0).getDistance()).isEqualTo(50),
                        () -> assertThat(sections.get(1).getUpStation()).isEqualTo(삼성역),
                        () -> assertThat(sections.get(1).getDownStation()).isEqualTo(선릉역),
                        () -> assertThat(sections.get(1).getDistance()).isEqualTo(10)
                );
            }

            @Test
            @DisplayName("하행역을 기준으로 추가가 가능하다.")
            void ygyspqk2lz() {
                //given
                Station 강남역 = stationRepository.save(new Station("강남역"));
                Station 삼성역 = stationRepository.save(new Station("삼성역"));
                Station 선릉역 = stationRepository.save(new Station("선릉역"));
                Line line = lineRepository.save(new Line("2호선", "갈매색"));
                SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 선릉역.getId(), 100);
                lineService.addSection(line.getId(), sectionRequest);

                //when
                lineService.addSection(line.getId(), new SectionRequest(삼성역.getId(), 선릉역.getId(), 30));

                //then
                Line result = lineRepository.findById(line.getId()).orElseThrow(IllegalArgumentException::new);
                List<Section> sections = result.getSections();
                assertAll(
                        () -> assertThat(sections).hasSize(2),
                        () -> assertThat(sections.get(0).getUpStation()).isEqualTo(강남역),
                        () -> assertThat(sections.get(0).getDownStation()).isEqualTo(삼성역),
                        () -> assertThat(sections.get(0).getDistance()).isEqualTo(70),
                        () -> assertThat(sections.get(1).getUpStation()).isEqualTo(삼성역),
                        () -> assertThat(sections.get(1).getDownStation()).isEqualTo(선릉역),
                        () -> assertThat(sections.get(1).getDistance()).isEqualTo(30)
                );
            }

            @Test
            @DisplayName("신규 구간의 길이는 기존 구간의 길이보다 작아야 된다.")
            void qlni2ywzxv() {
                Station 강남역 = stationRepository.save(new Station("강남역"));
                Station 삼성역 = stationRepository.save(new Station("삼성역"));
                Station 선릉역 = stationRepository.save(new Station("선릉역"));
                Line line = lineRepository.save(new Line("2호선", "갈매색"));
                SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 삼성역.getId(), 100);
                lineService.addSection(line.getId(), sectionRequest);

                // when, then
                assertThrows(IllegalArgumentException.class, () -> lineService.addSection(line.getId(), new SectionRequest(강남역.getId(), 선릉역.getId(), 101)));
            }
        }

        @Test
        @DisplayName("새로운 역을 상행 종점역으로 등록하는 경우")
        void addSection() {
            // given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));
            Station 사당역 = stationRepository.save(new Station("사당역"));
            Line line = lineRepository.save(new Line("2호선", "갈매색"));
            lineService.addSection(line.getId(), new SectionRequest(강남역.getId(), 삼성역.getId(), 100));

            // when
            lineService.addSection(line.getId(), new SectionRequest(사당역.getId(), 강남역.getId(), 100));

            // then
            List<Section> sections = line.getSections();
            assertAll(
                    () -> assertThat(sections.size()).isEqualTo(2),
                    () -> assertThat(sections.get(0).getUpStation()).isEqualTo(사당역),
                    () -> assertThat(sections.get(0).getDownStation()).isEqualTo(강남역),
                    () -> assertThat(sections.get(0).getDistance()).isEqualTo(100),
                    () -> assertThat(sections.get(1).getUpStation()).isEqualTo(강남역),
                    () -> assertThat(sections.get(1).getDownStation()).isEqualTo(삼성역),
                    () -> assertThat(sections.get(1).getDistance()).isEqualTo(100)
            );
        }

        @Test
        @DisplayName("새로운 역을 하행 종점역으로 등록하는 경우")
        void a1ddSection() {
            // given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));
            Station 선릉역 = stationRepository.save(new Station("선릉역"));
            Line line = lineRepository.save(new Line("2호선", "갈매색"));
            lineService.addSection(line.getId(), new SectionRequest(강남역.getId(), 삼성역.getId(), 100));

            // when
            lineService.addSection(line.getId(), new SectionRequest(삼성역.getId(), 선릉역.getId(), 50));

            // then
            List<Section> sections = line.getSections();
            assertAll(
                    () -> assertThat(sections.size()).isEqualTo(2),
                    () -> assertThat(sections.get(0).getUpStation()).isEqualTo(강남역),
                    () -> assertThat(sections.get(0).getDownStation()).isEqualTo(삼성역),
                    () -> assertThat(sections.get(0).getDistance()).isEqualTo(100),
                    () -> assertThat(sections.get(1).getUpStation()).isEqualTo(삼성역),
                    () -> assertThat(sections.get(1).getDownStation()).isEqualTo(선릉역),
                    () -> assertThat(sections.get(1).getDistance()).isEqualTo(50)
            );
        }

        @Test
        @DisplayName("신규 구간의 역들이 기존 구간의 역들에 다 포함되는 경우 추가될 수 없다.")
        void qlniywzxv() {
            // given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));
            Station 선릉역 = stationRepository.save(new Station("선릉역"));
            Line line = lineRepository.save(new Line("2호선", "갈매색"));
            SectionRequest sectionRequest1 = new SectionRequest(강남역.getId(), 삼성역.getId(), 100);
            SectionRequest sectionRequest2 = new SectionRequest(삼성역.getId(), 선릉역.getId(), 100);
            lineService.addSection(line.getId(), sectionRequest1);
            lineService.addSection(line.getId(), sectionRequest2);

            // when, then
            assertThrows(IllegalArgumentException.class, () -> lineService.addSection(line.getId(), new SectionRequest(강남역.getId(), 선릉역.getId(), 50)));
        }

        @Test
        @DisplayName("새로운 구간의 상행역과 하행역 둘중 하나는 기존 노선에 존재해야 된다.")
        void qlni3ywzxv() {
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));
            Station 선릉역 = stationRepository.save(new Station("선릉역"));
            Station 잠실역 = stationRepository.save(new Station("잠실역"));
            Line line = lineRepository.save(new Line("2호선", "갈매색"));
            SectionRequest sectionRequest = new SectionRequest(강남역.getId(), 삼성역.getId(), 100);
            lineService.addSection(line.getId(), sectionRequest);

            // when, then
            assertThrows(IllegalArgumentException.class, () -> lineService.addSection(line.getId(), new SectionRequest(선릉역.getId(), 잠실역.getId(), 101)));
        }
    }

    @Nested
    @DisplayName("구간 삭제시")
    class DeleteSectionTest {
        @Test
        @DisplayName("노선에 구간이 2개이상일 때만 가능하다")
        void deleteSection1() {
            // given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));
            Station 선릉역 = stationRepository.save(new Station("선릉역"));
            Line line = lineRepository.save(new Line("2호선", "갈매색"));
            line.registerSection(강남역, 삼성역, 100);
            line.registerSection(삼성역, 선릉역, 100);

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
        @DisplayName("하행 종점이 아니여도 가능하다.")
        void deleteSection2() {
            // given
            Station 강남역 = stationRepository.save(new Station("강남역"));
            Station 삼성역 = stationRepository.save(new Station("삼성역"));
            Station 선릉역 = stationRepository.save(new Station("선릉역"));
            Line line = lineRepository.save(new Line("2호선", "갈매색"));
            line.registerSection(강남역, 삼성역, 100);
            line.registerSection(삼성역, 선릉역, 100);

            // when
            lineService.deleteSection(line.getId(), 삼성역.getId());

            //then
            List<Section> sections = line.getSections();
            assertAll(
                    () -> assertThat(sections.size()).isEqualTo(1),
                    () -> assertThat(sections.get(0).getUpStation()).isEqualTo(강남역),
                    () -> assertThat(sections.get(0).getDownStation()).isEqualTo(선릉역),
                    () -> assertThat(sections.get(0).getDistance()).isEqualTo(200)
            );
        }
    }
}
