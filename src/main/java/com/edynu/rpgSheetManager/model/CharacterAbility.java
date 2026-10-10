package com.edynu.rpgSheetManager.model;

// Link between a character and an ability it has learned (table character_ability).
public class CharacterAbility {
    private Integer characterId;
    private Integer abilityId;
    private boolean toggled;

    public CharacterAbility() {
    }

    public CharacterAbility(Integer characterId, Integer abilityId, boolean toggled) {
        this.characterId = characterId;
        this.abilityId = abilityId;
        this.toggled = toggled;
    }

    public Integer getCharacterId() {
        return characterId;
    }

    public void setCharacterId(Integer characterId) {
        this.characterId = characterId;
    }

    public Integer getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(Integer abilityId) {
        this.abilityId = abilityId;
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
    }
}
