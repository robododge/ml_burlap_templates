  
plugins {
    java
}

group = "org.omscs.ml.a4burlap"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("de.siegmar:fastcsv:2.0.0")
    implementation("edu.brown.cs.burlap:burlap:3.0.1")
}


//TODO - FIX up some javaexes
task<JavaExec>("helloGridWorld") {
    group = "myRunners"
    classpath(configurations.runtimeClasspath)
    classpath(sourceSets["main"].runtimeClasspath)
    main = "org.omscs.ml.a4burlap.tutorial.HelloGridWorld"
}
