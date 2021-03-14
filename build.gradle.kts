plugins {
    kotlin("jvm") version "1.4.31"
    jacoco
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.8")
//    implementation("org.jetbrains.kotlin.spec.grammar.tools:kotlin-grammar-tools:0.1")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    testImplementation("junit:junit:4.12")
}

group = "com.github.acme"
version = "1.0-SNAPSHOT"
description = "coffee-maker"
java.sourceCompatibility = JavaVersion.VERSION_1_8

tasks {
    getByName<Test>("test") {
        finalizedBy("jacocoTestReport")
    }

    getByName<JacocoReport>("jacocoTestReport") {
        dependsOn("test")
        val src = fileTree("${projectDir}/src/main/kotlin").apply {
            exclude("**/MainKt.class")
        }
        reports {
            sourceDirectories.from(src)
            xml.isEnabled = true
            csv.isEnabled = true
            html.destination = file("${buildDir}/jacocoHtml")
        }
    }
}