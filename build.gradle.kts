@file:Suppress("PropertyName")

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kmongo_version: String by project
val bcrypt_version: String by project
val apache_commons_version: String by project
val mongodb_driver_version: String by project
val firebase_admin_version: String by project
val koin_version: String by project

plugins {
    application
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.10"
    id("org.jetbrains.dokka") version "1.5.0"
}

group = "com.twoplaylabs"
version = "1.1.10"
application {
    mainClass.set("com.twoplaylabs.ApplicationKt")
}

tasks.create("stage") {
    dependsOn("clean", "installDist")
}

repositories {
    mavenCentral()
}

dependencies {
    //region Server
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-call-logging:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-server-html-builder:$ktor_version")
    implementation("io.ktor:ktor-server-status-pages:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    //endregion

    //region Client
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation ("io.ktor:ktor-client-apache:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    //endregion

    //Region MongoDB
    implementation("org.litote.kmongo:kmongo:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-async:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongo_version")
    implementation("org.litote.kmongo:kmongo-id:$kmongo_version")
    implementation("org.mongodb:mongodb-driver-sync:$mongodb_driver_version")
    //endregion

    implementation("at.favre.lib:bcrypt:$bcrypt_version")

    implementation("org.apache.commons:commons-email:$apache_commons_version")

    implementation("com.google.firebase:firebase-admin:$firebase_admin_version")

    //region Koin
    // Koin Core features
    implementation("io.insert-koin:koin-ktor:$koin_version")
    // Testing
    // Koin Test features
    testImplementation ("io.insert-koin:koin-test:$koin_version")
    // Koin for JUnit 4
    testImplementation ("io.insert-koin:koin-test-junit4:$koin_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    //endregion
}