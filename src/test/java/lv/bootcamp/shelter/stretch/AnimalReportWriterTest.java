package lv.bootcamp.shelter.stretch;

import lv.bootcamp.shelter.model.Animal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Stretch goal: testing file output of AnimalReportWriter.
 * Each test writes into a @TempDir folder, so no files are left behind.
 *
 * @author Karena Litvinova
 */
@DisplayName("AnimalReportWriter (stretch)")
class AnimalReportWriterTest {

    private static final LocalDate DATE = LocalDate.of(2026, 1, 15);

    private final AnimalReportWriter writer = new AnimalReportWriter();

    @TempDir
    Path tempDir;

    private final List<Animal> animals = List.of(
            new Animal("Buddy", "Dog", 3, true, DATE),
            new Animal("Luna", "Cat", 2, true, DATE),
            new Animal("Max", "Dog", 5, true, DATE),
            new Animal("Milo", "Cat", 4, false, DATE)
    );

    private String writeAndRead(List<Animal> input) throws IOException {
        Path output = tempDir.resolve("report.txt");
        writer.writeReport(input, output);
        return Files.readString(output, StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("writes report file that contains total count")
    void shouldWriteTotalCount() throws IOException {
        List<Animal> three = animals.subList(0, 3);

        String content = writeAndRead(three);

        assertThat(content)
                .startsWith("=== Shelter Intake Report ===")
                .contains("Date generated: " + LocalDate.now())
                .contains("Total animals: 3");
    }

    @Test
    @DisplayName("writes per-species breakdown in alphabetical order")
    void shouldWriteSpeciesBreakdown() throws IOException {
        String content = writeAndRead(animals);

        assertThat(content).contains("Cat: 2 total, 1 vaccinated");
        assertThat(content).contains("Dog: 2 total, 2 vaccinated");
        assertThat(content.indexOf("Cat: 2 total")).isLessThan(content.indexOf("Dog: 2 total"));
    }

    @Test
    @DisplayName("writes oldest animal per species")
    void shouldWriteOldestPerSpecies() throws IOException {
        String content = writeAndRead(animals);

        assertThat(content)
                .contains("Dog: Max (age 5)")
                .contains("Cat: Milo (age 4)")
                .doesNotContain("Dog: Buddy (age 3)");
    }

    @Test
    @DisplayName("writes a valid report for an empty list")
    void shouldWriteReportForEmptyList() throws IOException {
        String content = writeAndRead(List.of());

        assertThat(content)
                .contains("Total animals: 0")
                .contains("--- Per-species breakdown ---")
                .contains("--- Oldest per species ---");
    }

    // ---------- stretch goal 2: stream-based assertion on grouped content ----------

    @Test
    @DisplayName("breakdown section contains exactly one line per species, sorted")
    void shouldContainOneGroupedLinePerSpecies() throws IOException {
        Path output = tempDir.resolve("grouped.txt");
        writer.writeReport(animals, output);

        List<String> lines = Files.readAllLines(output, StandardCharsets.UTF_8);
        List<String> breakdown = lines.stream()
                .dropWhile(line -> !line.equals("--- Per-species breakdown ---"))
                .skip(1)
                .takeWhile(line -> !line.isBlank())
                .toList();

        assertThat(breakdown)
                .containsExactly("Cat: 2 total, 1 vaccinated", "Dog: 2 total, 2 vaccinated");
        assertThat(breakdown)
                .allSatisfy(line -> assertThat(line).matches("\\w+: \\d+ total, \\d+ vaccinated"));
    }

    // ---------- stretch goal 3: spy keeps the real behaviour ----------

    @Test
    @DisplayName("spy: writeReport is called once and the real file is still written")
    void shouldCallRealMethodOnSpy() throws IOException {
        AnimalReportWriter spyWriter = spy(new AnimalReportWriter());
        Path output = tempDir.resolve("spy-report.txt");

        spyWriter.writeReport(animals, output);

        verify(spyWriter, times(1)).writeReport(animals, output);
        assertThat(output).exists();
        assertThat(Files.readString(output, StandardCharsets.UTF_8)).contains("Total animals: 4");
    }
}