plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "chatter"
version = "1.0-SNAPSHOT"

application { mainClass.set("chatter.MainKt") }
kotlin { jvmToolchain(17) }

dependencies {
    implementation(libs.bundles.ktorServer)
}
