# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션


## [Step - 0] 실습 - 단위 테스트 작성


### 고민
- [X] 지난 주차에 정의했던, `canSectionSave`를 `addSection` 메서드 안에서 호출되도록 이동?
  - Service 계층에서는 `addSection`만 호출하게 하는 것이 좋은 구조일까?
    - 이게 정말 도메인 역할과 책임을 분리한다는 것일까?
      - 예외를 정확하게 알려줘야할까?
      - 가령, 구간을 추가할 때 기존 노선 마지막 역과 연결할 수 없는 경우 '노선 마지막 역과 연결할 수 없습니다.'

- [X] Line에 포함된 모든 구간을 조회할 때, `getSections().getSections()`보다 깔끔한 방법이 없을까?
- [X] 테스트에서 Stubbing 하기 위해서, Station Entity에 `updateId` 메서드를 추가했다. 좋은 방법일까?
- [X] 엔티티별 핵심 메서드에 대해서 테스트를 했다면, Service 레이어에서는 어떠한 테스트를 해야할까?
- [ ] 테스트 코드 작성 중(getStations() 비교) LazyInitializationException이 발생했다. fetch = FetchType.EAGER 속성을 추가하는 것이 무조건 정답일까?
  - `2024-02-20` `@EntityGraph` 적용 시도 => 실패
    ```
      public interface LineRepository extends JpaRepository<Line, Long> {
        @EntityGraph(attributePaths = "sections")
        Optional<Line> findLineWithAllSectionsById(@Param("id") Long lineId);
      }
    ```
- [X] 지하철 구간 목록이 내부적으로 연결되도록 정렬하는 메서드(`sortByConnectedSections`)를 2중 for문으로 구현했다. 최적화 혹은 다른 방법이 있을까?
  - [X] 2중 for문은 가독성이 굉장히 떨어진다. 리팩토링 해보자.
- [X] **정렬된 구간 목록** ... 처음 보는 사람(과거의 나와 같은? 데이터베이스에서 데이터를 가져올 때 순서가 보장되지 않음을 인지하지 못한?) 개발자는 이해하지 못할 수 있다. 과연 메서드 명을 통해, 명시적으로 알려주는 것이 좋은 방향일까?