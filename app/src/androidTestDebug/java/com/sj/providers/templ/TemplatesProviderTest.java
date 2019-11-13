package com.sj.providers.templ;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.test.ProviderTestCase2;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;

public class TemplatesProviderTest extends ProviderTestCase2 {
    public TemplatesProviderTest() {
        super(TemplatesProvider.class, "com.sj.providers.Template");
    }

    public TemplatesProviderTest(Class providerClass, String providerAuthority) {
        super(providerClass, providerAuthority);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setContext(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testcaseInsert() {
        ContentProvider provider = getProvider();

        ContentValues values = new ContentValues();
        values.put(Templates.TITLE, "title-1");
        values.put(Templates.ABSTRACT, "Content-1");
        values.put(Templates.URL, "http://a.b.c");

        Uri uri = provider.insert(Templates.CONTENT_URI, values);
        String itemId = uri.getPathSegments().get(1);
        System.out.println("id: " + itemId);
    }
}