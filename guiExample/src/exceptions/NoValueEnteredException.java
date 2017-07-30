package exceptions;

public class NoValueEnteredException extends Exception 
{

	public NoValueEnteredException()
    {
        super("No Value entered! Could not proceed...");
    }
}
