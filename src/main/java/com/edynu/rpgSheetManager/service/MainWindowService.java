package com.edynu.rpgSheetManager.service;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.edynu.rpgSheetManager.dao.AbilityDAO;
import com.edynu.rpgSheetManager.dao.AmmunitionDAO;
import com.edynu.rpgSheetManager.dao.ArmorDAO;
import com.edynu.rpgSheetManager.dao.CharacterAbilityDAO;
import com.edynu.rpgSheetManager.dao.InventoryDAO;
import com.edynu.rpgSheetManager.dao.ItemDAO;
import com.edynu.rpgSheetManager.dao.PlayerCharacterDAO;
import com.edynu.rpgSheetManager.dao.SecondSkillDAO;
import com.edynu.rpgSheetManager.dao.SkillDAO;
import com.edynu.rpgSheetManager.dao.WeaponDAO;
import com.edynu.rpgSheetManager.model.Ability;
import com.edynu.rpgSheetManager.model.Ammunition;
import com.edynu.rpgSheetManager.model.Armor;
import com.edynu.rpgSheetManager.model.CharacterAbility;
import com.edynu.rpgSheetManager.model.Inventory;
import com.edynu.rpgSheetManager.model.Item;
import com.edynu.rpgSheetManager.model.ItemCategory;
import com.edynu.rpgSheetManager.model.PlayerCharacter;
import com.edynu.rpgSheetManager.model.SecondSkill;
import com.edynu.rpgSheetManager.model.Skill;
import com.edynu.rpgSheetManager.model.Weapon;
import com.edynu.rpgSheetManager.utils.Utils;

/**
 * Operations used by the MainWindow. "save" inserts the object when it has no id yet,
 * and updates it otherwise.
 */
public class MainWindowService {
    private final PlayerCharacterDAO characterDAO = new PlayerCharacterDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final SecondSkillDAO secondSkillDAO = new SecondSkillDAO();
    private final AbilityDAO abilityDAO = new AbilityDAO();
    private final CharacterAbilityDAO characterAbilityDAO = new CharacterAbilityDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final WeaponDAO weaponDAO = new WeaponDAO();
    private final ArmorDAO armorDAO = new ArmorDAO();
    private final AmmunitionDAO ammunitionDAO = new AmmunitionDAO();

    // Characters

    public List<PlayerCharacter> listCharacters() throws SQLException {
        List<PlayerCharacter> characters = characterDAO.list();
        for (PlayerCharacter character : characters) {
            character.setPhoto(Utils.loadCharacterPhoto(character.getId()));
        }
        return characters;
    }

    public void saveCharacter(PlayerCharacter character) throws SQLException {
        for (PlayerCharacter other : characterDAO.list()) {
            if (!other.getId().equals(character.getId()) && sameName(other.getName(), character.getName())) {
                throw new IllegalArgumentException("A character with the name \"" + other.getName() + "\" already exists.");
            }
        }
        if (character.getId() == null) {
            characterDAO.insert(character);
        } else {
            characterDAO.update(character);
        }
    }

    public void saveCharacterPhoto(PlayerCharacter character, File imageFile) throws IOException {
        byte[] jpegBytes = Utils.saveCharacterPhoto(character.getId(), imageFile, 160);
        character.setPhoto(jpegBytes);
    }

    public void deleteCharacter(int id) throws SQLException {
        characterDAO.delete(id);
        Utils.deleteCharacterPhoto(id);
    }

    // Skills

    public List<Skill> listSkills(int characterId) throws SQLException {
        return skillDAO.listByCharacter(characterId);
    }

    // Refuses the name when the character already has another skill with it (see sameName).
    public void saveSkill(Skill skill) throws SQLException {
        for (Skill other : skillDAO.listByCharacter(skill.getCharacterId())) {
            if (!other.getId().equals(skill.getId()) && sameName(other.getSkillName(), skill.getSkillName())) {
                throw new IllegalArgumentException("This character already has the skill \"" + other.getSkillName() + "\".");
            }
        }
        if (skill.getId() == null) {
            skillDAO.insert(skill);
        } else {
            skillDAO.update(skill);
        }
    }

    public void deleteSkill(int id) throws SQLException {
        skillDAO.delete(id);
    }

    // Second skills

    public List<SecondSkill> listSecondSkills(int characterId) throws SQLException {
        return secondSkillDAO.listByCharacter(characterId);
    }

    // Refuses the name when the character already has another second skill with it (see sameName).
    public void saveSecondSkill(SecondSkill secondSkill) throws SQLException {
        for (SecondSkill other : secondSkillDAO.listByCharacter(secondSkill.getCharacterId())) {
            if (!other.getId().equals(secondSkill.getId()) && sameName(other.getSecondSkillName(), secondSkill.getSecondSkillName())) {
                throw new IllegalArgumentException("This character already has the second skill \"" + other.getSecondSkillName() + "\".");
            }
        }
        if (secondSkill.getId() == null) {
            secondSkillDAO.insert(secondSkill);
        } else {
            secondSkillDAO.update(secondSkill);
        }
    }

