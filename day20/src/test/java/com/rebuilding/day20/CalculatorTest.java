package com.rebuilding.day20;

import junitparams.FileParameters;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class CalculatorTest {

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

    @Test
    @Parameters(method = "testAddData")
    public void testCase2(Issue issue, int second, int expectedSum) {
        Calculator calculator = new Calculator();

        int actualSum = calculator.add(issue.getId(), second);

        assertThat(actualSum).isEqualByComparingTo(expectedSum);
    }

    private Object[] testAddData() {
        return new Object[]{
                new Object[]{new Issue(1), 2, 3},
                new Object[]{new Issue(5), 10, 15}
        };
    }

    @Test
    @FileParameters("src/test/resources/JunitParamsTestParameters.csv")
    public void testCase3(int first, int second, int expectedSum) {
        Calculator calculator = new Calculator();

        int actualSum = calculator.add(first, second);

        assertThat(actualSum).isEqualByComparingTo(expectedSum);
    }

    @Test
    @Parameters(method = "mixedParameters")
    @TestCaseName("{0}, {1}, {2}, {3}")
    public void usageOfMultipleTypesOfParameters(boolean booleanValue, int[] primitiveArray, String stringValue, String[] stringArray) {

    }

    public Object mixedParameters() {
        boolean booleanValue = true;
        int[] primitiveArray = {1, 2, 3};
        String stringValue = "Test";
        String[] stringArray = {"one", "two", null};
        return new Object[]{
                new Object[]{booleanValue, primitiveArray, stringValue, stringArray}
        };
    }
}
