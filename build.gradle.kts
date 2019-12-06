import com.google.protobuf.gradle.*
import org.ostelco.gyorde.gradle.Version




plugins {
    kotlin("jvm") version "1.3.61"
    id("com.google.protobuf") version "0.8.10"
    java
    idea
}

group = "org.ostelco.gyorde"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {

    // Kotlin
    implementation(kotlin("stdlib-jdk8"))

    // Dropwizard
    implementation("io.dropwizard:dropwizard-core:${Version.dropwizard}")
    implementation("io.dropwizard:dropwizard-client:${Version.dropwizard}")
    testImplementation("io.dropwizard:dropwizard-testing:${Version.dropwizard}")

    // gRPC
    api("io.grpc:grpc-netty-shaded:${Version.grpc}")
    api("io.grpc:grpc-protobuf:${Version.grpc}")
    api("io.grpc:grpc-stub:${Version.grpc}")
    api("io.grpc:grpc-core:${Version.grpc}")
    implementation("com.google.protobuf:protobuf-java:${Version.protoc}")
    implementation("com.google.protobuf:protobuf-java-util:${Version.protoc}")
    implementation("javax.annotation:javax.annotation-api:${Version.javaxAnnotation}")

    // jUnit
    // testImplementation("org.assertj:assertj-core:3.12.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.2")

    testImplementation("junit:junit:4.12")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.5.2")
}

java {
    // TODO: Update to version_11, and make it build (something is blocking build in IDE for version_12)
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

var protobufGeneratedFilesBaseDir: String = ""

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:${Version.protoc}" }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${Version.grpc}"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
            }
        }
    }
    protobufGeneratedFilesBaseDir = generatedFilesBaseDir
}

idea {
    module {
        sourceDirs.addAll(files("${protobufGeneratedFilesBaseDir}/main/java"))
        sourceDirs.addAll(files("${protobufGeneratedFilesBaseDir}/main/grpc"))
    }
}

tasks.clean {
    delete(protobufGeneratedFilesBaseDir)
}


tasks.test {
    useJUnitPlatform()
}
