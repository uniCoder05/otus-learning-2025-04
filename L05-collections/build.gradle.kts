dependencies {
    implementation("ch.qos.logback:logback-classic")

    testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.openjdk.jmh:jmh-core")
}
