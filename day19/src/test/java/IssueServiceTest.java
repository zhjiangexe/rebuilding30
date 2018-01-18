import com.rebuilding.day19.entity.Issue;
import com.rebuilding.day19.service.IssueService;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.*;
import org.junit.runner.RunWith;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class IssueServiceTest {
    private IssueService mockService;
    /**
     * 第一層配置待測類別
     */
    @BeforeClass
    public static void rootBeforeClass() {
        System.out.println("root before class");
    }

    @Before
    public void rootBefore() {
        System.out.println("root before");
        mockService = mock(IssueService.class);
    }

    @After
    public void rootAfter() {
        System.out.println("root after");
    }

    @AfterClass
    public static void rootAfterClass() {
        System.out.println("root after class");
    }

    public class FindById {
        /**
         * ↑第二層，每一個方法建立一個內部類別，並以待測方法名稱當作內部類別命名
         * ↓透過setup配置該方法所需的測試資料或測試替身等
         */
        public static final long ID = 1L;

        @Before
        public void findByIdBefore() {
            System.out.println("findById before");
        }

        @After
        public void findByIdAfter() {
            System.out.println("findById after");
        }

        public class WhenIssueFound {
            /**
             * ↑第三層，依測試案例作為第三層內部類別命名
             * ↓並配置該案例所需的測試替身或測試資料
             */
            @Before
            public void FindByIdWhenIssueFoundBefore() {
                System.out.println("findById when issue found before");
                given(mockService.findById(ID)).willReturn(new Issue());
            }

            @Test
            public void testCase(){
                System.out.println("find by id when issue found test case");
            }
        }

        public class WhenIssueNotFound {

            @Before
            public void FindByIdWhenIssueNotFoundBefore() {
                System.out.println("findById when issue found before");
                given(mockService.findById(ID)).willReturn(new Issue());
            }

            @Test
            public void testCase(){
                System.out.println("find by id when issue not found test case");
            }
        }
    }
}
