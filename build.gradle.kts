  
plugins {
    java
    `java-library`
    `maven-publish`
}

group = "org.omscs.ml.a4burlap"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("de.siegmar:fastcsv:2.0.0")
    implementation("edu.brown.cs.burlap:burlap:3.0.1")
    implementation("org.ini4j:ini4j:0.5.4")
}

publishing {
    publications {
        create<MavenPublication>("a4burlap-library") {
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("compileClasspath ")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}

//TODO - add more java executables
task<JavaExec>("helloGridWorld") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    main = "org.omscs.ml.a4burlap.tutorial.HelloGridWorld"
}

task<JavaExec>("demoExperiment") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    main = "org.omscs.ml.a4burlap.experiments.RunExperiments"
}

task<JavaExec>("blockDudeViewer") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    getMainClass().set("org.omscs.ml.a4burlap.mdp.BlockDudeViewer")
}