    public void deleteSecondSkill(int id) throws SQLException {
        secondSkillDAO.delete(id);
    }

    // Abilities

    public List<Ability> listAbilities() throws SQLException {
        return abilityDAO.list();
    }

    // Refuses the name when another ability already has it (see sameName).
    public void saveAbility(Ability ability) throws SQLException {
        for (Ability other : abilityDAO.list()) {
            if (!other.getAbilityId().equals(ability.getAbilityId()) && sameName(other.getName(), ability.getName())) {
                throw new IllegalArgumentException("The ability \"" + other.getName() + "\" already exists.");
            }
        }
        if (ability.getAbilityId() == null) {
            abilityDAO.insert(ability);
        } else {
            abilityDAO.update(ability);
        }
    }

    public void deleteAbility(int id) throws SQLException {
        abilityDAO.delete(id);
    }

    public List<CharacterAbility> listLearnedAbilities(int characterId) throws SQLException {
        return characterAbilityDAO.listByCharacter(characterId);
    }

    // Links the ability to the character if not already learned.
    public void learnAbility(int characterId, int abilityId) throws SQLException {
        for (CharacterAbility learned : characterAbilityDAO.listByCharacter(characterId)) {
            if (learned.getAbilityId() == abilityId) {
                return;
            }
        }
        characterAbilityDAO.insert(new CharacterAbility(characterId, abilityId));
    }

    public void forgetAbility(int characterId, int abilityId) throws SQLException {
        characterAbilityDAO.delete(characterId, abilityId);
    }

    // Inventory

    public List<Inventory> listInventory(int characterId) throws SQLException {
        return inventoryDAO.listByCharacter(characterId);
    }

    // Refuses a quantity below 1 and an item the character already has with the same "equipped"
    // state (the table allows one row per character, item and equipped).
    public void saveInventory(Inventory entry) throws SQLException {
        if (entry.getQuantity() < 0) {
            throw new IllegalArgumentException("Quantity must be above or equal to 0.");
        }
        for (Inventory other : inventoryDAO.listByCharacter(entry.getCharacterId())) {
            if (!other.getId().equals(entry.getId()) && other.getItemId().equals(entry.getItemId())
                    && other.isEquipped() == entry.isEquipped()) {
                String state = entry.isEquipped() ? "equipped" : "not equipped";
                throw new IllegalArgumentException("This character already has \"" + itemDAO.findById(entry.getItemId()).getName()
                        + "\" (" + state + ") in the inventory. Change the quantity of that entry instead.");
            }
        }
        if (entry.getId() == null) {
            inventoryDAO.insert(entry);
        } else {
            inventoryDAO.update(entry);
        }
    }

    public void deleteInventory(int id) throws SQLException {
        inventoryDAO.delete(id);
    }

    // Items (deleting an item also removes it from the weapon/armor/ammunition table and from the inventories)

    public List<Item> listAllItems() throws SQLException {
        return itemDAO.list();
    }

    public List<Item> listGeneralItems() throws SQLException {
        return itemDAO.listByCategory(ItemCategory.GENERAL);
    }

    // Weapons, armors and ammunition are items too, so they all share the same names.
    // Refuses the name when another item of any category already has it (see sameName).
    private void checkItemName(Item item) throws SQLException {
        for (Item other : itemDAO.list()) {
            if (!other.getId().equals(item.getId()) && sameName(other.getName(), item.getName())) {
                throw new IllegalArgumentException("The item \"" + other.getName() + "\" (" + other.getCategory() + ") already exists.");
            }
        }
    }

    public void saveGeneralItem(Item item) throws SQLException {
        checkItemName(item);
        if (item.getId() == null) {
            itemDAO.insert(item);
        } else {
            itemDAO.update(item);
        }
    }

    public List<Weapon> listWeapons() throws SQLException {
        return weaponDAO.list();
    }

    public void saveWeapon(Weapon weapon) throws SQLException {
        checkItemName(weapon);
        if (weapon.getId() == null) {
            weaponDAO.insert(weapon);
        } else {
            weaponDAO.update(weapon);
        }
    }

    public List<Armor> listArmors() throws SQLException {
        return armorDAO.list();
    }

    public void saveArmor(Armor armor) throws SQLException {
        checkItemName(armor);
        if (armor.getId() == null) {
            armorDAO.insert(armor);
        } else {
            armorDAO.update(armor);
        }
    }

    public List<Ammunition> listAmmunition() throws SQLException {
        return ammunitionDAO.list();
    }

    public void saveAmmunition(Ammunition ammunition) throws SQLException {
        checkItemName(ammunition);
        if (ammunition.getId() == null) {
            ammunitionDAO.insert(ammunition);
        } else {
            ammunitionDAO.update(ammunition);
        }
    }

    public void deleteItem(int id) throws SQLException {
        itemDAO.delete(id);
    }

    // Names are the same when they only differ in upper/lower case or in spaces at the start/end.
    private static boolean sameName(String a, String b) {
        return a.trim().toLowerCase().equals(b.trim().toLowerCase());
    }
}
