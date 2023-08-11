package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.common.annotation.UnitTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * 지하철 노선 구간 추가에 대한 도메인 단위 테스트
 */
@UnitTest
@DisplayName("지하철 노선 구간 추가에 대한 도메인 단위 테스트")
public class SubwaySectionAddDomainTest {

    /**
     * @given 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 지하철 노선에 종점역에서 시작하는 구간을 추가할 때
     * @then 지하철 노선에 구간이 추가된다.
     */
    @Test
    @DisplayName("지하철 역과 노선이 존재하면 지하철 구간을 종점역에 추가할 때 지하철 구간이 추가된다.")
    void addSectionAtEnd() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        이호선.addSection(역삼역, 선릉역, Kilometer.of(10), manager);

        //then
        assertThat(이호선.getSections())
                .hasSize(2)
                .extracting("upStation.id", "downStation.id")
                .containsExactly(
                        tuple(강남역.getId(), 역삼역.getId()),
                        tuple(역삼역.getId(), 선릉역.getId()));
    }

    /**
     * @given 추가될 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 지하철 노선 중간에 구간을 추가할 때
     * @then 기존 구간이 추가될 구간을 제외한 구간으로 축소된다.
     */
    @Test
    @DisplayName("노선 중간에 구간을 추가할 때 기존 구간이 추가될 구간을 제외한 구간으로 축소된다.")
    void reduceSectionWhenAddSectionAtMiddle() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");
        Station 성수역 = Station.of(new Station.Id(4L), "성수역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        이호선.addSection(역삼역, 성수역, Kilometer.of(1), manager);

        //then
        assertThat(이호선.getSections())
                .filteredOn(SubwaySection::getUpStationId, 성수역.getId())
                .extracting(SubwaySection::getDistance, SubwaySection::getDownStationId)
                .containsExactly(tuple(Kilometer.of(9), 선릉역.getId()));
    }

    /**
     * @given 추가될 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 기존 구간 상행역에 구간을 추가할 때
     * @then 구간이 추가된다.
     */
    @Test
    @DisplayName("기존 구간 상행역에 구간을 추가할 때 구간이 추가된다.")
    void addSectionAtSectionUpStation() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");
        Station 성수역 = Station.of(new Station.Id(4L), "성수역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        이호선.addSection(역삼역, 성수역, Kilometer.of(1), manager);

        //then
        assertThat(이호선.getSections())
                .filteredOn(SubwaySection::getUpStationId, 역삼역.getId())
                .extracting(SubwaySection::getDistance, SubwaySection::getDownStationId)
                .containsExactly(tuple(Kilometer.of(1), 성수역.getId()));
    }

    /**
     * @given 추가될 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 기존 구간 하행역에 구간을 추가할 때
     * @then 구간이 추가된다.
     */
    @Test
    @DisplayName("기존 구간 하행역에 구간을 추가할 때 구간이 추가된다.")
    void addSectionAtSectionDownStation() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");
        Station 성수역 = Station.of(new Station.Id(4L), "성수역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        이호선.addSection(성수역, 선릉역, Kilometer.of(1), manager);

        //then
        assertThat(이호선.getSections())
                .filteredOn(SubwaySection::getUpStationId, 성수역.getId())
                .extracting(SubwaySection::getDistance, SubwaySection::getDownStationId)
                .containsExactly(tuple(Kilometer.of(1), 선릉역.getId()));
    }

    /**
     * @given 추가될 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 지하철 노선 기점에 구간을 추가할 때
     * @then 구간이 추가된다.
     */
    @Test
    @DisplayName("노선 기점에 구간을 추가할 때 구간이 추가된다.")
    void addSectionAtStart() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");
        Station 성수역 = Station.of(new Station.Id(4L), "성수역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        이호선.addSection(성수역, 강남역, Kilometer.of(1), manager);

        //then
        assertThat(이호선.getSections())
                .filteredOn(SubwaySection::getUpStationId, 성수역.getId())
                .extracting(SubwaySection::getDistance, SubwaySection::getDownStationId)
                .containsExactly(tuple(Kilometer.of(1), 강남역.getId()));
    }


    /**
     * @given 추가될 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 지하철 노선의 기점에 구간을 추가할 때
     * @then 노선의 기점역이 추가된 구간의 상행역으로 변경된다.
     */
    @Test
    @DisplayName("노선 기점에 구간을 추가할 때 노선의 기점역이 추가된 구간의 상행역으로 변경된다.")
    void updateStartStationIdWhenAddSectionAtStart() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");
        Station 성수역 = Station.of(new Station.Id(4L), "성수역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        이호선.addSection(성수역, 강남역, Kilometer.of(1), manager);

        //then
        assertThat(이호선.getStartStationId()).isEqualTo(성수역.getId());
    }

    /**
     * @given 추가될 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 지하철 노선에 구간을 추가할 때 새 구간의 거리가 중복된 구간 거리와 같으면
     * @then 에러가 발생한다.
     */
    @Test
    @DisplayName("노선 중간에 중복된 구간과 거리가 같은 구간은 추가할 수 없다.")
    void addSectionAtMiddleWithSameDistance() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");
        Station 성수역 = Station.of(new Station.Id(4L), "성수역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        Throwable throwable = catchThrowable(() -> 이호선.addSection(역삼역, 성수역, Kilometer.of(10), manager));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("거리는 0 초과이어야 합니다.");
    }

    /**
     * @given 추가될 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 지하철 노선의 기점과 종점을 연결하는 구간을 추가할 때
     * @then 에러가 발생한다.
     */
    @Test
    @DisplayName("지하철 노선은 기점과 종점을 연결하는 구간을 추가할 수 없다.")
    void cantCircular() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        Throwable throwable = catchThrowable(() -> 이호선.addSection(선릉역, 강남역, Kilometer.of(10), manager));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 순환되어있습니다.");
    }

    /**
     * @given 추가될 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 기존 노선과 추가될 구간이 연결되어 있지 않으면
     * @then 에러가 발생한다.
     */
    @Test
    @DisplayName("기존 노선과 추가될 구간이 연결되어 있지 않으면 에러가 발생한다.")
    void mustConnected() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");
        Station 성수역 = Station.of(new Station.Id(4L), "성수역");

        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        Throwable throwable = catchThrowable(() -> 이호선.addSection(선릉역, 성수역, Kilometer.of(10), manager));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 연결되어있지 않습니다.");
    }

    /**
     * @given 추가될 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 기존 노선과 구간이 완벽히 중복된다면
     * @then 에러가 발생한다.
     */
    @Test
    @DisplayName("기존 노선과 완벽히 중복된 구간은 추가할 수 없다.")
    void cantDuplicated() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");
        Station 성수역 = Station.of(new Station.Id(4L), "성수역");


        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));
        SubwaySection thirdSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(선릉역), SubwaySectionStation.from(성수역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection, thirdSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        Throwable throwable = catchThrowable(() -> 이호선.addSection(역삼역, 성수역, Kilometer.of(10), manager));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("구간이 중복되어있습니다.");
    }

    /**
     * @given 추가될 지하철 역이 존재하고
     * @given 지하철 노선이 존재하고
     * @when 기존 노선에 존재하는 같은 구간을 추가하면
     * @then 에러가 발생한다.
     */
    @Test
    @DisplayName("기존 노선에 존재하는 구간을 추가할 수 없다.")
    void cantExistSection() {
        //given
        Station 강남역 = Station.of(new Station.Id(1L), "강남역");
        Station 역삼역 = Station.of(new Station.Id(2L), "역삼역");
        Station 선릉역 = Station.of(new Station.Id(3L), "선릉역");
        Station 성수역 = Station.of(new Station.Id(4L), "성수역");


        //given
        SubwaySection firstSection = SubwaySection.of(new SubwaySection.Id(1L), SubwaySectionStation.from(강남역), SubwaySectionStation.from(역삼역), Kilometer.of(10));
        SubwaySection secondSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(역삼역), SubwaySectionStation.from(선릉역), Kilometer.of(10));
        SubwaySection thirdSection = SubwaySection.of(new SubwaySection.Id(2L), SubwaySectionStation.from(선릉역), SubwaySectionStation.from(성수역), Kilometer.of(10));

        SubwayLine 이호선 = SubwayLine.of(new SubwayLine.Id(1L), "2호선", "green", 강남역.getId(), List.of(firstSection, secondSection, thirdSection));
        SectionAddManager manager = new SectionAddManager();

        //when
        Throwable throwable = catchThrowable(() -> 이호선.addSection(선릉역, 성수역, Kilometer.of(10), manager));

        //then
        assertThat(throwable)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 등록된 구간입니다.");
    }
}
