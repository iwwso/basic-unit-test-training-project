package lv.bootcamp.shelter.task1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Task 1: Pure logic tests for AgeCalculator.
 * Every test follows Arrange - Act - Assert.
 *
 * @author Karena Litvinova
 */
@DisplayName("AgeCalculator")
class AgeCalculatorTest {

    private AgeCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new AgeCalculator();
    }

    // --- toMonths() ---

    @Test
    @DisplayName("toMonths: 0 years returns 0 months")
    void shouldReturnZeroMonthsForZeroYears() {
        // Act
        int result = calculator.toMonths(0);

        // Assert
        assertEquals(0, result);
    }

    @Test
    @DisplayName("toMonths: positive years returns correct months")
    void shouldConvertPositiveYearsToMonths() {
        int result = calculator.toMonths(3);

        assertEquals(36, result);
    }

    @Test
    @DisplayName("toMonths: largest value that still fits in int")
    void shouldConvertLargeValueWithoutOverflow() {
        // Arrange: Integer.MAX_VALUE / 12 = 178 956 970 is the biggest safe input
        int years = Integer.MAX_VALUE / 12;

        int result = calculator.toMonths(years);

        assertEquals(2_147_483_640, result);
    }

    @Test
    @DisplayName("toMonths: negative years throws IllegalArgumentException")
    void shouldThrowForNegativeYears() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.toMonths(-1));

        assertTrue(ex.getMessage().contains("negative"));
    }

    // --- dogToHumanYears() ---

    @Test
    @DisplayName("dogToHumanYears: age 0 returns 0")
    void shouldReturnZeroHumanYearsForPuppy() {
        assertEquals(0, calculator.dogToHumanYears(0));
    }

    @Test
    @DisplayName("dogToHumanYears: age 1 returns 15")
    void shouldReturnFifteenForOneYearOldDog() {
        assertEquals(15, calculator.dogToHumanYears(1));
    }

    @Test
    @DisplayName("dogToHumanYears: age 2 returns 24")
    void shouldReturnTwentyFourForTwoYearOldDog() {
        assertEquals(24, calculator.dogToHumanYears(2));
    }

    @Test
    @DisplayName("dogToHumanYears: age 5 returns 39")
    void shouldCalculateCorrectlyForOlderDog() {
        // 24 + (5 - 2) * 5 = 39
        assertEquals(39, calculator.dogToHumanYears(5));
    }

    @ParameterizedTest(name = "dog age {0} -> {1} human years")
    @CsvSource({
            "0, 0",
            "1, 15",
            "2, 24",
            "3, 29",
            "10, 64"
    })
    @DisplayName("dogToHumanYears: every branch of the formula")
    void shouldConvertDogAgeForEveryBranch(int dogAge, int expectedHumanYears) {
        assertEquals(expectedHumanYears, calculator.dogToHumanYears(dogAge));
    }

    @Test
    @DisplayName("dogToHumanYears: negative age throws IllegalArgumentException")
    void shouldThrowForNegativeDogAge() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> calculator.dogToHumanYears(-3));

        assertTrue(ex.getMessage().contains("-3"));
    }

    // --- isBaby() ---

    @Test
    @DisplayName("isBaby: age 0 returns true")
    void shouldReturnTrueForAgeZero() {
        assertTrue(calculator.isBaby(0));
    }

    @Test
    @DisplayName("isBaby: age 1 returns false")
    void shouldReturnFalseForAgeOne() {
        assertFalse(calculator.isBaby(1));
    }
}