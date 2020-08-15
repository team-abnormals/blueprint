package com.teamabnormals.abnormals_core.core.library.api;

/***
 * @author tessdotcpp
 * Use to make an entity that doesn't extend AgeableEntity compatible with Quark's potato poisoning.
 */
public interface IAgeableEntity {

    /**
     * Sets the growing age of the entity. With a negative value it's considered a child; use this method to check for
     * an age of 0 or greater and trigger the necessary changes.
     */
    void setGrowingAge(int age);

    int getGrowingAge();

}
