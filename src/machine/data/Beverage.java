package machine.data;

import java.util.HashMap;
import java.util.Map;

public class Beverage {
    String name;
    Map<Ingredient, Integer> ingredients;

    private Beverage(String name) {
        if(name == null) { throw new IllegalArgumentException("Beverage name cannot be null"); }
        this.name = name;
    }

    public Map<Ingredient, Integer> getRequiredIngredients() {
        return ingredients;
    }

    public static class Builder {
        private final Beverage instance;

        public Builder(String name) {
            this.instance = new Beverage(name);
            this.instance.ingredients = new HashMap<>();
        }

        public Builder addToRecipe(Ingredient ingredient, int requiredAmount) {
            if(ingredient == null) {
                throw new IllegalArgumentException("Invalid ingredient");
            }
            this.instance.ingredients.put(ingredient, requiredAmount);
            return this;
        }

        public Beverage build() {
            return this.instance;
        }
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object o1) {
        if(o1 instanceof Beverage) {
            Beverage i1 = (Beverage) o1;
            return this.name.equals(i1.name);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
