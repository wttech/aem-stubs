package com.cognifide.aem.stubs.faker;

import com.cognifide.aem.stubs.faker.Faker;

public class RockBand {

    private final Faker faker;

    protected RockBand(Faker faker) {
        this.faker = faker;
    }

    public String name() {
        return faker.resolve("rock_band.name");
    }
}
