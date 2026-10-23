package lv.bootcamp.shelter.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Extra tests for the AnimalType enum.
 * Added after the coverage check (Task 8): this class had no tests at all.
 *
 * @author Karyna Litvinova
 */
@DisplayName("AnimalType")
class AnimalTypeTest {

    @ParameterizedTest(name = "{0}: age {1} -> senior = {2}")
    @CsvSource({
            "DOG, 7, false",
            "DOG, 8, true",
            "CAT, 9, false",
            "CAT, 10, true",
            "BIRD, 12, false",
            "BIRD, 13, true",
            "RABBIT, 5, false",
            "RABBIT, 6, true"
    })
    @DisplayName("isSenior: true only above half of the average lifespan")
    void shouldDetectSeniorAtBoundary(AnimalType type, int age, boolean expected) {
        assertThat(type.isSenior(age)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({
            "Dog, DOG",
            "cat, CAT",
            "BIRD, BIRD",
            "'  Rabbit  ', RABBIT"
    })
    @DisplayName("fromDisplayName: ignores case and surrounding spaces")
    void shouldFindTypeByDisplayName(String input, AnimalType expected) {
        assertThat(AnimalType.fromDisplayName(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "Hamster", "Doggo"})
    @DisplayName("fromDisplayName: returns null for blank or unknown names")
    void shouldReturnNullForUnknownName(String input) {
        assertThat(AnimalType.fromDisplayName(input)).isNull();
    }

    @Test
    @DisplayName("each type has a display name and lifespan")
    void shouldExposeDisplayNameAndLifespan() {
        assertThat(AnimalType.DOG.getDisplayName()).isEqualTo("Dog");
        assertThat(AnimalType.DOG.getAverageLifespanYears()).isEqualTo(15);
        assertThat(AnimalType.values()).hasSize(4);
    }
}