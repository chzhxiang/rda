package com.rda.core.support;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.*;

public class PrefixResourceBundleMessageSource extends ResourceBundleMessageSource {
    private static List<String> localeSuffixes;
    private Set<String> basenames;

    public PrefixResourceBundleMessageSource() {
    }

    public Set<String> getBasenames() {
        return this.basenames;
    }

    @Required
    public void setLocationPatterns(String[] locationPatterns) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(this.getBundleClassLoader());

        try {
            this.basenames = new HashSet();
            String[] e = locationPatterns;
            int len$ = locationPatterns.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String locationPattern = e[i$];
                int lastIndexOf = locationPattern.lastIndexOf(47);
                String prefix;
                if(lastIndexOf == -1) {
                    prefix = "";
                } else {
                    prefix = locationPattern.substring(locationPattern.indexOf(58) + 1, lastIndexOf + 1);
                }

                Resource[] messageResources = resolver.getResources(locationPattern);
                Resource[] arr$ = messageResources;
                int len$1 = messageResources.length;

                for(int i$1 = 0; i$1 < len$1; ++i$1) {
                    Resource resource = arr$[i$1];
                    String resourceName = prefix + resource.getFilename();
                    Iterator i$2 = localeSuffixes.iterator();

                    while(i$2.hasNext()) {
                        String localeSuffix = (String)i$2.next();
                        if(resourceName.endsWith(localeSuffix)) {
                            this.basenames.add(resourceName.substring(0, resourceName.lastIndexOf(localeSuffix)));
                        }
                    }
                }
            }

            this.setBasenames((String[])this.basenames.toArray(new String[this.basenames.size()]));
        } catch (IOException var17) {
            throw new IllegalArgumentException("Cann\'t get resources[locationPattern = " + locationPatterns + "].", var17);
        }
    }

    public List<ResourceBundle> getResourceBundles(Locale locale) {
        LinkedList rbs = new LinkedList();
        Iterator i$ = this.basenames.iterator();

        while(i$.hasNext()) {
            String basename = (String)i$.next();
            ResourceBundle bundle = this.getResourceBundle(basename, locale);
            if(bundle != null) {
                rbs.add(bundle);
            }
        }

        return rbs;
    }

    static {
        Locale[] availableLocales = Locale.getAvailableLocales();
        localeSuffixes = new ArrayList(availableLocales.length);
        Locale[] arr$ = availableLocales;
        int len$ = availableLocales.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Locale availableLocale = arr$[i$];
            localeSuffixes.add("_" + availableLocale.toString() + ".properties");
        }

    }
}

