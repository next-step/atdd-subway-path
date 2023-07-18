package nextstep.subway.unit.application.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static nextstep.subway.unit.LineFixture.makeLine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.line.LineService;
import nextstep.subway.applicaion.line.request.SectionRequest;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.line.LineRepository;
import nextstep.subway.domain.line.exception.NoSuchLineException;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationRepository;
import nextstep.subway.domain.station.exception.NoSuchStationException;

@SpringBootTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@RequiredArgsConstructor
@Transactional
class LineServiceTest {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final LineService lineService;

    private Line 신분당선;
    private Station 강남역, 역삼역, 선릉역, 삼성역;

    @BeforeEach
    void setUp() {
        강남역 = stationRepository.save(new Station("강남역"));
        역삼역 = stationRepository.save(new Station("역삼역"));
        선릉역 = stationRepository.save(new Station("선릉역"));
        삼성역 = stationRepository.save(new Station("삼성역"));

        신분당선 = lineRepository.save(makeLine(강남역, 역삼역, 선릉역));
    }

    @DisplayName("구간을 추가한다")
    @Nested
    class AppendSectionTest {

        @Nested
        class Success {

            @Test
            void 구간을_추가한다() {
                // when
                lineService.appendSection(신분당선.getId(), makeSectionRequest(선릉역, 삼성역));

                // then
                final var actual = 신분당선.getStations();
                assertThat(actual).containsExactly(강남역, 역삼역, 선릉역, 삼성역);
            }
        }

        @Nested
        class Fail {

            @Test
            void 노선이_존재하지_않는_경우() {
                final var request = new SectionRequest(역삼역.getId(), 선릉역.getId(), 10);
                assertThatThrownBy(() -> lineService.appendSection(0L, request))
                        .isInstanceOf(NoSuchLineException.class);
            }

            @Test
            void 상행역이_존재하지_않는_경우() {
                final var request = new SectionRequest(0L, 선릉역.getId(), 10);
                assertThatThrownBy(() -> lineService.appendSection(신분당선.getId(), request))
                        .isInstanceOf(NoSuchStationException.class);
            }

            @Test
            void 하행역이_존재하지_않는_경우() {
                final var request = new SectionRequest(역삼역.getId(), 0L, 10);
                assertThatThrownBy(() -> lineService.appendSection(신분당선.getId(), request))
                        .isInstanceOf(NoSuchStationException.class);
            }
        }
    }

    @DisplayName("구간을 삭제한다")
    @Nested
    class RemoveSectionTest {

        @Nested
        class Success {

            @Test
            void 구간을_삭제한다() {
                // when
                lineService.removeSection(신분당선.getId(), 선릉역.getId());

                // then
                final var actual = 신분당선.getStations();
                assertThat(actual).containsExactly(강남역, 역삼역);
            }
        }

        @Nested
        class Fail {

            @Test
            void 노선이_존재하지_않는_경우() {
                assertThatThrownBy(() -> lineService.removeSection(0L, 선릉역.getId()))
                        .isInstanceOf(NoSuchLineException.class);
            }

            @Test
            void 역이_존재하지_않는_경우() {
                assertThatThrownBy(() -> lineService.removeSection(신분당선.getId(), 0L))
                        .isInstanceOf(NoSuchStationException.class);
            }
        }
    }

    private SectionRequest makeSectionRequest(final Station upStation, final Station downStation) {
        return new SectionRequest(upStation.getId(), downStation.getId(), 10);
    }
}
