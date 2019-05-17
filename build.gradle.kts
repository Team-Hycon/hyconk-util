plugins {
    `build-scan`
    kotlin("jvm") version "1.3.31"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib","1.3.31"))
    compile("org.bouncycastle:bctls-jdk15on:1.61")
    testImplementation("junit:junit:4.12")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service" 
    termsOfServiceAgree = "yes"
    publishAlways()
}