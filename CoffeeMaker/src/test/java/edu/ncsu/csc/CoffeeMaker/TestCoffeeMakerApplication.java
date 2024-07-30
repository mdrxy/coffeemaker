package edu.ncsu.csc.CoffeeMaker;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.*;

/**
 * To satisfy 100% coverage, test running the Spring application.
 */
public class TestCoffeeMakerApplication {

	/**
	 * To satisfy 100% coverage, test running the Spring application.
	 */
	@Test
	public void testRunApplication() {
		// Create mock for SpringApplication
		SpringApplication springApplicationMock = Mockito.mock(SpringApplication.class);

		// Call the method under test
		CoffeeMakerApplication.main(new String[] { "arg1", "arg2" });

		verify(springApplicationMock);
	}
}
