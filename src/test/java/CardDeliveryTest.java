import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.Keys;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    //Генератор даты
    private String date(int daysToAdd, String pattern) {
        return LocalDate.now().plusDays(daysToAdd).format(DateTimeFormatter.ofPattern(pattern));
    }

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
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME));
        //Метод отправки комбинации клавиш
        $("[data-test-id='date'] input").sendKeys(Keys.DELETE); //Очистка поля Дата
        //Ввод в поле Дата
        //Сгенерированная дата
        String data = date(daysToAdd,"dd.MM.yyyy");
        $("[data-test-id='date'] input").setValue(data);
        //Ввод в поле Фамилия и Имя
        $("[data-test-id='name'] input").setValue(name);
        //Ввод в поле Телефон
        $("[data-test-id='phone'] input").setValue(phone);
        //Нажатие на чек-бокс
        $("[data-test-id='agreement']").click();
        //Нажатие на кнопку Продолжить
        $$("button").find(exactText("Забронировать")).click();

        //Проверка
        $("[data-test-id='notification']")
                .shouldBe(visible, Duration.ofSeconds(15))
                .$(".notification__content")
                .shouldHave(exactText("Встреча успешно забронирована на " + data));
    }

    @Test
    public void shouldBeSuccessMessageSetMoscowAndDateWeekInAdvance() {
        open("http://localhost:9999/");

        //Выбор города через выпадающий список
        $("[data-test-id='city'] input").setValue("Мо"); //вводим Мо
        $$(".menu-item").findBy(exactText("Москва")).shouldBe(visible).click(); //ищем Москва в списке

        //Выбор даты через календарь
        String data = date(7,("dd.MM.yyyy"));//генерируем дату
        LocalDate dataFormat = LocalDate.parse(data, DateTimeFormatter.ofPattern("dd.MM.yyyy")); //переформ. в LocalDate
        int day = dataFormat.getDayOfMonth(); //получаем день
        String monthYear = dataFormat.format(DateTimeFormatter
                .ofPattern("MMMM yyyy", new Locale("ru"))); //получаем месяц на русском
        $("[data-test-id='date'] input").click();//кликаем по полю
        while ($(".calendar__name").text().equals(monthYear)) { //пока месяц/год не совпадут, кликать вперед
            $("[data-step='1']").click();
        }
        $$(".calendar__day").findBy(exactText(String.valueOf(day))).click();//выбрать день

        //Ввод в поле Фамилия и Имя
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        //Ввод в поле Телефон
        $("[data-test-id='phone'] input").setValue("+79649005050");
        //Нажатие на чек-бокс
        $("[data-test-id='agreement']").click();
        //Нажатие на кнопку Продолжить
        $$("button").find(exactText("Забронировать")).click();

        //Проверка
        $("[data-test-id='notification']")
                .shouldBe(visible, Duration.ofSeconds(15))
                .$(".notification__content")
                .shouldHave(exactText("Встреча успешно забронирована на " + data));
    }
}
