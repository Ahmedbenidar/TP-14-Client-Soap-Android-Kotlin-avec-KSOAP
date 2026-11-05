pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // ksoap2-android et ses deux dependances kxml / kobjects ne sont
        // publies que sur ce depot Sonatype, absents de Maven Central
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/ksoap2-android-releases")
            content {
                includeGroup("com.google.code.ksoap2-android")
                includeGroup("net.sourceforge.kxml")
                includeGroup("net.sourceforge.kobjects")
                includeGroup("org.xmlpull")
            }
        }
    }
}

rootProject.name = "banque-android-ksoap"
include(":app")
