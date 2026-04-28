#  twoway_movie_two

영화 관리 및 주차 서비스 기능을 포함한 웹 프로젝트입니다.

##  프로젝트 소개
Spring Boot 기반으로 영화 정보 관리(CRUD)와 주차 관리 기능을 구현한 프로젝트입니다.  
또한 사용자 문의를 처리할 수 있는 게시판 기능을 포함하고 있습니다.

##  사용 기술
- Backend: Java, Spring Boot
- Frontend: HTML, CSS
- Database: (사용 DB 입력)
- Build Tool: Gradle

##  주요 기능

###  영화 관리 (✔ 담당)
- 영화 등록 (Create)
- 영화 목록 조회 (Read)
- 영화 정보 수정 (Update)
- 영화 삭제 (Delete)

###  주차 관리
- 주차 정보 등록 및 관리

###  문의 게시판 
- 문의 글 작성
- 문의 목록 조회
- 문의 상세 조회
- (댓글/답변 기능 있으면 추가)

##  담당 역할
- 영화 관리 CRUD 기능 전체 구현


##  실행 방법
```bash
git clone https://github.com/yourusername/twoway_movie_two.git
cd twoway_movie_two
./gradlew bootRun
