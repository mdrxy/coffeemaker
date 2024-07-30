package edu.ncsu.csc.CoffeeMaker.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc.CoffeeMaker.models.Ingredient;

/**
 * This tells Spring that we are interested in storing a collection of Ingredient objects, each of
 * which is identified by a Long as the primary key
 */
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

	/**
	 * Finds a Recipe object with the provided name. Spring will generate code to make this happen.
	 * 
	 * @param name Name of the Recipe
	 * 
	 * @return Found Recipe, null if none.
	 */
	Ingredient findByName(String name);
}
