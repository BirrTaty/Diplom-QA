package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.testng.annotations.BeforeTest;
import ru.netology.data.DataHelper;
import ru.netology.data.DBHelper;
import ru.netology.page.CreditCardPage;
import ru.netology.page.MainPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentCreditCardTest {
    MainPage mainPage = open("http://localhost:8080", MainPage.class);
    CreditCardPage creditPaymentPage = mainPage.buyWithCardOnCredit();

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    public void cleanBase() {
        DBHelper.clearDB();
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeTest
    public void openCreditPaymentPage() {
        creditPaymentPage.checkVisibleHeadingCreditCard();
    }

//     Positive Tests

    @SneakyThrows
    @Test
    void shouldCreditApprovedCard() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.successMessageForm();
        assertEquals("APPROVED", DBHelper.getTransactionStatusCreditCard());
        assertNotNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditDeclinedCard() {
        var cardNumber = DataHelper.getDeclinedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);;
        creditPaymentPage.errorMessageForm();
        assertEquals("DECLINED", DBHelper.getTransactionStatusCreditCard());
        assertNotNull(DBHelper.getTransactionTypeCreditCard());
    }

    // Negative Tests

    @SneakyThrows
    @Test
    void shouldCreditAnotherCard() {
        var cardNumber = DataHelper.getAnotherCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageForm();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
        creditPaymentPage.closeErrorSendFormMessage();
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidLengthFormatCardNumber() {
        var cardNumber = DataHelper.getInvalidFieldFormat(14, 0, 0, 0, 0);
        var month = DataHelper.getMonth(4);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidCardNumberWhenAllDigitZero() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 0, 16, 0, 0);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditBuyWithInvalidCardNumberIncludeSymbolsAndLetters() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 5, 0, 6, 5);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditBuyWithEmptyCardNumberField() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 0, 0, 0, 0);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageCardNumberFieldEmpty();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditOutOfMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(-1);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditNonexistentMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidMonth();
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditEmptyMonthField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageMonthFieldEmpty();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidMonthWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidMonthField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidLengthFormatMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidMonthField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidMonthIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidMonthField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }


    @SneakyThrows
    @Test
    void shouldCreditOutOfYear() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(-1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageAboutOutOfDateYear();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditBuyWithValidityPeriodExpiresInFiveYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(5);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.successMessageForm();
        assertEquals("APPROVED", DBHelper.getTransactionStatusCreditCard());
        assertNotNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditValidityPeriodExpiresInSixYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(6);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.successMessageForm();
        assertEquals("APPROVED", DBHelper.getTransactionStatusCreditCard());
        assertNotNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditEmptyYearField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageYearFieldEmpty();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidLengthFormatYear() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidYearField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidYearIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidYearField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidYearWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageAboutOutOfDateYear();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidYearIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidYearField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditOwnerIncludeCyrillicLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("ru");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditOwnerFieldLengthConsistingOfOneLetter() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 0);
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditOwnerFieldLengthOverLimit() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getLongerOwner();
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditEmptyOwnerField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedOwnerField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidOwnerIncludeDigitsAndSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(4, 0, 0, 4, 0);
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditEmptyCodeField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageCodeFieldEmpty();
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidCodeWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 3, 0, 0);
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCodeField();
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidLengthFormatCode() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(2, 0, 0, 0, 0);
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCodeField();
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }

    @SneakyThrows
    @Test
    void shouldCreditWithInvalidCodeIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 2);
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCodeField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidCodeIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 0, 3, 0);
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidCodeField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
        creditPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }

    @SneakyThrows
    @Test
    void shouldCreditInvalidMonthIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        creditPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        creditPaymentPage.errorMessageInvalidMonthField();
        assertNull(DBHelper.getTransactionStatusCreditCard());
        assertNull(DBHelper.getTransactionTypeCreditCard());
    }
}
