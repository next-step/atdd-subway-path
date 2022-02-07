<p align="center">
    <img width="200px;" src="https://raw.githubusercontent.com/woowacourse/atdd-subway-admin-frontend/master/images/main_logo.png"/>
</p>
<p align="center">
  <img alt="npm" src="https://img.shields.io/badge/npm-6.14.15-blue">
  <img alt="node" src="https://img.shields.io/badge/node-14.18.2-blue">
  <a href="https://edu.nextstep.camp/c/R89PYi5H" alt="nextstep atdd">
    <img alt="Website" src="https://img.shields.io/website?url=https%3A%2F%2Fedu.nextstep.camp%2Fc%2FR89PYi5H">
  </a>
</p>

<br>

# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

<br>

## 🚀 Getting Started

### Install
#### npm 설치
```
cd frontend
npm install
```
> `frontend` 디렉토리에서 수행해야 합니다.

### Usage
#### webpack server 구동
```
npm run dev
```
#### application 구동
```
./gradlew bootRun
```

---

## 단위 테스트 실습

- 단위 테스트 코드 작성
  - [x] LineServiceMockTest
  - [x] LineServiceTest
- 비즈니스 로직 리팩토링
  - [x] LineTest 작성 (TDD 사이클)
    - [x] LineService 의 비즈니스 로직을 도메인으로 이동
          
---

## 1단계 - 구간 추가 기능 변경

- [x] 인수 조건 도출
- [x] 인수 테스트 작성
- [x] 신규 구간 추가 (TDD 사이클 진행)
  - [x] 상행 종점역 추가
    - [x] 신규 구간의 하행역이 기존 구간의 상행 종점역인 경우
  - [x] 하행 종점역 추가
    - [x] 신규 구간의 상행역이 기존 구간의 하행 종점역이 경우
  - [x] 신규 구간의 상행역과 하행역 중 하나도 기존 구간에 포함되지 않으면 추가 불가
  - [x] 신규 구간과 기존 구간과 동일한 경우 추가 불가
  - [x] 역과 역 사이 신규 역 추가 (기존 구간 사이에 구간 추가)
    - [x] 신규 구간의 상행역이 기존 구간의 상행역중 하나인 경우
    - [x] 신규 구간의 하행역이 기존 구간의 하행역중 하나인 경우
    - [x] 신규 구간의 거리가 기존 구간의 거리보다 같거나 크면 추가 불가
    - [x] 신규 구간의 거리에 따라 기존 구간 거리 수정
- [x] 노선 조회 시 역 목록 순서 수정
    - [x] 상행 종점역 -> 하행 종점역


### 1단계 인수 조건

- 유저스토리 : 시민들의 편의를 위해 지하철 구간에 역을 추가한다.
- 시나리오
  - 상행 종점역을 추가할 수 있다.
  - 하행 종점역을 추가할 수 있다.
  - 역과 역 사이(구간)에 역을 추가할 수 있다.
    - 신규 구간의 거리는 기존 구간의 거리보다 같거나 크면 추가할 수 없다.
    - 기존 노선에 포함된 역을 추가할 수 없다.
    - 신규 구간의 역들이 기존 노선에 하나도 포함되지 않으면 추가할 수 없다.


Feature: 구간 추가 기능

    Rule: 노선은 반드시 1개 이상의 구간을 가진다

    Background: 
        Given: 1개의 구간을 가진 노선을 생성하고
        And: 신규 역을 생성 한다

    Scenario: 새로운 상행 종점역 추가
        When 기존 구간의 상행 종점역과 동일한 하행역을 가지는 구간 생성 요청 하면
        Then 구간 생성이 성공하고
        And 역 목록을 응답 받는다

    Scenario: 새로운 하행 종점역 추가
        When 기존 구간의 하행 종점역과 동일한 상행역을 가지는 구간 생성 요청 하면
        Then 구간 생성이 성공하고
        And 역 목록을 응답 받는다

    Scenario: 새로운 구간 추가
        When 기존 구간의 상행역과 동일한 상행역을 가지는 구간 생성 요청 하면
        Then 구간 생성이 성공하고
        And 신규역과 기존 역의 길이가 신규 구간의 길이만큼 줄고
        And 역 목록을 응답 받는다

    Scenario: 기존 구간보다 길이가 긴 새로운 구간 추가
        When 기존 구간의 역 사이의 거리보다 크거나 같은 거리를 가진 신규 구간 생성 요청하면
        Then 구간 생성이 실패한다

    Scenario: 기존 구간에 모두 포함된 역을 가지는 구간 추가
        When 기존 구간에 등록된 역을 가지는 구간 생성 요청 하면
        Then 구간 생성이 실패한다

    Scenario: 기존 구간에 모두 포함되지 않은 역을 가지는 구간 추가
        When 기존 구간에 등록되지 않은 역을 가지는 구간 생성 요청 하면
        Then 구간 생성이 실패한다


