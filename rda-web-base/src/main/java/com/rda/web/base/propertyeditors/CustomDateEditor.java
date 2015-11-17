package com.rda.web.base.propertyeditors;

import com.rda.util.DateFormatUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyEditorSupport;
import java.text.ParseException;
import java.util.Date;

public class CustomDateEditor extends PropertyEditorSupport {
    private final boolean allowEmpty;
    private final int exactDateLength;
    private final String[] patterns = {"yyyy-MM-dd HH:mm:ss","yyyy-MM-dd"};

    public CustomDateEditor(boolean allowEmpty, int exactDateLength) {
        this.allowEmpty = allowEmpty;
        this.exactDateLength = exactDateLength;
    }

    public void setAsText(String text) throws IllegalArgumentException {
        if ((this.allowEmpty) && (StringUtils.isBlank(text))) {
            setValue(null);
        } else {
            if ((text != null) && (this.exactDateLength >= 0) && (text.length() != this.exactDateLength)) {
                throw new IllegalArgumentException("Could not parse date: it is not exactly" + this.exactDateLength + "characters long");
            }

            for (String pattern : this.patterns)
                try {
                    setValue(DateFormatUtils.parse(text, pattern));
                    return;
                } catch (ParseException ex) {
                }
            throw new IllegalArgumentException("Could not parse date: " + text);
        }
    }

    public String getAsText() {
        Date value = (Date) getValue();
        return value != null ? org.apache.commons.lang3.time.DateFormatUtils.format(value, this.patterns[0]) : "";
    }
}

