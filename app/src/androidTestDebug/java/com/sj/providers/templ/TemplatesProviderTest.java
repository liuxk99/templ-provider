package com.sj.providers.templ;

import android.test.ProviderTestCase2;

import androidx.test.core.app.ApplicationProvider;

class TemplatesProviderTest extends ProviderTestCase2 {
    public TemplatesProviderTest(Class providerClass, String providerAuthority) {
        super(providerClass, providerAuthority);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setContext(ApplicationProvider.getApplicationContext());
    }

}