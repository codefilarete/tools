package org.codefilarete.tool.collection;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.codefilarete.tool.collection.StreamSplitterTest.Dish.*;
import static org.codefilarete.tool.collection.StreamSplitterTest.Dish.Type.*;


class StreamSplitterTest {
	
	@Test
	void split() {
		Stream<Dish> menuStream = MENU.stream();
		
		Set<Dish> veggyDishes = new HashSet<>();
		Set<Dish> healthyDishes = new HashSet<>();
		Set<Dish> junkFood = new HashSet<>();
		new StreamSplitter<>(menuStream)
				.dispatch(Dish::isVegetarian, veggyDishes::add)
				.dispatch(dish -> dish.getCalories() < 400, healthyDishes::add)
				.dispatch(dish -> dish.getCalories() > 600, junkFood::add)
				.split();
		
		Assertions.assertThat(veggyDishes).containsExactlyInAnyOrder(FRENCH_FRIES, SEASON_FRUIT, RICE, PIZZA);
		Assertions.assertThat(healthyDishes).containsExactlyInAnyOrder(SEASON_FRUIT, RICE);
		Assertions.assertThat(junkFood).containsExactlyInAnyOrder(BEEF, PORK);
	}
	
	static class Dish {
		
		private final String name;
		private final boolean vegetarian;
		private final int calories;
		private final Type type;
		
		private Dish(String name, boolean vegetarian, int calories, Type type) {
			this.name = name;
			this.vegetarian = vegetarian;
			this.calories = calories;
			this.type = type;
		}
		
		public String getName() {
			return name;
		}
		
		public boolean isVegetarian() {
			return vegetarian;
		}
		
		public int getCalories() {
			return calories;
		}
		
		public Type getType() {
			return type;
		}
		
		public enum Type {
			MEAT, FISH, FRUIT, VEGETABLE, MIX
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		public static final Dish PORK = new Dish("pork", false, 800, MEAT);
		public static final Dish BEEF = new Dish("beef", false, 700, MEAT);
		public static final Dish CHICKEN = new Dish("chicken", false, 400, MEAT);
		public static final Dish FRENCH_FRIES = new Dish("french fries", true, 530, VEGETABLE);
		public static final Dish RICE = new Dish("rice", true, 350, VEGETABLE);
		public static final Dish SEASON_FRUIT = new Dish("season fruit", true, 120, FRUIT);
		public static final Dish PIZZA = new Dish("pizza", true, 550, MIX);
		public static final Dish PRAWNS = new Dish("prawns", false, 400, FISH);
		public static final Dish SALMON = new Dish("salmon", false, 450, FISH);
		
		public static final List<Dish> MENU = Arrays.asList(PORK,
				BEEF,
				CHICKEN,
				FRENCH_FRIES,
				RICE,
				SEASON_FRUIT,
				PIZZA,
				PRAWNS,
				SALMON);
	}
}
