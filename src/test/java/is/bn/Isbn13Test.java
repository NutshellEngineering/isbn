package is.bn;

import org.junit.jupiter.api.Test;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class Isbn13Test {

    @Test
    void valid_isbn13_is_accepted() {
        var isbn = new Isbn13("978-0-306-40615-7");
        assertThat(isbn.value(), is("9780306406157"));
        assertThat(isbn.version(), is(Isbn.Version.ISBN_13));
    }

    @Test
    void invalid_format_is_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new Isbn13("123456789012"));
    }

    @Test
    void invalid_check_digit_is_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new Isbn13("9780306406158"));
    }

    @Test
    void can_convert_to_isbn10() {
        var isbn13 = new Isbn13("9780306406157");
        var isbn10 = isbn13.toIsbn10();
        assertThat(isbn10, isPresentAndIs(new Isbn10("0306406152")));
    }

    @Test
    void cannot_convert_to_isbn10_if_prefix_is_not_978() {
        var invalid = new Isbn13("9791234567896");
        var result = invalid.toIsbn10();
        assertThat(result, isEmpty());
    }
}