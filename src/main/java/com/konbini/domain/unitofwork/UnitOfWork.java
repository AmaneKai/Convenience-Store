package com.konbini.domain.unitofwork;

/**
 * Coordinates atomic persistence across all repositories. A single unit of
 * work commit persists every repository it manages in one operation, mirroring
 * a database transaction for the file-backed CSV store.
 */
public interface UnitOfWork {

    /**
     * Persists the current in-memory state of all managed repositories.
     *
     * @return true if the commit succeeded for every repository
     */
    boolean commit();
}
