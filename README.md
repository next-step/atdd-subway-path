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

# 2단계 - 단위 테스트 작성
## 기능 요구사항
- [x] 역 추가 기능 변경
  - [x] (A -> B) + (A -> C) ==> A -> C -> B
  - [x] (A -> B) + (C -> A) ==> C -> A -> B
  - [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
  - [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
  - [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
- [x] 역 조회 기능 변경
  - 상행 종점 역부터 하행 종점 역까지 순서대로 조회 가능하도록 변경

# 3단계 - 지하철 구간 제거 리팩터링
## 기능 요구사항
- [ ] 구간 삭제 제약 사항 변경
  - [ ] 위치에 상관 없이 삭제 가능
    - [ ] 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
    - [ ] 중간역이 제거될 경우 재배치를 함
  - [ ] 구간이 하나인 노선에서 구간을 제거할 수 없음
- [ ] 다양한 예외 케이스 고려