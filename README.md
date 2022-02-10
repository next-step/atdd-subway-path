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

## Step1 - 구간 추가 기능 변경
### 요구사항
- 변경된 스펙에 대한 인수 조건 도출 및 인수 테스트 작성
  - 변경된 스펙 - 구간 추가 제약사항 변경
    - 역 사이에 새로운 역을 등록할 경우 기존 구간이 역을 기존으로 새로운 구간을 추가한다.
      - *A - C* 구간에 *A - B* 구간을 추가하는 경우 A-B, B-C 구간이 생긴다.
      - [X] 인수 조건 작성
        - scenario 노선에 구간 추가 
        - given 노선을 생성하고
        - when 노선 구간의 시작역을 기준으로 새로운 구간을 추가하면
        - then 새로운 구간이 추가된다.
      - [X] 인수 테스트 작성
      - [X] TDD로 기능 구현
    - 새로운 역을 상행 종점으로 등록할 경우 기존 구간 앞에 구간이 추가된다.
      - [X] 인수 조건 작성
        - given 노선을 생성하고
        - when 노선 구간의 상행역을 새로운 구간의 하행역으로 구간을 추가하면
        - then 새로운 구간이 추가된다.
      - [X] 인수 테스트 작성
    - 새로운 역을 하행 종점으로 등록할 경우 기존 구간 뒤에 구간이 추가된다.
      - [X] 인수 조건 작성
        - given 노선을 생성하고
        - when 노선 구간의 하행역을 새로운 구간의 상행역으로 구간을 추가하면
        - then 새로운 구간이 추가된다.
      - [X] 인수 테스트 작성
  - 변경된 스펙 - 예외 케이스
    - 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음
      - [X] 인수 조건 작성
        - given 노선을 생성하고
        - when 기존 구간과 길이가 같거나 긴 구간을 중간에 추가하면
        - then 구간 등록이 실패한다.
      - [X] 인수 테스트 작성
    - 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
      - [X] 인수 조건 작성
        - given 노선을 생성하고
        - when 기존 구간과 동일한 구간을 추가하면
        - then 구간 등록이 실패한다.
      - [X] 인수 테스트 작성
    - 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음
      - [X] 인수 조건 작성
        - given 노선을 생성하고
        - when 기존 구간에 포함되지 않는 역으로 이루어진 구간을 추가하면
        - then 구간 등록이 실패한다.
      - [X] 인수 테스트 작성
- 구간 추가 기능 요구사항 변경 대응
- 비즈니스 로직 TDD로 기능 구현

## Step2 - 구간 제거 기능 변경
### 요구사항
- 변경된 스펙에 대한 인수 조건 도출 및 인수 테스트 작성
  - 구간 삭제에 대한 제약 사항 변경 구현
    - 중간 역 삭제 기능 추가
      - [X] 인수 조건 작성
        - given 노선을 생성을 요청하고
        - and 구간을 추가 요청한 후
        - when 중간 역을 삭제 요청하면
        - then 삭제 요청이 성공한다.
      - [X] 인수 테스트 작성
      - [X] 기능 구현
    - 구간이 하나인 노선에서 마지막 구간을 제거할 때 제거 불가
      - [X] 인수 조건 작성
        - given 노선을 생성을 요청하고
        - when 구간 추가 없이 상행 역을 삭제 요청하면
        - then 삭제 요청이 실패한다.
      - [X] 인수 테스트 작성
      - [X] 기능 구현
    - 예외 케이스 고려
      - 등록되지 않은 역
        - [X] 인수 조건 작성
          - given 노선을 생성을 요청하고
          - and 역을 생성한 후
          - when 등록되지 않은 역을 삭제 요청하면
          - then 삭제 요청이 실패한다.
        - [X] 인수 테스트 작성
        - [X] 기능 구현
- 구간 추가 기능 요구사항 변경 대응
- 비즈니스 로직 TDD로 기능 구현

## Step3 - 경로 조회
### 요구사항
- 최단 경로 조회 인수 조건 도출
- 최단 경로 조회 인수 테스트 만들기
- 최단 경로 조회 기능 구현하기
