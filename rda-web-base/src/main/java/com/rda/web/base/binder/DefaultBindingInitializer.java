package com.rda.web.base.binder;


import com.rda.web.base.propertyeditors.CustomDateEditor;
import com.rda.web.base.propertyeditors.CustomRESTNumberEditor;
import com.rda.web.base.propertyeditors.CustomStringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;


public class DefaultBindingInitializer extends ConfigurableWebBindingInitializer
        implements WebBindingInitializer {
    public void initBinder(WebDataBinder binder, WebRequest request) {
        super.initBinder(binder, request);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(true, -1));
        binder.registerCustomEditor(String.class, new CustomStringTrimmerEditor(true, false));
        binder.registerCustomEditor(Integer.class, new CustomRESTNumberEditor(Integer.class, true));
    }
}

