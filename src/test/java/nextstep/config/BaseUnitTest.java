package nextstep.config;

import nextstep.subway.exception.ExceptionCode;
import nextstep.subway.exception.SubwayException;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;

public abstract class BaseUnitTest {

  protected void assertThatThrowsSubwayException(ThrowingCallable executable, ExceptionCode exceptionCode) {
    SubwayException subwayException = Assertions.catchThrowableOfType(executable, SubwayException.class);
    Assertions.assertThat(subwayException)
        .extracting(SubwayException::getExceptionCode)
        .isEqualTo(exceptionCode);
  }
}
