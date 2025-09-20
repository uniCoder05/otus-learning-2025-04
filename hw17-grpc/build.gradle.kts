import com.google.protobuf.gradle.id

plugins {
    id("idea")
    id("com.google.protobuf")
}

val errorProneAnnotations: String by project
val tomcatAnnotationsApi: String by project
val grpc: String by project
val grpcProtobuf: String by project

dependencies {
    implementation ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")

    implementation("ch.qos.logback:logback-classic")

    implementation("io.grpc:grpc-netty:$grpc")
    implementation("io.grpc:grpc-protobuf:$grpc")
    implementation("io.grpc:grpc-stub:$grpc")
    implementation("com.google.protobuf:protobuf-java:$grpcProtobuf")
    implementation("com.google.errorprone:error_prone_annotations:$errorProneAnnotations")
    implementation("org.apache.tomcat:annotations-api:$tomcatAnnotationsApi")
}

val protoSrcDir = "$projectDir/build/generated/sources/proto"

idea {
    module {
        sourceDirs = sourceDirs.plus(file("$protoSrcDir/main/java"))
        sourceDirs = sourceDirs.plus(file("$protoSrcDir/main/grpc"))
    }
}

sourceSets {
    main {
        java {
            srcDirs(
                "$protoSrcDir/main/java",
                "$protoSrcDir/main/grpc"
            )
        }
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$grpcProtobuf"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpc"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") {
                    outputSubDir = "grpc"
                }
            }
        }
    }
}

tasks.named("generateProto") {
    dependsOn(tasks.named("processResources"))
}

tasks.named("clean") {
    doLast {
        delete(protoSrcDir)
    }
}