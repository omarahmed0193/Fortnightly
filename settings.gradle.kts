plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.10.1"
}
refreshVersions {
    enableBuildSrcLibs()
}
rootProject.name = "Fortnightly"
include(":app")
