package org.smbc.datadiff.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.nio.file.Path;

public class ConfigLoader {
    private final ObjectMapper objectMapper;


    public ConfigLoader() {

        this.objectMapper =
                new ObjectMapper(
                        new YAMLFactory()
                );
    }


    public ReconciliationConfig load(
            Path configFile) throws IOException {

        /*
         * No configuration supplied.
         *
         * The comparator will use default rules:
         *
         * First CSV column = key
         * Remaining columns = comparison fields
         */

        if (configFile == null) {

            return new ReconciliationConfig();
        }


        return objectMapper.readValue(
                configFile.toFile(),
                ReconciliationConfig.class
        );
    }
}
