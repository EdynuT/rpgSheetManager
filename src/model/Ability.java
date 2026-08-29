package src.model;

public class Ability {
    private Integer id;
    private String name;
    private AbilityCategory category;
    private ActionType actionType;
    private CostType costType;
    private Integer costValue;
    private String description;

    public Ability() {
        
    }

    public Ability(Integer id, String name, AbilityCategory category, ActionType actionType, CostType costType, Integer costValue, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.actionType = actionType;
        this.costType = costType;
        this.costValue = costValue;
        this.description = description;
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

    public AbilityCategory getCategory() {
        return category;
    }

    public void setCategory(AbilityCategory category) {
        this.category = category;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public CostType getCostType() {
        return costType;
    }

    public void setCostType(CostType costType) {
        this.costType = costType;
    }

    public Integer getCostValue() {
        return costValue;
    }

    public void setCostValue(Integer costValue) {
        this.costValue = costValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
