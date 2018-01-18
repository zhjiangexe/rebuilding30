import rebuilding.day3.Calculator;
import org.junit.*;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorTest {
    @BeforeClass
    public static void beforeClass() {
        System.out.println("before class");
    }

    @Before
    public void before() {
        System.out.println("before");
    }

    @After
    public void after() {
        System.out.println("after");
    }

    @AfterClass
    public static void afterClass() {
        System.out.println("after class");
    }

    @Test
    public void Case1() {
        // given
        Calculator dealCalculator = new Calculator();
        Double expected = 1D;
//        Rock rock = new Rock();
        // when
//        Double actual = dealCalculator.getDiscount(1);

        // then
//        assertThat(actual)
//                .isEqualTo(expected);

    }

    @Test
    public void Case2() {
        System.out.println("test case 2");
    }
}
