package com.judopay;

import org.junit.Before;

public class JudoTest {

    @Before
    public void setupJudoSdk() {
        Judo.setup("823Eja2fEM6E9NAE", "382df6f458294f49f02f073e8f356f8983e2460631ea1b4c8ed4c3ee502dcbe6", Judo.SANDBOX);
    }

}
