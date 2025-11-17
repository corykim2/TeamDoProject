plugins {
	java
	id("org.springframework.boot") version "3.5.6"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.TeamAA"
version = "0.0.1-SNAPSHOT"
description = "Demo project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.14") //스웨거
	//implementation("org.springframework.boot:spring-boot-starter-security")
	//테스트용으로 잠시 주석처리
	//나중에 로그인 구현하고 다시 입력
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.mindrot:jbcrypt:0.4") //비밀번호 해싱

	implementation("org.springdoc:springdoc-openapi-ui:1.6.14")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation ("org.springframework.session:spring-session-data-redis")
	implementation ("redis.clients:jedis")  // 또는 lettuce
	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
