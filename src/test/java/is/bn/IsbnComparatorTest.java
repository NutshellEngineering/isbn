package is.bn;

import co.mp.ComparatorVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

final class IsbnComparatorTest {

    @Test
    void comparator_contract() {
        ComparatorVerifier.forComparator(IsbnComparator.class)
                .withExamples(
                        new Isbn10("3030516717"),
                        new Isbn13("978-0750709026"),
                        new Isbn13("978-1465480248"))
                .verify();
    }

    static Stream<Arguments> isbn_comparison_cases() {
        return Stream.of(
                Arguments.of(
                        // Mix of ISBN-10 and matching ISBN-13, plus unrelated 978 and 979 ISBN-13
                        List.of(
                                new Isbn10("0439023483"),            // convertible to 9780439023481
                                new Isbn13("9780439023481"),         // same as above, already ISBN-13
                                new Isbn13("9783161484100"),         // unrelated 978 ISBN-13
                                new Isbn13("9798886451740")          // non-convertible 979 ISBN-13
                        ),
                        // Expected order: promoted ISBN-10, equivalent ISBN-13, unrelated 978, then 979
                        List.of(
                                new Isbn10("0439023483"),
                                new Isbn13("9780439023481"),
                                new Isbn13("9783161484100"),
                                new Isbn13("9798886451740")
                        )
                ),
                Arguments.of(
                        // Start with a 979 ISBN-13, followed by a convertible ISBN-10 and an unrelated ISBN-13
                        List.of(
                                new Isbn13("9798886451740"),
                                new Isbn10("316148410X"),            // convertible to 9783161484100
                                new Isbn13("9780306406157")
                        ),
                        // Expected order: both 978-prefixed ISBN-13s first (promoted or native), then 979
                        List.of(
                                new Isbn13("9780306406157"),
                                new Isbn10("316148410X"),
                                new Isbn13("9798886451740")
                        )
                ),
                Arguments.of(
                        // Reverse order to test reordering
                        List.of(
                                new Isbn13("9798886451740"),
                                new Isbn13("9783161484100"),
                                new Isbn10("0439023483"),
                                new Isbn13("9780439023481")
                        ),
                        List.of(
                                new Isbn10("0439023483"),
                                new Isbn13("9780439023481"),
                                new Isbn13("9783161484100"),
                                new Isbn13("9798886451740")
                        )
                ),
                Arguments.of(
                        // Out-of-order mix
                        List.of(
                                new Isbn13("9783161484100"),
                                new Isbn10("0439023483"),
                                new Isbn13("9798886451740"),
                                new Isbn13("9780439023481")
                        ),
                        List.of(
                                new Isbn10("0439023483"),
                                new Isbn13("9780439023481"),
                                new Isbn13("9783161484100"),
                                new Isbn13("9798886451740")
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("isbn_comparison_cases")
    void given_unsorted_ISBNs_comparator_should_produce_correct_order(List<Isbn> input, List<Isbn> expectedOrder) {
        var mutable = new java.util.ArrayList<>(input);
        mutable.sort(IsbnComparator.INSTANCE);
        assertThat(mutable, contains(expectedOrder.toArray()));
    }

}