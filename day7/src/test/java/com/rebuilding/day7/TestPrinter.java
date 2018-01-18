package com.rebuilding.day7;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class TestPrinter implements TestRule {

    @Override
    public Statement apply(final Statement base, final Description description) {

        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                System.out.println("start " + description.getDisplayName());
                base.evaluate();
                System.out.println("end " + description.getDisplayName());
            }
        };
    }
}
