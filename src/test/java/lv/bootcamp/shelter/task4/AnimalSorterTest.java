package lv.bootcamp.shelter.task4;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Task 4: collection and sorting tests for AnimalSorter.
 *
 * @author Karyna Litvinova
 */
@DisplayName("AnimalSorter")
class AnimalSorterTest {

    private AnimalSorter sorter;

    private Animal buddy;
    private Animal luna;
    private Animal max;
    private Animal bella;

    @BeforeEach
    void setUp() {
        sorter = new AnimalSorter();
        buddy = new Animal("Buddy", "Dog", 3, true, LocalDate.of(2026, 1, 15));
        luna = new Animal("Luna", "Cat", 2, true, LocalDate.of(2026, 1, 10));
        max = new Animal("Max", "Dog", 5, false, LocalDate.of(2026, 1, 20));
        bella = new Animal("Bella", "Cat", 1, true, LocalDate.of(2026, 1, 5));
    }

    @Test
    @DisplayName("sortByName: returns empty list for null input")
    void shouldReturnEmptyForNullInputByName() {
        assertThat(sorter.sortByName(null)).isEmpty();
    }

    // --- sortByAge ---

    @Test
    @DisplayName("sortByAge: returns animals ordered youngest to oldest")
    void shouldSortByAgeAscending() {
        List<Animal> result = sorter.sortByAge(List.of(buddy, luna, max, bella));

        assertThat(result)
                .extracting(Animal::getName)
                .containsExactly("Bella", "Luna", "Buddy", "Max");
    }

    @Test
    @DisplayName("sortByAge: keeps original order for animals of the same age")
    void shouldKeepOrderForEqualAges() {
        Animal rex = new Animal("Rex", "Dog", 3, true, LocalDate.of(2026, 1, 1));

        List<Animal> result = sorter.sortByAge(List.of(buddy, rex, bella));

        assertThat(result)
                .extracting(Animal::getName)
                .containsExactly("Bella", "Buddy", "Rex");
    }

    @Test
    @DisplayName("sortByAge: returns empty list for null input")
    void shouldReturnEmptyForNullInput() {
        List<Animal> result = sorter.sortByAge(null);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("sortByAge: returns empty list for empty input")
    void shouldReturnEmptyForEmptyInput() {
        List<Animal> result = sorter.sortByAge(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("sortByAge: does not modify the original list")
    void shouldNotModifyOriginalList() {
        List<Animal> original = new ArrayList<>(List.of(max, buddy, bella));

        List<Animal> result = sorter.sortByAge(original);

        assertThat(original).containsExactly(max, buddy, bella);
        assertThat(result).isNotSameAs(original);
    }

    // --- sortByName ---

    @Test
    @DisplayName("sortByName: returns animals in alphabetical order")
    void shouldSortByNameAlphabetically() {
        List<Animal> result = sorter.sortByName(List.of(buddy, luna, max, bella));

        assertThat(result)
                .extracting(Animal::getName)
                .containsExactly("Bella", "Buddy", "Luna", "Max");
    }

    @Test
    @DisplayName("sortByName: is case-insensitive")
    void shouldSortNamesCaseInsensitively() {
        Animal zebra = new Animal("zebra", "Horse", 4, true, LocalDate.of(2026, 1, 1));
        Animal alpha = new Animal("Alpha", "Dog", 2, true, LocalDate.of(2026, 1, 2));
        Animal beta = new Animal("beta", "Cat", 1, true, LocalDate.of(2026, 1, 3));

        List<Animal> result = sorter.sortByName(List.of(zebra, beta, alpha));

        assertThat(result)
                .extracting(Animal::getName)
                .containsExactly("Alpha", "beta", "zebra");
    }


    // --- sortByIntakeDate ---

    @Test
    @DisplayName("sortByIntakeDate: returns animals from earliest to latest")
    void shouldSortByIntakeDateAscending() {
        List<Animal> result = sorter.sortByIntakeDate(List.of(buddy, luna, max, bella));

        assertThat(result)
                .extracting(Animal::getName)
                .containsExactly("Bella", "Luna", "Buddy", "Max");
        assertThat(result)
                .extracting(Animal::getIntakeDate)
                .isSorted();
    }
    @Test
    @DisplayName("sortByIntakeDate: returns empty list for null input")
    void shouldReturnEmptyForNullInputByDate() {
        assertThat(sorter.sortByIntakeDate(null)).isEmpty();
    }

    // --- sortBySpeciesThenAgeDescending ---

    @Test
    @DisplayName("sortBySpeciesThenAgeDescending: groups by species then orders by age desc")
    void shouldSortBySpeciesThenAgeDesc() {
        List<Animal> result = sorter.sortBySpeciesThenAgeDescending(List.of(buddy, luna, max, bella));

        assertThat(result)
                .extracting(Animal::getName)
                .containsExactly("Luna", "Bella", "Max", "Buddy");
        assertThat(result)
                .extracting(Animal::getSpecies)
                .containsExactly("Cat", "Cat", "Dog", "Dog");
    }
    @Test
    @DisplayName("sortBySpeciesThenAgeDescending: returns empty list for null input")
    void shouldReturnEmptyForNullInputBySpecies() {
        assertThat(sorter.sortBySpeciesThenAgeDescending(null)).isEmpty();
    }
}