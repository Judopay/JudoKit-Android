package com.judopay.arch.api;

import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class UserAgentTest {

    @Test
    public void shouldGenerateUserAgentString() {
        UserAgent userAgent = new UserAgent("5.0", "22", "LG", "Nexus 5", Locale.ENGLISH.toString());

        assertThat(userAgent.toString(), is("JudoPaymentsSDK/5.0 (android 22 LG Nexus 5; en)"));
    }

}