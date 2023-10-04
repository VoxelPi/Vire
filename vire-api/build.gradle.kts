plugins {
    `maven-publish`
    `java-library`
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    compileOnlyApi(kotlin("stdlib"))
    compileOnlyApi(libs.kotlin.reflect)
    compileOnlyApi(libs.bundles.kotlinx.coroutines)

    // Libraries
    compileOnlyApi(libs.bundles.event)
    compileOnlyApi(libs.slf4j.api)
    compileOnlyApi(libs.kotlin.logging.jvm)

    // Tests
    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)

    testImplementation(libs.slf4j.api)
    testImplementation(libs.log4j.slf4j.impl)
}

tasks.test {
    useJUnitPlatform()
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.voxelpi.vire"
            artifactId = "vire-api"
            version = project.version.toString()
            from(components["kotlin"])
            artifact(sourcesJar)
        }
    }
}
