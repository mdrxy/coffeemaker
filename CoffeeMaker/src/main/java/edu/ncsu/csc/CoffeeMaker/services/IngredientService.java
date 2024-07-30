package edu.ncsu.csc.CoffeeMaker.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;
import edu.ncsu.csc.CoffeeMaker.repositories.IngredientRepository;

/**
 * `@Component` defines that this is a "component" class that should be considered a candidate for
 * autowiring into our other classes (basically, when we use an instance of our Service in other
 * classes, Spring will automatically go create the instance without us needing to do anything else.
 * This will automatically provide usable instances of the Service to our API controllers (the next
 * section of this Workshop) and our tests).
 * 
 * `@Transactional` tells Spring to use this class in a way that supports transaction rollback,
 * which means that if something goes wrong in the middle of updating the database, it won't be left
 * in an inconsistent partial state. Also, more interestingly, it enables transaction rollback for
 * our tests
 */
@Component
@Transactional
public class IngredientService extends Service<Ingredient, Long> {

	/**
	 * Local IngredientRepository.
	 */
	@Autowired
	private IngredientRepository ingredientRepository;

	/**
	 * Returns this instance.
	 */
	@Override
	protected JpaRepository<Ingredient, Long> getRepository() {
		return ingredientRepository;
	}

	/**
	 * Find an ingredient with the provided name.
	 * 
	 * @param name Name of the ingredient to find
	 * 
	 * @return found ingredient, null if none
	 */
	public Ingredient findByName(final String name) {
		return ingredientRepository.findByName(name.trim().toLowerCase());
	}
}
