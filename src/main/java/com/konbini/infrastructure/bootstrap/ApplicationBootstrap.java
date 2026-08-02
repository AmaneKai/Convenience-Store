package com.konbini.infrastructure.bootstrap;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.konbini.infrastructure.config.StoreConfig;
import com.konbini.infrastructure.di.StoreModule;
import java.nio.file.Path;

/**
 * Bootstrap for the file-backed application: constructs the Guice container.
 * Data is loaded directly from the CSV seed files in the data directory; no
 * legacy migration is performed.
 */
public final class ApplicationBootstrap {

    private ApplicationBootstrap() {
    }

    /**
     * Creates the fully wired injector for the given data directory.
     *
     * @param dataDirectory the data directory
     * @return the Guice injector
     */
    public static Injector createInjector(Path dataDirectory) {
        StoreConfig config = new StoreConfig(dataDirectory);
        return Guice.createInjector(new StoreModule(config));
    }
}
