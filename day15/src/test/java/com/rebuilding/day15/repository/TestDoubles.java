package com.rebuilding.day15.repository;

import static org.mockito.Mockito.mock;

/**
 * 1. 建立名為TestDoubles的工具類別
 **/
public class TestDoubles {
    /**
     * 2. 加入private建構函式，避免開發人員直接建立此物件
     */
    private TestDoubles() {
    }

    /**
     * 3. 加入一個回傳mock的靜態方法，方法名稱是為強調所建立的Mock將會當成Stub使用
     */
    public static <T> T stub(Class<T> stubClass) {
        return mock(stubClass);
    }

}
