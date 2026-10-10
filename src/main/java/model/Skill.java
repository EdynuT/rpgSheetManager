package model;

public class Skill {
    private Integer id;
    private Integer characterId;
    private String skillName;
    private int value;

    public Skill() {
    }

    public Skill(Integer id, Integer characterId, String skillName, int value) {
        this.id = id;
        this.characterId = characterId;
        if (skillName != null && !skillName.trim().isEmpty()) {
            this.skillName = skillName;
        } else {
            throw new IllegalArgumentException("Skill name must not be null!");
        }
        this.value = value;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Integer characterId) {
        this.characterId = characterId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
