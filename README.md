# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

### step 1 - 단위 테스트 작성
1. 지하철 구간 관련 단위 테스트 완성하기
   1. 구간 단위 테스트 (LineTest)
   2. 구간 서비스 단위 테스트 with Mock (LineServiceMockTest)
   3. 구간 서비스 단위 테스트 without Mock (LineServiceTest)
2. 단위 테스트를 기반으로 비즈니스 로직 리팩터링 하기
3. 비즈니스 로직 리팩터링
   1. 구간 추가/삭제 기능에 대한 비즈니스 로직은 현재 LineService 에 대부분 위치
      1. 비즈니스 로직을 도메인 클래스(Line)으로 옮기기
   2. 리팩터링 시 LineTest 의 테스트 메서드를 활용하여 `TDD 사이클`로 리팩터링을 진행
   3. 리팩터링 과정에서 Line 이외 추가적인 클래스가 생겨도 좋음
      1. 구간 관리에 대한 책임을 Line 외 별도의 도메인 객체가 가지게 할 수 있음
         1. Line 이 List 형태로 가지고 있던 Section 을 Sections 라는 일급 콜렉션을 이용하여 분리함
4. step 1 피드백 리스트
   1. StationService -> StationRepository 로 했을 때의 side effect
   2. request 객체에 valid 함수를 생성! -> 이 경우 Service 레이어에서 검증하는데 이렇게 하기 보다는 요청을 받는 순간 검증을 하도록! -> spring validation 사용
   3. 구간을 등록할 때 (구간 등록시) 요구 사항 준수!
   4. Sections 에서 예외 사항을 자세하게 분리하는 방식!
   5. Sections 에서 지하철 노선에 상행 종점역과 하행 종점역만 있는 경우에 대한 예외 처리가 없다.
   6. LineTest 에서 하드 코딩보다는 전역 상수로 선언해보자!
   7. LineTest 에서 예외 케이스에 대한 검증 테스트도 같이 해보자!