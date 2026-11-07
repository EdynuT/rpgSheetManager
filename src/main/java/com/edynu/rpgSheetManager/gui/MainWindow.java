package com.edynu.rpgSheetManager.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import com.edynu.rpgSheetManager.model.Ability;
import com.edynu.rpgSheetManager.model.AbilityCategory;
import com.edynu.rpgSheetManager.model.ActionType;
import com.edynu.rpgSheetManager.model.Ammunition;
import com.edynu.rpgSheetManager.model.Armor;
import com.edynu.rpgSheetManager.model.CharacterAbility;
import com.edynu.rpgSheetManager.model.CostType;
import com.edynu.rpgSheetManager.model.Inventory;
import com.edynu.rpgSheetManager.model.Item;
import com.edynu.rpgSheetManager.model.ItemCategory;
import com.edynu.rpgSheetManager.model.PlayerCharacter;
import com.edynu.rpgSheetManager.model.SecondSkill;
import com.edynu.rpgSheetManager.model.Skill;
import com.edynu.rpgSheetManager.model.Weapon;
import com.edynu.rpgSheetManager.service.MainWindowService;
import com.edynu.rpgSheetManager.utils.DiceRoller;

/**
 * Characters on the left and one tab per model on the right. Each tab has a table, the
 * fields of the row clicked in the table and the New, Save and Delete buttons.
 *
 * @author EdynuT
 */
public class MainWindow extends javax.swing.JFrame {

    private final MainWindowService service = new MainWindowService();
    private final Icon noPhoto = noPhotoIcon();
    private final Map<Integer, Icon> photos = new HashMap<>(); // gallery picture of each character, by id

