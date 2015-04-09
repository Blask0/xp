package com.enonic.xp.support;

import com.google.common.annotations.Beta;

@Beta
public interface Editor<T>
{
    /**
     * @param toBeEdited to be edited
     * @return updated object, or null if no change was necessary.
     */
    T edit( T toBeEdited );
}
