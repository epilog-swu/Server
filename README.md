## 스마트워치와 AI를 활용한 낙상 감지 및 혈당 관리 서비스, Dialog

<img src="https://github.com/user-attachments/assets/58c6b8ef-c166-45f4-8403-4bb12c023c6f" width="800px" alert="logo"/>

## 사용 기술

> Java, Spring Boot, JPA, MySQL <br/>
> WebSocket <br/>
> AWS Elastic Beanstalk, RDS, Github actions

## 비고
### SVM 임계값 기반 알고리즘
<img src="https://github.com/user-attachments/assets/28df5aaf-164e-4334-8f3f-e15cfaeb2997" width="680px" />

### 전체 낙상 감지 알고리즘
<img src="https://github.com/user-attachments/assets/2fdde6dc-f34b-4e05-94e2-5215da097764" width="680px" />

### ERD
<img src="https://github.com/user-attachments/assets/e8df7518-24b0-4ab8-bc4e-f4e3ef90fdd3" width="700px" alert="ERD" />

### 시스템 아키텍처
<img src="https://github.com/user-attachments/assets/e73892fa-cc88-41b8-886d-de692a326904" width="680px" alert="architecture"/>

## 디렉토리 구조
```
📁 Server
├── build.gradle
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
                ├── images
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
> <a href="https://bini-team.notion.site/API-1d36c84daf184653b2f5985e68207657?pvs=4">API 명세서 보러가기</a> <br/>
> <a href="https://github.com/epilog-swu/Front/wiki">Github Wiki 보러가기</a><br/>
