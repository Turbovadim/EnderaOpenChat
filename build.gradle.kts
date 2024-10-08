import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20" apply true
}

group = "org.endera"
version = "1.0"

repositories {
    mavenCentral()
//    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://nexus.scarsz.me/content/groups/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.kyori:adventure-text-minimessage:4.16.0")
    compileOnly("com.discordsrv:discordsrv:1.28.0")

    // Local Lib
//    implementation("org.endera.enderalib:enderalib:1.0-SNAPSHOT")
    implementation("com.github.Turbovadim:EnderaLib:1.0.1")
}

tasks.jar {
    dependsOn(generatePluginYml)
}

tasks.processResources {
    dependsOn(generatePluginYml)
}

val generatePluginYml = tasks.create("generatePluginYml", Copy::class.java) {
    from("/src/main/configs/plugin.yml")
    into("/src/main/resources")
    expand(mapOf("projectVersion" to project.version))
    outputs.upToDateWhen { false }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile> {
    targetCompatibility = "17"
}