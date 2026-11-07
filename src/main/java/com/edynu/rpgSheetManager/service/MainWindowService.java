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
import com.edynu.rpgSheetManager.utils.ImageManager;

/**
 * Provides the application operations used by {@link com.edynu.rpgSheetManager.gui.MainWindow}.
 * This class coordinates data access objects, enforces rules that span multiple records,
 * and delegates character-photo storage to {@link ImageManager}.
 * <p>
 * Methods named {@code save} insert an object when it has no database identifier and update
 * the existing record otherwise. Methods that access the database propagate
 * {@link SQLException} so callers can report persistence failures.
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

    /**
     * Loads every character and attaches its locally stored photo, if one is available.
     *
     * @return all characters with their photo bytes populated when a photo exists
     * @throws SQLException if the character records cannot be read from the database
     */
    public List<PlayerCharacter> listCharacters() throws SQLException {
        List<PlayerCharacter> characters = characterDAO.list();
        for (PlayerCharacter character : characters) {
            character.setPhoto(ImageManager.loadCharacterPhoto(character.getId()));
        }
        return characters;
    }

    /**
     * Inserts or updates a character after checking that its name is unique.
     * Name comparisons ignore surrounding whitespace and letter case.
     *
     * @param character the character to persist
     * @throws IllegalArgumentException if another character already has the same name
     * @throws SQLException if the character records cannot be read or written
     */
    public void saveCharacter(PlayerCharacter character) throws SQLException {
        for (PlayerCharacter other : characterDAO.list()) {
            if (!other.getId().equals(character.getId()) && sameName(other.getName(), character.getName())) {
                throw new IllegalArgumentException("Já existe um personagem com o nome \"" + other.getName() + "\".");
            }
        }
        if (character.getId() == null) {
            characterDAO.insert(character);
        } else {
            characterDAO.update(character);
        }
    }

    /**
     * Resizes and stores a character photo locally, then assigns the saved JPEG bytes to
     * the character object. The photo is limited to 160 pixels on its longest side.
     *
     * @param character the character whose photo is being changed
     * @param imageFile the source image file
     * @throws IOException if the image cannot be read or the photo cannot be stored
     * @throws IllegalArgumentException if the source file is not a supported image
     */
    public void saveCharacterPhoto(PlayerCharacter character, File imageFile) throws IOException {
        byte[] jpegBytes = ImageManager.saveCharacterPhoto(character.getId(), imageFile, 160);
        character.setPhoto(jpegBytes);
    }

    /**
     * Deletes a character record and its locally stored photo.
     *
     * @param id the identifier of the character to delete
     * @throws SQLException if the character record cannot be deleted
     */
    public void deleteCharacter(int id) throws SQLException {
        characterDAO.delete(id);
        ImageManager.deleteCharacterPhoto(id);
    }

    // Skills

    /**
     * Loads the skills belonging to a character.
     *
     * @param characterId the identifier of the character
     * @return the character's skills
     * @throws SQLException if the skills cannot be read from the database
     */
    public List<Skill> listSkills(int characterId) throws SQLException {
        return skillDAO.listByCharacter(characterId);
    }

    /**
     * Inserts or updates a skill after checking that its name is unique for its character.
     * Name comparisons ignore surrounding whitespace and letter case.
     *
     * @param skill the skill to persist
     * @throws IllegalArgumentException if the character already has another skill with the same name
     * @throws SQLException if the skills cannot be read or written
     */
    public void saveSkill(Skill skill) throws SQLException {
        for (Skill other : skillDAO.listByCharacter(skill.getCharacterId())) {
            if (!other.getId().equals(skill.getId()) && sameName(other.getSkillName(), skill.getSkillName())) {
                throw new IllegalArgumentException("Este personagem já possui a perícia \"" + other.getSkillName() + "\".");
            }
        }
        if (skill.getId() == null) {
            skillDAO.insert(skill);
        } else {
            skillDAO.update(skill);
        }
    }

    /**
     * Deletes a skill record.
     *
     * @param id the identifier of the skill to delete
     * @throws SQLException if the skill cannot be deleted
     */
    public void deleteSkill(int id) throws SQLException {
        skillDAO.delete(id);
    }

    // Second skills

    /**
     * Loads the secondary skills belonging to a character.
     *
     * @param characterId the identifier of the character
     * @return the character's secondary skills
     * @throws SQLException if the secondary skills cannot be read from the database
     */
    public List<SecondSkill> listSecondSkills(int characterId) throws SQLException {
        return secondSkillDAO.listByCharacter(characterId);
    }

    /**
     * Inserts or updates a secondary skill after checking that its name is unique for its
     * character. Name comparisons ignore surrounding whitespace and letter case.
     *
     * @param secondSkill the secondary skill to persist
     * @throws IllegalArgumentException if the character already has another secondary skill
     *         with the same name
     * @throws SQLException if the secondary skills cannot be read or written
     */
    public void saveSecondSkill(SecondSkill secondSkill) throws SQLException {
        for (SecondSkill other : secondSkillDAO.listByCharacter(secondSkill.getCharacterId())) {
            if (!other.getId().equals(secondSkill.getId()) && sameName(other.getSecondSkillName(), secondSkill.getSecondSkillName())) {
                throw new IllegalArgumentException("Este personagem já possui a perícia secundária \""
                        + other.getSecondSkillName() + "\".");
            }
        }
        if (secondSkill.getId() == null) {
            secondSkillDAO.insert(secondSkill);
        } else {
            secondSkillDAO.update(secondSkill);
        }
    }

    /**
     * Deletes a secondary skill record.
     *
     * @param id the identifier of the secondary skill to delete
     * @throws SQLException if the secondary skill cannot be deleted
     */
    public void deleteSecondSkill(int id) throws SQLException {
        secondSkillDAO.delete(id);
    }

    // Abilities

    /**
     * Loads all abilities in the shared ability catalog.
     *
     * @return all abilities
     * @throws SQLException if the abilities cannot be read from the database
     */
    public List<Ability> listAbilities() throws SQLException {
        return abilityDAO.list();
    }

    /**
     * Inserts or updates an ability after checking that its name is unique in the catalog.
     * Name comparisons ignore surrounding whitespace and letter case.
     *
     * @param ability the ability to persist
     * @throws IllegalArgumentException if another ability already has the same name
     * @throws SQLException if the abilities cannot be read or written
     */
    public void saveAbility(Ability ability) throws SQLException {
        for (Ability other : abilityDAO.list()) {
            if (!other.getAbilityId().equals(ability.getAbilityId()) && sameName(other.getName(), ability.getName())) {
                throw new IllegalArgumentException("A habilidade \"" + other.getName() + "\" já existe.");
            }
        }
        if (ability.getAbilityId() == null) {
            abilityDAO.insert(ability);
        } else {
            abilityDAO.update(ability);
        }
    }

    /**
     * Deletes an ability from the shared catalog.
     *
     * @param id the identifier of the ability to delete
     * @throws SQLException if the ability cannot be deleted
     */
    public void deleteAbility(int id) throws SQLException {
        abilityDAO.delete(id);
    }

    /**
     * Loads the abilities learned by a character.
     *
     * @param characterId the identifier of the character
     * @return the character-to-ability links for that character
     * @throws SQLException if the learned abilities cannot be read from the database
     */
    public List<CharacterAbility> listLearnedAbilities(int characterId) throws SQLException {
        return characterAbilityDAO.listByCharacter(characterId);
    }

    /**
     * Marks an ability as learned by a character. If the character already has the ability,
     * this method leaves the existing link unchanged.
     *
     * @param characterId the identifier of the character
     * @param abilityId the identifier of the ability
     * @throws SQLException if the existing links cannot be read or the new link cannot be stored
     */
    public void learnAbility(int characterId, int abilityId) throws SQLException {
        for (CharacterAbility learned : characterAbilityDAO.listByCharacter(characterId)) {
            if (learned.getAbilityId() == abilityId) {
                return;
            }
        }
        characterAbilityDAO.insert(new CharacterAbility(characterId, abilityId));
    }

    /**
     * Removes the link that marks an ability as learned by a character.
     *
     * @param characterId the identifier of the character
     * @param abilityId the identifier of the ability
     * @throws SQLException if the link cannot be deleted
     */
    public void forgetAbility(int characterId, int abilityId) throws SQLException {
        characterAbilityDAO.delete(characterId, abilityId);
    }

    // Inventory

    /**
     * Loads the inventory entries belonging to a character.
     *
     * @param characterId the identifier of the character
     * @return the character's inventory entries
     * @throws SQLException if the inventory cannot be read from the database
     */
    public List<Inventory> listInventory(int characterId) throws SQLException {
        return inventoryDAO.listByCharacter(characterId);
    }

    /**
     * Inserts or updates an inventory entry.
     * <p>
     * Quantities must be zero or greater. A character can have only one entry for the same
     * item and equipped state, so an existing entry with that combination must be updated
     * rather than duplicated.
     *
     * @param entry the inventory entry to persist
     * @throws IllegalArgumentException if the quantity is negative or the same item and
     *         equipped state already exist in another entry for the character
     * @throws SQLException if the inventory or item records cannot be read or written
     */
    public void saveInventory(Inventory entry) throws SQLException {
        if (entry.getQuantity() < 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior ou igual a zero.");
        }
        for (Inventory other : inventoryDAO.listByCharacter(entry.getCharacterId())) {
            if (!other.getId().equals(entry.getId()) && other.getItemId().equals(entry.getItemId())
                    && other.isEquipped() == entry.isEquipped()) {
                String state = entry.isEquipped() ? "equipado" : "não equipado";
                throw new IllegalArgumentException("Este personagem já possui \""
                        + itemDAO.findById(entry.getItemId()).getName() + "\" (" + state
                        + ") no inventário. Altere a quantidade desse registro.");
            }
        }
        if (entry.getId() == null) {
            inventoryDAO.insert(entry);
        } else {
            inventoryDAO.update(entry);
        }
    }

    /**
     * Deletes an inventory entry.
     *
     * @param id the identifier of the inventory entry to delete
     * @throws SQLException if the entry cannot be deleted
     */
    public void deleteInventory(int id) throws SQLException {
        inventoryDAO.delete(id);
    }

    // Items

    /**
     * Loads every item from the shared item catalog, including weapons, armor, and ammunition.
     *
     * @return all catalog items
     * @throws SQLException if the items cannot be read from the database
     */
    public List<Item> listAllItems() throws SQLException {
        return itemDAO.list();
    }

    /**
     * Loads general-purpose items, excluding the weapon, armor, and ammunition categories.
     *
     * @return items in the general category
     * @throws SQLException if the items cannot be read from the database
     */
    public List<Item> listGeneralItems() throws SQLException {
        return itemDAO.listByCategory(ItemCategory.GENERAL);
    }

    /**
     * Checks a proposed item name against every category in the shared item catalog.
     * Weapons, armor, ammunition, and general items share one namespace for names.
     * Name comparisons ignore surrounding whitespace and letter case.
     *
     * @param item the item whose name is being checked
     * @throws IllegalArgumentException if another catalog item already has the same name
     * @throws SQLException if the item catalog cannot be read
     */
    private void checkItemName(Item item) throws SQLException {
        for (Item other : itemDAO.list()) {
            if (!other.getId().equals(item.getId()) && sameName(other.getName(), item.getName())) {
                throw new IllegalArgumentException("O item \"" + other.getName() + "\" (" + other.getCategory() + ") já existe.");
            }
        }
    }

    /**
     * Inserts or updates a general-purpose item after validating its name against the
     * entire shared item catalog.
     *
     * @param item the general-purpose item to persist
     * @throws IllegalArgumentException if another catalog item already has the same name
     * @throws SQLException if the item catalog cannot be read or the item cannot be written
     */
    public void saveGeneralItem(Item item) throws SQLException {
        checkItemName(item);
        if (item.getId() == null) {
            itemDAO.insert(item);
        } else {
            itemDAO.update(item);
        }
    }

    /**
     * Loads all weapons from the item catalog.
     *
     * @return all weapons
     * @throws SQLException if the weapons cannot be read from the database
     */
    public List<Weapon> listWeapons() throws SQLException {
        return weaponDAO.list();
    }

    /**
     * Inserts or updates a weapon after validating its name against every item category.
     *
     * @param weapon the weapon to persist
     * @throws IllegalArgumentException if another catalog item already has the same name
     * @throws SQLException if the item catalog cannot be read or the weapon cannot be written
     */
    public void saveWeapon(Weapon weapon) throws SQLException {
        checkItemName(weapon);
        if (weapon.getId() == null) {
            weaponDAO.insert(weapon);
        } else {
            weaponDAO.update(weapon);
        }
    }

    /**
     * Loads all armor entries from the item catalog.
     *
     * @return all armor entries
     * @throws SQLException if the armor entries cannot be read from the database
     */
    public List<Armor> listArmors() throws SQLException {
        return armorDAO.list();
    }

    /**
     * Inserts or updates an armor entry after validating its name against every item category.
     *
     * @param armor the armor entry to persist
     * @throws IllegalArgumentException if another catalog item already has the same name
     * @throws SQLException if the item catalog cannot be read or the armor cannot be written
     */
    public void saveArmor(Armor armor) throws SQLException {
        checkItemName(armor);
        if (armor.getId() == null) {
            armorDAO.insert(armor);
        } else {
            armorDAO.update(armor);
        }
    }

    /**
     * Loads all ammunition entries from the item catalog.
     *
     * @return all ammunition entries
     * @throws SQLException if the ammunition entries cannot be read from the database
     */
    public List<Ammunition> listAmmunition() throws SQLException {
        return ammunitionDAO.list();
    }

    /**
     * Inserts or updates an ammunition entry after validating its name against every item category.
     *
     * @param ammunition the ammunition entry to persist
     * @throws IllegalArgumentException if another catalog item already has the same name
     * @throws SQLException if the item catalog cannot be read or the ammunition cannot be written
     */
    public void saveAmmunition(Ammunition ammunition) throws SQLException {
        checkItemName(ammunition);
        if (ammunition.getId() == null) {
            ammunitionDAO.insert(ammunition);
        } else {
            ammunitionDAO.update(ammunition);
        }
    }

    /**
     * Deletes an item from the shared catalog. Database foreign-key rules remove associated
     * weapon, armor, ammunition, and inventory records where applicable.
     *
     * @param id the identifier of the item to delete
     * @throws SQLException if the item cannot be deleted
     */
    public void deleteItem(int id) throws SQLException {
        itemDAO.delete(id);
    }

    /**
     * Compares two names without regard to surrounding whitespace or letter case.
     *
     * @param a the first name
     * @param b the second name
     * @return {@code true} when the trimmed names are equal ignoring letter case
     */
    private static boolean sameName(String a, String b) {
        return a.trim().toLowerCase().equals(b.trim().toLowerCase());
    }
}
