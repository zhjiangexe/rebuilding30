package com.rebuilding.day14.repository;

import com.rebuilding.day14.entity.Issue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static com.rebuilding.day14.repository.TestDoubles.stub;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;

public class BDDMockitoTest {
    private IssueRepository repository;

    @Before
    public void setup() {
        repository = stub(IssueRepository.class);
    }

    /**
     * 案例1：無方法參數，有回傳值或物件
     */
    @Test
    public void testCount() {
        /**
         * 調用BDDMockito類別的方法
         * given(想要偽造的方法).willReturn(指定的回傳值或物件)
         */
        given(repository.count()).willReturn(1L);

        /**
         * actualCount此時就會取得指定的回傳值1L
         */
        long actualCount = repository.count();

        assertThat(actualCount).isEqualByComparingTo(1L);
    }

    /**
     * 案例2：有方法參數，有回傳值或物件
     */
    @Test
    public void testFindById1() {

        Issue expected = new Issue();
        /**
         * 調用BDDMockito類別的方法
         * given(想要偽造的方法(指定參數)).willReturn(指定的回傳值或物件)
         */
        given(repository.findById(1L)).willReturn(expected);

        /**
         * actual此時就會取得指定的回傳Issue
         */
        Issue actual = repository.findById(1L);

        /**
         * 如果傳入非指定的參數，則會取得2L
         */
        // Issue actual = repository.findById(2L);

        assertThat(actual).isSameAs(expected);
    }

    /**
     * 案例3：有方法參數，有回傳值或物件，使用ArgumentMatchers
     */
    @Test
    public void testFindById2() {

        Issue expected = new Issue();

        given(repository.findById(eq(1L))).willReturn(expected);

        Issue actual = repository.findById(1L);

        assertThat(actual).isSameAs(expected);
    }

    /**
     * 案例4：有方法參數，有回傳值或物件，使用ArgumentMatchers anyLong
     */
    @Test
    public void testFindById3() {

        Issue expected = new Issue();

        /**
         * 調用BDDMockito類別的方法
         * given(想要偽造的方法(anyLong())).willReturn(指定的回傳值或物件)
         */
        given(repository.findById(anyLong())).willReturn(expected);

        /**
         * 任意給個型態為Long的參數，都可取得指定的回傳值
         */
        Issue actual = repository.findById(99L);

        assertThat(actual).isSameAs(expected);
    }

    /**
     * 案例4：有方法參數，有回傳值或物件，拋出異常
     */
    @Test(expected = Exception.class)
    public void testFindById4() {

        /**
         * 調用BDDMockito類別的方法
         * given(想要偽造的方法).willThrow(指定的Exception)
         */
        given(repository.findById(1L)).willThrow(new Exception());

        /**
         * 調用此方法會發生異常
         * 透過@Test屬性excepted捕捉期望異常
         */
        repository.findById(1L);
    }

    /**
     * 案例5：無回傳值或物件，拋出異常
     */
    @Test(expected = Exception.class)
    public void testDelete() {

        Issue deleted = new Issue();

        /**
         * 調用BDDMockito類別的方法，沒有回傳值，跟前面不一樣是以given()開頭
         * willThrow(指定的Exception).given(Stub).執行方法
         */
        willThrow(new Exception()).given(repository).delete(deleted);

        /**
         * 調用此方法會發生異常
         * 透過@Test屬性excepted捕捉期望異常
         */
        repository.delete(deleted);
    }

    /**
     * 案例6：如果具有連Argument Matcher都無法解決的情境，則實作Answer介面來解決
     */
    @Test
    public void testFindById5() {

        Issue expected = new Issue();

        /**
         * 調用BDDMockito類別的方法，沒有回傳值，跟前面不一樣是以given()開頭
         * willThrow(想要偽造的方法).willAnswer(複雜情境實作Answer介面)
         */
        given(repository.findById(1L)).willAnswer(new Answer<Issue>() {
            @Override
            public Issue answer(InvocationOnMock invocation) {
                Long idParameter = (Long) invocation.getArguments()[0];
                if (idParameter.equals(1L)) {
                    return expected;
                } else {
                    return null;
                }
            }
        });

        Issue actual = repository.findById(1L);

        assertThat(actual).isSameAs(expected);
    }

}
