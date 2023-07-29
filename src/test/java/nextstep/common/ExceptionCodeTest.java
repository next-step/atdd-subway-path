package nextstep.common;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.exception.ExceptionCode;
import nextstep.subway.exception.SecetionExceptionCode;
import nextstep.subway.exception.StationExceptionCode;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ExceptionCodeTest {

  @Test
  @DisplayName("예외코드의 시스템 예외코드는 모두 유니크해야한다.")
  void exceptionCodeTest() {

    // given
    List<ExceptionCode> exceptionCodes = Stream.of (
        SecetionExceptionCode.values(),
        StationExceptionCode.values()
    ).flatMap(Stream::of)
     .collect(Collectors.toList());

    // when
    List<String> serviceErrorCodeList = exceptionCodes.stream()
        .map(ExceptionCode::getServiceErrorCode)
        .distinct()
        .collect(Collectors.toUnmodifiableList());

    // then
    Assertions.assertThat(serviceErrorCodeList)
        .asList()
        .hasSize(exceptionCodes.size());
  }
}
