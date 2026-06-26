plugins {
    kotlin("jvm") version "1.9.25"
}

group = "com.sankhipkate.qa"
version = "1.0.0"

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
    testImplementation("org.seleniumhq.selenium:selenium-java:4.23.0")
    testImplementation("io.github.bonigarcia:webdrivermanager:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}
