
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

    implementation(kotlin("stdlib-jdk8"))

    api("io.grpc:grpc-netty-shaded:${Version.grpc}")
    api("io.grpc:grpc-protobuf:${Version.grpc}")
    api("io.grpc:grpc-stub:${Version.grpc}")
    api("io.grpc:grpc-core:${Version.grpc}")
    implementation("com.google.protobuf:protobuf-java:${Version.protoc}")
    implementation("com.google.protobuf:protobuf-java-util:${Version.protoc}")
    implementation("javax.annotation:javax.annotation-api:${Version.javaxAnnotation}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_12
    targetCompatibility = JavaVersion.VERSION_12
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