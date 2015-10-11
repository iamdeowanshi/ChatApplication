package com.mtvindia.connect.app.di;

import dagger.ObjectGraph;

/**
 * A singleton class, which is responsible for holding dagger ObjectGraph, adding modules to it
 * & injecting dependencies.
 *
 * @author Farhan Ali
 */
public class Injector {

    // Singleton instance
    private static Injector instance;

    private ObjectGraph objectGraph;

    // Private constructor to make it singleton
    private Injector() {
    }

    /**
     * Get the singleton Injector instance
     *
     * @return Injector
     */
    public static Injector instance() {
        if (instance == null) {
            instance = new Injector();
        }
        
        return  instance;
    }

    /**
     * Create ObjectGraph for a dagger module.
     *
     * @param module
     */
    public void createModule(Object module) {
        objectGraph = ObjectGraph.create(module);
    }

    /**
     * Inject dependencies to the existing ObjectGraph
     *
     * @param object @Inject annotated object
     */
    public void inject(Object object) {
        objectGraph.inject(object);
    }

    /**
     * Add one or more modules to the existing ObjectGraph.
     *
     * @param modules @Module annotated objects
     */
    public void addModules(Object... modules) {
        objectGraph.plus(modules);
    }

}
