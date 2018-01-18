# 使用 JUnitParamsRunner 編寫參數化測試

由於JUnit原生的參數化測試API有諸多限制，使得閱讀及維護不是那麼的容易，所以以下要介紹使用JUnitParams來簡化測試程式。

在那之前，我們必須先引用JUnitParams套件庫
build.gradle
```java
dependencies {
    /*....*/
    testCompile group: 'pl.pragmatists', name: 'JUnitParams', version: '1.1.1'
}
```

## 待測目標介紹

這裡建立了一個簡單的計算機Calculator待測類別，以及一個加法add()。
```java
package com.rebuilding.day20;

public class Calculator {
    public int add(int firstNumber, int secondNumber) {
        return firstNumber + secondNumber;
    }
}
```

## 編寫參數化測試

接下來必須在編寫的測試類別上註解`@RunWith(JUnitParamsRunner.class)`，這樣才有辦法執行簡化的參數化測試。
```java
package com.rebuilding.day20;

import junitparams.JUnitParamsRunner;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class CalculatorTest {
}
```

介紹以下三種方法，編寫註解，並依照其指定方式帶入參數
1. @Parameters({帶入測試參數陣列})
2. @Parameters(method="指定回傳資料陣列的方法")
3. @FileParameters("指定csv檔案位置")

1. @Parameters({帶入測試參數陣列})
方法參數依照所需要的資料提供，並在@Parameters value直接提供參數，雙引號代表一筆輸入資料，資料內使用,或|分隔值，值可以為多種型別。
```java
@Test
@Parameters(value = {
        "0, 0, 0, print1, true",
        "1| 1| 2| print2| false"
})
public void testCase1(int first, int second, int expectedSum, String print, boolean trueFalse) {
    Calculator calculator = new Calculator();

    int actualSum = calculator.add(first, second);

    System.out.println(print + ", " + trueFalse);

    assertThat(actualSum).isEqualByComparingTo(expectedSum);
}
````

2. @Parameters(method="指定回傳資料陣列的方法")
方法參數依照所需要的資料提供，並在@Parameters method指定提供資料的方法，資料由new Object[]所構成，也可以混合的傳遞物件，
```java
@Test
@Parameters(method = "testAddData")
public void testCase2(Issue issue, int second, int expectedSum) {
    Calculator calculator = new Calculator();

    int actualSum = calculator.add(issue.getId(), second);

    assertThat(actualSum).isEqualByComparingTo(expectedSum);
}

/** 建立指定的方法並回傳資料陣列 **/
private Object[] testAddData() {
    return new Object[]{
            new Object[]{new Issue(1), 2, 3},
            new Object[]{new Issue(5), 10, 15}
    };
}
```

3. @FileParameters("指定csv檔案位置")
我們也可以利用csv檔案提供的測試資料

準備好測試資料，src/test/resources/JunitParamsTestParameters.csv
```csv
1,2,3
-10, 30, 20
15, -5, 10
-5, -10, -15
```

使用@FileParameters來讀取csv檔案取得測試資料
```java
@Test
@FileParameters("src/test/resources/JunitParamsTestParameters.csv")
public void testCase3(int first, int second, int expectedSum) {
    Calculator calculator = new Calculator();

    int actualSum = calculator.add(first, second);

    assertThat(actualSum).isEqualByComparingTo(expectedSum);
}
```


另外，可以透過@TestCaseName指定測試案例參數化的名稱，如下：
```java
@Test
@Parameters(value = {
        "0, 0, 0, print1, true",
        "1| 1| 2| print2| false"
})
@TestCaseName("test case 1 -> first: {0}, second: {1}, expect: {2}, print: {3}, something: {4}")
public void testCase1(int first, int second, int expectedSum, String print, boolean trueFalse) {
}
```

它還有幾項使用方式可以參考[官方範例](https://github.com/Pragmatists/JUnitParams/tree/master/src/test/java/junitparams/usage)

本篇介紹了JUnitParamsRunner，它幫我們的參數化測試做了相當的簡化，免去原生繁雜的寫法。

二十天過去，Java單元測試就到這邊了。

接下來的篇章....

突入Spring單元測試、整合測試。