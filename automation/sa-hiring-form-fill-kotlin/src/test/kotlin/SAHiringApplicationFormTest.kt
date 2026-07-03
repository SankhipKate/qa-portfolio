import io.github.bonigarcia.wdm.WebDriverManager
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import java.nio.file.Paths
import java.time.Duration

@DisplayName("SA Hiring application form")
class SAHiringApplicationFormTest {

    private lateinit var driver: WebDriver
    private lateinit var wait: WebDriverWait

    @BeforeEach
    fun setUp() {
        WebDriverManager.chromedriver().setup()

        val options = ChromeOptions()
        options.addArguments("--window-size=1280,1600")

        driver = ChromeDriver(options)
        wait = WebDriverWait(driver, Duration.ofSeconds(10))
    }

    @AfterEach
    fun tearDown() {
        driver.quit()
    }

    @Test
    @DisplayName("Valid candidate can submit application in UAT-like flow")
    fun validCandidateCanSubmitApplicationInUatLikeFlow() {
        val formUrl = Paths.get("src/test/resources/sa-hiring-application-form.html")
            .toAbsolutePath()
            .toUri()
            .toString()

        driver.get(formUrl)

        pauseForDemo("Initial empty form is opened", 4000)

        assertTrue(driver.title.contains("SA Hiring"))
        assertFalse(element("city").isEnabled)
        assertFalse(element("alternateCity").isEnabled)
        assertFalse(element("submitButton").isEnabled)

        select("province", "Benguet")
        wait.until(ExpectedConditions.elementToBeClickable(By.id("city")))
        select("city", "Baguio")

        wait.until(ExpectedConditions.elementToBeClickable(By.id("alternateCity")))
        select("alternateCity", "La Trinidad")

        pauseForDemo("Province, city and alternate city selected", 4000)

        type("firstName", "Test")
        click("noMiddleName")
        type("lastName", "Candidate")
        type("mobileNumber", "09171234567")
        type("email", "qa.sa.hiring@example.com")
        select("age", "32")
        click("tattoosNo")
        select("education", "College graduate")
        select("workExperience", "3+ years")
        click("salesRetail")
        click("salesFinancing")
        type("previousCompany", "Test Fintech Company")

        pauseForDemo("Candidate personal data and experience fields filled", 4000)

        val resumePath = Paths.get("src/test/resources/test-resume.pdf")
            .toAbsolutePath()
            .toString()
        element("resume").sendKeys(resumePath)

        click("scooterYes")
        select("availability", "Immediately")
        select("source", "Facebook ad")
        click("consent")

        pauseForDemo("Full form is filled before submit", 5000)

        wait.until(ExpectedConditions.elementToBeClickable(By.id("submitButton")))
        assertTrue(element("submitButton").isEnabled)

        click("submitButton")

        val successScreen = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successScreen")))
        assertTrue(successScreen.text.contains("Application successfully submitted"))

        pauseForDemo("Success screen is displayed", 7000)

        assertEquals("Benguet", text("resultProvince"))
        assertEquals("Baguio", text("resultCity"))
        assertEquals("Test", text("resultFirstName"))
        assertEquals("Candidate", text("resultLastName"))
        assertEquals("09171234567", text("resultMobileNumber"))
        assertEquals("qa.sa.hiring@example.com", text("resultEmail"))
        assertEquals("Immediately", text("resultAvailability"))
        assertEquals("Facebook ad", text("resultSource"))
    }

    private fun pauseForDemo(stepName: String, milliseconds: Long) {
        println("Demo pause: $stepName")
        Thread.sleep(milliseconds)
    }

    private fun element(id: String): WebElement = driver.findElement(By.id(id))

    private fun type(id: String, value: String) {
        val field = element(id)
        field.clear()
        field.sendKeys(value)
    }

    private fun click(id: String) {
        element(id).click()
    }

    private fun select(id: String, visibleText: String) {
        Select(element(id)).selectByVisibleText(visibleText)
    }

    private fun text(id: String): String = element(id).text
}
