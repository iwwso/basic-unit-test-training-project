package lv.bootcamp.shelter.task5;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Task 5: nested test classes for AnimalCsvParser.
 * File tests use @TempDir - JUnit creates a temporary folder and deletes it after the test.
 *
 * @author Karyna Litvinova
 */
@DisplayName("AnimalCsvParser")
class AnimalCsvParserTest {

    private static final String HEADER = "name,species,age,vaccinated,intakeDate";

    private AnimalCsvParser parser;

    @BeforeEach
    void setUp() {
        parser = new AnimalCsvParser();
    }

    // ==================== parseRow tests ====================

    @Nested
    @DisplayName("When parsing valid rows")
    class ValidRows {

        @Test
        @DisplayName("parses a complete row into an Animal")
        void shouldParseCompleteRow() {
            Optional<Animal> result = parser.parseRow("Buddy,Dog,3,true,2026-01-15");

            assertThat(result).isPresent();
            Animal animal = result.orElseThrow();
            assertThat(animal.getName()).isEqualTo("Buddy");
            assertThat(animal.getSpecies()).isEqualTo("Dog");
            assertThat(animal.getAge()).isEqualTo(3);
            assertThat(animal.isVaccinated()).isTrue();
            assertThat(animal.getIntakeDate()).isEqualTo(LocalDate.of(2026, 1, 15));
        }

        @Test
        @DisplayName("trims whitespace from fields")
        void shouldTrimWhitespace() {
            Optional<Animal> result = parser.parseRow("  Buddy , Dog , 3 , true , 2026-01-15 ");

            assertThat(result).contains(
                    new Animal("Buddy", "Dog", 3, true, LocalDate.of(2026, 1, 15)));
        }

        @Test
        @DisplayName("parses vaccinated=false correctly")
        void shouldParseFalseVaccination() {
            Optional<Animal> result = parser.parseRow("Max,Dog,5,false,2026-01-18");

            assertThat(result).isPresent();
            assertThat(result.orElseThrow().isVaccinated()).isFalse();
        }

        @Test
        @DisplayName("ignores extra columns after the fifth one")
        void shouldIgnoreExtraColumns() {
            Optional<Animal> result = parser.parseRow("Buddy,Dog,3,true,2026-01-15,extra");

            assertThat(result).isPresent();
            assertThat(result.orElseThrow().getName()).isEqualTo("Buddy");
        }
    }

    @Nested
    @DisplayName("When parsing malformed rows")
    class MalformedRows {

        @Test
        @DisplayName("returns empty for null input")
        void shouldReturnEmptyForNull() {
            assertThat(parser.parseRow(null)).isEmpty();
        }

        @Test
        @DisplayName("returns empty for blank input")
        void shouldReturnEmptyForBlank() {
            assertThat(parser.parseRow("   ")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when row has fewer than 5 fields")
        void shouldReturnEmptyForTooFewFields() {
            assertThat(parser.parseRow("Buddy,Dog,3")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when name is missing")
        void shouldReturnEmptyForMissingName() {
            assertThat(parser.parseRow(",Dog,3,true,2026-01-15")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when age is not a number")
        void shouldReturnEmptyForBadAge() {
            assertThat(parser.parseRow("Buddy,Dog,old,true,2026-01-15")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when age is negative")
        void shouldReturnEmptyForNegativeAge() {
            assertThat(parser.parseRow("Buddy,Dog,-1,true,2026-01-15")).isEmpty();
        }

        @Test
        @DisplayName("returns empty when date is invalid")
        void shouldReturnEmptyForBadDate() {
            assertThat(parser.parseRow("Buddy,Dog,3,true,not-a-date")).isEmpty();
        }
    }

    @Nested
    @DisplayName("When handling edge cases")
    class EdgeCases {

        @Test
        @DisplayName("handles vaccinated field as any non-true string -> false")
        void shouldTreatNonTrueAsFalse() {
            Optional<Animal> result = parser.parseRow("Buddy,Dog,3,maybe,2026-01-15");

            assertThat(result).isPresent();
            assertThat(result.orElseThrow().isVaccinated()).isFalse();
        }

        @Test
        @DisplayName("handles TRUE in upper case as vaccinated")
        void shouldTreatUpperCaseTrueAsTrue() {
            Optional<Animal> result = parser.parseRow("Buddy,Dog,3,TRUE,2026-01-15");

            assertThat(result.orElseThrow().isVaccinated()).isTrue();
        }

        @Test
        @DisplayName("handles age 0 as valid")
        void shouldAcceptAgeZero() {
            Optional<Animal> result = parser.parseRow("Kitty,Cat,0,true,2026-01-15");

            assertThat(result).isPresent();
            assertThat(result.orElseThrow().getAge()).isZero();
        }
    }

    // ==================== parseFile tests ====================

    @Nested
    @DisplayName("When parsing a CSV file")
    class ParseFile {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("parses valid rows and counts skipped rows")
        void shouldParseFileAndCountSkipped() throws IOException {
            Path file = tempDir.resolve("intake.csv");
            Files.writeString(file, String.join("\n",
                    HEADER,
                    "Buddy,Dog,3,true,2026-01-15",
                    "Luna,Cat,2,true,2026-01-16",
                    "Max,Dog,5,false,2026-01-18",
                    "Simba,Cat,-1,true,2026-02-08"), StandardCharsets.UTF_8);

            AnimalCsvParser.ParseResult result = parser.parseFile(file);

            assertThat(result.animals())
                    .extracting(Animal::getName)
                    .containsExactly("Buddy", "Luna", "Max");
            assertThat(result.skippedRows()).isEqualTo(1);
        }

        @Test
        @DisplayName("returns empty result for file with only a header")
        void shouldReturnEmptyForHeaderOnly() throws IOException {
            Path file = tempDir.resolve("header-only.csv");
            Files.writeString(file, HEADER, StandardCharsets.UTF_8);

            AnimalCsvParser.ParseResult result = parser.parseFile(file);

            assertThat(result.animals()).isEmpty();
            assertThat(result.skippedRows()).isZero();
        }

        @Test
        @DisplayName("parses the real intake.csv: 20 animals, 5 skipped rows")
        void shouldParseRealIntakeFile() throws IOException, URISyntaxException {
            Path file = Path.of(Objects.requireNonNull(
                    getClass().getResource("/intake.csv")).toURI());

            AnimalCsvParser.ParseResult result = parser.parseFile(file);

            assertThat(result.animals()).hasSize(20);
            assertThat(result.skippedRows()).isEqualTo(5);
            assertThat(result.animals().get(0).getName()).isEqualTo("Buddy");
        }

        @Test
        @DisplayName("throws IOException for non-existent file")
        void shouldThrowForMissingFile() {
            Path missing = tempDir.resolve("does-not-exist.csv");

            assertThatThrownBy(() -> parser.parseFile(missing))
                    .isInstanceOf(IOException.class);
        }
    }
}