### 1단계 피드백
- [x] 인수 테스트 코드 리팩토링
  - 메소드 분리
  - 중복 제거
- [x] 단위 테스트 코드 리팩토링 (메서드 분리)

### 2단계 요구 사항 정리
- [x] 인수 조건 도출
- [x] 인수 테스트 작성
- [x] 구간 제거 기능 변경
  - [x] 구간이 1개 남은 경우 삭제 불가
  - [x] 구간에 등록되지 않은 역 삭제 불가
  - [x] 하행 종점역 삭제
  - [x] 상행 종점역 삭제
  - [x] 중간 역 삭제
    - [x] 구간의 거리는 삭제 전 두 구간의 거리의 합으로 재 설정

### 2단계 인수 조건

- 유저스토리 : 운행 중단된 지하철 구간의 역을 삭제한다.
- 시나리오
  - 상행 종점역을 삭제할 수 있다.
  - 하행 종점역을 삭제할 수 있다.
  - 중간 역을 삭제한다.
    - 삭제된 역의 상행 역과 하행 역이 하나의 구간으로 재 설정된다.
    - 재 설정된 역의 거리는 변경되지 않는다.
  - 구간에 등록되지 않은 역은 삭제할 수 없다.
  - 노선에 구간이 1개 인경우 삭제 할 수 없다.

Feature: 구간 제거 기능 변경

    Rule: 노선은 반드시 1개 이상의 구간을 가진다

    Background: 
        Given: 2개의 구간을 가진 노선을 생성한다

    Scenario: 상행 종점역 삭제
        When 상행 종점역 삭제 요청을 하면
        Then 구간 삭제가 성공하고
        And 역 목록을 응답 받는다

    Scenario: 하행 종점역 삭제
        When 하행 종점역 삭제 요청을 하면
        Then 구간 삭제가 성공하고
        And 역 목록을 응답 받는다

    Scenario: 중간 역 삭제
        When 중간 역 삭제 요청을 하면
        Then 구간 삭제가 성공하고
        And 역 목록을 응답 받는다
        And 구간의 거리는 유지된다

    Scenario: 존재하지 않는 중간 역 삭제
        When 존재하지 않는 중간 역 삭제 요청을 하면
        Then 구간 삭제가 실패한다
    
    Scenario: 노선이 1개인 경우 역 삭제
        When 상행 종점역, 하행 종점역을 제외한 모든 중간 역을 삭제 요청하고
        When 종점 역 삭제 요청을 하면
        Then 구간 삭제가 실패한다


### 2단계 피드백

- [x] 구간 삭제 후 거리 검증 추가
  - 테스트만을 위한 프로덕션 코드가 생기는건 좋지 않지만 구간 삭제 후 거리 변경은 중요 로직 중 하나이고, 추후에 API로 제공될 가능성이 있다면 괜찮다고 생각된다(오버 엔지니어링으로 봐야하는건가?)
  - 무분별하게 테스트'만'을 위한 프로덕션 코드는 좋지 않다(fact)
  - (구간 등록 후 거리 검증 하기 위해 이미 관련 메소드를 만들어 두었다....)
- [x] 커스텀 예외 클래스 정적 팩토리 메소드 적용
  - 세부사항을 정적 메소드로 구분하지 않고 클래스로 분리하면 클래스가 너무 많아질 수 있다.(트레이드 오프?)
  - 상황에 맞춰서 잘 사용해보자.
- [x] HttpStatusCode 구분
  - 400 (Bad Request) (피드백:요청값의 포맷 오류)
    - 타입 오류, 필수 값 누락 등 API 명세를 지키지 않은 경우가 제일 적합한 것 같다
    - 서버는 무엇이 잘못된 건지 알려주면 더 좋을 것
  - 409 (Conflict) (피드백:논리적인 오류)
    - 이름(conflict)만으로는 매우 추상적이다.
    - 요구사항의 제한사항에 위배되는 경우가 가장 적합할듯 하다
    - 구간이 1개 남은 경우 구간을 삭제할 수 없다 는 제약사항은 API 명세서에도 작성되지 않는다.
    - 구간에 포함되지 않는 역은 삭제할 수 없다 는 제약사항도 API 명세서에 없다.

### 3단계 요구 사항 정리
- [ ] 최단 경로 조회 인수 조건 도출
- [ ] 최단 경로 조회 인수 테스트 작성
- [ ] 최단 경로 조회 기능 구현 
  - [ ] 출발역이 존재하지 않은 경우 조회 불가
  - [ ] 도착역이 존재하지 않은 경우 조회 불가
  - [ ] 출발역과 도착역이 동일한 경우 조회 불가
  - [ ] 출발역에서 도착역을 도달할 수 없는 경우 조회 불가
  - [ ] 출발역과 도착역의 최단 경로 조회
    - jgrapht 라이브러리 다익스트라 알고리즘 활용

