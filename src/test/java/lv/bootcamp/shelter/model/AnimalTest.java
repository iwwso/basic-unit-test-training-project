package lv.bootcamp.shelter.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Extra tests for Animal: constructor, equals/hashCode, toString.
 * Added after the coverage check (Task 8).
 *
 * @author Karyna Litvinova
 */
@DisplayName("Animal")
class AnimalTest {

    private static final LocalDate DATE = LocalDate.of(2026, 1, 15);

    private final Animal buddy = new Animal("Buddy", "Dog", 3, true, DATE);

    @Test
    @DisplayName("constructor stores all fields")
    void shouldStoreAllFields() {
        assertThat(buddy.getName()).isEqualTo("Buddy");
        assertThat(buddy.getSpecies()).isEqualTo("Dog");
        assertThat(buddy.getAge()).isEqualTo(3);
        assertThat(buddy.isVaccinated()).isTrue();
        assertThat(buddy.getIntakeDate()).isEqualTo(DATE);
    }

    @Test
    @DisplayName("equal animals have the same hashCode")
    void shouldBeEqualWhenAllFieldsMatch() {
        Animal copy = new Animal("Buddy", "Dog", 3, true, DATE);

        assertThat(copy).isEqualTo(buddy);
        assertThat(copy.hashCode()).isEqualTo(buddy.hashCode());
    }

    @Test
    @DisplayName("not equal to null, another type or itself with changed fields")
    void shouldNotBeEqualWhenSomethingDiffers() {
        assertThat(buddy).isEqualTo(buddy);
        assertThat(buddy).isNotEqualTo(null);
        assertThat(buddy).isNotEqualTo("Buddy");
        assertThat(buddy).isNotEqualTo(new Animal("Max", "Dog", 3, true, DATE));
        assertThat(buddy).isNotEqualTo(new Animal("Buddy", "Cat", 3, true, DATE));
        assertThat(buddy).isNotEqualTo(new Animal("Buddy", "Dog", 4, true, DATE));
        assertThat(buddy).isNotEqualTo(new Animal("Buddy", "Dog", 3, false, DATE));
        assertThat(buddy).isNotEqualTo(new Animal("Buddy", "Dog", 3, true, DATE.plusDays(1)));
    }

    @Test
    @DisplayName("toString shows vaccinated / not vaccinated")
    void shouldDescribeAnimalInToString() {
        Animal max = new Animal("Max", "Dog", 5, false, DATE);

        assertThat(buddy.toString()).isEqualTo("Buddy (Dog, age 3, vaccinated, intake 2026-01-15)");
        assertThat(max.toString()).contains("not vaccinated");
    }
}