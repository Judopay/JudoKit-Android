package com.judopay.model;

import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class AddressTest {

    @Test
    public void shouldConstructAddressWithCorrectPropertyAssingment() {
        Address address = new Address("line1", "line2", "line3", "town", "postcode", 1);

        assertThat(address.getLine1(), equalTo("line1"));
        assertThat(address.getLine2(), equalTo("line2"));
        assertThat(address.getLine3(), equalTo("line3"));
        assertThat(address.getTown(), equalTo("town"));
        assertThat(address.getPostCode(), equalTo("postcode"));
        assertThat(address.getCountryCode(), equalTo(1));
    }

    @Test
    public void shouldBuildAddress() {
        Address address = new Address.Builder()
                .setLine1("line1")
                .setLine2("line2")
                .setLine3("line3")
                .setTown("town")
                .setPostCode("postcode")
                .setCountryCode(1)
                .build();

        assertThat(address.getLine1(), equalTo("line1"));
        assertThat(address.getLine2(), equalTo("line2"));
        assertThat(address.getLine3(), equalTo("line3"));
        assertThat(address.getTown(), equalTo("town"));
        assertThat(address.getPostCode(), equalTo("postcode"));
        assertThat(address.getCountryCode(), equalTo(1));
    }

    @Test
    public void shouldBuildPostCodeOnly() {
        Address address = new Address.Builder()
                .setPostCode("postcode")
                .build();

        assertThat(address.getLine1(), equalTo(null));
        assertThat(address.getLine2(), equalTo(null));
        assertThat(address.getLine3(), equalTo(null));
        assertThat(address.getTown(), equalTo(null));
        assertThat(address.getPostCode(), equalTo("postcode"));
        assertThat(address.getCountryCode(), equalTo(0));
    }
}
