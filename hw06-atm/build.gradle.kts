plugins {
    id("java")
}

group = "ru.otus"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.11.1")
    implementation ("ch.qos.logback:logback-classic")
}

tasks.test {
    useJUnitPlatform()
}