# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 🚀 실습 - 단위 테스트 작성
### 요구사항
- [x] 지하철 구간 관련 단위 테스트를 완성하세요.
  - [x] 구간 단위 테스트 (LineTest)
  - [x] 구간 서비스 단위 테스트 with Mock (LineServiceMockTest)
  - [x] 구간 서비스 단위 테스트 without Mock (LineServiceTest)
- [x] 단위 테스트를 기반으로 비즈니스 로직을 리팩터링 하세요.
  - 구간 추가/삭제 기능에 대한 비즈니스 로직은 현재 LineService에 대부분 위치하고 있음
  - 비즈니스 로직을 도메인 클래스(Line)으로 옮기기
  - 리팩터링 시 LineTest의 테스트 메서드를 활용하여 TDD 사이클로 리팩터링을 진행
  - 리팩터링 과정에서 Line 이외 추가적인 클래스가 생겨도 좋음
  - 구간 관리에 대한 책임을 Line 외 별도의 도메인 객체가 가지게 할 수 있음

## 🚀 1단계 - 지하철 구간 추가 기능 개선
### 요구사항
- 구간 추가 제약사항 변경
  - [x] 역 사이에 새로운 역을 등록할 경우
  - [x] 새로운 역을 상행 종점으로 등록할 경우
  - [x] 새로운 역을 하행 종점으로 등록할 경우
- [x] 노선 조회시 응답되는 역 목록 수정
  - 구간이 저장되는 순서로 역 목록을 조회할 경우 순서가 다르게 조회될 수 있음
  - 아래의 순서대로 역 목록을 응답하는 로직을 변경해야 함
    1. 상행 종점이 상행역인 구간을 먼저 찾는다.
    2. 그 다음, 해당 구간의 하행역이 상행역인 다른 구간을 찾는다.
    3. 2번을 반복하다가 하행 종점역을 찾으면 조회를 멈춘다.
- 변경된 스펙 - 예외 케이스
  - [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
  - [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
  - [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음