package com.bblackbird.violation;

import org.junit.Test;

import java.util.EnumSet;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

public class ErrorCodeTypeTest {


    @Test
    public void testAllExceptions() {

        Exception ex = new Exception("test");

        EnumSet.allOf(ErrorCodeType.class).forEach( errorType -> {
            System.out.println(errorType.name());
            Exception e = errorType.exception("test", ex);
            System.out.println(e);
            assertThat(e, notNullValue());
            assertThat(e, instanceOf(Exception.class));
            assertThat(e.getMessage(), is("test"));
        });

    }

    @Test
    public void testAllExceptionsWithExceptionOnly() {

        Exception ex = new Exception("test");

        EnumSet.allOf(ErrorCodeType.class).forEach( errorType -> {
            System.out.println(errorType.name());
            Exception e = errorType.exception(ex);
            System.out.println(e);
            assertThat(e, notNullValue());
            assertThat(e, instanceOf(Exception.class));
            assertThat(e.getMessage(), is("java.lang.Exception: test"));
        });

    }

    @Test
    public void testAllExceptionsWithErrorMessageOnly() {

        EnumSet.allOf(ErrorCodeType.class).forEach( errorType -> {
            System.out.println(errorType.name());
            Exception e = errorType.exception("test");
            System.out.println(e);
            assertThat(e, notNullValue());
            assertThat(e, instanceOf(Exception.class));
            assertThat(e.getMessage(), is("test"));
        });

    }


}
