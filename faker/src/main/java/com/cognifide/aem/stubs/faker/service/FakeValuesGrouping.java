package com.cognifide.aem.stubs.faker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FakeValuesGrouping implements FakeValuesInterface {

    private List<AEMFakerValues> fakeValuesList = new ArrayList<AEMFakerValues>();

    public void add(AEMFakerValues fakeValues) {
        fakeValuesList.add(fakeValues);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Map get(String key) {
        Map result = null;
        for (AEMFakerValues fakeValues : fakeValuesList) {
            if (fakeValues.supportsPath(key)) {
                if (result != null) {
                    final Map newResult = fakeValues.get(key);
                    result.putAll(newResult);
                } else {
                    result = fakeValues.get(key);
                }
            }
        }
        return result;
    }
}
