# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션


## [Step - 0] 실습 - 단위 테스트 작성


### 고민
- [ ] 지난 주차에 정의했던, `canSectionSave`를 `addSection` 메서드 안에서 호출되도록 이동?
  - Service 계층에서는 `addSection`만 호출하게 하는 것이 좋은 구조일까?
    - 이게 정말 도메인 역할과 책임을 분리한다는 것일까?

- [ ] Line에 포함된 모든 구간을 조회할 때, `getSections().getSections()`보다 깔끔한 방법이 없을까?