# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 🚀 실습 - 단위 테스트 작성
### 요구사항
- [ ] 지하철 구간 관련 단위 테스트를 완성하세요.
  - [ ] 구간 단위 테스트 (LineTest)
  - [x] 구간 서비스 단위 테스트 with Mock (LineServiceMockTest)
  - [x] 구간 서비스 단위 테스트 without Mock (LineServiceTest)
- [ ] 단위 테스트를 기반으로 비즈니스 로직을 리팩터링 하세요.
  - 구간 추가/삭제 기능에 대한 비즈니스 로직은 현재 LineService에 대부분 위치하고 있음
  - 비즈니스 로직을 도메인 클래스(Line)으로 옮기기
  - 리팩터링 시 LineTest의 테스트 메서드를 활용하여 TDD 사이클로 리팩터링을 진행
  - 리팩터링 과정에서 Line 이외 추가적인 클래스가 생겨도 좋음
  - 구간 관리에 대한 책임을 Line 외 별도의 도메인 객체가 가지게 할 수 있음