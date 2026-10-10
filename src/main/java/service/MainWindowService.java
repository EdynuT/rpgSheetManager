package service;

import dao.AbilityDAO;
import dao.AmunitionDAO;
import dao.ArmorDAO;
import dao.CharacterAbilityDAO;
import dao.InventoryDAO;
import dao.ItemDAO;
import dao.PlayerCharacterDAO;
import dao.SecondBoundDAO;
import dao.SkillDAO;
import dao.WeaponDAO;
import java.sql.SQLException;
import java.util.List;
import model.Ability;
import model.Amunition;
import model.Armor;
import model.CharacterAbility;
import model.Inventory;
import model.Item;
import model.ItemCategory;
import model.PlayerCharacter;
import model.SecondBound;
import model.Skill;
import model.Weapon;

/**
 * Operations used by the MainWindow. "save" inserts the object when it has no id yet,
 * and updates it otherwise.
 */
public class MainWindowService {
    private final PlayerCharacterDAO characterDAO = new PlayerCharacterDAO();
    private final SkillDAO skillDAO = new SkillDAO();
    private final SecondBoundDAO bondDAO = new SecondBoundDAO();
    private final AbilityDAO abilityDAO = new AbilityDAO();
    private final CharacterAbilityDAO characterAbilityDAO = new CharacterAbilityDAO();
    private final InventoryDAO inventoryDAO = new InventoryDAO();
    private final ItemDAO itemDAO = new ItemDAO();
    private final WeaponDAO weaponDAO = new WeaponDAO();
    private final ArmorDAO armorDAO = new ArmorDAO();
    private final AmunitionDAO amunitionDAO = new AmunitionDAO();

    // Characters

    public List<PlayerCharacter> listCharacters() throws SQLException {
        return characterDAO.list();
    }

    public void saveCharacter(PlayerCharacter character) throws SQLException {
        if (character.getId() == null) {
            characterDAO.insert(character);
        } else {
            characterDAO.update(character);
        }
    }

    public void deleteCharacter(int id) throws SQLException {
        characterDAO.delete(id);
    }

    // Skills

    public List<Skill> listSkills(int characterId) throws SQLException {
        return skillDAO.listByCharacter(characterId);
    }

    public void saveSkill(Skill skill) throws SQLException {
        if (skill.getId() == null) {
            skillDAO.insert(skill);
        } else {
            skillDAO.update(skill);
        }
    }

    public void deleteSkill(int id) throws SQLException {
        skillDAO.delete(id);
    }

    // Secondary bonds

    public List<SecondBound> listBonds(int characterId) throws SQLException {
        return bondDAO.listByCharacter(characterId);
    }

    public void saveBond(SecondBound bond) throws SQLException {
        if (bond.getId() == null) {
            bondDAO.insert(bond);
        } else {
            bondDAO.update(bond);
        }
    }

    public void deleteBond(int id) throws SQLException {
        bondDAO.delete(id);
    }

    // Abilities

    public List<Ability> listAbilities() throws SQLException {
        return abilityDAO.list();
    }

    public void saveAbility(Ability ability) throws SQLException {
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

    // Links the ability to the character, or just changes "active" when it is already linked.
    public void learnAbility(int characterId, int abilityId, boolean active) throws SQLException {
        CharacterAbility link = new CharacterAbility(characterId, abilityId, active);
        for (CharacterAbility learned : characterAbilityDAO.listByCharacter(characterId)) {
            if (learned.getAbilityId() == abilityId) {
                characterAbilityDAO.update(link);
                return;
            }
        }
        characterAbilityDAO.insert(link);
    }

    public void forgetAbility(int characterId, int abilityId) throws SQLException {
        characterAbilityDAO.delete(characterId, abilityId);
    }

    // Inventory

    public List<Inventory> listInventory(int characterId) throws SQLException {
        return inventoryDAO.listByCharacter(characterId);
    }

    public void saveInventory(Inventory entry) throws SQLException {
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

    public void saveGeneralItem(Item item) throws SQLException {
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
        if (armor.getId() == null) {
            armorDAO.insert(armor);
        } else {
            armorDAO.update(armor);
        }
    }

    public List<Amunition> listAmmunition() throws SQLException {
        return amunitionDAO.list();
    }

    public void saveAmmunition(Amunition ammunition) throws SQLException {
        if (ammunition.getId() == null) {
            amunitionDAO.insert(ammunition);
        } else {
            amunitionDAO.update(ammunition);
        }
    }

    public void deleteItem(int id) throws SQLException {
        itemDAO.delete(id);
    }
}
