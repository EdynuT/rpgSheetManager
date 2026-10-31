package com.edynu.rpgSheetManager.model;

public class SecondSkill {
    private Integer id;
    private Integer characterId;
    private String secondSkillName;
    private Integer value = 0;

    public SecondSkill() {
    }

    public SecondSkill(Integer id, Integer characterId, String secondSkillName, Integer value) {
        this.id = id;
        this.characterId = characterId;
        if (secondSkillName != null && !secondSkillName.trim().isEmpty()) {
            this.secondSkillName = secondSkillName;
        } else {
            throw new IllegalArgumentException("Second skill name is required.");
        }
        this.value = (value != null) ? value : 0;
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

    public String getSecondSkillName() {
        return secondSkillName;
    }

    public void setSecondSkillName(String secondSkillName) {
        this.secondSkillName = secondSkillName;
    }


    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = (value != null) ? value : 0;
    }
}
