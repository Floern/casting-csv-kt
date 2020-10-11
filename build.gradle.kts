import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val artifactGroup = "com.floern.castingcsv"
val artifactName = "casting-csv-kt"
val artifactVersion = "1.1"
val publicationName = "release"

buildscript {
	dependencies {
		classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin")
	}
}

plugins {
	kotlin("jvm") version "1.4.10"
	id("com.jfrog.bintray") version "1.8.5"
	id("maven-publish")
}

group = artifactGroup
version = artifactVersion

kotlin {
	explicitApi()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
	implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.11.1")

	testImplementation(kotlin("test-junit"))
}

val sourcesJar by tasks.creating(Jar::class) {
	archiveClassifier.set("sources")
	from(sourceSets.main.get().allSource)
}

publishing {
	publications {
		create<MavenPublication>(publicationName) {
			groupId = artifactGroup
			artifactId = artifactName
			version = artifactVersion
			from(components["java"])
			artifact(sourcesJar)
		}
	}
}

bintray {
	user = findProperty("bintrayUser") as String?
	key = findProperty("bintrayKey") as String?

	setPublications(publicationName)
	publish = true

	pkg.apply {
        repo = "maven"
        name = artifactName
        vcsUrl = "https://github.com/Floern/casting-csv-kt.git"
        websiteUrl = "https://github.com/Floern/casting-csv-kt"
        issueTrackerUrl = "https://github.com/Floern/casting-csv-kt/issues"
		githubRepo = "Floern/casting-csv-kt"
		githubReleaseNotesFile = "README.md"
        setLicenses("Apache-2.0")
		setLabels("kotlin", "csv")

        version.apply {
            name = artifactVersion
        }
    }
}