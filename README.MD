如果想要启动程序，你需要一个application.yml模板
````yaml
server:
  port: 80
spring:
  mail:
    username: *
    password: *
    host: *
    properties:
      mail.smtp.ssl.enable: *
````
程序基于jdk11