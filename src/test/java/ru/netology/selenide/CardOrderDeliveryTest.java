package ru.netology.selenide;

import com.codeborne.selenide.Condition;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.Keys.*;

public class CardOrderDeliveryTest {
    private WebDriver driver;

    @BeforeAll
    static void setUpAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        options.addArguments("--remote-debugging-port=9999");
        driver = new ChromeDriver(options);
        open("http://localhost:9999");
        $("[data-test-id=date] .input__control").sendKeys(Keys.chord(SHIFT, HOME, DELETE));
    }

    @AfterEach
    void tearDown() {
        driver.quit();
        driver = null;
    }

    @Test
    void sendFormPassed() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $("[data-test-id=date] .input__control").sendKeys(Keys.chord(SHIFT, HOME, DELETE));
        $("[data-test-id=date] .input__control").setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendEmptyForm() {
        $(".button").click();
        $("[data-test-id=city] .input__sub").shouldBe(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    void sendWrongCity() {
        $("[data-test-id=city] .input__control").setValue("Ванкувер");
        $("[data-test-id=date] .input__control").setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Доставка в выбранный город недоступна")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendDateLess3Days() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $("[data-test-id=date] .input__control").setValue(LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Заказ на выбранную дату невозможен")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendWrongName() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $("[data-test-id=date] .input__control").setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
        $("[data-test-id=name] .input__control").setValue("Ivanov Ivan");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Имя и Фамилия указаные неверно")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendWrongPhone() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $("[data-test-id=date] .input__control").setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+7111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Телефон указан неверно")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendWithoutAgreement() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $("[data-test-id=date] .input__control").setValue(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".button").click();
        assertEquals("rgba(255, 92, 92, 1)", $(".input_invalid").getCssValue("color"));
    }
}