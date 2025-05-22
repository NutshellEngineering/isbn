package is.bn;

import org.junit.jupiter.api.Test;

import static com.github.npathai.hamcrestopt.OptionalMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class Isbn10Test {

    @Test
    void valid_isbn10_is_accepted() {
        var isbn = new Isbn10("0-306-40615-2");
        assertThat(isbn.value(), is("0306406152"));
        assertThat(isbn.version(), is(Isbn.Version.ISBN_10));
    }

    @Test
    void invalid_format_is_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new Isbn10("123456789"));
    }

    @Test
    void invalid_check_digit_is_rejected() {
        assertThrows(IllegalArgumentException.class, () -> new Isbn10("0306406153"));
    }

    @Test
    void can_convert_to_isbn13() {
        var isbn10 = new Isbn10("0306406152");
        var isbn13 = isbn10.toIsbn13();
        assertThat(isbn13, isPresent());
        assertThat(isbn13, isPresentAndIs(new Isbn13("9780306406157")));
    }

    @Test
    void to_isbn10_returns_self() {
        var isbn10 = new Isbn10("0306406152");
        var result = isbn10.toIsbn10();
        assertThat(result, isPresent());
        assertThat(result, isPresentAnd(is(isbn10)));
    }
}