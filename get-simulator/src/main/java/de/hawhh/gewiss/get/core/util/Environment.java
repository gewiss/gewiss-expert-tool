package de.hawhh.gewiss.get.core.util;

/**
 * Helper class for accessing enviroment variables.
 */
class Environment {

    /**
     * Reads the environment variable for the given key and returns either the found value or the given default value.
     *
     * @param key the name of the enviroment variable
     * @param deflt the given default value
     * @return the value of the environment variable or the default value if the environment variable was not found
     */
    public static String getFromEnvOrDefault(String key, String deflt) {
        String value = getFromEnv(key);
        if (value == null) {
            return deflt;
        }
        return value;
    }

    /**
     * Read the value of the given environment variable.
     *
     * @param key the name of the given environment variable
     * @return
     */
    private static String getFromEnv(String key) {
        return System.getenv().get(key);
    }

    /**
     * Read the value of the given environment variable and return as Integer.
     *
     * @param key the name of the given environment variable
     * @return
     */
    private static Integer getIntFromEnv(String key) {
        String value = System.getenv().get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Reads the environment variable for the given key and returns either the found value or the given default value.
     *
     * @param key the name of the enviroment variable
     * @param deflt the given default value
     * @return the value of the environment variable or the default value if the environment variable was not found
     */
    public static Integer getIntFromEnvOrDefault(String key, Integer deflt) {
        Integer value = getIntFromEnv(key);
        if (value == null) {
            return deflt;
        }
        return value;
    }
    
    /**
     * Read the value of the given environment variable and return as Long.
     *
     * @param key the name of the given environment variable
     * @return
     */
    private static Long getLongFromEnv(String key) {
        String value = System.getenv().get(key);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Reads the environment variable for the given key and returns either the found value or the given default value.
     *
     * @param key the name of the enviroment variable
     * @param deflt the given default value
     * @return the value of the environment variable or the default value if the environment variable was not found
     */
    public static Long getLongFromEnvOrDefault(String key, Long deflt) {
        Long value = getLongFromEnv(key);
        if (value == null) {
            return deflt;
        }
        return value;
    }
}