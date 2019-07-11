package com.judopay;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class JudoSetupTest {

    @Test
    public void shouldEnableSslPinningWhenLiveEnvironment() {
        Judo judo = new Judo.Builder("apiToken", "apiSecret")
                .setJudoId("100407196")
                .setEnvironment(Judo.LIVE)
                .build();

        assertThat(judo.isSslPinningEnabled(), is(true));
    }

    @Test
    public void shouldEnableSslPinningWhenSandboxEnvironment() {
        Judo judo = new Judo.Builder("apiToken", "apiSecret")
                .setJudoId("100407196")
                .setEnvironment(Judo.SANDBOX)
                .build();

        assertThat(judo.isSslPinningEnabled(), is(true));
    }
}
