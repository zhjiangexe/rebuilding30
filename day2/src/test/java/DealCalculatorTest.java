import org.junit.*;
import rebuilding.day2.DealCalculator;

public class DealCalculatorTest {
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
        DealCalculator dealCalculator = new DealCalculator();
        Double expected = 1D;
//        assertThat()
        Double actual = dealCalculator.getDiscount(1);
//        assertThat(actual)
//        assertEquals(expected, actual);

    }

    @Test
    public void Case2() {
        System.out.println("test case 2");
    }
}
