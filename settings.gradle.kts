rootProject.name = "otusJava"
include("hw01-gradle")
include("L01-gradle")

include("L02-gradle2")
include("L02-gradle2-libApi")
include("L02-gradle2-libApiUse")
include("L02-logging")

include("L03-qa")
include("L04-generics")
include("L05-collections")
include("L06-annotations")
include("hw03-test-framework")
include("L08-gc:homework")
include("L08-gc:demo")
include("L09-docker")
include("L10-byteCodes")
include("hw05-aop")
include("L11-Java8")
include("L12-solid")
include("L13-creationalPatterns")
include("hw06-atm")
include("L14-behavioralPatterns")
include("L15-structuralPatterns:demo")
include("L15-structuralPatterns:homework")
include("L16-io:demo")
include("L16-io:homework")
include("L17-nio")
include("L18-jdbc:demo")
include("L18-jdbc:homework")

pluginManagement {
    val jgitver: String by settings
    val dependencyManagement: String by settings
    val springframeworkBoot: String by settings
    val johnrengelmanShadow: String by settings
    val jib: String by settings
    val protobufVer: String by settings
    val sonarlint: String by settings
    val spotless: String by settings

    plugins {
        id("fr.brouillard.oss.gradle.jgitver") version jgitver
        id("io.spring.dependency-management") version dependencyManagement
        id("org.springframework.boot") version springframeworkBoot
        id("com.github.johnrengelman.shadow") version johnrengelmanShadow
        id("com.google.cloud.tools.jib") version jib
        id("com.google.protobuf") version protobufVer
        id("name.remal.sonarlint") version sonarlint
        id("com.diffplug.spotless") version spotless
    }
}