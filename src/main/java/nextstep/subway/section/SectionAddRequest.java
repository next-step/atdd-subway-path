package nextstep.subway.section;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SectionAddRequest {
    private Long upstationId;
    private Long downstationId;
    private int distance;

    /*
    * LineServiceMockTest given절 세팅을 위해
    * @AllArgsConstructor를 추가했는데
    * 테스트를 위해 이렇게 메소드를 추가해도 괜찮은 걸까요?
    * */
}
