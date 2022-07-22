# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

# 1단계 - 단위 테스트 작성
## 기능 요구사항
- [x] 지하철 구간 관련 단위 테스트를 완성
  - [x] 구간 단위 테스트 (LineTest)
  - [x] 구간 서비스 단위 테스트 with Mock (LineServiceMockTest)
    - [x] addSection
    - [x] deleteSection
    - [x] saveLine
    - [x] showLines
    - [x] findById
    - [x] updateLine
    - [x] deleteLine
  - [x] 구간 서비스 단위 테스트 without Mock (LineServiceTest)
    - [x] addSection
    - [X] deleteSection
    - [x] saveLine
    - [x] showLines
    - [x] findById
    - [x] updateLine
    - [x] deleteLine
- [x] 단위 테스트를 기반으로 비즈니스 로직을 리팩터링
  - [x] 비즈니스 로직을 도메인 클래스(Line)으로 옮기기