package is.bn;

import org.junit.jupiter.api.Test;

import static com.github.npathai.hamcrestopt.OptionalMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class IsbnTest {

    @Test
    void from_string_parses_correctly() {
        var i10 = Isbn.fromString("0-306-40615-2");
        assertThat(i10, instanceOf(Isbn10.class));

        var i13 = Isbn.fromString("978-0-306-40615-7");
        assertThat(i13, instanceOf(Isbn13.class));
    }

    @Test
    void from_string_rejects_invalid_length() {
        assertThrows(IllegalArgumentException.class, () -> Isbn.fromString("12345"));
    }

    @Test
    void convert_works_both_ways() {
        var i10 = new Isbn10("0306406152");
        var i13 = Isbn.convert(i10, Isbn.Version.ISBN_13);
        assertThat(i13, isPresent());
        assertThat(i13, isPresentAnd(instanceOf(Isbn13.class)));

        var i13v = new Isbn13("9780306406157");
        var i10v = Isbn.convert(i13v, Isbn.Version.ISBN_10);
        assertThat(i10v, isPresent());
        assertThat(i10v, isPresentAnd(instanceOf(Isbn10.class)));
    }

    @Test
    void convert_rejects_inconvertible() {
        var i13 = new Isbn13("9791234567896");
        var result = Isbn.convert(i13, Isbn.Version.ISBN_10);
        assertThat(result, isEmpty());
    }
}