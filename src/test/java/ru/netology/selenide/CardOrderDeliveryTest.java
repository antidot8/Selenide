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

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
        $("[data-test-id=date] .input__control").sendKeys(Keys.chord(SHIFT, HOME, DELETE));
    }

    @Test
    void sendFormPassed() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        $("[data-test-id=date] .input__control").setValue(date);
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
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        $("[data-test-id=date] .input__control").setValue(date);
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Доставка в выбранный город недоступна")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendDateLess3Days() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        String date = LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        $("[data-test-id=date] .input__control").setValue(date);
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Заказ на выбранную дату невозможен")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendWrongName() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        $("[data-test-id=date] .input__control").setValue(date);
        $("[data-test-id=name] .input__control").setValue("Ivanov Ivan");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Имя и Фамилия указаные неверно")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendWrongPhone() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        $("[data-test-id=date] .input__control").setValue(date);
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+7111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Телефон указан неверно")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendWithoutAgreement() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        $("[data-test-id=date] .input__control").setValue(date);
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".button").click();
        assertEquals("rgba(255, 92, 92, 1)", $(".input_invalid").getCssValue("color"));
    }

    @Test
    void sendIfUseDropdownlistOfCities() {
        $("[data-test-id=city] .input__control").setValue("Ка");
        $$(".menu-item__control").find(exactText("Казань")).click();
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        $("[data-test-id=date] .input__control").setValue(date);
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void sendIfUseCalendarPopup() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        $(".icon_name_calendar").click();
        int dayRequired = Integer.parseInt(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd")));
        int monthAvailableOrder = Integer.parseInt(LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("MM")));
        int monthRequired = Integer.parseInt(LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("MM")));
        int monthToday = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("MM")));
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
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
    }

    @Test
    void dateMatchCheck() {
        $("[data-test-id=city] .input__control").setValue("Казань");
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));
        $("[data-test-id=date] .input__control").setValue(date);
        $("[data-test-id=name] .input__control").setValue("Иванов Иван");
        $("[data-test-id=phone] .input__control").setValue("+71111111111");
        $(".checkbox__box").click();
        $(".button").click();
        $(withText("Успешно!")).shouldBe(visible, Duration.ofSeconds(15));
        String message = $(".notification__content").getText();
        assertEquals("Встреча успешно забронирована на "+ date, message.strip());
    }
}