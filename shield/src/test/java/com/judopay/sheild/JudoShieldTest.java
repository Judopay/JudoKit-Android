package com.judopay.sheild;

import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class JudoShieldTest {

    @Test
    public void shouldReturnEmptyListWhenContextNull() {
        JudoShield judoShield = new JudoShield();
        Map<String, String> shieldData = judoShield.getShieldData(null);

        assertThat(shieldData, notNullValue());
        assertThat(shieldData.size(), is(0));
    }

}