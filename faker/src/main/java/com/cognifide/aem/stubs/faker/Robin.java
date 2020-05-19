package com.cognifide.aem.stubs.faker;

import com.cognifide.aem.stubs.faker.Faker;

public class Robin {
    private final Faker faker;

    protected Robin(Faker faker) {
        this.faker = faker;
    }

    public String quote() {
        return faker.fakeValuesService().resolve("robin.quotes", this, faker);
    }
}
