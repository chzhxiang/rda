package com.rda.web.base.propertyeditors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;

 public class CustomStringTrimmerEditor extends StringTrimmerEditor
 {
   private static final Logger logger = LoggerFactory.getLogger(CustomStringTrimmerEditor.class);
   private final boolean fullDelete;
   private final boolean emptyAsNull;

   public CustomStringTrimmerEditor(boolean fullDelete, boolean emptyAsNull)
   {
     super(emptyAsNull);
     this.fullDelete = fullDelete;
     this.emptyAsNull = emptyAsNull;
   }

   public void setAsText(String text)
   {
     if (this.fullDelete) {
       if (text == null) {
         setValue(null);
       } else {
         String value = text.trim();
         if ((this.emptyAsNull) && (value.isEmpty()))
           setValue(null);
         else
           setValue(escape(value));
       }
     }
     else
       super.setAsText(escape(text));
   }

   private String escape(String value)
   {
//       v = HtmlUtils.htmlEscape(value);
//     if (logger.isDebugEnabled()) {
//       logger.debug("String value escaped: {}", new Object[] { v });
//     }
     return value;
   }
 }

