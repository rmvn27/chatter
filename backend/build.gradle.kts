plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.serialization)
    alias(libs.plugins.kotlinKapt)
    alias(libs.plugins.anvil)
    application
}

group = "chatter"
version = "1.0-SNAPSHOT"

application { mainClass.set("chatter.MainKt") }
kotlin {
    jvmToolchain(21)
    compilerOptions {
        // enable context receivers which are still experimental
        // but are available for some versions
        //
        // https://github.com/Kotlin/KEEP/blob/master/proposals/context-receivers.md
        freeCompilerArgs.add("-Xcontext-receivers")

        val optIns = listOf("kotlinx.coroutines.ExperimentalCoroutinesApi")
        optIns.forEach { freeCompilerArgs.add("-opt-in=$it") }
    }
}

// setup query and entity generation
sqldelight {
    databases {
        create("DatabaseQueries") {
            packageName.set("chatter.db")
            srcDirs("src/main/sqldelight")

            // use postgres
            dialect(libs.sqldelightPostgresDialect)

            // derive db model from the migrations
            verifyMigrations.set(true)
            deriveSchemaFromMigrations.set(true)
        }
    }
}

dependencies {
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.ktor)

    implementation(libs.bundles.sql)

    implementation(libs.bundles.arrow)
    implementation(libs.bundles.logging)
    implementation(libs.bundles.crypto)

    implementation(libs.dagger)
    implementation(libs.anvilAnnotations)
    kapt(libs.daggerCompiler)

    implementation(libs.slugify)
    implementation(libs.redis)
    implementation(libs.nats)
}