    // What is on the screen. The rows of each table are in the same order as its list.
    private List<PlayerCharacter> characters = new ArrayList<>();
    private PlayerCharacter selected; // character clicked in the gallery, null when there is none
    private List<Skill> skills = new ArrayList<>();
    private List<SecondSkill> secondSkills = new ArrayList<>();
    private List<Ability> abilities = new ArrayList<>();
    private List<CharacterAbility> learned = new ArrayList<>();
    private List<Inventory> inventory = new ArrayList<>();
    private List<Item> allItems = new ArrayList<>();
    private List<Item> inventoryItems = new ArrayList<>();
    private List<Item> generalItems = new ArrayList<>();
    private List<Weapon> weapons = new ArrayList<>();
    private List<Armor> armors = new ArrayList<>();
    private List<Ammunition> ammunition = new ArrayList<>();

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        cmbCategory.setModel(new DefaultComboBoxModel<>(AbilityCategory.values()));
        cmbActionType.setModel(new DefaultComboBoxModel<>(ActionType.values()));
        cmbCostType.setModel(new DefaultComboBoxModel<>(CostType.values()));
        cmbInventoryCategory.setModel(new DefaultComboBoxModel<>(new ItemCategory[] {
            ItemCategory.GENERAL, ItemCategory.WEAPON, ItemCategory.ARMOR, ItemCategory.AMMUNITION
        }));
        cmbInventoryCategory.addActionListener(evt -> {
            refreshInventoryItemChoices();
            loadInventory();
        });
        // Each character of the gallery is shown as its photo with the name below it.
        lstCharacter.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                PlayerCharacter character = (PlayerCharacter) value;
                JLabel cell = (JLabel) super.getListCellRendererComponent(list, character.getName(), index, isSelected, cellHasFocus);
                cell.setIcon(photos.get(character.getId()));
                cell.setHorizontalAlignment(JLabel.CENTER);
                cell.setHorizontalTextPosition(JLabel.CENTER);
                cell.setVerticalTextPosition(JLabel.BOTTOM);
                return cell;
            }
        });
        // The ...TableMouseClicked methods fill the form with the selected row. The selected row
        // also changes with the arrow keys or when the mouse moves during the click, and that is
        // not a mouse click, so they also run on every selection change. Without this, Save and
        // Delete could act on a row that is not the one shown in the form.
        tblSkills.getSelectionModel().addListSelectionListener(e -> skillsTableMouseClicked(null));
        tblSecondSkills.getSelectionModel().addListSelectionListener(e -> secondSkillsTableMouseClicked(null));
        tblAbilities.getSelectionModel().addListSelectionListener(e -> abilitiesTableMouseClicked(null));
        tblInventory.getSelectionModel().addListSelectionListener(e -> inventoryTableMouseClicked(null));
        tblItems.getSelectionModel().addListSelectionListener(e -> itemsTableMouseClicked(null));
        tblWeapons.getSelectionModel().addListSelectionListener(e -> weaponsTableMouseClicked(null));
        tblArmor.getSelectionModel().addListSelectionListener(e -> armorTableMouseClicked(null));
        tblAmmunition.getSelectionModel().addListSelectionListener(e -> ammunitionTableMouseClicked(null));
        loadCharacters();
        loadItems();
        loadAbilities();
        if (!characters.isEmpty()) {
            lstCharacter.setSelectedIndex(0);
        }
    }

    // ---------------------------------------------------------------- Characters

    private void loadCharacters() {
        try {
            characters = service.listCharacters();
        } catch (SQLException e) {
            showError(e);
        }
        photos.clear();
        for (PlayerCharacter character : characters) {
            photos.put(character.getId(), photoIcon(character.getPhoto(), 80));
        }
        fillGallery();
    }

    // Shows the characters whose name contains the text of the search field.
    private void fillGallery() {
        String search = txtSearch.getText().trim().toLowerCase();
        DefaultListModel<PlayerCharacter> model = new DefaultListModel<>();
        for (PlayerCharacter character : characters) {
            if (character.getName().toLowerCase().contains(search)) {
                model.addElement(character);
            }
        }
        lstCharacter.setModel(model);
        if (selected != null) {
            selectInGallery(selected.getId());
        }
    }

    private void selectInGallery(int characterId) {
        for (int i = 0; i < lstCharacter.getModel().getSize(); i++) {
            if (((PlayerCharacter) lstCharacter.getModel().getElementAt(i)).getId() == characterId) {
                lstCharacter.setSelectedIndex(i);
            }
        }
    }

    // Fills the Character tab and the tables of the selected character.
    private void showCharacter() {
        txtName.setText(selected.getName());
        txtLevel.setText(String.valueOf(selected.getLevel()));
        txtAge.setText(String.valueOf(selected.getAge()));
        txtRace.setText(selected.getRace());
        txtClass.setText(selected.getCharacterClass());
        txtSubclass.setText(selected.getSubclass());
        txtOrigin.setText(selected.getOrigin());
        txtLanguages.setText(selected.getLanguage());
        txtHealth.setText(String.valueOf(selected.getCurrentHealth()));
        txtBaseHealth.setText(String.valueOf(selected.getBaseHealth()));
        txtMana.setText(String.valueOf(selected.getCurrentMana()));
        txtBaseMana.setText(String.valueOf(selected.getBaseMana()));
        txtStamina.setText(String.valueOf(selected.getCurrentStamina()));
        txtBaseStamina.setText(String.valueOf(selected.getBaseStamina()));
        txtSanity.setText(String.valueOf(selected.getCurrentSanity()));
        txtBaseSanity.setText(String.valueOf(selected.getBaseSanity()));
        loadSkills();
        loadSecondSkills();
        loadAbilities();
        loadInventory();
    }

    private void clearCharacter() {
        selected = null;
        lstCharacter.clearSelection();
        clear(txtName, txtLevel, txtAge, txtRace, txtClass, txtSubclass, txtOrigin, txtLanguages,
                txtHealth, txtBaseHealth, txtMana, txtBaseMana, txtStamina, txtBaseStamina, txtSanity, txtBaseSanity);
        loadSkills();
        loadSecondSkills();
        loadAbilities();
        loadInventory();
    }

    // ---------------------------------------------------------------- Skills

    private void loadSkills() {
        skills = new ArrayList<>();
        try {
            if (selected != null) {
                skills = service.listSkills(selected.getId());
            }
        } catch (SQLException e) {
            showError(e);
        }
        DefaultTableModel model = (DefaultTableModel) tblSkills.getModel();
        model.setRowCount(0);
        for (Skill skill : skills) {
            model.addRow(new Object[] {skill.getSkillName(), skill.getValue()});
        }
        clearSkillForm();
    }

    private void clearSkillForm() {
        tblSkills.clearSelection();
        clear(txtSkillName, txtSkillValue);
    }

    // ---------------------------------------------------------------- Second skills

    private void loadSecondSkills() {
        secondSkills = new ArrayList<>();
        try {
            if (selected != null) {
                secondSkills = service.listSecondSkills(selected.getId());
            }
        } catch (SQLException e) {
            showError(e);
        }
        DefaultTableModel model = (DefaultTableModel) tblSecondSkills.getModel();
        model.setRowCount(0);
        for (SecondSkill secondSkill : secondSkills) {
            model.addRow(new Object[] {secondSkill.getSecondSkillName(), secondSkill.getValue()});
        }
        clearSecondSkillForm();
    }

    private void clearSecondSkillForm() {
        tblSecondSkills.clearSelection();
        clear(txtSecondSkillName, txtSecondSkillValue);
    }

    // ---------------------------------------------------------------- Abilities

    // All abilities; "Aprendida" refers to the selected character.
    private void loadAbilities() {
        learned = new ArrayList<>();
        try {
            abilities = service.listAbilities();
            if (selected != null) {
                learned = service.listLearnedAbilities(selected.getId());
            }
        } catch (SQLException e) {
            showError(e);
        }
        DefaultTableModel model = (DefaultTableModel) tblAbilities.getModel();
        model.setRowCount(0);
        for (Ability ability : abilities) {
            CharacterAbility link = findLearned(ability.getAbilityId());
            model.addRow(new Object[] {
                ability.getName(),
                ability.getCategory(),
                ability.getActionType(),
                ability.getCostType(),
                ability.getCostValue(),
                link != null ? "Sim" : "Não"
            });
        }
        clearAbilityForm();
    }

    private CharacterAbility findLearned(int abilityId) {
        for (CharacterAbility link : learned) {
            if (link.getAbilityId() == abilityId) {
                return link;
            }
        }
        return null;
    }

    private void clearAbilityForm() {
        tblAbilities.clearSelection();
        clear(txtAbilityName, txtCost, txtAbilityDescription);
    }

    // ---------------------------------------------------------------- Inventory

    private void loadInventory() {
        inventory = new ArrayList<>();
        try {
            if (selected != null) {
                inventory = service.listInventory(selected.getId());
            }
        } catch (SQLException e) {
            showError(e);
        }
        ItemCategory category = (ItemCategory) cmbInventoryCategory.getSelectedItem();
        if (category != null) {
            List<Inventory> categoryInventory = new ArrayList<>();
            for (Inventory entry : inventory) {
                Item item = findItem(entry.getItemId());
                if (item != null && item.getCategory() == category) {
                    categoryInventory.add(entry);
                }
            }
            inventory = categoryInventory;
        }
        DefaultTableModel model = (DefaultTableModel) tblInventory.getModel();
        model.setRowCount(0);
        for (Inventory entry : inventory) {
            Item item = findItem(entry.getItemId());
            model.addRow(new Object[] {item.getName(), item.getCategory(), entry.getQuantity(),
                entry.isEquipped() ? "Sim" : "Não", item.getWeight()});
        }
        clearInventoryForm();
    }

    private Item findItem(int itemId) {
        for (Item item : allItems) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        return null;
    }

    private void clearInventoryForm() {
        tblInventory.clearSelection();
        cmbItem.setSelectedIndex(-1);
        clear(txtQuantity);
        chkEquipped.setSelected(false);
    }

    private void refreshInventoryItemChoices() {
        inventoryItems.clear();
        cmbItem.removeAllItems();
        ItemCategory category = (ItemCategory) cmbInventoryCategory.getSelectedItem();
        if (category == null) {
            return;
        }
        for (Item item : allItems) {
            if (item.getCategory() == category) {
                inventoryItems.add(item);
                cmbItem.addItem(item.getName());
            }
        }
        cmbItem.setSelectedIndex(-1);
    }

    // ---------------------------------------------------------------- Items, weapons, armor and ammunition

    // Reloads the four item tabs and the item list of the inventory.
    private void loadItems() {
        try {
            allItems = service.listAllItems();
            generalItems = service.listGeneralItems();
            weapons = service.listWeapons();
            armors = service.listArmors();
            ammunition = service.listAmmunition();
        } catch (SQLException e) {
            showError(e);
        }

        DefaultTableModel model = (DefaultTableModel) tblItems.getModel();
        model.setRowCount(0);
        for (Item item : generalItems) {
            model.addRow(new Object[] {item.getName(), item.getWeight(), item.getDescription()});
        }
        model = (DefaultTableModel) tblWeapons.getModel();
        model.setRowCount(0);
        for (Weapon weapon : weapons) {
            model.addRow(new Object[] {weapon.getName(), weapon.getDamageDice(), weapon.getScalingAttribute(),
                weapon.getCriticalRange(), weapon.getCriticalMultiplier(), weapon.getWeight()});
        }
        model = (DefaultTableModel) tblArmor.getModel();
        model.setRowCount(0);
        for (Armor armor : armors) {
            model.addRow(new Object[] {armor.getName(), armor.getPhysicalAC(), armor.getElementalAC(), armor.getWeight()});
        }
        model = (DefaultTableModel) tblAmmunition.getModel();
        model.setRowCount(0);
        for (Ammunition ammo : ammunition) {
            model.addRow(new Object[] {ammo.getName(), ammo.getDamageDice(), ammo.getWeight()});
        }

        refreshInventoryItemChoices();
        clearItemForm();
        clearWeaponForm();
        clearArmorForm();
        clearAmmoForm();
        loadInventory(); // a deleted item also leaves the inventory
    }

    private void clearItemForm() {
        tblItems.clearSelection();
        clear(txtItemName, txtItemWeight, txtItemDescription);
    }

    private void clearWeaponForm() {
        tblWeapons.clearSelection();
        clear(txtWeaponName, txtWeaponWeight, txtWeaponAttribute, txtWeaponDamage, txtWeaponRange,
                txtWeaponMultiplier, txtWeaponDescription, txtWeaponDice);
    }

    private void clearArmorForm() {
        tblArmor.clearSelection();
        clear(txtArmorName, txtArmorWeight, txtPhysicalAc, txtElementalAc, txtArmorDescription);
    }

    private void clearAmmoForm() {
        tblAmmunition.clearSelection();
        clear(txtAmmoName, txtAmmoWeight, txtAmmoDamage, txtAmmoDescription, txtAmmoDice);
    }

    // ---------------------------------------------------------------- Photos

    // Photo reduced to fit in size x size pixels, or the "Foto" box when there is no photo.
    private Icon photoIcon(byte[] photo, int size) {
        if (photo == null) {
            return noPhoto;
        }
        ImageIcon icon = new ImageIcon(photo);
        double scale = (double) size / Math.max(icon.getIconWidth(), icon.getIconHeight());
        int width = Math.max(1, (int) (icon.getIconWidth() * scale));
        int height = Math.max(1, (int) (icon.getIconHeight() * scale));
        return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    private static Icon noPhotoIcon() {
        BufferedImage box = new BufferedImage(80, 80, BufferedImage.TYPE_INT_RGB);
        Graphics g = box.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 80, 80);
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, 79, 79);
        g.drawString("Foto", 27, 45);
        g.dispose();
        return new ImageIcon(box);
    }

    // ---------------------------------------------------------------- Helpers

    /**
     * Rolls the dice typed in a "Dados" field and shows the result in a message.
     * Used by the Roll buttons of the Skills, Second Skills, Weapons and Ammunition tabs.
     * <p>
     * Accepts what {@link DiceRoller#rollDice(String)} accepts: dice ("1d20"), numbers,
     * + - * / and parentheses, with or without spaces, e.g. "1d20 + 5" or "(2d6 + 3) * 2".
     *
     * @param field the text field with the dice expression
     * @param name  the name of the entry being rolled, shown as the title of the result
     */
    private void rollCustomDice(JTextField field, String name) {
        String expression = field.getText().trim();
        if (expression.isEmpty()) {
            showMessage("Digite os dados para rolar primeiro, por exemplo, 1d20 + 5.");
            return;
        }
        try {
            int result = new DiceRoller().rollDice(expression);
            String title = name.trim().isEmpty() ? "Rolar" : name.trim();
            showMessage(title + "\n" + expression + " = " + result);
        } catch (RuntimeException e) {
            showInvalidRoll(expression);
        }
    }

    // The dice library throws a RuntimeException with a technical message when it cannot read the
    // text (e.g. "fire"), so the Roll buttons show this message instead.
    private void showInvalidRoll(String expression) {
        showMessage("\"" + expression + "\" não é uma rolagem válida. Use algo como 1d20 + 5 ou 2d6 - 1.");
    }

    /**
     * Multiplies only the dice count (N in NdX) by the given critical multiplier,
     * keeping flat modifiers intact (e.g. "2d6 + 3" with multiplier 3 becomes "6d6 + 3").
     */
    private String multiplyDiceCount(String expression, int multiplier) {
        if (multiplier <= 1) {
            return expression;
        }
        Matcher matcher = Pattern.compile("(?i)(\\d*)d(\\d+)").matcher(expression);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String countStr = matcher.group(1);
            int count = countStr.isEmpty() ? 1 : Integer.parseInt(countStr);
            int multipliedCount = count * multiplier;
            matcher.appendReplacement(sb, multipliedCount + "d" + matcher.group(2));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Helper to roll weapon damage (normal or critical) and display the dialog.
     */
    private void rollWeaponDamage(boolean critical) {
        int row = tblWeapons.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione uma arma na tabela primeiro.");
            return;
        }
        Weapon weapon = weapons.get(row);

        String expression = DiceRoller.findDamageExpression(weapon.getDamageDice());
        if (expression == null && weapon.getDamageDice() != null && !weapon.getDamageDice().trim().isEmpty()) {
            expression = weapon.getDamageDice().trim();
        }
        if (expression == null) {
            showMessage("A arma " + weapon.getName() + " não possui dados de dano (como 2d6 + 3).");
            return;
        }

        String label = "Dano: ";
        if (critical) {
            int multiplier = Math.max(1, weapon.getCriticalMultiplier());
            expression = multiplyDiceCount(expression, multiplier);
            label = "Dano crítico (x" + multiplier + "): ";
        }

        try {
            int damage = new DiceRoller().rollDice(expression);
            showMessage(weapon.getName() + "\n" + label + expression + " = " + damage);
        } catch (RuntimeException e) {
            showInvalidRoll(expression);
        }
    }

    private void clear(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private int number(JTextField field, String name) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " deve ser um número inteiro.");
        }
    }

    // Accepts both 1.5 and 1,5.
    private double decimal(JTextField field, String name) {
        try {
            return Double.parseDouble(field.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " deve ser um número.");
        }
    }

    private boolean confirm(String question) {
        Object[] options = {"Sim", "Não"};
        return JOptionPane.showOptionDialog(this, question, "Confirmar", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]) == JOptionPane.YES_OPTION;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Mensagem", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        pnlLeft = new javax.swing.JPanel();
        pnlSearch = new javax.swing.JPanel();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        scrCharacter = new javax.swing.JScrollPane();
        lstCharacter = new javax.swing.JList<com.edynu.rpgSheetManager.model.PlayerCharacter>();
        tabMain = new javax.swing.JTabbedPane();
        pnlCharacterTab = new javax.swing.JPanel();
        pnlCharacterFields = new javax.swing.JPanel();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        lblLevel = new javax.swing.JLabel();
        txtLevel = new javax.swing.JTextField();
        lblAge = new javax.swing.JLabel();
        txtAge = new javax.swing.JTextField();
        lblRace = new javax.swing.JLabel();
        txtRace = new javax.swing.JTextField();
        lblClass = new javax.swing.JLabel();
        txtClass = new javax.swing.JTextField();
        lblSubclass = new javax.swing.JLabel();
        txtSubclass = new javax.swing.JTextField();
        lblOrigin = new javax.swing.JLabel();
        txtOrigin = new javax.swing.JTextField();
        lblLanguages = new javax.swing.JLabel();
        txtLanguages = new javax.swing.JTextField();
        lblHealth = new javax.swing.JLabel();
        txtHealth = new javax.swing.JTextField();
        lblBaseHealth = new javax.swing.JLabel();
        txtBaseHealth = new javax.swing.JTextField();
        lblMana = new javax.swing.JLabel();
        txtMana = new javax.swing.JTextField();
        lblBaseMana = new javax.swing.JLabel();
        txtBaseMana = new javax.swing.JTextField();
        lblStamina = new javax.swing.JLabel();
        txtStamina = new javax.swing.JTextField();
        lblBaseStamina = new javax.swing.JLabel();
        txtBaseStamina = new javax.swing.JTextField();
        lblSanity = new javax.swing.JLabel();
        txtSanity = new javax.swing.JTextField();
        lblBaseSanity = new javax.swing.JLabel();
        txtBaseSanity = new javax.swing.JTextField();
        pnlCharacterButtons = new javax.swing.JPanel();
        btnNewCharacter = new javax.swing.JButton();
        btnSaveCharacter = new javax.swing.JButton();
        btnDeleteCharacter = new javax.swing.JButton();
        btnChangePhoto = new javax.swing.JButton();
        pnlSkillsTab = new javax.swing.JPanel();
        scrSkills = new javax.swing.JScrollPane();
        tblSkills = new javax.swing.JTable();
        pnlSkillsForm = new javax.swing.JPanel();
        pnlSkillsFields = new javax.swing.JPanel();
        lblSkillName = new javax.swing.JLabel();
        txtSkillName = new javax.swing.JTextField();
        lblSkillValue = new javax.swing.JLabel();
        txtSkillValue = new javax.swing.JTextField();
        lblSkillDice = new javax.swing.JLabel();
        txtSkillDice = new javax.swing.JTextField();
        pnlSkillsButtons = new javax.swing.JPanel();
        btnNewSkill = new javax.swing.JButton();
        btnSaveSkill = new javax.swing.JButton();
        btnDeleteSkill = new javax.swing.JButton();
        btnRollSkillDice = new javax.swing.JButton();
        pnlSecondSkillsTab = new javax.swing.JPanel();
        scrSecondSkills = new javax.swing.JScrollPane();
        tblSecondSkills = new javax.swing.JTable();
        pnlSecondSkillsForm = new javax.swing.JPanel();
        pnlSecondSkillsFields = new javax.swing.JPanel();
        lblSecondSkillName = new javax.swing.JLabel();
        txtSecondSkillName = new javax.swing.JTextField();
        lblSecondSkillValue = new javax.swing.JLabel();
        txtSecondSkillValue = new javax.swing.JTextField();
        lblSecondSkillDice = new javax.swing.JLabel();
        txtSecondSkillDice = new javax.swing.JTextField();
        pnlSecondSkillsButtons = new javax.swing.JPanel();
        btnNewSecondSkill = new javax.swing.JButton();
        btnSaveSecondSkill = new javax.swing.JButton();
        btnDeleteSecondSkill = new javax.swing.JButton();
        btnRollSecondSkillDice = new javax.swing.JButton();
        pnlAbilitiesTab = new javax.swing.JPanel();
        scrAbilities = new javax.swing.JScrollPane();
        tblAbilities = new javax.swing.JTable();
        pnlAbilitiesForm = new javax.swing.JPanel();
        pnlAbilitiesFields = new javax.swing.JPanel();
        lblAbilityName = new javax.swing.JLabel();
        txtAbilityName = new javax.swing.JTextField();
        lblCategory = new javax.swing.JLabel();
        cmbCategory = new javax.swing.JComboBox<com.edynu.rpgSheetManager.model.AbilityCategory>();
        lblActionType = new javax.swing.JLabel();
        cmbActionType = new javax.swing.JComboBox<com.edynu.rpgSheetManager.model.ActionType>();
        lblCostType = new javax.swing.JLabel();
        cmbCostType = new javax.swing.JComboBox<com.edynu.rpgSheetManager.model.CostType>();
        lblCost = new javax.swing.JLabel();
        txtCost = new javax.swing.JTextField();
        lblAbilityDescription = new javax.swing.JLabel();
        txtAbilityDescription = new javax.swing.JTextField();
        pnlAbilitiesButtons = new javax.swing.JPanel();
        btnNewAbility = new javax.swing.JButton();
        btnSaveAbility = new javax.swing.JButton();
        btnDeleteAbility = new javax.swing.JButton();
        btnLearn = new javax.swing.JButton();
        btnForget = new javax.swing.JButton();
        btnRollDamage = new javax.swing.JButton();
        pnlInventoryTab = new javax.swing.JPanel();
        pnlInventoryCategory = new javax.swing.JPanel();
        lblInventoryCategory = new javax.swing.JLabel();
        cmbInventoryCategory = new javax.swing.JComboBox<com.edynu.rpgSheetManager.model.ItemCategory>();
        scrInventory = new javax.swing.JScrollPane();
        tblInventory = new javax.swing.JTable();
        pnlInventoryForm = new javax.swing.JPanel();
        pnlInventoryFields = new javax.swing.JPanel();
        lblItem = new javax.swing.JLabel();
        cmbItem = new javax.swing.JComboBox<String>();
        lblQuantity = new javax.swing.JLabel();
        txtQuantity = new javax.swing.JTextField();
        lblEquipped = new javax.swing.JLabel();
        chkEquipped = new javax.swing.JCheckBox();
        pnlInventoryButtons = new javax.swing.JPanel();
        btnSaveEntry = new javax.swing.JButton();
        btnDeleteEntry = new javax.swing.JButton();
        pnlItemsTab = new javax.swing.JPanel();
        scrItems = new javax.swing.JScrollPane();
        tblItems = new javax.swing.JTable();
        pnlItemsForm = new javax.swing.JPanel();
        pnlItemsFields = new javax.swing.JPanel();
        lblItemName = new javax.swing.JLabel();
        txtItemName = new javax.swing.JTextField();
        lblItemWeight = new javax.swing.JLabel();
        txtItemWeight = new javax.swing.JTextField();
        lblItemDescription = new javax.swing.JLabel();
        txtItemDescription = new javax.swing.JTextField();
        pnlItemsButtons = new javax.swing.JPanel();
        btnNewItem = new javax.swing.JButton();
        btnSaveItem = new javax.swing.JButton();
        btnDeleteItem = new javax.swing.JButton();
        pnlWeaponsTab = new javax.swing.JPanel();
        scrWeapons = new javax.swing.JScrollPane();
        tblWeapons = new javax.swing.JTable();
        pnlWeaponsForm = new javax.swing.JPanel();
        pnlWeaponsFields = new javax.swing.JPanel();
        lblWeaponName = new javax.swing.JLabel();
        txtWeaponName = new javax.swing.JTextField();
        lblWeaponWeight = new javax.swing.JLabel();
        txtWeaponWeight = new javax.swing.JTextField();
        lblWeaponAttribute = new javax.swing.JLabel();
        txtWeaponAttribute = new javax.swing.JTextField();
        lblWeaponDamage = new javax.swing.JLabel();
        txtWeaponDamage = new javax.swing.JTextField();
        lblWeaponRange = new javax.swing.JLabel();
        txtWeaponRange = new javax.swing.JTextField();
        lblWeaponMultiplier = new javax.swing.JLabel();
        txtWeaponMultiplier = new javax.swing.JTextField();
        lblWeaponDescription = new javax.swing.JLabel();
        txtWeaponDescription = new javax.swing.JTextField();
        lblWeaponDice = new javax.swing.JLabel();
        txtWeaponDice = new javax.swing.JTextField();
        pnlWeaponsButtons = new javax.swing.JPanel();
        btnNewWeapon = new javax.swing.JButton();
        btnSaveWeapon = new javax.swing.JButton();
        btnDeleteWeapon = new javax.swing.JButton();
        btnRollWeaponDamage = new javax.swing.JButton();
        btnRollWeaponCritical = new javax.swing.JButton();
        btnRollWeaponDice = new javax.swing.JButton();
        pnlArmorTab = new javax.swing.JPanel();
        scrArmor = new javax.swing.JScrollPane();
        tblArmor = new javax.swing.JTable();
        pnlArmorForm = new javax.swing.JPanel();
        pnlArmorFields = new javax.swing.JPanel();
        lblArmorName = new javax.swing.JLabel();
        txtArmorName = new javax.swing.JTextField();
        lblArmorWeight = new javax.swing.JLabel();
        txtArmorWeight = new javax.swing.JTextField();
        lblPhysicalAc = new javax.swing.JLabel();
        txtPhysicalAc = new javax.swing.JTextField();
        lblElementalAc = new javax.swing.JLabel();
        txtElementalAc = new javax.swing.JTextField();
        lblArmorDescription = new javax.swing.JLabel();
        txtArmorDescription = new javax.swing.JTextField();
        pnlArmorButtons = new javax.swing.JPanel();
        btnNewArmor = new javax.swing.JButton();
        btnSaveArmor = new javax.swing.JButton();
        btnDeleteArmor = new javax.swing.JButton();
        pnlAmmunitionTab = new javax.swing.JPanel();
        scrAmmunition = new javax.swing.JScrollPane();
        tblAmmunition = new javax.swing.JTable();
        pnlAmmunitionForm = new javax.swing.JPanel();
        pnlAmmunitionFields = new javax.swing.JPanel();
        lblAmmoName = new javax.swing.JLabel();
        txtAmmoName = new javax.swing.JTextField();
        lblAmmoWeight = new javax.swing.JLabel();
        txtAmmoWeight = new javax.swing.JTextField();
        lblAmmoDamage = new javax.swing.JLabel();
        txtAmmoDamage = new javax.swing.JTextField();
        lblAmmoDescription = new javax.swing.JLabel();
        txtAmmoDescription = new javax.swing.JTextField();
        lblAmmoDice = new javax.swing.JLabel();
        txtAmmoDice = new javax.swing.JTextField();
        pnlAmmunitionButtons = new javax.swing.JPanel();
        btnNewAmmo = new javax.swing.JButton();
        btnSaveAmmo = new javax.swing.JButton();
        btnDeleteAmmo = new javax.swing.JButton();
        btnRollAmmoDamage = new javax.swing.JButton();
        btnRollAmmoDice = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gerenciador de Fichas de RPG");
        setPreferredSize(new java.awt.Dimension(1100, 650));

        lblTitle.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTitle.setText("RPG SHEET MANAGER");
        lblTitle.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 10, 0));
        getContentPane().add(lblTitle, java.awt.BorderLayout.PAGE_START);

        pnlLeft.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 0));
        pnlLeft.setPreferredSize(new java.awt.Dimension(250, 400));
        pnlLeft.setLayout(new java.awt.BorderLayout(0, 5));

        pnlSearch.setLayout(new java.awt.BorderLayout(0, 3));

        lblSearch.setText("Nome do personagem");
        pnlSearch.add(lblSearch, java.awt.BorderLayout.PAGE_START);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });
        pnlSearch.add(txtSearch, java.awt.BorderLayout.CENTER);

        pnlLeft.add(pnlSearch, java.awt.BorderLayout.PAGE_START);

        lstCharacter.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstCharacter.setFixedCellHeight(110);
        lstCharacter.setFixedCellWidth(110);
        lstCharacter.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        lstCharacter.setVisibleRowCount(-1);
        lstCharacter.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                characterListValueChanged(evt);
            }
        });
        scrCharacter.setViewportView(lstCharacter);

        pnlLeft.add(scrCharacter, java.awt.BorderLayout.CENTER);

        getContentPane().add(pnlLeft, java.awt.BorderLayout.LINE_START);

        tabMain.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 10));

        pnlCharacterTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlCharacterTab.setLayout(new java.awt.BorderLayout(10, 10));

        pnlCharacterFields.setLayout(new java.awt.GridLayout(0, 4, 8, 8));

        lblName.setText("Nome");
        pnlCharacterFields.add(lblName);

        txtName.setColumns(12);
        pnlCharacterFields.add(txtName);

        lblLevel.setText("Nível");
        pnlCharacterFields.add(lblLevel);
        pnlCharacterFields.add(txtLevel);

        lblAge.setText("Idade");
        pnlCharacterFields.add(lblAge);
        pnlCharacterFields.add(txtAge);

        lblRace.setText("Raça");
        pnlCharacterFields.add(lblRace);
        pnlCharacterFields.add(txtRace);

        lblClass.setText("Classe");
        pnlCharacterFields.add(lblClass);
        pnlCharacterFields.add(txtClass);

        lblSubclass.setText("Subclasse");
        pnlCharacterFields.add(lblSubclass);
        pnlCharacterFields.add(txtSubclass);

        lblOrigin.setText("Origem");
        pnlCharacterFields.add(lblOrigin);
        pnlCharacterFields.add(txtOrigin);

        lblLanguages.setText("Idiomas");
        pnlCharacterFields.add(lblLanguages);
        pnlCharacterFields.add(txtLanguages);

        lblHealth.setText("Vida");
        pnlCharacterFields.add(lblHealth);
        pnlCharacterFields.add(txtHealth);

        lblBaseHealth.setText("Vida base");
        pnlCharacterFields.add(lblBaseHealth);
        pnlCharacterFields.add(txtBaseHealth);

        lblMana.setText("Mana");
        pnlCharacterFields.add(lblMana);
        pnlCharacterFields.add(txtMana);

        lblBaseMana.setText("Mana base");
        pnlCharacterFields.add(lblBaseMana);
        pnlCharacterFields.add(txtBaseMana);

        lblStamina.setText("Vigor");
        pnlCharacterFields.add(lblStamina);
        pnlCharacterFields.add(txtStamina);

        lblBaseStamina.setText("Vigor base");
        pnlCharacterFields.add(lblBaseStamina);
        pnlCharacterFields.add(txtBaseStamina);

        lblSanity.setText("Sanidade");
        pnlCharacterFields.add(lblSanity);
        pnlCharacterFields.add(txtSanity);

        lblBaseSanity.setText("Sanidade base");
        pnlCharacterFields.add(lblBaseSanity);
        pnlCharacterFields.add(txtBaseSanity);

        pnlCharacterTab.add(pnlCharacterFields, java.awt.BorderLayout.PAGE_START);

        pnlCharacterButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewCharacter.setText("Novo");
        btnNewCharacter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCharacterButtonActionPerformed(evt);
            }
        });
        pnlCharacterButtons.add(btnNewCharacter);

        btnSaveCharacter.setText("Salvar");
        btnSaveCharacter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCharacterButtonActionPerformed(evt);
            }
        });
        pnlCharacterButtons.add(btnSaveCharacter);

        btnDeleteCharacter.setText("Excluir");
        btnDeleteCharacter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCharacterButtonActionPerformed(evt);
            }
        });
        pnlCharacterButtons.add(btnDeleteCharacter);

        btnChangePhoto.setText("Alterar foto");
        btnChangePhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePhotoButtonActionPerformed(evt);
            }
        });
        pnlCharacterButtons.add(btnChangePhoto);

        pnlCharacterTab.add(pnlCharacterButtons, java.awt.BorderLayout.CENTER);

        tabMain.addTab("Personagem", pnlCharacterTab);

        pnlSkillsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSkillsTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblSkills.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Perícia", "Valor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSkills.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                skillsTableMouseClicked(evt);
            }
        });
        scrSkills.setViewportView(tblSkills);

        pnlSkillsTab.add(scrSkills, java.awt.BorderLayout.CENTER);

        pnlSkillsForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlSkillsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblSkillName.setText("Perícia");
        pnlSkillsFields.add(lblSkillName);

        txtSkillName.setColumns(12);
        pnlSkillsFields.add(txtSkillName);

        lblSkillValue.setText("Valor");
        pnlSkillsFields.add(lblSkillValue);
        pnlSkillsFields.add(txtSkillValue);

        lblSkillDice.setText("Dados");
        pnlSkillsFields.add(lblSkillDice);

        txtSkillDice.setToolTipText("Dados para rolar, por exemplo, 1d20 + 5");
        pnlSkillsFields.add(txtSkillDice);

        pnlSkillsForm.add(pnlSkillsFields, java.awt.BorderLayout.PAGE_START);

        pnlSkillsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewSkill.setText("Novo");
        btnNewSkill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSkillButtonActionPerformed(evt);
            }
        });
        pnlSkillsButtons.add(btnNewSkill);

        btnSaveSkill.setText("Salvar");
        btnSaveSkill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSkillButtonActionPerformed(evt);
            }
        });
        pnlSkillsButtons.add(btnSaveSkill);

        btnDeleteSkill.setText("Excluir");
        btnDeleteSkill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSkillButtonActionPerformed(evt);
            }
        });
        pnlSkillsButtons.add(btnDeleteSkill);

        btnRollSkillDice.setText("Rolar");
        btnRollSkillDice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollSkillDiceButtonActionPerformed(evt);
            }
        });
        pnlSkillsButtons.add(btnRollSkillDice);

        pnlSkillsForm.add(pnlSkillsButtons, java.awt.BorderLayout.CENTER);

        pnlSkillsTab.add(pnlSkillsForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Perícias", pnlSkillsTab);

        pnlSecondSkillsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSecondSkillsTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblSecondSkills.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Perícia secundária", "Valor"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblSecondSkills.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                secondSkillsTableMouseClicked(evt);
            }
        });
        scrSecondSkills.setViewportView(tblSecondSkills);

        pnlSecondSkillsTab.add(scrSecondSkills, java.awt.BorderLayout.CENTER);

        pnlSecondSkillsForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlSecondSkillsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblSecondSkillName.setText("Perícia secundária");
        pnlSecondSkillsFields.add(lblSecondSkillName);

        txtSecondSkillName.setColumns(12);
        pnlSecondSkillsFields.add(txtSecondSkillName);

        lblSecondSkillValue.setText("Valor");
        pnlSecondSkillsFields.add(lblSecondSkillValue);
        pnlSecondSkillsFields.add(txtSecondSkillValue);

        lblSecondSkillDice.setText("Dados");
        pnlSecondSkillsFields.add(lblSecondSkillDice);

        txtSecondSkillDice.setToolTipText("Dados para rolar, por exemplo, 1d20 + 5");
        pnlSecondSkillsFields.add(txtSecondSkillDice);

        pnlSecondSkillsForm.add(pnlSecondSkillsFields, java.awt.BorderLayout.PAGE_START);

        pnlSecondSkillsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewSecondSkill.setText("Novo");
        btnNewSecondSkill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSecondSkillButtonActionPerformed(evt);
            }
        });
        pnlSecondSkillsButtons.add(btnNewSecondSkill);

        btnSaveSecondSkill.setText("Salvar");
        btnSaveSecondSkill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSecondSkillButtonActionPerformed(evt);
            }
        });
        pnlSecondSkillsButtons.add(btnSaveSecondSkill);

        btnDeleteSecondSkill.setText("Excluir");
        btnDeleteSecondSkill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSecondSkillButtonActionPerformed(evt);
            }
        });
        pnlSecondSkillsButtons.add(btnDeleteSecondSkill);

        btnRollSecondSkillDice.setText("Rolar");
        btnRollSecondSkillDice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollSecondSkillDiceButtonActionPerformed(evt);
            }
        });
        pnlSecondSkillsButtons.add(btnRollSecondSkillDice);

        pnlSecondSkillsForm.add(pnlSecondSkillsButtons, java.awt.BorderLayout.CENTER);

        pnlSecondSkillsTab.add(pnlSecondSkillsForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Perícias secundárias", pnlSecondSkillsTab);

        pnlAbilitiesTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlAbilitiesTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblAbilities.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Categoria", "Ação", "Tipo de custo", "Custo", "Aprendida"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAbilities.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                abilitiesTableMouseClicked(evt);
            }
        });
        scrAbilities.setViewportView(tblAbilities);

        pnlAbilitiesTab.add(scrAbilities, java.awt.BorderLayout.CENTER);

        pnlAbilitiesForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlAbilitiesFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblAbilityName.setText("Nome");
        pnlAbilitiesFields.add(lblAbilityName);

        txtAbilityName.setColumns(12);
        pnlAbilitiesFields.add(txtAbilityName);

        lblCategory.setText("Categoria");
        pnlAbilitiesFields.add(lblCategory);
        pnlAbilitiesFields.add(cmbCategory);

        lblActionType.setText("Ação");
        pnlAbilitiesFields.add(lblActionType);
        pnlAbilitiesFields.add(cmbActionType);

        lblCostType.setText("Tipo de custo");
        pnlAbilitiesFields.add(lblCostType);
        pnlAbilitiesFields.add(cmbCostType);

        lblCost.setText("Custo");
        pnlAbilitiesFields.add(lblCost);
        pnlAbilitiesFields.add(txtCost);

        lblAbilityDescription.setText("Descrição");
        pnlAbilitiesFields.add(lblAbilityDescription);
        pnlAbilitiesFields.add(txtAbilityDescription);

        pnlAbilitiesForm.add(pnlAbilitiesFields, java.awt.BorderLayout.PAGE_START);

        pnlAbilitiesButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewAbility.setText("Novo");
        btnNewAbility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAbilityButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnNewAbility);

        btnSaveAbility.setText("Salvar");
        btnSaveAbility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAbilityButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnSaveAbility);

        btnDeleteAbility.setText("Excluir");
        btnDeleteAbility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAbilityButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnDeleteAbility);

        btnLearn.setText("Aprender");
        btnLearn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                learnButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnLearn);

        btnForget.setText("Esquecer");
        btnForget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forgetButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnForget);

        btnRollDamage.setText("Rolar dano");
        btnRollDamage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollDamageButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnRollDamage);

        pnlAbilitiesForm.add(pnlAbilitiesButtons, java.awt.BorderLayout.CENTER);

        pnlAbilitiesTab.add(pnlAbilitiesForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Habilidades", pnlAbilitiesTab);

        pnlInventoryTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlInventoryTab.setLayout(new java.awt.BorderLayout(10, 10));

        pnlInventoryCategory.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblInventoryCategory.setText("Categoria:");
        pnlInventoryCategory.add(lblInventoryCategory);
        pnlInventoryCategory.add(cmbInventoryCategory);

        pnlInventoryTab.add(pnlInventoryCategory, java.awt.BorderLayout.PAGE_START);

        tblInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Categoria", "Quantidade", "Equipado", "Peso"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblInventory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inventoryTableMouseClicked(evt);
            }
        });
        scrInventory.setViewportView(tblInventory);

        pnlInventoryTab.add(scrInventory, java.awt.BorderLayout.CENTER);

        pnlInventoryForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlInventoryFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblItem.setText("Item");
        pnlInventoryFields.add(lblItem);
        pnlInventoryFields.add(cmbItem);

        lblQuantity.setText("Quantidade");
        pnlInventoryFields.add(lblQuantity);
        pnlInventoryFields.add(txtQuantity);

        lblEquipped.setText("Equipado");
        pnlInventoryFields.add(lblEquipped);
        pnlInventoryFields.add(chkEquipped);

        pnlInventoryForm.add(pnlInventoryFields, java.awt.BorderLayout.PAGE_START);

        pnlInventoryButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnSaveEntry.setText("Salvar");
        btnSaveEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEntryButtonActionPerformed(evt);
            }
        });
        pnlInventoryButtons.add(btnSaveEntry);

        btnDeleteEntry.setText("Excluir");
        btnDeleteEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEntryButtonActionPerformed(evt);
            }
        });
        pnlInventoryButtons.add(btnDeleteEntry);

        pnlInventoryForm.add(pnlInventoryButtons, java.awt.BorderLayout.CENTER);

        pnlInventoryTab.add(pnlInventoryForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Inventário", pnlInventoryTab);

        pnlItemsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlItemsTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblItems.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Peso", "Descrição"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                itemsTableMouseClicked(evt);
            }
        });
        scrItems.setViewportView(tblItems);

        pnlItemsTab.add(scrItems, java.awt.BorderLayout.CENTER);

        pnlItemsForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlItemsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblItemName.setText("Nome");
        pnlItemsFields.add(lblItemName);

        txtItemName.setColumns(12);
        pnlItemsFields.add(txtItemName);

        lblItemWeight.setText("Peso");
        pnlItemsFields.add(lblItemWeight);
        pnlItemsFields.add(txtItemWeight);

        lblItemDescription.setText("Descrição");
        pnlItemsFields.add(lblItemDescription);
        pnlItemsFields.add(txtItemDescription);

        pnlItemsForm.add(pnlItemsFields, java.awt.BorderLayout.PAGE_START);

        pnlItemsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewItem.setText("Novo");
        btnNewItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newItemButtonActionPerformed(evt);
            }
        });
        pnlItemsButtons.add(btnNewItem);

        btnSaveItem.setText("Salvar");
        btnSaveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveItemButtonActionPerformed(evt);
            }
        });
        pnlItemsButtons.add(btnSaveItem);

        btnDeleteItem.setText("Excluir");
        btnDeleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteItemButtonActionPerformed(evt);
            }
        });
        pnlItemsButtons.add(btnDeleteItem);

        pnlItemsForm.add(pnlItemsButtons, java.awt.BorderLayout.CENTER);

        pnlItemsTab.add(pnlItemsForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Itens", pnlItemsTab);

        pnlWeaponsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlWeaponsTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblWeapons.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Dano", "Atributo", "Margem crítica", "Multiplicador crítico", "Peso"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblWeapons.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                weaponsTableMouseClicked(evt);
            }
        });
        scrWeapons.setViewportView(tblWeapons);

        pnlWeaponsTab.add(scrWeapons, java.awt.BorderLayout.CENTER);

        pnlWeaponsForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlWeaponsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblWeaponName.setText("Nome");
        pnlWeaponsFields.add(lblWeaponName);

        txtWeaponName.setColumns(12);
        pnlWeaponsFields.add(txtWeaponName);

        lblWeaponWeight.setText("Peso");
        pnlWeaponsFields.add(lblWeaponWeight);
        pnlWeaponsFields.add(txtWeaponWeight);

        lblWeaponAttribute.setText("Atributo");
        pnlWeaponsFields.add(lblWeaponAttribute);
        pnlWeaponsFields.add(txtWeaponAttribute);

        lblWeaponDamage.setText("Dano");
        pnlWeaponsFields.add(lblWeaponDamage);
        pnlWeaponsFields.add(txtWeaponDamage);

        lblWeaponRange.setText("Margem crítica");
        pnlWeaponsFields.add(lblWeaponRange);
        pnlWeaponsFields.add(txtWeaponRange);

        lblWeaponMultiplier.setText("Multiplicador crítico");
        pnlWeaponsFields.add(lblWeaponMultiplier);
        pnlWeaponsFields.add(txtWeaponMultiplier);

        lblWeaponDescription.setText("Descrição");
        pnlWeaponsFields.add(lblWeaponDescription);
        pnlWeaponsFields.add(txtWeaponDescription);

        lblWeaponDice.setText("Dados");
        pnlWeaponsFields.add(lblWeaponDice);

        txtWeaponDice.setToolTipText("Dados para rolar, por exemplo, 1d20 + 5");
        pnlWeaponsFields.add(txtWeaponDice);

        pnlWeaponsForm.add(pnlWeaponsFields, java.awt.BorderLayout.PAGE_START);

        pnlWeaponsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewWeapon.setText("Novo");
        btnNewWeapon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newWeaponButtonActionPerformed(evt);
            }
        });
        pnlWeaponsButtons.add(btnNewWeapon);

        btnSaveWeapon.setText("Salvar");
        btnSaveWeapon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveWeaponButtonActionPerformed(evt);
            }
        });
        pnlWeaponsButtons.add(btnSaveWeapon);

        btnDeleteWeapon.setText("Excluir");
        btnDeleteWeapon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteWeaponButtonActionPerformed(evt);
            }
        });
        pnlWeaponsButtons.add(btnDeleteWeapon);

        btnRollWeaponDamage.setText("Rolar dano");
        btnRollWeaponDamage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollWeaponDamageButtonActionPerformed(evt);
            }
        });
        pnlWeaponsButtons.add(btnRollWeaponDamage);

        btnRollWeaponCritical.setText("Rolar crítico");
        btnRollWeaponCritical.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollWeaponCriticalButtonActionPerformed(evt);
            }
        });
        pnlWeaponsButtons.add(btnRollWeaponCritical);

        btnRollWeaponDice.setText("Rolar");
        btnRollWeaponDice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollWeaponDiceButtonActionPerformed(evt);
            }
        });
        pnlWeaponsButtons.add(btnRollWeaponDice);

        pnlWeaponsForm.add(pnlWeaponsButtons, java.awt.BorderLayout.CENTER);

        pnlWeaponsTab.add(pnlWeaponsForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Armas", pnlWeaponsTab);

        pnlArmorTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlArmorTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblArmor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "CA física", "CA elemental", "Peso"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblArmor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                armorTableMouseClicked(evt);
            }
        });
        scrArmor.setViewportView(tblArmor);

        pnlArmorTab.add(scrArmor, java.awt.BorderLayout.CENTER);

        pnlArmorForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlArmorFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblArmorName.setText("Nome");
        pnlArmorFields.add(lblArmorName);

        txtArmorName.setColumns(12);
        pnlArmorFields.add(txtArmorName);

        lblArmorWeight.setText("Peso");
        pnlArmorFields.add(lblArmorWeight);
        pnlArmorFields.add(txtArmorWeight);

        lblPhysicalAc.setText("CA física");
        pnlArmorFields.add(lblPhysicalAc);
        pnlArmorFields.add(txtPhysicalAc);

        lblElementalAc.setText("CA elemental");
        pnlArmorFields.add(lblElementalAc);
        pnlArmorFields.add(txtElementalAc);

        lblArmorDescription.setText("Descrição");
        pnlArmorFields.add(lblArmorDescription);
        pnlArmorFields.add(txtArmorDescription);

        pnlArmorForm.add(pnlArmorFields, java.awt.BorderLayout.PAGE_START);

        pnlArmorButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewArmor.setText("Novo");
        btnNewArmor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newArmorButtonActionPerformed(evt);
            }
        });
        pnlArmorButtons.add(btnNewArmor);

        btnSaveArmor.setText("Salvar");
        btnSaveArmor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveArmorButtonActionPerformed(evt);
            }
        });
        pnlArmorButtons.add(btnSaveArmor);

        btnDeleteArmor.setText("Excluir");
        btnDeleteArmor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteArmorButtonActionPerformed(evt);
            }
        });
        pnlArmorButtons.add(btnDeleteArmor);

        pnlArmorForm.add(pnlArmorButtons, java.awt.BorderLayout.CENTER);

        pnlArmorTab.add(pnlArmorForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Armaduras", pnlArmorTab);

        pnlAmmunitionTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlAmmunitionTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblAmmunition.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Dano", "Peso"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblAmmunition.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ammunitionTableMouseClicked(evt);
            }
        });
        scrAmmunition.setViewportView(tblAmmunition);

        pnlAmmunitionTab.add(scrAmmunition, java.awt.BorderLayout.CENTER);

        pnlAmmunitionForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlAmmunitionFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblAmmoName.setText("Nome");
        pnlAmmunitionFields.add(lblAmmoName);

        txtAmmoName.setColumns(12);
        pnlAmmunitionFields.add(txtAmmoName);

        lblAmmoWeight.setText("Peso");
        pnlAmmunitionFields.add(lblAmmoWeight);
        pnlAmmunitionFields.add(txtAmmoWeight);

        lblAmmoDamage.setText("Dano");
        pnlAmmunitionFields.add(lblAmmoDamage);
        pnlAmmunitionFields.add(txtAmmoDamage);

        lblAmmoDescription.setText("Descrição");
        pnlAmmunitionFields.add(lblAmmoDescription);
        pnlAmmunitionFields.add(txtAmmoDescription);

        lblAmmoDice.setText("Dados");
        pnlAmmunitionFields.add(lblAmmoDice);

        txtAmmoDice.setToolTipText("Dados para rolar, por exemplo, 1d20 + 5");
        pnlAmmunitionFields.add(txtAmmoDice);

        pnlAmmunitionForm.add(pnlAmmunitionFields, java.awt.BorderLayout.PAGE_START);

        pnlAmmunitionButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewAmmo.setText("Novo");
        btnNewAmmo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAmmoButtonActionPerformed(evt);
            }
        });
        pnlAmmunitionButtons.add(btnNewAmmo);

        btnSaveAmmo.setText("Salvar");
        btnSaveAmmo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAmmoButtonActionPerformed(evt);
            }
        });
        pnlAmmunitionButtons.add(btnSaveAmmo);

        btnDeleteAmmo.setText("Excluir");
        btnDeleteAmmo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAmmoButtonActionPerformed(evt);
            }
        });
        pnlAmmunitionButtons.add(btnDeleteAmmo);

        btnRollAmmoDamage.setText("Rolar dano");
        btnRollAmmoDamage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollAmmoDamageButtonActionPerformed(evt);
            }
        });
        pnlAmmunitionButtons.add(btnRollAmmoDamage);

        btnRollAmmoDice.setText("Rolar");
        btnRollAmmoDice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollAmmoDiceButtonActionPerformed(evt);
            }
        });
        pnlAmmunitionButtons.add(btnRollAmmoDice);

        pnlAmmunitionForm.add(pnlAmmunitionButtons, java.awt.BorderLayout.CENTER);

        pnlAmmunitionTab.add(pnlAmmunitionForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Munições", pnlAmmunitionTab);

        getContentPane().add(tabMain, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
        fillGallery();
    }//GEN-LAST:event_searchFieldKeyReleased

    private void characterListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_characterListValueChanged
        PlayerCharacter chosen = (PlayerCharacter) lstCharacter.getSelectedValue();
        if (evt.getValueIsAdjusting() || chosen == null || chosen == selected) {
            return;
        }
        selected = chosen;
        showCharacter();
    }//GEN-LAST:event_characterListValueChanged

    private void newCharacterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCharacterButtonActionPerformed
        clearCharacter();
        txtName.requestFocus();
    }//GEN-LAST:event_newCharacterButtonActionPerformed

    private void saveCharacterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCharacterButtonActionPerformed
        try {
            PlayerCharacter character = new PlayerCharacter(selected == null ? null : selected.getId(),
                    number(txtLevel, "Nível"), txtName.getText(), number(txtAge, "Idade"), txtRace.getText(),
                    txtClass.getText(), txtSubclass.getText(), txtOrigin.getText(), txtLanguages.getText(),
                    number(txtBaseHealth, "Vida base"), number(txtHealth, "Vida"),
                    number(txtBaseMana, "Mana base"), number(txtMana, "Mana"),
                    number(txtBaseStamina, "Vigor base"), number(txtStamina, "Vigor"),
                    number(txtBaseSanity, "Sanidade base"), number(txtSanity, "Sanidade"));
            character.setPhoto(selected == null ? null : selected.getPhoto());
            service.saveCharacter(character);
            loadCharacters();
            selectInGallery(character.getId());
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveCharacterButtonActionPerformed

    private void deleteCharacterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCharacterButtonActionPerformed
        if (selected == null) {
            showMessage("Selecione um personagem primeiro.");
            return;
        }
        if (!confirm("Excluir " + selected.getName() + "?")) {
            return;
        }
        try {
            service.deleteCharacter(selected.getId());
            loadCharacters();
            clearCharacter();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteCharacterButtonActionPerformed

private void changePhotoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePhotoButtonActionPerformed
        if (selected == null) {
            showMessage("Selecione um personagem primeiro.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Selecione uma foto do personagem");
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            service.saveCharacterPhoto(selected, chooser.getSelectedFile());
            loadCharacters();
            selectInGallery(selected.getId());
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_changePhotoButtonActionPerformed

    private void skillsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_skillsTableMouseClicked
        int row = tblSkills.getSelectedRow();
        if (row < 0) {
            return;
        }
        Skill skill = skills.get(row);
        txtSkillName.setText(skill.getSkillName());
        txtSkillValue.setText(String.valueOf(skill.getValue()));
    }//GEN-LAST:event_skillsTableMouseClicked

    private void newSkillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSkillButtonActionPerformed
        clearSkillForm();
    }//GEN-LAST:event_newSkillButtonActionPerformed

    private void saveSkillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSkillButtonActionPerformed
        if (selected == null) {
            showMessage("Selecione um personagem primeiro.");
            return;
        }
        try {
            int row = tblSkills.getSelectedRow();
            Integer id = row >= 0 ? skills.get(row).getId() : null;
            service.saveSkill(new Skill(id, selected.getId(), txtSkillName.getText(), number(txtSkillValue, "Valor")));
            loadSkills();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveSkillButtonActionPerformed

    private void deleteSkillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSkillButtonActionPerformed
        int row = tblSkills.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione uma perícia na tabela primeiro.");
            return;
        }
        if (!confirm("Excluir a perícia " + skills.get(row).getSkillName() + "?")) {
            return;
        }
        try {
            service.deleteSkill(skills.get(row).getId());
            loadSkills();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteSkillButtonActionPerformed

    // Rolls the dice typed in the "Dados" field of this tab.
    private void rollSkillDiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollSkillDiceButtonActionPerformed
        rollCustomDice(txtSkillDice, txtSkillName.getText());
    }//GEN-LAST:event_rollSkillDiceButtonActionPerformed

    private void secondSkillsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_secondSkillsTableMouseClicked
        int row = tblSecondSkills.getSelectedRow();
        if (row < 0) {
            return;
        }
        SecondSkill secondSkill = secondSkills.get(row);
        txtSecondSkillName.setText(secondSkill.getSecondSkillName());
        txtSecondSkillValue.setText(String.valueOf(secondSkill.getValue()));
    }//GEN-LAST:event_secondSkillsTableMouseClicked

    private void newSecondSkillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSecondSkillButtonActionPerformed
        clearSecondSkillForm();
    }//GEN-LAST:event_newSecondSkillButtonActionPerformed

    private void saveSecondSkillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSecondSkillButtonActionPerformed
        if (selected == null) {
            showMessage("Selecione um personagem primeiro.");
            return;
        }
        try {
            int row = tblSecondSkills.getSelectedRow();
            Integer id = row >= 0 ? secondSkills.get(row).getId() : null;
            service.saveSecondSkill(new SecondSkill(id, selected.getId(), txtSecondSkillName.getText(), number(txtSecondSkillValue, "Valor")));
            loadSecondSkills();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveSecondSkillButtonActionPerformed

    private void deleteSecondSkillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSecondSkillButtonActionPerformed
        int row = tblSecondSkills.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione uma perícia secundária na tabela primeiro.");
            return;
        }
        if (!confirm("Excluir a perícia secundária " + secondSkills.get(row).getSecondSkillName() + "?")) {
            return;
        }
        try {
            service.deleteSecondSkill(secondSkills.get(row).getId());
            loadSecondSkills();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteSecondSkillButtonActionPerformed

    // Rolls the dice typed in the "Dados" field of this tab.
    private void rollSecondSkillDiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollSecondSkillDiceButtonActionPerformed
        rollCustomDice(txtSecondSkillDice, txtSecondSkillName.getText());
    }//GEN-LAST:event_rollSecondSkillDiceButtonActionPerformed

    private void abilitiesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_abilitiesTableMouseClicked
        int row = tblAbilities.getSelectedRow();
        if (row < 0) {
            return;
        }
        Ability ability = abilities.get(row);
        txtAbilityName.setText(ability.getName());
        cmbCategory.setSelectedItem(ability.getCategory());
        cmbActionType.setSelectedItem(ability.getActionType());
        cmbCostType.setSelectedItem(ability.getCostType());
        txtCost.setText(String.valueOf(ability.getCostValue()));
        txtAbilityDescription.setText(ability.getDescription());
    }//GEN-LAST:event_abilitiesTableMouseClicked

    private void newAbilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAbilityButtonActionPerformed
        clearAbilityForm();
    }//GEN-LAST:event_newAbilityButtonActionPerformed

    private void saveAbilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAbilityButtonActionPerformed
        try {
            int row = tblAbilities.getSelectedRow();
            Integer id = row >= 0 ? abilities.get(row).getAbilityId() : null;
            service.saveAbility(new Ability(id, txtAbilityName.getText(), (AbilityCategory) cmbCategory.getSelectedItem(),
                    (ActionType) cmbActionType.getSelectedItem(), (CostType) cmbCostType.getSelectedItem(),
                    number(txtCost, "Custo"), txtAbilityDescription.getText()));
            loadAbilities();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveAbilityButtonActionPerformed

    private void deleteAbilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAbilityButtonActionPerformed
        int row = tblAbilities.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione uma habilidade na tabela primeiro.");
            return;
        }
        if (!confirm("Excluir a habilidade " + abilities.get(row).getName() + "?")) {
            return;
        }
        try {
            service.deleteAbility(abilities.get(row).getAbilityId());
            loadAbilities();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteAbilityButtonActionPerformed

    private void learnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_learnButtonActionPerformed
        int row = tblAbilities.getSelectedRow();
        if (selected == null || row < 0) {
            showMessage("Selecione um personagem e uma habilidade na tabela primeiro.");
            return;
        }
        try {
            service.learnAbility(selected.getId(), abilities.get(row).getAbilityId());
            loadAbilities();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_learnButtonActionPerformed

    private void forgetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forgetButtonActionPerformed
        int row = tblAbilities.getSelectedRow();
        if (selected == null || row < 0) {
            showMessage("Selecione um personagem e uma habilidade na tabela primeiro.");
            return;
        }
        try {
            service.forgetAbility(selected.getId(), abilities.get(row).getAbilityId());
            loadAbilities();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_forgetButtonActionPerformed

    /**
     * Rolls the damage of the ability clicked in the table.
     * <p>
     * Abilities have no damage field, only a free description, so the damage dice are
     * searched inside the description ("Deals 2d6 + 3 fire damage" gives "2d6 + 3").
     * See {@link DiceRoller#findDamageExpression(String)} for how the expression is chosen.
     */
    private void rollDamageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollDamageButtonActionPerformed
        int row = tblAbilities.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione uma habilidade na tabela primeiro.");
            return;
        }
        Ability ability = abilities.get(row);

        // 1. Find the damage dice written in the description.
        String expression = DiceRoller.findDamageExpression(ability.getDescription());
        if (expression == null) {
            showMessage("A descrição de " + ability.getName() + " não possui dados de dano (como 2d6 + 3).");
            return;
        }

        // 2. Roll them and show the result.
        try {
            int damage = new DiceRoller().rollDice(expression);
            showMessage(ability.getName() + "\nDamage: " + expression + " = " + damage);
        } catch (RuntimeException e) {
            showInvalidRoll(expression);
        }
    }//GEN-LAST:event_rollDamageButtonActionPerformed

    private void inventoryTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryTableMouseClicked
        int row = tblInventory.getSelectedRow();
        if (row < 0) {
            return;
        }
        Inventory entry = inventory.get(row);
        cmbItem.setSelectedIndex(inventoryItems.indexOf(findItem(entry.getItemId())));
        txtQuantity.setText(String.valueOf(entry.getQuantity()));
        chkEquipped.setSelected(entry.isEquipped());
    }//GEN-LAST:event_inventoryTableMouseClicked

    private void saveEntryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEntryButtonActionPerformed
        if (selected == null || cmbItem.getSelectedIndex() < 0) {
            showMessage("Selecione um personagem e escolha um item primeiro.");
            return;
        }
        try {
            int row = tblInventory.getSelectedRow();
            Integer id = row >= 0 ? inventory.get(row).getId() : null;
            int itemId = inventoryItems.get(cmbItem.getSelectedIndex()).getId();
            service.saveInventory(new Inventory(id, selected.getId(), itemId, number(txtQuantity, "Quantidade"), chkEquipped.isSelected()));
            loadInventory();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveEntryButtonActionPerformed

    private void deleteEntryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEntryButtonActionPerformed
        int row = tblInventory.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione um item na tabela primeiro.");
            return;
        }
        if (!confirm("Remover este item do inventário?")) {
            return;
        }
        try {
            service.deleteInventory(inventory.get(row).getId());
            loadInventory();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteEntryButtonActionPerformed

    private void itemsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_itemsTableMouseClicked
        int row = tblItems.getSelectedRow();
        if (row < 0) {
            return;
        }
        Item item = generalItems.get(row);
        txtItemName.setText(item.getName());
        txtItemWeight.setText(String.valueOf(item.getWeight()));
        txtItemDescription.setText(item.getDescription());
    }//GEN-LAST:event_itemsTableMouseClicked

    private void newItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newItemButtonActionPerformed
        clearItemForm();
    }//GEN-LAST:event_newItemButtonActionPerformed

    private void saveItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveItemButtonActionPerformed
        try {
            int row = tblItems.getSelectedRow();
            Integer id = row >= 0 ? generalItems.get(row).getId() : null;
            service.saveGeneralItem(new Item(id, txtItemName.getText(), txtItemDescription.getText(),
                    decimal(txtItemWeight, "Peso"), ItemCategory.GENERAL));
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveItemButtonActionPerformed

    private void deleteItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteItemButtonActionPerformed
        int row = tblItems.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione um item na tabela primeiro.");
            return;
        }
        if (!confirm("Excluir o item " + generalItems.get(row).getName() + "?")) {
            return;
        }
        try {
            service.deleteItem(generalItems.get(row).getId());
            loadItems();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteItemButtonActionPerformed

    private void weaponsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_weaponsTableMouseClicked
        int row = tblWeapons.getSelectedRow();
        if (row < 0) {
            return;
        }
        Weapon weapon = weapons.get(row);
        txtWeaponName.setText(weapon.getName());
        txtWeaponWeight.setText(String.valueOf(weapon.getWeight()));
        txtWeaponAttribute.setText(weapon.getScalingAttribute());
        txtWeaponDamage.setText(weapon.getDamageDice());
        txtWeaponRange.setText(String.valueOf(weapon.getCriticalRange()));
        txtWeaponMultiplier.setText(String.valueOf(weapon.getCriticalMultiplier()));
        txtWeaponDescription.setText(weapon.getDescription());
    }//GEN-LAST:event_weaponsTableMouseClicked

    private void newWeaponButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newWeaponButtonActionPerformed
        clearWeaponForm();
    }//GEN-LAST:event_newWeaponButtonActionPerformed

    private void saveWeaponButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveWeaponButtonActionPerformed
        try {
            int row = tblWeapons.getSelectedRow();
            Integer id = row >= 0 ? weapons.get(row).getId() : null;
            service.saveWeapon(new Weapon(id, txtWeaponName.getText(), txtWeaponDescription.getText(),
                    decimal(txtWeaponWeight, "Peso"), txtWeaponAttribute.getText(),
                    txtWeaponDamage.getText(), number(txtWeaponRange, "Margem crítica"),
                    number(txtWeaponMultiplier, "Multiplicador crítico")));
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveWeaponButtonActionPerformed

    private void deleteWeaponButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteWeaponButtonActionPerformed
        int row = tblWeapons.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione uma arma na tabela primeiro.");
            return;
        }
        if (!confirm("Excluir a arma " + weapons.get(row).getName() + "?")) {
            return;
        }
        try {
            service.deleteItem(weapons.get(row).getId());
            loadItems();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteWeaponButtonActionPerformed

    private void rollWeaponDamageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollWeaponDamageButtonActionPerformed
        rollWeaponDamage(false);
    }//GEN-LAST:event_rollWeaponDamageButtonActionPerformed

    private void rollWeaponCriticalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollWeaponCriticalButtonActionPerformed
        rollWeaponDamage(true);
    }//GEN-LAST:event_rollWeaponCriticalButtonActionPerformed

    private void rollWeaponDiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollWeaponDiceButtonActionPerformed
        rollCustomDice(txtWeaponDice, txtWeaponName.getText());
    }//GEN-LAST:event_rollWeaponDiceButtonActionPerformed

    private void armorTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_armorTableMouseClicked
        int row = tblArmor.getSelectedRow();
        if (row < 0) {
            return;
        }
        Armor armor = armors.get(row);
        txtArmorName.setText(armor.getName());
        txtArmorWeight.setText(String.valueOf(armor.getWeight()));
        txtPhysicalAc.setText(String.valueOf(armor.getPhysicalAC()));
        txtElementalAc.setText(String.valueOf(armor.getElementalAC()));
        txtArmorDescription.setText(armor.getDescription());
    }//GEN-LAST:event_armorTableMouseClicked

    private void newArmorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newArmorButtonActionPerformed
        clearArmorForm();
    }//GEN-LAST:event_newArmorButtonActionPerformed

    private void saveArmorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveArmorButtonActionPerformed
        try {
            int row = tblArmor.getSelectedRow();
            Integer id = row >= 0 ? armors.get(row).getId() : null;
            service.saveArmor(new Armor(id, txtArmorName.getText(), txtArmorDescription.getText(),
                    decimal(txtArmorWeight, "Peso"), number(txtPhysicalAc, "CA física"),
                    number(txtElementalAc, "CA elemental")));
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveArmorButtonActionPerformed

    private void deleteArmorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteArmorButtonActionPerformed
        int row = tblArmor.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione uma armadura na tabela primeiro.");
            return;
        }
        if (!confirm("Excluir a armadura " + armors.get(row).getName() + "?")) {
            return;
        }
        try {
            service.deleteItem(armors.get(row).getId());
            loadItems();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteArmorButtonActionPerformed

    private void ammunitionTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ammunitionTableMouseClicked
        int row = tblAmmunition.getSelectedRow();
        if (row < 0) {
            return;
        }
        Ammunition ammo = ammunition.get(row);
        txtAmmoName.setText(ammo.getName());
        txtAmmoWeight.setText(String.valueOf(ammo.getWeight()));
        txtAmmoDamage.setText(ammo.getDamageDice());
        txtAmmoDescription.setText(ammo.getDescription());
    }//GEN-LAST:event_ammunitionTableMouseClicked

    private void newAmmoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAmmoButtonActionPerformed
        clearAmmoForm();
    }//GEN-LAST:event_newAmmoButtonActionPerformed

    private void saveAmmoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAmmoButtonActionPerformed
        try {
            int row = tblAmmunition.getSelectedRow();
            Integer id = row >= 0 ? ammunition.get(row).getId() : null;
            service.saveAmmunition(new Ammunition(id, txtAmmoName.getText(), txtAmmoDescription.getText(),
                    decimal(txtAmmoWeight, "Peso"), txtAmmoDamage.getText()));
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveAmmoButtonActionPerformed

    private void deleteAmmoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAmmoButtonActionPerformed
        int row = tblAmmunition.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione uma munição na tabela primeiro.");
            return;
        }
        if (!confirm("Excluir a munição " + ammunition.get(row).getName() + "?")) {
            return;
        }
        try {
            service.deleteItem(ammunition.get(row).getId());
            loadItems();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteAmmoButtonActionPerformed

    // Rolls the damage of the ammunition clicked in the table.
    private void rollAmmoDamageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollAmmoDamageButtonActionPerformed
        int row = tblAmmunition.getSelectedRow();
        if (row < 0) {
            showMessage("Selecione uma munição na tabela primeiro.");
            return;
        }
        Ammunition ammo = ammunition.get(row);

        String expression = DiceRoller.findDamageExpression(ammo.getDamageDice());
        if (expression == null && ammo.getDamageDice() != null && !ammo.getDamageDice().trim().isEmpty()) {
            expression = ammo.getDamageDice().trim();
        }
        if (expression == null) {
            showMessage("A munição " + ammo.getName() + " não possui dados de dano (como 1d6 + 1).");
            return;
        }

        try {
            int damage = new DiceRoller().rollDice(expression);
            showMessage(ammo.getName() + "\nDamage: " + expression + " = " + damage);
        } catch (RuntimeException e) {
            showInvalidRoll(expression);
        }
    }//GEN-LAST:event_rollAmmoDamageButtonActionPerformed

    // Rolls the custom dice expression typed in the "Dados" field of the Ammunition tab.
    private void rollAmmoDiceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollAmmoDiceButtonActionPerformed
        rollCustomDice(txtAmmoDice, txtAmmoName.getText());
    }//GEN-LAST:event_rollAmmoDiceButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChangePhoto;
    private javax.swing.JButton btnDeleteAbility;
    private javax.swing.JButton btnDeleteAmmo;
    private javax.swing.JButton btnDeleteArmor;
    private javax.swing.JButton btnDeleteCharacter;
    private javax.swing.JButton btnDeleteEntry;
    private javax.swing.JButton btnDeleteItem;
    private javax.swing.JButton btnDeleteSecondSkill;
    private javax.swing.JButton btnDeleteSkill;
    private javax.swing.JButton btnDeleteWeapon;
    private javax.swing.JButton btnForget;
    private javax.swing.JButton btnLearn;
    private javax.swing.JButton btnNewAbility;
    private javax.swing.JButton btnNewAmmo;
    private javax.swing.JButton btnNewArmor;
    private javax.swing.JButton btnNewCharacter;
    private javax.swing.JButton btnNewItem;
    private javax.swing.JButton btnNewSecondSkill;
    private javax.swing.JButton btnNewSkill;
    private javax.swing.JButton btnNewWeapon;
    private javax.swing.JButton btnRollAmmoDamage;
    private javax.swing.JButton btnRollAmmoDice;
    private javax.swing.JButton btnRollDamage;
    private javax.swing.JButton btnRollSecondSkillDice;
    private javax.swing.JButton btnRollSkillDice;
    private javax.swing.JButton btnRollWeaponCritical;
    private javax.swing.JButton btnRollWeaponDamage;
    private javax.swing.JButton btnRollWeaponDice;
    private javax.swing.JButton btnSaveAbility;
    private javax.swing.JButton btnSaveAmmo;
    private javax.swing.JButton btnSaveArmor;
    private javax.swing.JButton btnSaveCharacter;
    private javax.swing.JButton btnSaveEntry;
    private javax.swing.JButton btnSaveItem;
    private javax.swing.JButton btnSaveSecondSkill;
    private javax.swing.JButton btnSaveSkill;
    private javax.swing.JButton btnSaveWeapon;
    private javax.swing.JCheckBox chkEquipped;
    private javax.swing.JComboBox<com.edynu.rpgSheetManager.model.ActionType> cmbActionType;
    private javax.swing.JComboBox<com.edynu.rpgSheetManager.model.AbilityCategory> cmbCategory;
    private javax.swing.JComboBox<com.edynu.rpgSheetManager.model.CostType> cmbCostType;
    private javax.swing.JComboBox<com.edynu.rpgSheetManager.model.ItemCategory> cmbInventoryCategory;
    private javax.swing.JComboBox<String> cmbItem;
    private javax.swing.JLabel lblAbilityDescription;
    private javax.swing.JLabel lblAbilityName;
    private javax.swing.JLabel lblActionType;
    private javax.swing.JLabel lblAge;
    private javax.swing.JLabel lblAmmoDamage;
    private javax.swing.JLabel lblAmmoDescription;
    private javax.swing.JLabel lblAmmoDice;
    private javax.swing.JLabel lblAmmoName;
    private javax.swing.JLabel lblAmmoWeight;
    private javax.swing.JLabel lblArmorDescription;
    private javax.swing.JLabel lblArmorName;
    private javax.swing.JLabel lblArmorWeight;
    private javax.swing.JLabel lblBaseHealth;
    private javax.swing.JLabel lblBaseMana;
    private javax.swing.JLabel lblBaseSanity;
    private javax.swing.JLabel lblBaseStamina;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblClass;
    private javax.swing.JLabel lblCost;
    private javax.swing.JLabel lblCostType;
    private javax.swing.JLabel lblElementalAc;
    private javax.swing.JLabel lblEquipped;
    private javax.swing.JLabel lblHealth;
    private javax.swing.JLabel lblInventoryCategory;
    private javax.swing.JLabel lblItem;
    private javax.swing.JLabel lblItemDescription;
    private javax.swing.JLabel lblItemName;
    private javax.swing.JLabel lblItemWeight;
    private javax.swing.JLabel lblLanguages;
    private javax.swing.JLabel lblLevel;
    private javax.swing.JLabel lblMana;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblOrigin;
    private javax.swing.JLabel lblPhysicalAc;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblRace;
    private javax.swing.JLabel lblSanity;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblSecondSkillDice;
    private javax.swing.JLabel lblSecondSkillName;
    private javax.swing.JLabel lblSecondSkillValue;
    private javax.swing.JLabel lblSkillDice;
    private javax.swing.JLabel lblSkillName;
    private javax.swing.JLabel lblSkillValue;
    private javax.swing.JLabel lblStamina;
    private javax.swing.JLabel lblSubclass;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblWeaponAttribute;
    private javax.swing.JLabel lblWeaponDamage;
    private javax.swing.JLabel lblWeaponDescription;
    private javax.swing.JLabel lblWeaponDice;
    private javax.swing.JLabel lblWeaponMultiplier;
    private javax.swing.JLabel lblWeaponName;
    private javax.swing.JLabel lblWeaponRange;
    private javax.swing.JLabel lblWeaponWeight;
    private javax.swing.JList<com.edynu.rpgSheetManager.model.PlayerCharacter> lstCharacter;
    private javax.swing.JPanel pnlAbilitiesButtons;
    private javax.swing.JPanel pnlAbilitiesFields;
    private javax.swing.JPanel pnlAbilitiesForm;
    private javax.swing.JPanel pnlAbilitiesTab;
    private javax.swing.JPanel pnlAmmunitionButtons;
    private javax.swing.JPanel pnlAmmunitionFields;
    private javax.swing.JPanel pnlAmmunitionForm;
    private javax.swing.JPanel pnlAmmunitionTab;
    private javax.swing.JPanel pnlArmorButtons;
    private javax.swing.JPanel pnlArmorFields;
    private javax.swing.JPanel pnlArmorForm;
    private javax.swing.JPanel pnlArmorTab;
    private javax.swing.JPanel pnlCharacterButtons;
    private javax.swing.JPanel pnlCharacterFields;
    private javax.swing.JPanel pnlCharacterTab;
    private javax.swing.JPanel pnlInventoryButtons;
    private javax.swing.JPanel pnlInventoryCategory;
    private javax.swing.JPanel pnlInventoryFields;
    private javax.swing.JPanel pnlInventoryForm;
    private javax.swing.JPanel pnlInventoryTab;
    private javax.swing.JPanel pnlItemsButtons;
    private javax.swing.JPanel pnlItemsFields;
    private javax.swing.JPanel pnlItemsForm;
    private javax.swing.JPanel pnlItemsTab;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlSearch;
    private javax.swing.JPanel pnlSecondSkillsButtons;
    private javax.swing.JPanel pnlSecondSkillsFields;
    private javax.swing.JPanel pnlSecondSkillsForm;
    private javax.swing.JPanel pnlSecondSkillsTab;
    private javax.swing.JPanel pnlSkillsButtons;
    private javax.swing.JPanel pnlSkillsFields;
    private javax.swing.JPanel pnlSkillsForm;
    private javax.swing.JPanel pnlSkillsTab;
    private javax.swing.JPanel pnlWeaponsButtons;
    private javax.swing.JPanel pnlWeaponsFields;
    private javax.swing.JPanel pnlWeaponsForm;
    private javax.swing.JPanel pnlWeaponsTab;
    private javax.swing.JScrollPane scrAbilities;
    private javax.swing.JScrollPane scrAmmunition;
    private javax.swing.JScrollPane scrArmor;
    private javax.swing.JScrollPane scrCharacter;
    private javax.swing.JScrollPane scrInventory;
    private javax.swing.JScrollPane scrItems;
    private javax.swing.JScrollPane scrSecondSkills;
    private javax.swing.JScrollPane scrSkills;
    private javax.swing.JScrollPane scrWeapons;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblAbilities;
    private javax.swing.JTable tblAmmunition;
    private javax.swing.JTable tblArmor;
    private javax.swing.JTable tblInventory;
    private javax.swing.JTable tblItems;
    private javax.swing.JTable tblSecondSkills;
    private javax.swing.JTable tblSkills;
    private javax.swing.JTable tblWeapons;
    private javax.swing.JTextField txtAbilityDescription;
    private javax.swing.JTextField txtAbilityName;
    private javax.swing.JTextField txtAge;
    private javax.swing.JTextField txtAmmoDamage;
    private javax.swing.JTextField txtAmmoDescription;
    private javax.swing.JTextField txtAmmoDice;
    private javax.swing.JTextField txtAmmoName;
    private javax.swing.JTextField txtAmmoWeight;
    private javax.swing.JTextField txtArmorDescription;
    private javax.swing.JTextField txtArmorName;
    private javax.swing.JTextField txtArmorWeight;
    private javax.swing.JTextField txtBaseHealth;
    private javax.swing.JTextField txtBaseMana;
    private javax.swing.JTextField txtBaseSanity;
    private javax.swing.JTextField txtBaseStamina;
    private javax.swing.JTextField txtClass;
    private javax.swing.JTextField txtCost;
    private javax.swing.JTextField txtElementalAc;
    private javax.swing.JTextField txtHealth;
    private javax.swing.JTextField txtItemDescription;
    private javax.swing.JTextField txtItemName;
    private javax.swing.JTextField txtItemWeight;
    private javax.swing.JTextField txtLanguages;
    private javax.swing.JTextField txtLevel;
    private javax.swing.JTextField txtMana;
    private javax.swing.JTextField txtName;
    private javax.swing.JTextField txtOrigin;
    private javax.swing.JTextField txtPhysicalAc;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtRace;
    private javax.swing.JTextField txtSanity;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtSecondSkillDice;
    private javax.swing.JTextField txtSecondSkillName;
    private javax.swing.JTextField txtSecondSkillValue;
    private javax.swing.JTextField txtSkillDice;
    private javax.swing.JTextField txtSkillName;
    private javax.swing.JTextField txtSkillValue;
    private javax.swing.JTextField txtStamina;
    private javax.swing.JTextField txtSubclass;
    private javax.swing.JTextField txtWeaponAttribute;
    private javax.swing.JTextField txtWeaponDamage;
    private javax.swing.JTextField txtWeaponDescription;
    private javax.swing.JTextField txtWeaponDice;
    private javax.swing.JTextField txtWeaponMultiplier;
    private javax.swing.JTextField txtWeaponName;
    private javax.swing.JTextField txtWeaponRange;
    private javax.swing.JTextField txtWeaponWeight;
    // End of variables declaration//GEN-END:variables
}
