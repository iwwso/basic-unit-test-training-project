package lv.bootcamp.shelter.task23;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tasks 2 and 3: parameterized tests and exception tests for AnimalValidator.
 *
 * @author Karyna Litvinova
 */
@DisplayName("AnimalValidator")
class AnimalValidatorTest {

    private static final LocalDate TODAY = LocalDate.now();

    private AnimalValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AnimalValidator();
    }

    // ==================== Task 2: Parameterized tests ====================

    @Nested
    @DisplayName("validateName")
    class ValidateName {

        @ParameterizedTest
        @ValueSource(strings = {"Buddy", "Luna", "Mr. Whiskers", "X"})
        @DisplayName("accepts valid names")
        void shouldAcceptValidNames(String name) {
            assertDoesNotThrow(() -> validator.validateName(name));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("rejects blank or null names")
        void shouldRejectBlankNames(String name) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> validator.validateName(name));

            assertThat(ex.getMessage()).contains("must not be blank");
        }

        @Test
        @DisplayName("accepts name of exactly 100 characters (boundary)")
        void shouldAcceptNameOfMaxLength() {
            String name = "a".repeat(100);

            assertDoesNotThrow(() -> validator.validateName(name));
        }

        @Test
        @DisplayName("rejects name longer than 100 characters")
        void shouldRejectOverlyLongName() {
            String name = "a".repeat(101);

            assertThatThrownBy(() -> validator.validateName(name))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("100 characters");
        }
    }

    @Nested
    @DisplayName("validateAge")
    class ValidateAge {

        @ParameterizedTest
        @CsvSource({
                "0",
                "1",
                "10",
                "50"
        })
        @DisplayName("accepts valid ages")
        void shouldAcceptValidAges(int age) {
            assertDoesNotThrow(() -> validator.validateAge(age));
        }

        @ParameterizedTest
        @CsvSource({
                "-1, must not be negative",
                "-100, must not be negative",
                "51, seems unrealistic",
                "999, seems unrealistic"
        })
        @DisplayName("rejects invalid ages with correct message")
        void shouldRejectInvalidAges(int age, String expectedMessagePart) {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> validator.validateAge(age));

            assertThat(ex.getMessage()).contains(expectedMessagePart);
        }
    }

    @Nested
    @DisplayName("validate with many invalid inputs (@CsvSource)")
    class ValidateManyInputs {

        @ParameterizedTest(name = "[{index}] name=''{0}'', species=''{1}'', age={2}")
        @CsvSource({
                "'', Dog, 3, Name must not be blank",
                "Buddy, '', 3, Species must not be blank",
                "Buddy, Dog, -1, Age must not be negative",
                "Buddy, Dog, 51, Age seems unrealistic"
        })
        @DisplayName("rejects invalid input with the right message")
        void shouldRejectInvalidInput(String name, String species, int age, String expectedMessage) {
            Animal animal = new Animal(name, species, age, true, TODAY);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> validator.validate(animal));

            assertThat(ex.getMessage()).contains(expectedMessage);
        }
    }

    // ==================== Task 3: Exception tests ====================

    @Nested
    @DisplayName("validate (full animal)")
    class ValidateFullAnimal {

        @Test
        @DisplayName("throws NullPointerException for null animal")
        void shouldThrowForNullAnimal() {
            NullPointerException ex = assertThrows(NullPointerException.class,
                    () -> validator.validate(null));

            assertThat(ex.getMessage()).contains("must not be null");
        }

        @Test
        @DisplayName("throws for animal with null name")
        void shouldThrowForNullName() {
            Animal animal = new Animal(null, "Dog", 3, true, TODAY);

            assertThatThrownBy(() -> validator.validate(animal))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Name must not be blank");
        }

        @Test
        @DisplayName("throws for animal with blank name")
        void shouldThrowForBlankName() {
            Animal animal = new Animal("  ", "Dog", 3, true, TODAY);

            assertThatThrownBy(() -> validator.validate(animal))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Name");
        }

        @Test
        @DisplayName("throws for animal with blank species")
        void shouldThrowForBlankSpecies() {
            Animal animal = new Animal("Buddy", " ", 3, true, TODAY);

            assertThatThrownBy(() -> validator.validate(animal))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Species must not be blank");
        }
        @Test
        @DisplayName("throws for animal with null species")
        void shouldThrowForNullSpecies() {
            Animal animal = new Animal("Buddy", null, 3, true, TODAY);

            assertThatThrownBy(() -> validator.validate(animal))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Species must not be blank");
        }

        @Test
        @DisplayName("throws for animal with negative age")
        void shouldThrowForNegativeAge() {
            Animal animal = new Animal("Buddy", "Dog", -2, true, TODAY);

            assertThatThrownBy(() -> validator.validate(animal))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("must not be negative")
                    .hasMessageContaining("-2");
        }

        @Test
        @DisplayName("does not throw for fully valid animal")
        void shouldPassForValidAnimal() {
            Animal buddy = new Animal("Buddy", "Dog", 3, true, TODAY);

            assertDoesNotThrow(() -> validator.validate(buddy));
        }
    }
}