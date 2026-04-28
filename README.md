#  TwoWay Movie

영화 관리 및 주차 서비스 기능을 구현한 웹 프로젝트입니다.

---

##  프로젝트 소개
영화 정보를 효율적으로 관리하기 위해 CRUD 기능을 구현하고,  
주차 서비스 기능과 사용자 문의 게시판을 포함한 웹 애플리케이션입니다.

사용자 중심의 기능 설계와 웹 서비스 흐름 이해를 목표로 개발했습니다.

---

##  사용 기술
- Backend: Java, Spring Boot
- Frontend: HTML, CSS
- Database: (사용 DB 입력 ex. MySQL)
- Build Tool: Gradle

---

## ✨ 주요 기능

###  영화 관리 (✔ 담당)
- 영화 등록 (Create)
- 영화 목록 조회 (Read)
- 영화 정보 수정 (Update)
- 영화 삭제 (Delete)

---

###  주차 관리
- 주차 정보 등록 및 관리

---

##  실행 화면

###  영화 목록
- 영화 정보를 리스트 형태로 출력
- 수정 / 삭제 기능 제공

![영화목록](영화정보출력.PNG)
![영화목록](자세히보기.PNG)
---

##  담당 역할
- 영화 관리 CRUD 기능 전체 구현
- Controller / Service / Repository 계층 구조 설계
- 사용자 입력 처리 및 유효성 검사 구현
- UI 화면 구성 및 기능 연결

---

##  실행 방법
```bash
git clone https://github.com/yourusername/twoway_movie_two.git
cd twoway_movie_two
./gradlew bootRun
