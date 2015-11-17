/*******************************************************************************
 * Project Key : rda
 ******************************************************************************/
package com.rda.web.init;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.rda.util.PropertiesLoaderUtils;
import com.rda.util.StringUtils;


/**
 * <P>
 * webAppContext初始化时将properties设置到environment中，<br/>
 * 这样使用<context:import/>标签时才可以使用placeholder来动态改变import的文件.<br/>
 * 此initializer需要加到web.xml中并从web.xml中的contextparam中取初始化值.<br/>
 * 例如:<br/>
 * <web-app >
 *  <display-name>iatp</display-name> 
 *  <context-param> 
 *  	<param-name>contextInitializerClasses</param-name> 
 *  	<param-value>com.rda.web.init.WebAppContextInitializer</param-value>
 *  </context-param> 
 *  <context-param> 
 *  	<param-name>eagerPropertyFileLocation</param-name> 
 *  <param-value>classpath:conf/ds.properties</param-value> 
 *  </context-param> 
 *  </web-app> 
 * </P>
 *  
 * @see	http://spring.io/blog/2011/02/15/spring-3-1-m1-unified-property-management/
 * @see http://stackoverflow.com/questions/16481206/spring-property-placeholder-not-working
 * @author lianrao
 * @since	0.0.1
 */
public class WebAppContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

	public static final String EAGER_PROPERTY_FILE_LOCATION = "eagerPropertyFileLocation";

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextInitializer#initialize(org.springframework.context.ConfigurableApplicationContext)
	 * @author lianrao
	 */
	@Override
	public void initialize(ConfigurableWebApplicationContext applicationContext) {
		String locs = applicationContext.getServletContext().getInitParameter(EAGER_PROPERTY_FILE_LOCATION);
		if (StringUtils.isEmpty(locs)) {
			return;
		}

		String[] split = locs.split(",");
		for (String path : split) {
			Properties props;
			try {
				props = PropertiesLoaderUtils.loadProperties(path);
				PropertiesPropertySource ps = new PropertiesPropertySource("profile", props);
				applicationContext.getEnvironment().getPropertySources().addFirst(ps);
			} catch (IOException e) {
				
			}
		}

	}

}
