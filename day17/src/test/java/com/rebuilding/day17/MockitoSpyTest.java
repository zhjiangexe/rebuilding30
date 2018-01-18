package com.rebuilding.day17;

import com.rebuilding.day17.entity.Issue;
import com.rebuilding.day17.repository.IssueRepository;
import com.rebuilding.day17.repository.imp.IssueRepositoryImp;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.*;

public class MockitoSpyTest {

    private IssueRepository repository;

    @Before
    public void setup() {
        repository = spy(new IssueRepositoryImp());
    }

    @Test
    public void testExecuteTimes() {
        /**
         * arrange
         * 建立欲存入的Issue物件
         */
        Issue expected = new Issue();

        /**
         * act
         * 執行方法，此時會真的執行物件IssueRepositoryImp的方法save()
         */
        Issue saved = repository.save(expected);

        /**
         * assert
         * 驗證執行次數
         */
        verify(repository, times(1)).save(saved);
    }

    @Test
    public void testReturnObject() {
        /**
         * arrange
         * 建立欲存入的Issue物件
         * 偽造部分方法，但其他方法不會被影響，依然會執行真正物件的方法
         */
        Issue expected = new Issue();
        willReturn(expected).given(repository).save(any());

        /**
         * act
         * 執行方法，此時將不會執行真正的物件方法
         */
        Issue saved2 = repository.save(expected);

        /**
         * 驗證回傳物件是否相同
         */
        assertThat(saved2).isSameAs(expected);
    }
}
