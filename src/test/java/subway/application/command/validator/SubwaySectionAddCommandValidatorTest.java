package subway.application.command.validator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.application.command.in.SubwaySectionAddUsecase;
import subway.common.annotation.UnitTest;
import subway.core.ValidationErrorException;
import subway.domain.Kilometer;
import subway.domain.Station;
import subway.domain.SubwayLine;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 지하철 구간 추가 요청 검증을 테스트합니다.
 */
@UnitTest
@DisplayName("지하철 구간 추가 커맨드 검증 단위 테스트")
class SubwaySectionAddCommandValidatorTest {

    private final SubwaySectionAddCommandValidator validator = new SubwaySectionAddCommandValidator();

    /**
     * @when 지하철 구간 추가 요청이 들어올 때
     * @then 검증에 성공된 커맨드 객체를 반환한다.
     */
    @Test
    @DisplayName("추가 요청 시에 검증에 성공된 커맨드 객체를 반환한다.")
    void createCommand() {
        //when
        SubwaySectionAddUsecase.Command.SectionCommand subwaySection = SubwaySectionAddUsecase.Command.SectionCommand
                .builder()
                .upStationId(new Station.Id(1L))
                .downStationId(new Station.Id(2L))
                .distance(Kilometer.of(1))
                .build();

        SubwaySectionAddUsecase.Command command = SubwaySectionAddUsecase.Command
                .builder()
                .subwayLineId(new SubwayLine.Id(1L))
                .subwaySection(subwaySection)
                .validator(validator)
                .build();
        //then
        assertAll(
                () -> assertThat(command).isNotNull()
                        .extracting(SubwaySectionAddUsecase.Command::getSubwaySection)
                        .extracting(
                                SubwaySectionAddUsecase.Command.SectionCommand::getDistance,
                                SubwaySectionAddUsecase.Command.SectionCommand::getUpStationId,
                                SubwaySectionAddUsecase.Command.SectionCommand::getDownStationId)
                        .containsExactly(Kilometer.of(1), new Station.Id(1L), new Station.Id(2L)),
                () -> assertThat(command.getSubwaySection().getDistance().isPositive()).isTrue());
    }

    /**
     * @when 거리가 0 이하인 지하철 구간 추가 요청이 들어올 때
     * @then 에러를 발생한다.
     */
    @Test
    @DisplayName("거리가 0인 지하철 구간 추가 요청은 불가능하다.")
    void validateDistance() {
        //when
        Throwable throwable = Assertions.catchThrowable(() -> 구간_추가_요청을_한다(0));

        //then
        assertThat(throwable)
                .isExactlyInstanceOf(ValidationErrorException.class);
    }

    private SubwaySectionAddUsecase.Command 구간_추가_요청을_한다(int distance) {
        SubwaySectionAddUsecase.Command.SectionCommand subwaySection = SubwaySectionAddUsecase.Command.SectionCommand
                .builder()
                .upStationId(new Station.Id(1L))
                .downStationId(new Station.Id(2L))
                .distance(Kilometer.of(distance))
                .build();

        return SubwaySectionAddUsecase.Command
                .builder()
                .subwayLineId(new SubwayLine.Id(1L))
                .subwaySection(subwaySection)
                .validator(validator)
                .build();
    }

    /**
     * @when upStationId과 downStationId가 같은 지하철 구간 추가 요청이 들어올 때
     * @then 에러를 발생한다.
     */
    @Test
    @DisplayName("upStationId과 downStationId가 같은 지하철 구간 추가 요청은 불가능하다.")
    void validateSection() {
        //when
        Throwable throwable = Assertions.catchThrowable(() -> 구간_추가_요청을_한다(1L, 1L));

        //then
        assertThat(throwable)
                .isExactlyInstanceOf(ValidationErrorException.class);
    }

    private SubwaySectionAddUsecase.Command 구간_추가_요청을_한다(Long upStationId, Long downStationId) {
        SubwaySectionAddUsecase.Command.SectionCommand subwaySection = SubwaySectionAddUsecase.Command.SectionCommand
                .builder()
                .upStationId(new Station.Id(upStationId))
                .downStationId(new Station.Id(downStationId))
                .distance(Kilometer.of(1))
                .build();

        return SubwaySectionAddUsecase.Command
                .builder()
                .subwayLineId(new SubwayLine.Id(1L))
                .subwaySection(subwaySection)
                .validator(validator)
                .build();
    }
}