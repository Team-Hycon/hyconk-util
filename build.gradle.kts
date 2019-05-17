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
    compile("com.rfksystems:blake2b:1.0.0")
    compile("ru.d-shap:hex:1.2")
    compile("org.bitcoinj:core:0.15")
    compile("com.google.protobuf:protobuf-parent:3.8.0-rc-1")
    compile("org.web3j:crypto:4.3.0")
    testImplementation("junit:junit:4.12")
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service" 
    termsOfServiceAgree = "yes"
    publishAlways()
}