val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val kmongoVersion: String by project
val bcryptVersion: String by project
val apacheCommonsEmailVersion:String by project
val mongodbDriverVersion:String by project
val firebaseAdminVersion:String by project

plugins {
    application
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.10"
    id("org.jetbrains.dokka") version "1.5.0"
}

group = "com.twoplaylabs"
version = "0.0.1"
application {
    mainClass.set("com.twoplaylabs.ApplicationKt")
}

tasks.create("stage") {
    dependsOn("clean","installDist")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-auth-jwt:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-gson:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("org.litote.kmongo:kmongo:$kmongoVersion")
    implementation("org.litote.kmongo:kmongo-async:$kmongoVersion")
    implementation("org.litote.kmongo:kmongo-coroutine:$kmongoVersion")
    implementation("org.litote.kmongo:kmongo-id:$kmongoVersion")
    implementation("at.favre.lib:bcrypt:$bcryptVersion")
    implementation("org.apache.commons:commons-email:$apacheCommonsEmailVersion")
    implementation("org.mongodb:mongodb-driver-sync:$mongodbDriverVersion")
    implementation("com.google.firebase:firebase-admin:$firebaseAdminVersion")

    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}