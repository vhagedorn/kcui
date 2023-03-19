package me.vadim.ja;

/**
 * @author vadim
 */
public interface ApplicationEnvironment {

	void preInit();

	Application createApplication() throws Exception;

	void cleanUp();

}
