# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 실습
- [x] LineService의 비즈니스 로직을 도메인 클래스(Line)로 옮기기
- [x] 구간 단위 테스트 작성
- [x] 구간 서비스 단위 테스트 with Mock
- [x] 구간 서비스 단위 테스트 without Mock

## STEP 1
- [x] 인수 조건을 검증하는 인수 테스트 작성
  - 인수 조건은 인수 테스트 메서드 상단에 주석으로 작성하세요.
- [x] 인수 테스트를 충족하는 기능 구현
- [x] 인수 테스트 이후 기능 구현은 TDD로 진행
- [x] 도메인 레이어 단위 테스트

### 요구사항
- [x] 기존 구간의 역을 기준으로 새로운 구간을 추가
  - [x] 기존 구간 A-C에 신규 구간 A-B를 추가하는 경우 A역을 기준으로 추가돼요. (A-B-C)
  - [x] 새로운 길이를 뺀 나머지 새롭게 추가된 역과의 길이로 설정
  - [x] 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록할 수 없음.
  - [x] 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음.
  - [x] 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 노선에 추가할 수 없음.
- [x] 노선에 등록된 역 조회 시 아래의 순서대로 역 목록 응답 로직 변경
  1. 상행 종점이 상행역인 구간을 먼저 찾는다역.
  2. 그 다음, 해당 구간의 하행역이 상행역인 다른 구간을 찾는다.
  3. 2를 반복, 하행 종점역을 찾으면 조회 종료 및 반환

## STEP 2
### 요구사항
- [x] 위치에 상관없이 역 삭제가 가능하도록 기능 수정
  - [x] 종점이 제거될 경우 종점 바로 이전 역이 종점이 됨
  - [x] 중간역이 제거될 경우 재배치를 함
  - [x] A - B - C 의 경우 B를 제거할 시 A - C로 재배치됨.
  - [x] 거리는 두 구간의 거리의 합으로 정함.
  - [x] 구간이 하나인 노선에서는 마지막 구간을 제거할 수 없음.
  - [x] 노선에 등록되어있지 않은 역을 제거할 수 없음.
