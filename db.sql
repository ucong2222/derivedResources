# 데이터베이스 생성
DROP DATABASE IF EXISTS derivedResources;
CREATE DATABASE derivedResources;
USE derivedResources;

# deriveRequest 테이블 추가
CREATE TABLE deriveRequest (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, # 번호
  regDate DATETIME DEFAULT NULL, # 작성날짜
  updateDate DATETIME DEFAULT NULL, # 갱신날짜
  url CHAR(200) UNIQUE NOT NULL,
  originUrl CHAR(200) UNIQUE NOT NULL,
  width SMALLINT(10) UNSIGNED NOT NULL,
  height SMALLINT(10) UNSIGNED NOT NULL,
  maxWidth SMALLINT(10) UNSIGNED NOT NULL,
  PRIMARY KEY (id)
);

# 파일 테이블 추가
CREATE TABLE genFile (
  id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT, # 번호
  regDate DATETIME DEFAULT NULL, # 작성날짜
  updateDate DATETIME DEFAULT NULL, # 갱신날짜
  delDate DATETIME DEFAULT NULL, # 삭제날짜
  delStatus TINYINT(1) UNSIGNED NOT NULL DEFAULT 0, # 삭제상태(0:미삭제,1:삭제)
  relTypeCode CHAR(50) NOT NULL, # 관련 데이터 타입(article, member)
  relId INT(10) UNSIGNED NOT NULL, # 관련 데이터 번호
  originFileName VARCHAR(100) NOT NULL, # 업로드 당시의 파일이름
  fileExt CHAR(10) NOT NULL, # 확장자
  typeCode CHAR(20) NOT NULL, # 종류코드 (common)
  type2Code CHAR(20) NOT NULL, # 종류2코드 (attatchment)
  fileSize INT(10) UNSIGNED NOT NULL, # 파일의 사이즈
  fileExtTypeCode CHAR(10) NOT NULL, # 파일규격코드(img, video)
  fileExtType2Code CHAR(10) NOT NULL, # 파일규격2코드(jpg, mp4)
  fileNo SMALLINT(2) UNSIGNED NOT NULL, # 파일번호 (1)
  fileDir CHAR(20) NOT NULL, # 파일이 저장되는 폴더명
  PRIMARY KEY (id),
  KEY relId (relId,relTypeCode,typeCode,type2Code,fileNo)
); 

# originUrl 인덱스를 일반 인덱스로 변경
ALTER TABLE deriveRequest DROP INDEX originUrl, ADD KEY originUrl (originUrl); 

# 미디어의 너비, 높이 칼럼 추가
ALTER TABLE genFile ADD COLUMN `width` SMALLINT(2) UNSIGNED NOT NULL AFTER `fileDir`;
ALTER TABLE genFile ADD COLUMN `height` SMALLINT(2) UNSIGNED NOT NULL AFTER `width`;

# 원본 파일을 가지고 있는 요청인지 여부 추가
ALTER TABLE deriveRequest
ADD COLUMN `originStatus` TINYINT(1) UNSIGNED DEFAULT 0 NOT NULL
COMMENT '원본 파일을 가지고 있는 요청인지 여부'
AFTER `originUrl`;

# 관련된 파일 번호
ALTER TABLE deriveRequest
ADD COLUMN `genFileId` INT(1) UNSIGNED DEFAULT 0 NOT NULL
COMMENT '관련된 파일 번호'
AFTER `originUrl`;