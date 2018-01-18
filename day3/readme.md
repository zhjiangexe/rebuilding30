## 待測系統(System Under Test)介紹

讓我們建立一個計算機類別Calculator，並且加入一個會回傳兩數相加的方法add，如下

```java
package rebuilding.day3;

public class Calculator {

    public int add(int number1, int number2) {
        return number1 + number2;
    }
}
```

有了待測系統之後，要來編寫測試並進行驗證，我們會需要編寫斷言(assert)來進行驗證。

## AssertJ介紹

AssertJ是一個編寫斷言的套件庫，AssertJ為編寫斷言來來了流暢寫法(fluent)，有用過Java 8的stream的朋友，應該會知道類似的流暢或鍊式寫法，並且語法跟自然語言相近，對於編寫測試時力求容易閱讀及維護這之上提供了相當大的改進。

而且透過IDE的代碼自動補全功能，使得編寫斷言更加容易。

想使用它的話，需要把assertj-core加入到build.gradle的dependencies裡
```java
dependencies {
    testCompile group: 'junit', name: 'junit', version: '4.12'
    // ↓加入使用assertj需要的套件庫
    testCompile group: 'org.assertj', name: 'assertj-core', version: '3.8.0'
}
```

我們編寫斷言時，會透過org.assertj.core.api.Assertions.assertThat()方法，
此方法將實際值(actual)做為方法參數，並使用方法參數的類型返回物件，也就是說在調用assertThat()方法之後，我們可以使用返回的對象來編寫斷言。

簡單來看，模式大概是這樣：
1. `assertThat(actual)` // 我斷言(執行實際結果)
2. `.isEqualTo(expected)` // 會等於(預期結果)

以下是一些基本用法
```java
// 驗證物件是否為null
assertThat(actual).isNull();
assertThat(actual).isNotNull();

// 驗證true or false
assertThat(actual).isTrue();
assertThat(actual).isFalse();

// 驗證List
assertThat(emptyList).isEmpty();
assertThat(emptyList).hasSize(5);

// 驗證Map
assertThat(map).containsKey(KEY);
```

## 3A原則
想要編寫好的測試，遵循3A原則這樣的模式，可以讓我們編寫出具有良好結構的測試案例

如下：
```java
@Test
public void testCase() {
    // 1. Arrange

    // 2. Act

    // 3. Assert
}
```

在使用`@Test`註解的方法，即測試案例內編寫：

1. Arrange：初始化目標物件(待測類別)、初始化相依物件、方法參數、預期結果(excepted)

2. Act：調用目標物件及欲測試的方法

3. Assert：驗證實際結果(actual)是否符合預期結果

## 使用3A原則編寫測試案例

前面我們介紹了一個待測系統，如下：
```java
package rebuilding.day3;

public class Calculator {

    public int add(int number1, int number2) {
        return number1 + number2;
    }
}

```

接下來，讓我們使用3A原則，以及AssertJ來對Calculator進行驗證，完成我們的測試案例

1. 建立測試類別及測試案例

   - 測試類別：該類別會建立src/test/java底下，且通常來說package路徑會與待測類別相同，如rebuilding.day3。類別名稱通常會是待測類別名稱加上後綴Test，如CalculatorTest

   - 測試案例：我們這裡就先以驗證5+10=15來命名，add_5_and_10_return_15

2. Arrange初始化，建立Calculator物件，準備方法參數number1, number2，以及預期結果
```java
package rebuilding.day3;

import org.junit.Test;

public class CalculatorTest {

    @Test
    public void add_5_and_10_return_15() {
        // 1. Arrange
        Calculator cal = new Calculator();
        int number1 = 5;
        int number2 = 10;
        int excepted = 15;

        // 2. Act

        // 3. Assert
    }
}
```

3. Act調用calculator的add方法
```java
package rebuilding.day3;

import org.junit.Test;

public class CalculatorTest {

    @Test
    public void add_5_and_10_return_15() {
        // 1. Arrange
        Calculator calculator = new Calculator();
        int number1 = 5;
        int number2 = 10;
        int excepted = 15;

        // 2. Act
        int actual = calculator.add(5, 10);

        // 3. Assert
    }
}
```

4. Assert驗證結果
```java
package rebuilding.day3;
import org.junit.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorTest {

    @Test
    public void add_5_and_10_return_15() {
        // 1. Arrange
        Calculator calculator = new Calculator();
        int number1 = 5;
        int number2 = 10;
        int excepted = 15;

        // 2. Act
        int actual = calculator.add(number1, number2);

        // 3. Assert
        assertThat(actual).isEqualTo(excepted);
    }
}
```

完成了測試案例之後，讓我們執行test run，可以看到測試案例如果符合預期則綠燈通過，如果想看看紅燈，可以改一下excepted來試試看喔。

---
以上，是今天的部分，接下來要再看看AssertJ還有哪些特性。
