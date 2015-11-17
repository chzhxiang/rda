/*******************************************************************************
 * rda
 ******************************************************************************/
package com.rda.mybatis.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <P>mybatis接口mapper标志annotation</P>
 * @author lianrao
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
@Documented
public @interface Mapper {
		String value() default "";
}
