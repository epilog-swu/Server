## 스마트워치를 활용한 AI 낙상 감지 및 혈당 관리 서비스 Dialog

<img src="https://github.com/user-attachments/assets/58c6b8ef-c166-45f4-8403-4bb12c023c6f" width="800px" alert="logo"/>

## 사용 기술

> Java, Spring Boot, JPA, MySQL <br/>
> WebSocket <br/>
> AWS Elastic Beanstalk, RDS, Github actions

## 주요 기능

#### ✨ 두 단계의 정밀한 알고리즘을 통한 낙상감지 및 후속 조치

가속도 센서와 자이로 센서를 이용한 SVM 임계값 기반 알고리즘을 통해 비정상적인 움직임을 모니터링합니다. <br/>
이상이 감지되면 낙상 감지를 학습한 AI 모델을 이용해서 낙상을 판단합니다. <br/>

두 단계의 판단을 통해 낙상이 감지되면 워치에 알림을 보내서 환자에게 낙상 여부를 묻습니다. <br/>
비상 상황을 고려하여 15초 이내 응답이 없을 경우 낙상이라고 판단합니다. <br/>

낙상 판단이 끝나면 보호자에게 긴급 SMS를 전송합니다.


#### ✨ 환자의 복약과 식사, 혈당 기록 시간 관리

워치의 햅틱 인터페이스를 활용하여 복약 시간과 식사 시간, 식사 시간 2시간 후 혈당 입력 알림을 보냅니다. <br/>

매일 식사와 복약 체크리스트를 자동으로 생성합니다.<br/>
환자가 해결해야 하는 미션(체크리스트)을 제공하여 자기주도적으로 건강을 챙길 수 있도록 돕습니다.

#### ✨ 혈당, 혈압, 몸무게, 운동, 기분 등 다양한 건강 기록, 자동 문서화

워치와 모바일 어디서나 기록을 남길 수 있게 하여 접근성을 높였습니다. <br/>
작성된 기록은 그래프와 보고서 형태로 확인 가능하며, <br/>
서면 제출에 용이하도록 선택한 기간의 기록을 PDF로 변환하는 기능을 제공합니다.

## 비고
#### SVM 임계값 기반 알고리즘
<img src="https://github.com/user-attachments/assets/28df5aaf-164e-4334-8f3f-e15cfaeb2997" width="780px" />

## ERD
<img src="https://github.com/user-attachments/assets/e8df7518-24b0-4ab8-bc4e-f4e3ef90fdd3" width="780px" alert="ERD" />

## 시스템 아키텍처
<img src="https://github.com/user-attachments/assets/e73892fa-cc88-41b8-886d-de692a326904" width="780px" alert="architecture"/>

## 디렉토리 구조
```
.
├── HELP.md
├── build.gradle
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src
    ├── main
    ├── ├── generated
    └── ├── java
        │   └── com
        │       └── epi
        │           └── epilog
        │               ├── EpilogApplication.java
        │               ├── app
        │               │   ├── controller
        │               │   ├── domain
        │               │   ├── dto
        │               │   ├── repository
        │               │   └── service
        │               └── global
        │                   ├── config
        │                   ├── exception
        │                   ├── socket
        │                   └── utils
        └── resources
            ├── application.yml
            └── templates
                ├── diabetes_log.html
                └── fonts
```

## 팀원
<table>
  <tr>
    <td>양수빈</td>
    <td>박현아</td>
    <td>신서영</td>
  </tr>
  <tr>
    <td>Server/AI Developer</td>
    <td>Android Developer</td>
    <td>Android Developer</td>
  </tr>
</table>

#### 🔗 관련 링크
> 개발과 관련된 더 다양한 이야기를 확인해주세요!<br/>
> <a href="https://github.com/epilog-swu/Front/wiki">Github Wiki 보러가기</a><br/>
