import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @ParameterizedTest
    @CsvSource({
            "Москва,3,Иванов Иван,+79649005050",
            "Москва,4,Иванова Анна-Мария,+79649005050",
            "Москва,3,Иванова Алёна,+79649005050"
    })
    public void shouldBeSuccessMessage(String city,int daysToAdd,String name, String phone) {
        open("http://localhost:9999/");
        //Ввод в поле Город
        $("[data-test-id='city'] input").setValue(city);
        //Очистка поля Дата
        $("[data-test-id='date'] input").click(); //Клик по полю Дата
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        //Метод отправки комбинации клавиш
        $("[data-test-id='date'] input").sendKeys(Keys.DELETE); //Очистка поля Дата
        //Создание даты
        String date = LocalDate.now().plusDays(daysToAdd).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        //Ввод в поле Дата
        $("[data-test-id='date'] input").setValue(date);
        //Ввод в поле Фамилия и Имя
        $("[data-test-id='name'] input").setValue(name);
        //Ввод в поле Телефон
        $("[data-test-id='phone'] input").setValue(phone);
        //Нажатие на чек-бокс
        $("[data-test-id='agreement']").click();
        //Нажатие на кнопку Продолжить
        $("button").click();

        //Проверка
        $(byText("Встреча успешно забронирована на " + date)).shouldBe(hidden, Duration
                .ofSeconds(15));
    }

    @Test
    public void shouldSetMoscow() {
        open("http://localhost:9999/");
        $("[data-test-id='city'] input").setValue("Мо");
        $(byText("Москва")).shouldBe(visible).click();
        $("[data-test-id='city'] input").shouldBe(value("Москва"));
    }

    @Test
    public void shouldSetDateWeekInAdvance() {
        open("http://localhost:9999/");
        $("[data-test-id='date'] input").click();
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        $("[data-test-id='date'] input").sendKeys(Keys.DELETE);
        String date = LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $("[data-test-id='date'] input").setValue(date);
        $("[data-test-id='date'] input").shouldHave(value(date));
    }
}
