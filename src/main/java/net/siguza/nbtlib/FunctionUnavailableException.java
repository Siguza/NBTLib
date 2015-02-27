// Bukkit Plugin "NBTLib" by Siguza
// Released under the CC BY 3.0 (CreativeCommons Attribution 3.0 Unported) license.
// The full license and a human-readable summary can be found at the following location:
// http://creativecommons.org/licenses/by/3.0/

package net.siguza.nbtlib;

/**
 * This exception is thrown when a method cannot be invoked because the underlying functionality is unavailable or inaccessible.
 *
 * @since 1.0.0
 */
public class FunctionUnavailableException extends Exception
{
    /**
     * {@inheritDoc}
     */
    public FunctionUnavailableException(String message)
    {
        super(message);
    }
}
