package com.rda.web.base.propertyeditors;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

public class CustomRESTNumberEditor extends CustomNumberEditor {
    public CustomRESTNumberEditor(Class<? extends Number> numberClass, boolean allowEmpty)
            throws IllegalArgumentException {
        super(numberClass, allowEmpty);
    }

    public void setAsText(String text) throws IllegalArgumentException {
        super.setAsText(text);
    }
}

