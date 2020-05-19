package com.cognifide.aem.stubs.faker;

public class Animal {
    private final Faker faker;

    protected Animal(Faker faker) {
        this.faker = faker;
    }

    public String name() {
        return faker.fakeValuesService().resolve("creature.animal.name", this, faker);
    }
}
