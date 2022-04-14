package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.testng.annotations.BeforeTest;
import ru.netology.data.DataHelper;
import ru.netology.data.DBHelper;
import ru.netology.page.DebitCardPage;
import ru.netology.page.MainPage;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class PaymentDebitCardTest {
    MainPage mainPage = open("http://localhost:8080", MainPage.class);
    DebitCardPage debitPaymentPage = mainPage.buyWithCard();

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
    public void openDebitPaymentPage() {
        debitPaymentPage.checkVisibleHeadingDebitCard();
    }

    // Positive Test

    @SneakyThrows
    @Test
    void shouldUsualBuyWithApprovedCard() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(2);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.successMessageForm();
        assertEquals("APPROVED", DBHelper.getTransactionStatusDebitCard());
        assertNotNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithDeclinedCard() {
        var cardNumber = DataHelper.getDeclinedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        assertEquals("DECLINED", DBHelper.getTransactionStatusDebitCard());
        assertNotNull(DBHelper.getTransactionTypeDebitCard());
        debitPaymentPage.errorMessageForm();
    }

    // Negative Test

    @SneakyThrows
    @Test
    void shouldUsualBuyWithAnotherCard() {
        var cardNumber = DataHelper.getAnotherCardNumber();
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageForm();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
        debitPaymentPage.closeErrorSendFormMessage();
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidLengthFormatCardNumber() {
        var cardNumber = DataHelper.getInvalidFieldFormat(14, 0, 0, 0, 0);
        var month = DataHelper.getMonth(4);
        var year = DataHelper.getYear(3);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

        @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidCardNumberWhenAllDigitZero() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 0, 16, 0, 0);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidCardNumberIncludeSymbolsAndLetters() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 5, 0, 6, 5);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCardNumberField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithEmptyCardNumberField() {
        var cardNumber = DataHelper.getInvalidFieldFormat(0, 0, 0, 0, 0);
        var month = DataHelper.getMonth(3);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageCardNumberFieldEmpty();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithOutOfDateMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(-1);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithNonexistentMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidMonth();
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageAboutOutOfDateMonthOrNonexistentMonth();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithEmptyMonthField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var year = DataHelper.getYear(0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageMonthFieldEmpty();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidMonthWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidMonthField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidLengthFormatMonth() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidMonthField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidMonthIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidMonthField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidMonthIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidMonthField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithOutOfYear() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(-1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageAboutOutOfDateYear();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithValidityPeriodExpiresInFiveYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(5);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.successMessageForm();
        assertEquals("APPROVED", DBHelper.getTransactionStatusDebitCard());
        assertNotNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithValidityPeriodExpiresInSixYears() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(6);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.successMessageForm();
        assertEquals("APPROVED", DBHelper.getTransactionStatusDebitCard());
        assertNotNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithEmptyYearField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageYearFieldEmpty();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidYearWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 2, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageAboutOutOfDateYear();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidLengthFormatYear() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(1, 0, 0, 0, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidYearField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidYearIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidYearField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidYearIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getInvalidFieldFormat(0, 0, 0, 2, 0);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidYearField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithOwnerIncludeCyrillicLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getOwner("ru");
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }


    @SneakyThrows
    @Test
    void shouldUsualBuyWithOwnerFieldLengthConsistingOfOneLetter() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 0);
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }


    @SneakyThrows
    @Test
    void shouldUsualBuyWithOwnerFieldLengthOverLimit() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getLongerOwner();
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    //    Test 24
    @SneakyThrows
    @Test
    void shouldUsualBuyWithEmptyOwnerField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedOwnerField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidOwnerIncludeDigitsAndSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(2);
        var owner = DataHelper.getInvalidFieldFormat(4, 0, 0, 4, 0);
        var code = DataHelper.getValidCode();
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidOwnerField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }


    @SneakyThrows
    @Test
    void shouldUsualBuyWithEmptyCodeField() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0,0,0,0,0);
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageCodeFieldEmpty();
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidCodeWhenAllDigitZero() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 3, 0, 0);
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCodeField();
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidLengthFormatCode() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(2, 0, 0, 0, 0);
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCodeField();
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidCodeIncludeLetters() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 1, 0, 0, 2);
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCodeField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }

    @SneakyThrows
    @Test
    void shouldUsualBuyWithInvalidCodeIncludeSymbols() {
        var cardNumber = DataHelper.getApprovedCardNumber();
        var month = DataHelper.getMonth(0);
        var year = DataHelper.getYear(1);
        var owner = DataHelper.getOwner("en");
        var code = DataHelper.getInvalidFieldFormat(0, 0, 0, 3, 0);
        debitPaymentPage.fillOutAllFields(cardNumber, month, year, owner, code);
        debitPaymentPage.errorMessageInvalidCodeField();
        assertNull(DBHelper.getTransactionStatusDebitCard());
        assertNull(DBHelper.getTransactionTypeDebitCard());
        debitPaymentPage.errorMessageOwnerFieldEmptyWhenTestedCodeField();
    }
}


