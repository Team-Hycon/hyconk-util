plugins {
    `build-scan`
    kotlin("jvm") version "1.3.31"
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib","1.3.31"))
    testImplementation("junit:junit:4.12")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service" 
    termsOfServiceAgree = "yes"
    publishAlways()
}