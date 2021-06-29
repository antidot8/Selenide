package ru.netology.selenide;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.openqa.selenium.Keys.*;

public class CardOrderDeliveryTest {
    String generateDate(int days, String pattern) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern(pattern));
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        $("[data-test-id=date] .input__control").sendKeys(Keys.chord(SHIFT, HOME, DELETE));
    }

    @Test
    void sendFormPassed() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        String date = generateDate(3, "dd.MM.uuuu");
        $("[data-test-id=date] .input__control").setValue(date);
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=notification] .notification__content").shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Встреча успешно забронирована на " + date));
    }

    @Test
    void sendEmptyForm() {
        $(".button").click();
        $("[data-test-id=city] .input__sub").shouldBe(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    void sendWrongCity() {
        $("[data-test-id=city] .input__control").setValue("Ванкувер");
        $("[data-test-id=date] .input__control").setValue(generateDate(3, "dd.MM.uuuu"));
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Доставка в выбранный город недоступна")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendDateLess3Days() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $("[data-test-id=date] .input__control").setValue(generateDate(2, "dd.MM.uuuu"));
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Заказ на выбранную дату невозможен")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendWrongName() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $("[data-test-id=date] .input__control").setValue(generateDate(3, "dd.MM.uuuu"));
        $("[data-test-id=name] .input__control").setValue("Ivanov Ivan");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Имя и Фамилия указаные неверно")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendWrongPhone() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $("[data-test-id=date] .input__control").setValue(generateDate(3, "dd.MM.uuuu"));
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+7111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Телефон указан неверно")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendWithoutAgreement() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $("[data-test-id=date] .input__control").setValue(generateDate(3, "dd.MM.uuuu"));
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".button").click();
        assertEquals("rgba(255, 92, 92, 1)", $(".input_invalid").getCssValue("color"));
    }

    @Test
    void sendIfUseDropdownlistOfCities() {
        $("[data-test-id=city] .input__control").setValue("Ка");
        $$(".menu-item__control").find(exactText("Казань")).click();
        String date = generateDate(3, "dd.MM.uuuu");
        $("[data-test-id=date] .input__control").setValue(date);
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=notification] .notification__content").shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Встреча успешно забронирована на " + date));
    }

    @Test
    void sendIfUseCalendarPopup() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $(".icon_name_calendar").click();
        String date = generateDate(7, "dd.MM.uuuu");
        int dayRequired = Integer.parseInt(generateDate(7, "dd"));
        int monthAvailableOrder = Integer.parseInt(generateDate(3, "MM"));
        int monthRequired = Integer.parseInt(generateDate(7, "MM"));
        int monthToday = Integer.parseInt(generateDate(0, "MM"));
        if (monthRequired > monthToday) {
            if (monthAvailableOrder == monthToday) {
                $(".calendar__title [data-step='1']").click();
            }
        }
        $$(".calendar__day").find(exactText(Integer.toString(dayRequired))).click();
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $("[data-test-id=notification] .notification__content").shouldBe(visible, Duration.ofSeconds(15))
                .shouldHave(exactText("Встреча успешно забронирована на " + date));
    }
}