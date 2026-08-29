package src.model;

public class Item {
    private Integer id;
    private String name;
    private String description;
    private double weight;
    private ItemCategory category;

    public Item() {
        
    }

    public Item(Integer id, String name, String description, double weight, ItemCategory category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.weight = weight;
        this.category = category;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public void setCategory(ItemCategory category) {
        this.category = category;
    }
}
