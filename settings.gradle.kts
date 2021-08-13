plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.11.0"
}
refreshVersions {
    enableBuildSrcLibs()
}
rootProject.name = "Fortnightly"
include(":app")
