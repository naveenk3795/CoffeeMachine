package machine.data;

public class Ingredient {
    String name;

    public Ingredient(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Ingredient name cannot be null");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object o1) {
        if (o1 instanceof Ingredient) {
            Ingredient i1 = (Ingredient) o1;
            return this.name.equals(i1.name);
        }
        return false;
    }
}
