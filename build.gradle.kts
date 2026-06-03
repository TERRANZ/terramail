plugins {
    java
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("org.javamodularity.moduleplugin") version "1.8.12"
}

group = "com.terramail"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // JavaFX
    implementation("org.openjfx:javafx-controls:21")
    implementation("org.openjfx:javafx-fxml:21")
    implementation("org.openjfx:javafx-web:21")
    // MySQL
    implementation("com.mysql:mysql-connector-j:8.3.0")

    // JavaMail API
    implementation("com.sun.mail:javax.mail:1.6.2")

    // SLF4J + Logback
    implementation("org.slf4j:slf4j-api:2.0.11")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Jackson
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")

    // Testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaCompile>("compileJava") {
    options.encoding = "UTF-8"
}
