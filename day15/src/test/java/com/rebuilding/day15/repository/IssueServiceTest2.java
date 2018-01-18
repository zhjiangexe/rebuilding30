package com.rebuilding.day15.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static info.solidsoft.mockito.java8.AssertionMatcher.assertArg;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class IssueServiceTest2 {
    private IssueRepository repository;

    @Before
    public void setup() {
        repository = mock(IssueRepository.class);
    }

    /**
     * 驗證待測系統調用了1次Mock物件的指定方法
     */
    @Test
    public void verifyWhenMethodWasCalledOnce() {

        repository.findById(1L);

        verify(repository).findById(1L);
    }

    /**
     * 驗證待測系統調用了2次Mock物件的指定方法
     */
    @Test
    public void verifyThatMethodWasCalledOnce() {

        repository.findById(1L);
        repository.findById(1L);

        verify(repository, times(2)).findById(1L);
    }

    /**
     * 驗證待測系統從未調用Mock物件的指定方法
     */
    @Test
    public void verifyThatMethodWasNotInvoked() {

        verify(repository, never()).findById(1L);
    }

    /**
     * 驗證待測系統調用了2次Mock物件的指定方法，除此之外沒有任何互動
     */
    @Test
    public void verifyThatNoOtherMethodsWereInvoked() {

        repository.findById(1L);
        repository.findById(1L);

        verify(repository, times(2)).findById(1L);

        verifyNoMoreInteractions(repository);
    }

    /**
     * 驗證待測系統與Mock物件沒有任何互動，換句話說，驗證待測系統從未調用Mock物件的任何方法
     */
    @Test
    public void verifyThatNoInteractionsHappenedBetweenSUTAndMock() {

        verifyZeroInteractions(repository);
    }

    /**
     * 驗證待測系統調用了1次Mock物件的指定方法，且透過調用ArgumentMatchers.eq()指定方法參數為1L
     */
    @Test
    public void verifyThatMethodWasInvokedByPassing1LAsMethodParameter() {

        repository.findById(1L);

        verify(repository).findById(eq(1L));
    }

    /**
     * 驗證待測系統調用了1次Mock物件的指定方法，且透過調用ArgumentMatchers.anyLong()指定方法參數為任意Long型態
     */
    @Test
    public void verifyThatMethodWasInvokedByPassingAnyLongAsMethodParameter() {

        repository.findById(3L);

        verify(repository).findById(anyLong());
    }

    /**
     * 如果想為傳入Mock物件指定方法的方法參數進行斷言，則必須透過ArgumentCaptor<T>來取得實際方法參數
     *
     * 驗證待測系統調用了1次Mock物件的指定方法，且由外部斷言方法參數為1L
     */
    @Test
    public void verifyThatMethodWasInvokedByUsingArgumentCaptor() {
        repository.findById(1L);

        ArgumentCaptor<Long> argument = ArgumentCaptor.forClass(Long.class);

        verify(repository).findById(argument.capture());

        Long actualId = argument.getValue();

        assertThat(actualId).isEqualTo(1L);
    }

    /**
     * 如果想為傳入Mock物件指定方法的方法參數進行斷言
     * 除了上述的方式以外，如果是使用java8的使用者，則可以引入額外的相依套件庫
     *
     * 驗證待測系統調用了1次Mock物件的指定方法，且由外部斷言方法參數為1L
     */
    @Test
    public void verifyThatMethodWasInvokedByUsingArgumentCaptorJava8() {
        repository.findById(1L);

        verify(repository).findById(
                assertArg(
                        argument -> assertThat(argument).isEqualTo(1L)
                )
        );
    }
}
