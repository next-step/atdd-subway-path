# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 1단계 - 단위 테스트 작성
### 요구사항
- [x] 지하철 구간 단위 테스트 완성
  - 구간 단위 테스트 (LineTest)
  - 구간 서비스 단위 테스트 with Mock (LineServiceMockTest)
  - 구간 서비스 단위 테스트 without Mock (LineServiceTest)
- [x] 단위 테스트 기반으로 비즈니스 로직 리팩터링

### 참고
- 기존 기능에 대한 단위 테스트 코드 작성
- 비즈니스 로직을 LineService -> Line (도메인 클래스) 로 옮기기
- 리팩터링 시, LineTest 의 테스트 메서드를 활용하여 TDD 사이클로 진행
- 구간 관리에 대한 책임을 line 외 별도의 도메인 객체가 가지게 할 수 있음