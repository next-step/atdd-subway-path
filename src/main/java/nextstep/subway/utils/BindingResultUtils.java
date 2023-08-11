package nextstep.subway.utils;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class BindingResultUtils {

  public static List<String> getAllErrorMessagesFrom(BindingResult bindingResult) {
    return bindingResult.getAllErrors().stream()
        .map(ObjectError::getDefaultMessage)
        .collect(Collectors.toList());
  }
}
