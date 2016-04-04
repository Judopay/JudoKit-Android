package com.judopay;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class JudoSetupTest {

    @Test
    public void shouldEnabledSslPinningWhenLiveEnvironment() {
        Judo.setup("apiToken", "apiSecret", Judo.LIVE);

        assertThat(Judo.isSslPinningEnabled(), is(true));
    }

    @Test
    public void shouldEnabledSslPinningWhenSandboxEnvironment() {
        Judo.setup("apiToken", "apiSecret", Judo.SANDBOX);

        assertThat(Judo.isSslPinningEnabled(), is(true));
    }

    @Test
    public void shouldNotEnableSslPinningWhenUatEnvironment() {
        Judo.setup("apiToken", "apiSecret", Judo.UAT);

        assertThat(Judo.isSslPinningEnabled(), is(false));
    }

}