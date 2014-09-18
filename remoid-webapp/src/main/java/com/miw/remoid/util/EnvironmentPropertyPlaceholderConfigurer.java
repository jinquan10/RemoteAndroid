package com.miw.remoid.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;


/**
 * Original Version :
 * <p>
 * A small extension to org.springframework.beans.factory.config.
 * PlatformPropertyPlaceholderConfigurer that, when asked to replace the
 * placeholder "somevar", will first look for a property named
 * "platformprefix.somevar", or failing a match will then look for the property
 * "somevar".
 * </p>
 * 
 * <p>
 * The "platformprefix" part of the placholder is derived from the Java system
 * property "os.name". For convenience the "os.name" value is mapped to a prefix
 * that is easier to type. For example, on Windows XP the value of "os.name" is
 * "Windows XP" and this class maps "platformprefix" to "win".
 * </p>
 * 
 * <p>
 * This class has a default set of mappings (see
 * DEFAULT_PLATFORM_PREFIX_MAPPINGS) which can be overridden by setting the
 * property envPrefixMappings.
 * </p>
 * 
 * <p>
 * See http://lopica.sourceforge.net/os.html for an extensive list of platform
 * names used by the os.name Java system property.
 * </p>
 */
public class EnvironmentPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentPropertyPlaceholderConfigurer.class);

    private static final Map<String, String> DEFAULT_ENV_PREFIX_MAPPINGS;

    static {
        DEFAULT_ENV_PREFIX_MAPPINGS = new HashMap<String, String>();
        DEFAULT_ENV_PREFIX_MAPPINGS.put("DEV", "dev");
        DEFAULT_ENV_PREFIX_MAPPINGS.put("QA", "qa");
        DEFAULT_ENV_PREFIX_MAPPINGS.put("STG", "stg");
        DEFAULT_ENV_PREFIX_MAPPINGS.put("VAL", "val");
        DEFAULT_ENV_PREFIX_MAPPINGS.put("PROD", "prod");

    }

    private Map<String, String> envPrefixMappings;

    private String envPrefix;

    /**
     * Override the default platform prefix mappings.
     * 
     * @param envPrefixMappings
     */
    public void setEnvPrefixMappings(Map envPrefixMappings) {
        this.envPrefixMappings = envPrefixMappings;
    }

    /**
     * Attempt to determine the prefix to use for this platform. First check any
     * user defined prefix mappings. If no match then check the default platform
     * mappings.
     */
    public void afterPropertiesSet() throws Exception {

        String env = System.getProperty("app.env");

        LOGGER.info("app.env :" + env);
        if (envPrefixMappings != null) {
            envPrefix = envPrefixMappings.get(env);
        }
        if (envPrefix == null) {
            envPrefix = DEFAULT_ENV_PREFIX_MAPPINGS.get(env);
        }
    }

    /**
     * Override the PropertyPlaceholderConfigurer.resolvePlaceholder(...) method
     * to first look for a placeholder with the platform prefix.
     */
    protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
        String propVal = null;
        if (systemPropertiesMode == SYSTEM_PROPERTIES_MODE_OVERRIDE) {
            if (envPrefix != null) {
                propVal = resolveSystemProperty(envPrefix + "." + placeholder);
            }
            if (propVal == null) {
                propVal = resolveSystemProperty(placeholder);
            }
        }
        if (propVal == null) {
            if (envPrefix != null) {
                propVal = resolvePlaceholder(envPrefix + "." + placeholder, props);
            }
            if (propVal == null) {
                propVal = resolvePlaceholder(placeholder, props);
            }
        }
        if (propVal == null && systemPropertiesMode == SYSTEM_PROPERTIES_MODE_FALLBACK) {
            if (envPrefix != null) {
                propVal = resolveSystemProperty(envPrefix + "." + placeholder);
            }
            if (propVal == null) {
                propVal = resolveSystemProperty(placeholder);
            }
        }
        return propVal;
    }

}
