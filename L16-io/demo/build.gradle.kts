plugins {
    id("com.google.protobuf")
    id("idea")
}

dependencies {
    implementation ("ch.qos.logback:logback-classic")
    implementation("com.google.guava:guava")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.glassfish:jakarta.json")
    implementation("com.google.protobuf:protobuf-java-util")
    implementation("com.google.errorprone:error_prone_annotations")
    implementation("com.google.j2objc:j2objc-annotations")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.google.code.gson:gson")

    compileOnly ("org.projectlombok:lombok")
    annotationProcessor ("org.projectlombok:lombok")
}

val protoSrcDir = "$projectDir/build/generated"

idea {
    module {
        sourceDirs = sourceDirs.plus(file(protoSrcDir))
    }
}

sourceSets {
    main {
        proto {
            srcDir(protoSrcDir)
        }
    }
}

protobuf {
    generatedFilesBaseDir = protoSrcDir

    protoc {
        artifact = "com.google.protobuf:protoc:3.19.4"
    }

    generateProtoTasks {
        ofSourceSet("main")
    }
}

afterEvaluate {
    tasks {
        getByName("generateProto").dependsOn(processResources)
    }
}
