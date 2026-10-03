/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
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
import model.Ability;
import model.AbilityCategory;
import model.ActionType;
import model.Amunition;
import model.Armor;
import model.CharacterAbility;
import model.CostType;
import model.Inventory;
import model.Item;
import model.ItemCategory;
import model.PlayerCharacter;
import model.SecondBound;
import model.Skill;
import model.Weapon;
import service.MainWindowService;

/**
 * Characters on the left and one tab per model on the right. Each tab has a table, the
 * fields of the row clicked in the table and the New, Save and Delete buttons.
 *
 * @author edynu
 */
public class MainWindow extends javax.swing.JFrame {

    private final MainWindowService service = new MainWindowService();
    private final Icon noPhoto = noPhotoIcon();
    private final Map<Integer, Icon> photos = new HashMap<>(); // gallery picture of each character, by id

    // What is on the screen. The rows of each table are in the same order as its list.
    private List<PlayerCharacter> characters = new ArrayList<>();
    private PlayerCharacter selected; // character clicked in the gallery, null when there is none
    private List<Skill> skills = new ArrayList<>();
    private List<SecondBound> bonds = new ArrayList<>();
    private List<Ability> abilities = new ArrayList<>();
    private List<CharacterAbility> learned = new ArrayList<>();
    private List<Inventory> inventory = new ArrayList<>();
    private List<Item> allItems = new ArrayList<>();
    private List<Item> generalItems = new ArrayList<>();
    private List<Weapon> weapons = new ArrayList<>();
    private List<Armor> armors = new ArrayList<>();
    private List<Amunition> ammunition = new ArrayList<>();

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        categoryCombo.setModel(new DefaultComboBoxModel<>(AbilityCategory.values()));
        actionTypeCombo.setModel(new DefaultComboBoxModel<>(ActionType.values()));
        costTypeCombo.setModel(new DefaultComboBoxModel<>(CostType.values()));
        // Each character of the gallery is shown as its photo with the name below it.
        characterList.setCellRenderer(new DefaultListCellRenderer() {
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
        loadCharacters();
        loadItems();
        loadAbilities();
        if (!characters.isEmpty()) {
            characterList.setSelectedIndex(0);
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
        String search = searchField.getText().trim().toLowerCase();
        DefaultListModel<PlayerCharacter> model = new DefaultListModel<>();
        for (PlayerCharacter character : characters) {
            if (character.getName().toLowerCase().contains(search)) {
                model.addElement(character);
            }
        }
        characterList.setModel(model);
        if (selected != null) {
            selectInGallery(selected.getId());
        }
    }

    private void selectInGallery(int characterId) {
        for (int i = 0; i < characterList.getModel().getSize(); i++) {
            if (((PlayerCharacter) characterList.getModel().getElementAt(i)).getId() == characterId) {
                characterList.setSelectedIndex(i);
            }
        }
    }

    // Fills the Character tab and the tables of the selected character.
    private void showCharacter() {
        nameField.setText(selected.getName());
        levelField.setText(String.valueOf(selected.getLevel()));
        ageField.setText(String.valueOf(selected.getAge()));
        raceField.setText(selected.getRace());
        classField.setText(selected.getCharacterClass());
        subclassField.setText(selected.getSubclass());
        originField.setText(selected.getOrigin());
        languagesField.setText(selected.getLanguage());
        healthField.setText(String.valueOf(selected.getCurrentHealth()));
        baseHealthField.setText(String.valueOf(selected.getBaseHealth()));
        manaField.setText(String.valueOf(selected.getCurrentMana()));
        baseManaField.setText(String.valueOf(selected.getBaseMana()));
        staminaField.setText(String.valueOf(selected.getCurrentStamina()));
        baseStaminaField.setText(String.valueOf(selected.getBaseStamina()));
        sanityField.setText(String.valueOf(selected.getCurrentSanity()));
        baseSanityField.setText(String.valueOf(selected.getBaseSanity()));
        loadSkills();
        loadBonds();
        loadAbilities();
        loadInventory();
    }

    private void clearCharacter() {
        selected = null;
        characterList.clearSelection();
        clear(nameField, levelField, ageField, raceField, classField, subclassField, originField, languagesField,
                healthField, baseHealthField, manaField, baseManaField, staminaField, baseStaminaField, sanityField, baseSanityField);
        loadSkills();
        loadBonds();
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
        DefaultTableModel model = (DefaultTableModel) skillsTable.getModel();
        model.setRowCount(0);
        for (Skill skill : skills) {
            model.addRow(new Object[] {skill.getSkillName(), skill.getValue()});
        }
        clearSkillForm();
    }

    private void clearSkillForm() {
        skillsTable.clearSelection();
        clear(skillNameField, skillValueField);
    }

    // ---------------------------------------------------------------- Bonds

    private void loadBonds() {
        bonds = new ArrayList<>();
        try {
            if (selected != null) {
                bonds = service.listBonds(selected.getId());
            }
        } catch (SQLException e) {
            showError(e);
        }
        DefaultTableModel model = (DefaultTableModel) bondsTable.getModel();
        model.setRowCount(0);
        for (SecondBound bond : bonds) {
            model.addRow(new Object[] {bond.getBoundName(), bond.getValue()});
        }
        clearBondForm();
    }

    private void clearBondForm() {
        bondsTable.clearSelection();
        clear(bondNameField, bondValueField);
    }

    // ---------------------------------------------------------------- Abilities

    // All abilities; "Learned" and "Active" refer to the selected character.
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
        DefaultTableModel model = (DefaultTableModel) abilitiesTable.getModel();
        model.setRowCount(0);
        for (Ability ability : abilities) {
            CharacterAbility link = findLearned(ability.getAbilityId());
            model.addRow(new Object[] {ability.getName(), ability.getCategory(), ability.getActionType(),
                ability.getCostType(), ability.getCostValue(), link != null ? "Yes" : "No",
                link != null && link.isToggled() ? "Yes" : "No"});
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
        abilitiesTable.clearSelection();
        clear(abilityNameField, costField, abilityDescriptionField);
        activeCheck.setSelected(false);
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
        DefaultTableModel model = (DefaultTableModel) inventoryTable.getModel();
        model.setRowCount(0);
        for (Inventory entry : inventory) {
            Item item = findItem(entry.getItemId());
            model.addRow(new Object[] {item.getName(), item.getCategory(), entry.getQuantity(),
                entry.isEquipped() ? "Yes" : "No", item.getWeight()});
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
        inventoryTable.clearSelection();
        itemCombo.setSelectedIndex(-1);
        clear(quantityField);
        equippedCheck.setSelected(false);
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

        DefaultTableModel model = (DefaultTableModel) itemsTable.getModel();
        model.setRowCount(0);
        for (Item item : generalItems) {
            model.addRow(new Object[] {item.getName(), item.getWeight(), item.getDescription()});
        }
        model = (DefaultTableModel) weaponsTable.getModel();
        model.setRowCount(0);
        for (Weapon weapon : weapons) {
            model.addRow(new Object[] {weapon.getName(), weapon.getDamageDice(), weapon.getScalingAttribute(),
                weapon.getCriticalRange(), weapon.getCriticalMultiplier(), weapon.getWeight()});
        }
        model = (DefaultTableModel) armorTable.getModel();
        model.setRowCount(0);
        for (Armor armor : armors) {
            model.addRow(new Object[] {armor.getName(), armor.getPhysicalAC(), armor.getElementalAC(), armor.getWeight()});
        }
        model = (DefaultTableModel) ammunitionTable.getModel();
        model.setRowCount(0);
        for (Amunition ammo : ammunition) {
            model.addRow(new Object[] {ammo.getName(), ammo.getDamageDice(), ammo.getWeight()});
        }

        itemCombo.removeAllItems();
        for (Item item : allItems) {
            itemCombo.addItem(item.getName());
        }
        clearItemForm();
        clearWeaponForm();
        clearArmorForm();
        clearAmmoForm();
        loadInventory(); // a deleted item also leaves the inventory
    }

    private void clearItemForm() {
        itemsTable.clearSelection();
        clear(itemNameField, itemWeightField, itemDescriptionField);
    }

    private void clearWeaponForm() {
        weaponsTable.clearSelection();
        clear(weaponNameField, weaponWeightField, weaponAttributeField, weaponDamageField, weaponRangeField,
                weaponMultiplierField, weaponDescriptionField);
    }

    private void clearArmorForm() {
        armorTable.clearSelection();
        clear(armorNameField, armorWeightField, physicalAcField, elementalAcField, armorDescriptionField);
    }

    private void clearAmmoForm() {
        ammunitionTable.clearSelection();
        clear(ammoNameField, ammoWeightField, ammoDamageField, ammoDescriptionField);
    }

    // ---------------------------------------------------------------- Photos

    // Image reduced to fit in size x size pixels and saved as JPEG, to keep the database small.
    private byte[] toJpeg(BufferedImage image, int size) throws IOException {
        double scale = Math.min(1.0, (double) size / Math.max(image.getWidth(), image.getHeight()));
        int width = Math.max(1, (int) (image.getWidth() * scale));
        int height = Math.max(1, (int) (image.getHeight() * scale));
        BufferedImage small = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        small.getGraphics().drawImage(new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH)).getImage(), 0, 0, null);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(small, "jpg", bytes);
        return bytes.toByteArray();
    }

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

    private void clear(JTextField... fields) {
        for (JTextField field : fields) {
            field.setText("");
        }
    }

    private int number(JTextField field, String name) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a whole number.");
        }
    }

    // Accepts both 1.5 and 1,5.
    private double decimal(JTextField field, String name) {
        try {
            return Double.parseDouble(field.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " must be a number.");
        }
    }

    private boolean confirm(String question) {
        return JOptionPane.showConfirmDialog(this, question, "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    private void showError(Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        titleLabel = new javax.swing.JLabel();
        leftPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        searchLabel = new javax.swing.JLabel();
        searchField = new javax.swing.JTextField();
        characterScroll = new javax.swing.JScrollPane();
        characterList = new javax.swing.JList<>();
        tabs = new javax.swing.JTabbedPane();
        characterTab = new javax.swing.JPanel();
        characterFields = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        levelLabel = new javax.swing.JLabel();
        levelField = new javax.swing.JTextField();
        ageLabel = new javax.swing.JLabel();
        ageField = new javax.swing.JTextField();
        raceLabel = new javax.swing.JLabel();
        raceField = new javax.swing.JTextField();
        classLabel = new javax.swing.JLabel();
        classField = new javax.swing.JTextField();
        subclassLabel = new javax.swing.JLabel();
        subclassField = new javax.swing.JTextField();
        originLabel = new javax.swing.JLabel();
        originField = new javax.swing.JTextField();
        languagesLabel = new javax.swing.JLabel();
        languagesField = new javax.swing.JTextField();
        healthLabel = new javax.swing.JLabel();
        healthField = new javax.swing.JTextField();
        baseHealthLabel = new javax.swing.JLabel();
        baseHealthField = new javax.swing.JTextField();
        manaLabel = new javax.swing.JLabel();
        manaField = new javax.swing.JTextField();
        baseManaLabel = new javax.swing.JLabel();
        baseManaField = new javax.swing.JTextField();
        staminaLabel = new javax.swing.JLabel();
        staminaField = new javax.swing.JTextField();
        baseStaminaLabel = new javax.swing.JLabel();
        baseStaminaField = new javax.swing.JTextField();
        sanityLabel = new javax.swing.JLabel();
        sanityField = new javax.swing.JTextField();
        baseSanityLabel = new javax.swing.JLabel();
        baseSanityField = new javax.swing.JTextField();
        characterButtons = new javax.swing.JPanel();
        newCharacterButton = new javax.swing.JButton();
        saveCharacterButton = new javax.swing.JButton();
        deleteCharacterButton = new javax.swing.JButton();
        changePhotoButton = new javax.swing.JButton();
        skillsTab = new javax.swing.JPanel();
        skillsScroll = new javax.swing.JScrollPane();
        skillsTable = new javax.swing.JTable();
        skillsForm = new javax.swing.JPanel();
        skillsFields = new javax.swing.JPanel();
        skillNameLabel = new javax.swing.JLabel();
        skillNameField = new javax.swing.JTextField();
        skillValueLabel = new javax.swing.JLabel();
        skillValueField = new javax.swing.JTextField();
        skillsButtons = new javax.swing.JPanel();
        newSkillButton = new javax.swing.JButton();
        saveSkillButton = new javax.swing.JButton();
        deleteSkillButton = new javax.swing.JButton();
        bondsTab = new javax.swing.JPanel();
        bondsScroll = new javax.swing.JScrollPane();
        bondsTable = new javax.swing.JTable();
        bondsForm = new javax.swing.JPanel();
        bondsFields = new javax.swing.JPanel();
        bondNameLabel = new javax.swing.JLabel();
        bondNameField = new javax.swing.JTextField();
        bondValueLabel = new javax.swing.JLabel();
        bondValueField = new javax.swing.JTextField();
        bondsButtons = new javax.swing.JPanel();
        newBondButton = new javax.swing.JButton();
        saveBondButton = new javax.swing.JButton();
        deleteBondButton = new javax.swing.JButton();
        abilitiesTab = new javax.swing.JPanel();
        abilitiesScroll = new javax.swing.JScrollPane();
        abilitiesTable = new javax.swing.JTable();
        abilitiesForm = new javax.swing.JPanel();
        abilitiesFields = new javax.swing.JPanel();
        abilityNameLabel = new javax.swing.JLabel();
        abilityNameField = new javax.swing.JTextField();
        categoryLabel = new javax.swing.JLabel();
        categoryCombo = new javax.swing.JComboBox<>();
        actionTypeLabel = new javax.swing.JLabel();
        actionTypeCombo = new javax.swing.JComboBox<>();
        costTypeLabel = new javax.swing.JLabel();
        costTypeCombo = new javax.swing.JComboBox<>();
        costLabel = new javax.swing.JLabel();
        costField = new javax.swing.JTextField();
        abilityDescriptionLabel = new javax.swing.JLabel();
        abilityDescriptionField = new javax.swing.JTextField();
        activeLabel = new javax.swing.JLabel();
        activeCheck = new javax.swing.JCheckBox();
        abilitiesButtons = new javax.swing.JPanel();
        newAbilityButton = new javax.swing.JButton();
        saveAbilityButton = new javax.swing.JButton();
        deleteAbilityButton = new javax.swing.JButton();
        learnButton = new javax.swing.JButton();
        forgetButton = new javax.swing.JButton();
        inventoryTab = new javax.swing.JPanel();
        inventoryScroll = new javax.swing.JScrollPane();
        inventoryTable = new javax.swing.JTable();
        inventoryForm = new javax.swing.JPanel();
        inventoryFields = new javax.swing.JPanel();
        itemLabel = new javax.swing.JLabel();
        itemCombo = new javax.swing.JComboBox<>();
        quantityLabel = new javax.swing.JLabel();
        quantityField = new javax.swing.JTextField();
        equippedLabel = new javax.swing.JLabel();
        equippedCheck = new javax.swing.JCheckBox();
        inventoryButtons = new javax.swing.JPanel();
        newEntryButton = new javax.swing.JButton();
        saveEntryButton = new javax.swing.JButton();
        deleteEntryButton = new javax.swing.JButton();
        itemsTab = new javax.swing.JPanel();
        itemsScroll = new javax.swing.JScrollPane();
        itemsTable = new javax.swing.JTable();
        itemsForm = new javax.swing.JPanel();
        itemsFields = new javax.swing.JPanel();
        itemNameLabel = new javax.swing.JLabel();
        itemNameField = new javax.swing.JTextField();
        itemWeightLabel = new javax.swing.JLabel();
        itemWeightField = new javax.swing.JTextField();
        itemDescriptionLabel = new javax.swing.JLabel();
        itemDescriptionField = new javax.swing.JTextField();
        itemsButtons = new javax.swing.JPanel();
        newItemButton = new javax.swing.JButton();
        saveItemButton = new javax.swing.JButton();
        deleteItemButton = new javax.swing.JButton();
        weaponsTab = new javax.swing.JPanel();
        weaponsScroll = new javax.swing.JScrollPane();
        weaponsTable = new javax.swing.JTable();
        weaponsForm = new javax.swing.JPanel();
        weaponsFields = new javax.swing.JPanel();
        weaponNameLabel = new javax.swing.JLabel();
        weaponNameField = new javax.swing.JTextField();
        weaponWeightLabel = new javax.swing.JLabel();
        weaponWeightField = new javax.swing.JTextField();
        weaponAttributeLabel = new javax.swing.JLabel();
        weaponAttributeField = new javax.swing.JTextField();
        weaponDamageLabel = new javax.swing.JLabel();
        weaponDamageField = new javax.swing.JTextField();
        weaponRangeLabel = new javax.swing.JLabel();
        weaponRangeField = new javax.swing.JTextField();
        weaponMultiplierLabel = new javax.swing.JLabel();
        weaponMultiplierField = new javax.swing.JTextField();
        weaponDescriptionLabel = new javax.swing.JLabel();
        weaponDescriptionField = new javax.swing.JTextField();
        weaponsButtons = new javax.swing.JPanel();
        newWeaponButton = new javax.swing.JButton();
        saveWeaponButton = new javax.swing.JButton();
        deleteWeaponButton = new javax.swing.JButton();
        armorTab = new javax.swing.JPanel();
        armorScroll = new javax.swing.JScrollPane();
        armorTable = new javax.swing.JTable();
        armorForm = new javax.swing.JPanel();
        armorFields = new javax.swing.JPanel();
        armorNameLabel = new javax.swing.JLabel();
        armorNameField = new javax.swing.JTextField();
        armorWeightLabel = new javax.swing.JLabel();
        armorWeightField = new javax.swing.JTextField();
        physicalAcLabel = new javax.swing.JLabel();
        physicalAcField = new javax.swing.JTextField();
        elementalAcLabel = new javax.swing.JLabel();
        elementalAcField = new javax.swing.JTextField();
        armorDescriptionLabel = new javax.swing.JLabel();
        armorDescriptionField = new javax.swing.JTextField();
        armorButtons = new javax.swing.JPanel();
        newArmorButton = new javax.swing.JButton();
        saveArmorButton = new javax.swing.JButton();
        deleteArmorButton = new javax.swing.JButton();
        ammunitionTab = new javax.swing.JPanel();
        ammunitionScroll = new javax.swing.JScrollPane();
        ammunitionTable = new javax.swing.JTable();
        ammunitionForm = new javax.swing.JPanel();
        ammunitionFields = new javax.swing.JPanel();
        ammoNameLabel = new javax.swing.JLabel();
        ammoNameField = new javax.swing.JTextField();
        ammoWeightLabel = new javax.swing.JLabel();
        ammoWeightField = new javax.swing.JTextField();
        ammoDamageLabel = new javax.swing.JLabel();
        ammoDamageField = new javax.swing.JTextField();
        ammoDescriptionLabel = new javax.swing.JLabel();
        ammoDescriptionField = new javax.swing.JTextField();
        ammunitionButtons = new javax.swing.JPanel();
        newAmmoButton = new javax.swing.JButton();
        saveAmmoButton = new javax.swing.JButton();
        deleteAmmoButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1100, 650));
        setTitle("RPG Sheet Manager");

        titleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 0, 10, 0));
        titleLabel.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("RPG SHEET MANAGER");
        getContentPane().add(titleLabel, java.awt.BorderLayout.PAGE_START);

        leftPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 0));
        leftPanel.setPreferredSize(new java.awt.Dimension(250, 400));
        leftPanel.setLayout(new java.awt.BorderLayout(0, 5));

        searchPanel.setLayout(new java.awt.BorderLayout(0, 3));

        searchLabel.setText("Character name");
        searchPanel.add(searchLabel, java.awt.BorderLayout.PAGE_START);

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                searchFieldKeyReleased(evt);
            }
        });
        searchPanel.add(searchField, java.awt.BorderLayout.CENTER);

        leftPanel.add(searchPanel, java.awt.BorderLayout.PAGE_START);

        characterList.setFixedCellHeight(110);
        characterList.setFixedCellWidth(110);
        characterList.setLayoutOrientation(javax.swing.JList.HORIZONTAL_WRAP);
        characterList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        characterList.setVisibleRowCount(-1);
        characterList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                characterListValueChanged(evt);
            }
        });
        characterScroll.setViewportView(characterList);

        leftPanel.add(characterScroll, java.awt.BorderLayout.CENTER);

        getContentPane().add(leftPanel, java.awt.BorderLayout.LINE_START);

        tabs.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 10, 10));

        characterTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        characterTab.setLayout(new java.awt.BorderLayout(10, 10));

        characterFields.setLayout(new java.awt.GridLayout(0, 4, 8, 8));

        nameLabel.setText("Name");
        characterFields.add(nameLabel);

        nameField.setColumns(12);
        characterFields.add(nameField);

        levelLabel.setText("Level");
        characterFields.add(levelLabel);
        characterFields.add(levelField);

        ageLabel.setText("Age");
        characterFields.add(ageLabel);
        characterFields.add(ageField);

        raceLabel.setText("Race");
        characterFields.add(raceLabel);
        characterFields.add(raceField);

        classLabel.setText("Class");
        characterFields.add(classLabel);
        characterFields.add(classField);

        subclassLabel.setText("Subclass");
        characterFields.add(subclassLabel);
        characterFields.add(subclassField);

        originLabel.setText("Origin");
        characterFields.add(originLabel);
        characterFields.add(originField);

        languagesLabel.setText("Languages");
        characterFields.add(languagesLabel);
        characterFields.add(languagesField);

        healthLabel.setText("Health");
        characterFields.add(healthLabel);
        characterFields.add(healthField);

        baseHealthLabel.setText("Base health");
        characterFields.add(baseHealthLabel);
        characterFields.add(baseHealthField);

        manaLabel.setText("Mana");
        characterFields.add(manaLabel);
        characterFields.add(manaField);

        baseManaLabel.setText("Base mana");
        characterFields.add(baseManaLabel);
        characterFields.add(baseManaField);

        staminaLabel.setText("Stamina");
        characterFields.add(staminaLabel);
        characterFields.add(staminaField);

        baseStaminaLabel.setText("Base stamina");
        characterFields.add(baseStaminaLabel);
        characterFields.add(baseStaminaField);

        sanityLabel.setText("Sanity");
        characterFields.add(sanityLabel);
        characterFields.add(sanityField);

        baseSanityLabel.setText("Base sanity");
        characterFields.add(baseSanityLabel);
        characterFields.add(baseSanityField);

        characterTab.add(characterFields, java.awt.BorderLayout.PAGE_START);

        characterButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        newCharacterButton.setText("New");
        newCharacterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCharacterButtonActionPerformed(evt);
            }
        });
        characterButtons.add(newCharacterButton);

        saveCharacterButton.setText("Save");
        saveCharacterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCharacterButtonActionPerformed(evt);
            }
        });
        characterButtons.add(saveCharacterButton);

        deleteCharacterButton.setText("Delete");
        deleteCharacterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCharacterButtonActionPerformed(evt);
            }
        });
        characterButtons.add(deleteCharacterButton);

        changePhotoButton.setText("Change photo");
        changePhotoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePhotoButtonActionPerformed(evt);
            }
        });
        characterButtons.add(changePhotoButton);

        characterTab.add(characterButtons, java.awt.BorderLayout.CENTER);

        tabs.addTab("Character", characterTab);

        skillsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        skillsTab.setLayout(new java.awt.BorderLayout(10, 10));

        skillsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Skill", "Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        skillsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                skillsTableMouseClicked(evt);
            }
        });
        skillsScroll.setViewportView(skillsTable);

        skillsTab.add(skillsScroll, java.awt.BorderLayout.CENTER);

        skillsForm.setLayout(new java.awt.BorderLayout(0, 10));

        skillsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        skillNameLabel.setText("Skill");
        skillsFields.add(skillNameLabel);

        skillNameField.setColumns(12);
        skillsFields.add(skillNameField);

        skillValueLabel.setText("Value");
        skillsFields.add(skillValueLabel);
        skillsFields.add(skillValueField);

        skillsForm.add(skillsFields, java.awt.BorderLayout.PAGE_START);

        skillsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        newSkillButton.setText("New");
        newSkillButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSkillButtonActionPerformed(evt);
            }
        });
        skillsButtons.add(newSkillButton);

        saveSkillButton.setText("Save");
        saveSkillButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSkillButtonActionPerformed(evt);
            }
        });
        skillsButtons.add(saveSkillButton);

        deleteSkillButton.setText("Delete");
        deleteSkillButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSkillButtonActionPerformed(evt);
            }
        });
        skillsButtons.add(deleteSkillButton);

        skillsForm.add(skillsButtons, java.awt.BorderLayout.CENTER);

        skillsTab.add(skillsForm, java.awt.BorderLayout.LINE_END);

        tabs.addTab("Skills", skillsTab);

        bondsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bondsTab.setLayout(new java.awt.BorderLayout(10, 10));

        bondsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Bond", "Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bondsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bondsTableMouseClicked(evt);
            }
        });
        bondsScroll.setViewportView(bondsTable);

        bondsTab.add(bondsScroll, java.awt.BorderLayout.CENTER);

        bondsForm.setLayout(new java.awt.BorderLayout(0, 10));

        bondsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        bondNameLabel.setText("Bond");
        bondsFields.add(bondNameLabel);

        bondNameField.setColumns(12);
        bondsFields.add(bondNameField);

        bondValueLabel.setText("Value");
        bondsFields.add(bondValueLabel);
        bondsFields.add(bondValueField);

        bondsForm.add(bondsFields, java.awt.BorderLayout.PAGE_START);

        bondsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        newBondButton.setText("New");
        newBondButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBondButtonActionPerformed(evt);
            }
        });
        bondsButtons.add(newBondButton);

        saveBondButton.setText("Save");
        saveBondButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBondButtonActionPerformed(evt);
            }
        });
        bondsButtons.add(saveBondButton);

        deleteBondButton.setText("Delete");
        deleteBondButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBondButtonActionPerformed(evt);
            }
        });
        bondsButtons.add(deleteBondButton);

        bondsForm.add(bondsButtons, java.awt.BorderLayout.CENTER);

        bondsTab.add(bondsForm, java.awt.BorderLayout.LINE_END);

        tabs.addTab("Bonds", bondsTab);

        abilitiesTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        abilitiesTab.setLayout(new java.awt.BorderLayout(10, 10));

        abilitiesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Category", "Action", "Cost type", "Cost", "Learned", "Active"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        abilitiesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                abilitiesTableMouseClicked(evt);
            }
        });
        abilitiesScroll.setViewportView(abilitiesTable);

        abilitiesTab.add(abilitiesScroll, java.awt.BorderLayout.CENTER);

        abilitiesForm.setLayout(new java.awt.BorderLayout(0, 10));

        abilitiesFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        abilityNameLabel.setText("Name");
        abilitiesFields.add(abilityNameLabel);

        abilityNameField.setColumns(12);
        abilitiesFields.add(abilityNameField);

        categoryLabel.setText("Category");
        abilitiesFields.add(categoryLabel);
        abilitiesFields.add(categoryCombo);

        actionTypeLabel.setText("Action");
        abilitiesFields.add(actionTypeLabel);
        abilitiesFields.add(actionTypeCombo);

        costTypeLabel.setText("Cost type");
        abilitiesFields.add(costTypeLabel);
        abilitiesFields.add(costTypeCombo);

        costLabel.setText("Cost");
        abilitiesFields.add(costLabel);
        abilitiesFields.add(costField);

        abilityDescriptionLabel.setText("Description");
        abilitiesFields.add(abilityDescriptionLabel);
        abilitiesFields.add(abilityDescriptionField);

        activeLabel.setText("Active");
        abilitiesFields.add(activeLabel);
        abilitiesFields.add(activeCheck);

        abilitiesForm.add(abilitiesFields, java.awt.BorderLayout.PAGE_START);

        abilitiesButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        newAbilityButton.setText("New");
        newAbilityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAbilityButtonActionPerformed(evt);
            }
        });
        abilitiesButtons.add(newAbilityButton);

        saveAbilityButton.setText("Save");
        saveAbilityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAbilityButtonActionPerformed(evt);
            }
        });
        abilitiesButtons.add(saveAbilityButton);

        deleteAbilityButton.setText("Delete");
        deleteAbilityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAbilityButtonActionPerformed(evt);
            }
        });
        abilitiesButtons.add(deleteAbilityButton);

        learnButton.setText("Learn");
        learnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                learnButtonActionPerformed(evt);
            }
        });
        abilitiesButtons.add(learnButton);

        forgetButton.setText("Forget");
        forgetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forgetButtonActionPerformed(evt);
            }
        });
        abilitiesButtons.add(forgetButton);

        abilitiesForm.add(abilitiesButtons, java.awt.BorderLayout.CENTER);

        abilitiesTab.add(abilitiesForm, java.awt.BorderLayout.LINE_END);

        tabs.addTab("Abilities", abilitiesTab);

        inventoryTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inventoryTab.setLayout(new java.awt.BorderLayout(10, 10));

        inventoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Item", "Category", "Quantity", "Equipped", "Weight"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        inventoryTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inventoryTableMouseClicked(evt);
            }
        });
        inventoryScroll.setViewportView(inventoryTable);

        inventoryTab.add(inventoryScroll, java.awt.BorderLayout.CENTER);

        inventoryForm.setLayout(new java.awt.BorderLayout(0, 10));

        inventoryFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        itemLabel.setText("Item");
        inventoryFields.add(itemLabel);
        inventoryFields.add(itemCombo);

        quantityLabel.setText("Quantity");
        inventoryFields.add(quantityLabel);
        inventoryFields.add(quantityField);

        equippedLabel.setText("Equipped");
        inventoryFields.add(equippedLabel);
        inventoryFields.add(equippedCheck);

        inventoryForm.add(inventoryFields, java.awt.BorderLayout.PAGE_START);

        inventoryButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        newEntryButton.setText("New");
        newEntryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newEntryButtonActionPerformed(evt);
            }
        });
        inventoryButtons.add(newEntryButton);

        saveEntryButton.setText("Save");
        saveEntryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEntryButtonActionPerformed(evt);
            }
        });
        inventoryButtons.add(saveEntryButton);

        deleteEntryButton.setText("Delete");
        deleteEntryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEntryButtonActionPerformed(evt);
            }
        });
        inventoryButtons.add(deleteEntryButton);

        inventoryForm.add(inventoryButtons, java.awt.BorderLayout.CENTER);

        inventoryTab.add(inventoryForm, java.awt.BorderLayout.LINE_END);

        tabs.addTab("Inventory", inventoryTab);

        itemsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        itemsTab.setLayout(new java.awt.BorderLayout(10, 10));

        itemsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Weight", "Description"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        itemsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                itemsTableMouseClicked(evt);
            }
        });
        itemsScroll.setViewportView(itemsTable);

        itemsTab.add(itemsScroll, java.awt.BorderLayout.CENTER);

        itemsForm.setLayout(new java.awt.BorderLayout(0, 10));

        itemsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        itemNameLabel.setText("Name");
        itemsFields.add(itemNameLabel);

        itemNameField.setColumns(12);
        itemsFields.add(itemNameField);

        itemWeightLabel.setText("Weight");
        itemsFields.add(itemWeightLabel);
        itemsFields.add(itemWeightField);

        itemDescriptionLabel.setText("Description");
        itemsFields.add(itemDescriptionLabel);
        itemsFields.add(itemDescriptionField);

        itemsForm.add(itemsFields, java.awt.BorderLayout.PAGE_START);

        itemsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        newItemButton.setText("New");
        newItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newItemButtonActionPerformed(evt);
            }
        });
        itemsButtons.add(newItemButton);

        saveItemButton.setText("Save");
        saveItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveItemButtonActionPerformed(evt);
            }
        });
        itemsButtons.add(saveItemButton);

        deleteItemButton.setText("Delete");
        deleteItemButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteItemButtonActionPerformed(evt);
            }
        });
        itemsButtons.add(deleteItemButton);

        itemsForm.add(itemsButtons, java.awt.BorderLayout.CENTER);

        itemsTab.add(itemsForm, java.awt.BorderLayout.LINE_END);

        tabs.addTab("Items", itemsTab);

        weaponsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        weaponsTab.setLayout(new java.awt.BorderLayout(10, 10));

        weaponsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Damage", "Attribute", "Critical range", "Critical multiplier", "Weight"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        weaponsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                weaponsTableMouseClicked(evt);
            }
        });
        weaponsScroll.setViewportView(weaponsTable);

        weaponsTab.add(weaponsScroll, java.awt.BorderLayout.CENTER);

        weaponsForm.setLayout(new java.awt.BorderLayout(0, 10));

        weaponsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        weaponNameLabel.setText("Name");
        weaponsFields.add(weaponNameLabel);

        weaponNameField.setColumns(12);
        weaponsFields.add(weaponNameField);

        weaponWeightLabel.setText("Weight");
        weaponsFields.add(weaponWeightLabel);
        weaponsFields.add(weaponWeightField);

        weaponAttributeLabel.setText("Attribute");
        weaponsFields.add(weaponAttributeLabel);
        weaponsFields.add(weaponAttributeField);

        weaponDamageLabel.setText("Damage");
        weaponsFields.add(weaponDamageLabel);
        weaponsFields.add(weaponDamageField);

        weaponRangeLabel.setText("Critical range");
        weaponsFields.add(weaponRangeLabel);
        weaponsFields.add(weaponRangeField);

        weaponMultiplierLabel.setText("Critical multiplier");
        weaponsFields.add(weaponMultiplierLabel);
        weaponsFields.add(weaponMultiplierField);

        weaponDescriptionLabel.setText("Description");
        weaponsFields.add(weaponDescriptionLabel);
        weaponsFields.add(weaponDescriptionField);

        weaponsForm.add(weaponsFields, java.awt.BorderLayout.PAGE_START);

        weaponsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        newWeaponButton.setText("New");
        newWeaponButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newWeaponButtonActionPerformed(evt);
            }
        });
        weaponsButtons.add(newWeaponButton);

        saveWeaponButton.setText("Save");
        saveWeaponButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveWeaponButtonActionPerformed(evt);
            }
        });
        weaponsButtons.add(saveWeaponButton);

        deleteWeaponButton.setText("Delete");
        deleteWeaponButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteWeaponButtonActionPerformed(evt);
            }
        });
        weaponsButtons.add(deleteWeaponButton);

        weaponsForm.add(weaponsButtons, java.awt.BorderLayout.CENTER);

        weaponsTab.add(weaponsForm, java.awt.BorderLayout.LINE_END);

        tabs.addTab("Weapons", weaponsTab);

        armorTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        armorTab.setLayout(new java.awt.BorderLayout(10, 10));

        armorTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Physical AC", "Elemental AC", "Weight"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        armorTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                armorTableMouseClicked(evt);
            }
        });
        armorScroll.setViewportView(armorTable);

        armorTab.add(armorScroll, java.awt.BorderLayout.CENTER);

        armorForm.setLayout(new java.awt.BorderLayout(0, 10));

        armorFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        armorNameLabel.setText("Name");
        armorFields.add(armorNameLabel);

        armorNameField.setColumns(12);
        armorFields.add(armorNameField);

        armorWeightLabel.setText("Weight");
        armorFields.add(armorWeightLabel);
        armorFields.add(armorWeightField);

        physicalAcLabel.setText("Physical AC");
        armorFields.add(physicalAcLabel);
        armorFields.add(physicalAcField);

        elementalAcLabel.setText("Elemental AC");
        armorFields.add(elementalAcLabel);
        armorFields.add(elementalAcField);

        armorDescriptionLabel.setText("Description");
        armorFields.add(armorDescriptionLabel);
        armorFields.add(armorDescriptionField);

        armorForm.add(armorFields, java.awt.BorderLayout.PAGE_START);

        armorButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        newArmorButton.setText("New");
        newArmorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newArmorButtonActionPerformed(evt);
            }
        });
        armorButtons.add(newArmorButton);

        saveArmorButton.setText("Save");
        saveArmorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveArmorButtonActionPerformed(evt);
            }
        });
        armorButtons.add(saveArmorButton);

        deleteArmorButton.setText("Delete");
        deleteArmorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteArmorButtonActionPerformed(evt);
            }
        });
        armorButtons.add(deleteArmorButton);

        armorForm.add(armorButtons, java.awt.BorderLayout.CENTER);

        armorTab.add(armorForm, java.awt.BorderLayout.LINE_END);

        tabs.addTab("Armor", armorTab);

        ammunitionTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        ammunitionTab.setLayout(new java.awt.BorderLayout(10, 10));

        ammunitionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Damage", "Weight"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ammunitionTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ammunitionTableMouseClicked(evt);
            }
        });
        ammunitionScroll.setViewportView(ammunitionTable);

        ammunitionTab.add(ammunitionScroll, java.awt.BorderLayout.CENTER);

        ammunitionForm.setLayout(new java.awt.BorderLayout(0, 10));

        ammunitionFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        ammoNameLabel.setText("Name");
        ammunitionFields.add(ammoNameLabel);

        ammoNameField.setColumns(12);
        ammunitionFields.add(ammoNameField);

        ammoWeightLabel.setText("Weight");
        ammunitionFields.add(ammoWeightLabel);
        ammunitionFields.add(ammoWeightField);

        ammoDamageLabel.setText("Damage");
        ammunitionFields.add(ammoDamageLabel);
        ammunitionFields.add(ammoDamageField);

        ammoDescriptionLabel.setText("Description");
        ammunitionFields.add(ammoDescriptionLabel);
        ammunitionFields.add(ammoDescriptionField);

        ammunitionForm.add(ammunitionFields, java.awt.BorderLayout.PAGE_START);

        ammunitionButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        newAmmoButton.setText("New");
        newAmmoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAmmoButtonActionPerformed(evt);
            }
        });
        ammunitionButtons.add(newAmmoButton);

        saveAmmoButton.setText("Save");
        saveAmmoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAmmoButtonActionPerformed(evt);
            }
        });
        ammunitionButtons.add(saveAmmoButton);

        deleteAmmoButton.setText("Delete");
        deleteAmmoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAmmoButtonActionPerformed(evt);
            }
        });
        ammunitionButtons.add(deleteAmmoButton);

        ammunitionForm.add(ammunitionButtons, java.awt.BorderLayout.CENTER);

        ammunitionTab.add(ammunitionForm, java.awt.BorderLayout.LINE_END);

        tabs.addTab("Ammunition", ammunitionTab);

        getContentPane().add(tabs, java.awt.BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void searchFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_searchFieldKeyReleased
        fillGallery();
    }//GEN-LAST:event_searchFieldKeyReleased

    private void characterListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_characterListValueChanged
        PlayerCharacter chosen = (PlayerCharacter) characterList.getSelectedValue();
        if (evt.getValueIsAdjusting() || chosen == null || chosen == selected) {
            return;
        }
        selected = chosen;
        showCharacter();
    }//GEN-LAST:event_characterListValueChanged

    private void newCharacterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCharacterButtonActionPerformed
        clearCharacter();
        nameField.requestFocus();
    }//GEN-LAST:event_newCharacterButtonActionPerformed

    private void saveCharacterButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCharacterButtonActionPerformed
        try {
            PlayerCharacter character = new PlayerCharacter(selected == null ? null : selected.getId(),
                    number(levelField, "Level"), nameField.getText(), number(ageField, "Age"), raceField.getText(),
                    classField.getText(), subclassField.getText(), originField.getText(), languagesField.getText(),
                    number(baseHealthField, "Base health"), number(healthField, "Health"),
                    number(baseManaField, "Base mana"), number(manaField, "Mana"),
                    number(baseStaminaField, "Base stamina"), number(staminaField, "Stamina"),
                    number(baseSanityField, "Base sanity"), number(sanityField, "Sanity"));
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
            showMessage("Select a character first.");
            return;
        }
        if (!confirm("Delete " + selected.getName() + "?")) {
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
            showMessage("Select a character first.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            BufferedImage image = ImageIO.read(chooser.getSelectedFile());
            if (image == null) {
                showMessage("This file is not an image.");
                return;
            }
            selected.setPhoto(toJpeg(image, 160));
            service.saveCharacter(selected);
            loadCharacters();
            selectInGallery(selected.getId());
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_changePhotoButtonActionPerformed

    private void skillsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_skillsTableMouseClicked
        int row = skillsTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Skill skill = skills.get(row);
        skillNameField.setText(skill.getSkillName());
        skillValueField.setText(String.valueOf(skill.getValue()));
    }//GEN-LAST:event_skillsTableMouseClicked

    private void newSkillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSkillButtonActionPerformed
        clearSkillForm();
    }//GEN-LAST:event_newSkillButtonActionPerformed

    private void saveSkillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSkillButtonActionPerformed
        if (selected == null) {
            showMessage("Select a character first.");
            return;
        }
        try {
            int row = skillsTable.getSelectedRow();
            Integer id = row >= 0 ? skills.get(row).getId() : null;
            service.saveSkill(new Skill(id, selected.getId(), skillNameField.getText(), number(skillValueField, "Value")));
            loadSkills();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveSkillButtonActionPerformed

    private void deleteSkillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSkillButtonActionPerformed
        int row = skillsTable.getSelectedRow();
        if (row < 0) {
            showMessage("Click a skill in the table first.");
            return;
        }
        if (!confirm("Delete the skill " + skills.get(row).getSkillName() + "?")) {
            return;
        }
        try {
            service.deleteSkill(skills.get(row).getId());
            loadSkills();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteSkillButtonActionPerformed

    private void bondsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bondsTableMouseClicked
        int row = bondsTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        SecondBound bond = bonds.get(row);
        bondNameField.setText(bond.getBoundName());
        bondValueField.setText(String.valueOf(bond.getValue()));
    }//GEN-LAST:event_bondsTableMouseClicked

    private void newBondButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBondButtonActionPerformed
        clearBondForm();
    }//GEN-LAST:event_newBondButtonActionPerformed

    private void saveBondButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBondButtonActionPerformed
        if (selected == null) {
            showMessage("Select a character first.");
            return;
        }
        try {
            int row = bondsTable.getSelectedRow();
            Integer id = row >= 0 ? bonds.get(row).getId() : null;
            service.saveBond(new SecondBound(id, selected.getId(), bondNameField.getText(), number(bondValueField, "Value")));
            loadBonds();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveBondButtonActionPerformed

    private void deleteBondButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBondButtonActionPerformed
        int row = bondsTable.getSelectedRow();
        if (row < 0) {
            showMessage("Click a bond in the table first.");
            return;
        }
        if (!confirm("Delete the bond " + bonds.get(row).getBoundName() + "?")) {
            return;
        }
        try {
            service.deleteBond(bonds.get(row).getId());
            loadBonds();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteBondButtonActionPerformed

    private void abilitiesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_abilitiesTableMouseClicked
        int row = abilitiesTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Ability ability = abilities.get(row);
        abilityNameField.setText(ability.getName());
        categoryCombo.setSelectedItem(ability.getCategory());
        actionTypeCombo.setSelectedItem(ability.getActionType());
        costTypeCombo.setSelectedItem(ability.getCostType());
        costField.setText(String.valueOf(ability.getCostValue()));
        abilityDescriptionField.setText(ability.getDescription());
        CharacterAbility link = findLearned(ability.getAbilityId());
        activeCheck.setSelected(link != null && link.isToggled());
    }//GEN-LAST:event_abilitiesTableMouseClicked

    private void newAbilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAbilityButtonActionPerformed
        clearAbilityForm();
    }//GEN-LAST:event_newAbilityButtonActionPerformed

    private void saveAbilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAbilityButtonActionPerformed
        try {
            int row = abilitiesTable.getSelectedRow();
            Integer id = row >= 0 ? abilities.get(row).getAbilityId() : null;
            service.saveAbility(new Ability(id, abilityNameField.getText(), (AbilityCategory) categoryCombo.getSelectedItem(),
                    (ActionType) actionTypeCombo.getSelectedItem(), (CostType) costTypeCombo.getSelectedItem(),
                    number(costField, "Cost"), abilityDescriptionField.getText()));
            loadAbilities();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveAbilityButtonActionPerformed

    private void deleteAbilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAbilityButtonActionPerformed
        int row = abilitiesTable.getSelectedRow();
        if (row < 0) {
            showMessage("Click an ability in the table first.");
            return;
        }
        if (!confirm("Delete the ability " + abilities.get(row).getName() + "?")) {
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
        int row = abilitiesTable.getSelectedRow();
        if (selected == null || row < 0) {
            showMessage("Select a character and click an ability in the table first.");
            return;
        }
        try {
            service.learnAbility(selected.getId(), abilities.get(row).getAbilityId(), activeCheck.isSelected());
            loadAbilities();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_learnButtonActionPerformed

    private void forgetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forgetButtonActionPerformed
        int row = abilitiesTable.getSelectedRow();
        if (selected == null || row < 0) {
            showMessage("Select a character and click an ability in the table first.");
            return;
        }
        try {
            service.forgetAbility(selected.getId(), abilities.get(row).getAbilityId());
            loadAbilities();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_forgetButtonActionPerformed

    private void inventoryTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inventoryTableMouseClicked
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Inventory entry = inventory.get(row);
        itemCombo.setSelectedIndex(allItems.indexOf(findItem(entry.getItemId())));
        quantityField.setText(String.valueOf(entry.getQuantity()));
        equippedCheck.setSelected(entry.isEquipped());
    }//GEN-LAST:event_inventoryTableMouseClicked

    private void newEntryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newEntryButtonActionPerformed
        clearInventoryForm();
    }//GEN-LAST:event_newEntryButtonActionPerformed

    private void saveEntryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEntryButtonActionPerformed
        if (selected == null || itemCombo.getSelectedIndex() < 0) {
            showMessage("Select a character and choose an item first.");
            return;
        }
        try {
            int row = inventoryTable.getSelectedRow();
            Integer id = row >= 0 ? inventory.get(row).getId() : null;
            int itemId = allItems.get(itemCombo.getSelectedIndex()).getId();
            service.saveInventory(new Inventory(id, selected.getId(), itemId, number(quantityField, "Quantity"), equippedCheck.isSelected()));
            loadInventory();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveEntryButtonActionPerformed

    private void deleteEntryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEntryButtonActionPerformed
        int row = inventoryTable.getSelectedRow();
        if (row < 0) {
            showMessage("Click an item in the table first.");
            return;
        }
        if (!confirm("Remove this item from the inventory?")) {
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
        int row = itemsTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Item item = generalItems.get(row);
        itemNameField.setText(item.getName());
        itemWeightField.setText(String.valueOf(item.getWeight()));
        itemDescriptionField.setText(item.getDescription());
    }//GEN-LAST:event_itemsTableMouseClicked

    private void newItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newItemButtonActionPerformed
        clearItemForm();
    }//GEN-LAST:event_newItemButtonActionPerformed

    private void saveItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveItemButtonActionPerformed
        try {
            int row = itemsTable.getSelectedRow();
            Integer id = row >= 0 ? generalItems.get(row).getId() : null;
            service.saveGeneralItem(new Item(id, itemNameField.getText(), itemDescriptionField.getText(),
                    decimal(itemWeightField, "Weight"), ItemCategory.GENERAL));
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveItemButtonActionPerformed

    private void deleteItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteItemButtonActionPerformed
        int row = itemsTable.getSelectedRow();
        if (row < 0) {
            showMessage("Click an item in the table first.");
            return;
        }
        if (!confirm("Delete the item " + generalItems.get(row).getName() + "?")) {
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
        int row = weaponsTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Weapon weapon = weapons.get(row);
        weaponNameField.setText(weapon.getName());
        weaponWeightField.setText(String.valueOf(weapon.getWeight()));
        weaponAttributeField.setText(weapon.getScalingAttribute());
        weaponDamageField.setText(weapon.getDamageDice());
        weaponRangeField.setText(String.valueOf(weapon.getCriticalRange()));
        weaponMultiplierField.setText(String.valueOf(weapon.getCriticalMultiplier()));
        weaponDescriptionField.setText(weapon.getDescription());
    }//GEN-LAST:event_weaponsTableMouseClicked

    private void newWeaponButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newWeaponButtonActionPerformed
        clearWeaponForm();
    }//GEN-LAST:event_newWeaponButtonActionPerformed

    private void saveWeaponButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveWeaponButtonActionPerformed
        try {
            int row = weaponsTable.getSelectedRow();
            Integer id = row >= 0 ? weapons.get(row).getId() : null;
            service.saveWeapon(new Weapon(id, weaponNameField.getText(), weaponDescriptionField.getText(),
                    decimal(weaponWeightField, "Weight"), ItemCategory.WEAPON, weaponAttributeField.getText(),
                    weaponDamageField.getText(), number(weaponRangeField, "Critical range"),
                    number(weaponMultiplierField, "Critical multiplier")));
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveWeaponButtonActionPerformed

    private void deleteWeaponButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteWeaponButtonActionPerformed
        int row = weaponsTable.getSelectedRow();
        if (row < 0) {
            showMessage("Click a weapon in the table first.");
            return;
        }
        if (!confirm("Delete the weapon " + weapons.get(row).getName() + "?")) {
            return;
        }
        try {
            service.deleteItem(weapons.get(row).getId());
            loadItems();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteWeaponButtonActionPerformed

    private void armorTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_armorTableMouseClicked
        int row = armorTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Armor armor = armors.get(row);
        armorNameField.setText(armor.getName());
        armorWeightField.setText(String.valueOf(armor.getWeight()));
        physicalAcField.setText(String.valueOf(armor.getPhysicalAC()));
        elementalAcField.setText(String.valueOf(armor.getElementalAC()));
        armorDescriptionField.setText(armor.getDescription());
    }//GEN-LAST:event_armorTableMouseClicked

    private void newArmorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newArmorButtonActionPerformed
        clearArmorForm();
    }//GEN-LAST:event_newArmorButtonActionPerformed

    private void saveArmorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveArmorButtonActionPerformed
        try {
            int row = armorTable.getSelectedRow();
            Integer id = row >= 0 ? armors.get(row).getId() : null;
            service.saveArmor(new Armor(id, armorNameField.getText(), armorDescriptionField.getText(),
                    decimal(armorWeightField, "Weight"), number(physicalAcField, "Physical AC"),
                    number(elementalAcField, "Elemental AC")));
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveArmorButtonActionPerformed

    private void deleteArmorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteArmorButtonActionPerformed
        int row = armorTable.getSelectedRow();
        if (row < 0) {
            showMessage("Click an armor in the table first.");
            return;
        }
        if (!confirm("Delete the armor " + armors.get(row).getName() + "?")) {
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
        int row = ammunitionTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        Amunition ammo = ammunition.get(row);
        ammoNameField.setText(ammo.getName());
        ammoWeightField.setText(String.valueOf(ammo.getWeight()));
        ammoDamageField.setText(ammo.getDamageDice());
        ammoDescriptionField.setText(ammo.getDescription());
    }//GEN-LAST:event_ammunitionTableMouseClicked

    private void newAmmoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAmmoButtonActionPerformed
        clearAmmoForm();
    }//GEN-LAST:event_newAmmoButtonActionPerformed

    private void saveAmmoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAmmoButtonActionPerformed
        try {
            if (ammoNameField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Ammunition name must not be null");
            }
            int row = ammunitionTable.getSelectedRow();
            Amunition ammo = new Amunition();
            ammo.setId(row >= 0 ? ammunition.get(row).getId() : null);
            ammo.setName(ammoNameField.getText());
            ammo.setWeight(decimal(ammoWeightField, "Weight"));
            ammo.setDamageDice(ammoDamageField.getText());
            ammo.setDescription(ammoDescriptionField.getText());
            ammo.setCategory(ItemCategory.AMMUNITION);
            service.saveAmmunition(ammo);
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveAmmoButtonActionPerformed

    private void deleteAmmoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAmmoButtonActionPerformed
        int row = ammunitionTable.getSelectedRow();
        if (row < 0) {
            showMessage("Click an ammunition in the table first.");
            return;
        }
        if (!confirm("Delete the ammunition " + ammunition.get(row).getName() + "?")) {
            return;
        }
        try {
            service.deleteItem(ammunition.get(row).getId());
            loadItems();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_deleteAmmoButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel abilitiesButtons;
    private javax.swing.JPanel abilitiesFields;
    private javax.swing.JPanel abilitiesForm;
    private javax.swing.JScrollPane abilitiesScroll;
    private javax.swing.JPanel abilitiesTab;
    private javax.swing.JTable abilitiesTable;
    private javax.swing.JTextField abilityDescriptionField;
    private javax.swing.JLabel abilityDescriptionLabel;
    private javax.swing.JTextField abilityNameField;
    private javax.swing.JLabel abilityNameLabel;
    private javax.swing.JComboBox<model.ActionType> actionTypeCombo;
    private javax.swing.JLabel actionTypeLabel;
    private javax.swing.JCheckBox activeCheck;
    private javax.swing.JLabel activeLabel;
    private javax.swing.JTextField ageField;
    private javax.swing.JLabel ageLabel;
    private javax.swing.JTextField ammoDamageField;
    private javax.swing.JLabel ammoDamageLabel;
    private javax.swing.JTextField ammoDescriptionField;
    private javax.swing.JLabel ammoDescriptionLabel;
    private javax.swing.JTextField ammoNameField;
    private javax.swing.JLabel ammoNameLabel;
    private javax.swing.JTextField ammoWeightField;
    private javax.swing.JLabel ammoWeightLabel;
    private javax.swing.JPanel ammunitionButtons;
    private javax.swing.JPanel ammunitionFields;
    private javax.swing.JPanel ammunitionForm;
    private javax.swing.JScrollPane ammunitionScroll;
    private javax.swing.JPanel ammunitionTab;
    private javax.swing.JTable ammunitionTable;
    private javax.swing.JPanel armorButtons;
    private javax.swing.JTextField armorDescriptionField;
    private javax.swing.JLabel armorDescriptionLabel;
    private javax.swing.JPanel armorFields;
    private javax.swing.JPanel armorForm;
    private javax.swing.JTextField armorNameField;
    private javax.swing.JLabel armorNameLabel;
    private javax.swing.JScrollPane armorScroll;
    private javax.swing.JPanel armorTab;
    private javax.swing.JTable armorTable;
    private javax.swing.JTextField armorWeightField;
    private javax.swing.JLabel armorWeightLabel;
    private javax.swing.JTextField baseHealthField;
    private javax.swing.JLabel baseHealthLabel;
    private javax.swing.JTextField baseManaField;
    private javax.swing.JLabel baseManaLabel;
    private javax.swing.JTextField baseSanityField;
    private javax.swing.JLabel baseSanityLabel;
    private javax.swing.JTextField baseStaminaField;
    private javax.swing.JLabel baseStaminaLabel;
    private javax.swing.JTextField bondNameField;
    private javax.swing.JLabel bondNameLabel;
    private javax.swing.JTextField bondValueField;
    private javax.swing.JLabel bondValueLabel;
    private javax.swing.JPanel bondsButtons;
    private javax.swing.JPanel bondsFields;
    private javax.swing.JPanel bondsForm;
    private javax.swing.JScrollPane bondsScroll;
    private javax.swing.JPanel bondsTab;
    private javax.swing.JTable bondsTable;
    private javax.swing.JComboBox<model.AbilityCategory> categoryCombo;
    private javax.swing.JLabel categoryLabel;
    private javax.swing.JButton changePhotoButton;
    private javax.swing.JPanel characterButtons;
    private javax.swing.JPanel characterFields;
    private javax.swing.JList<model.PlayerCharacter> characterList;
    private javax.swing.JScrollPane characterScroll;
    private javax.swing.JPanel characterTab;
    private javax.swing.JTextField classField;
    private javax.swing.JLabel classLabel;
    private javax.swing.JTextField costField;
    private javax.swing.JLabel costLabel;
    private javax.swing.JComboBox<model.CostType> costTypeCombo;
    private javax.swing.JLabel costTypeLabel;
    private javax.swing.JButton deleteAbilityButton;
    private javax.swing.JButton deleteAmmoButton;
    private javax.swing.JButton deleteArmorButton;
    private javax.swing.JButton deleteBondButton;
    private javax.swing.JButton deleteCharacterButton;
    private javax.swing.JButton deleteEntryButton;
    private javax.swing.JButton deleteItemButton;
    private javax.swing.JButton deleteSkillButton;
    private javax.swing.JButton deleteWeaponButton;
    private javax.swing.JTextField elementalAcField;
    private javax.swing.JLabel elementalAcLabel;
    private javax.swing.JCheckBox equippedCheck;
    private javax.swing.JLabel equippedLabel;
    private javax.swing.JButton forgetButton;
    private javax.swing.JTextField healthField;
    private javax.swing.JLabel healthLabel;
    private javax.swing.JPanel inventoryButtons;
    private javax.swing.JPanel inventoryFields;
    private javax.swing.JPanel inventoryForm;
    private javax.swing.JScrollPane inventoryScroll;
    private javax.swing.JPanel inventoryTab;
    private javax.swing.JTable inventoryTable;
    private javax.swing.JComboBox<String> itemCombo;
    private javax.swing.JTextField itemDescriptionField;
    private javax.swing.JLabel itemDescriptionLabel;
    private javax.swing.JLabel itemLabel;
    private javax.swing.JTextField itemNameField;
    private javax.swing.JLabel itemNameLabel;
    private javax.swing.JTextField itemWeightField;
    private javax.swing.JLabel itemWeightLabel;
    private javax.swing.JPanel itemsButtons;
    private javax.swing.JPanel itemsFields;
    private javax.swing.JPanel itemsForm;
    private javax.swing.JScrollPane itemsScroll;
    private javax.swing.JPanel itemsTab;
    private javax.swing.JTable itemsTable;
    private javax.swing.JTextField languagesField;
    private javax.swing.JLabel languagesLabel;
    private javax.swing.JButton learnButton;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JTextField levelField;
    private javax.swing.JLabel levelLabel;
    private javax.swing.JTextField manaField;
    private javax.swing.JLabel manaLabel;
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JButton newAbilityButton;
    private javax.swing.JButton newAmmoButton;
    private javax.swing.JButton newArmorButton;
    private javax.swing.JButton newBondButton;
    private javax.swing.JButton newCharacterButton;
    private javax.swing.JButton newEntryButton;
    private javax.swing.JButton newItemButton;
    private javax.swing.JButton newSkillButton;
    private javax.swing.JButton newWeaponButton;
    private javax.swing.JTextField originField;
    private javax.swing.JLabel originLabel;
    private javax.swing.JTextField physicalAcField;
    private javax.swing.JLabel physicalAcLabel;
    private javax.swing.JTextField quantityField;
    private javax.swing.JLabel quantityLabel;
    private javax.swing.JTextField raceField;
    private javax.swing.JLabel raceLabel;
    private javax.swing.JTextField sanityField;
    private javax.swing.JLabel sanityLabel;
    private javax.swing.JButton saveAbilityButton;
    private javax.swing.JButton saveAmmoButton;
    private javax.swing.JButton saveArmorButton;
    private javax.swing.JButton saveBondButton;
    private javax.swing.JButton saveCharacterButton;
    private javax.swing.JButton saveEntryButton;
    private javax.swing.JButton saveItemButton;
    private javax.swing.JButton saveSkillButton;
    private javax.swing.JButton saveWeaponButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JTextField skillNameField;
    private javax.swing.JLabel skillNameLabel;
    private javax.swing.JTextField skillValueField;
    private javax.swing.JLabel skillValueLabel;
    private javax.swing.JPanel skillsButtons;
    private javax.swing.JPanel skillsFields;
    private javax.swing.JPanel skillsForm;
    private javax.swing.JScrollPane skillsScroll;
    private javax.swing.JPanel skillsTab;
    private javax.swing.JTable skillsTable;
    private javax.swing.JTextField staminaField;
    private javax.swing.JLabel staminaLabel;
    private javax.swing.JTextField subclassField;
    private javax.swing.JLabel subclassLabel;
    private javax.swing.JTabbedPane tabs;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JTextField weaponAttributeField;
    private javax.swing.JLabel weaponAttributeLabel;
    private javax.swing.JTextField weaponDamageField;
    private javax.swing.JLabel weaponDamageLabel;
    private javax.swing.JTextField weaponDescriptionField;
    private javax.swing.JLabel weaponDescriptionLabel;
    private javax.swing.JTextField weaponMultiplierField;
    private javax.swing.JLabel weaponMultiplierLabel;
    private javax.swing.JTextField weaponNameField;
    private javax.swing.JLabel weaponNameLabel;
    private javax.swing.JTextField weaponRangeField;
    private javax.swing.JLabel weaponRangeLabel;
    private javax.swing.JTextField weaponWeightField;
    private javax.swing.JLabel weaponWeightLabel;
    private javax.swing.JPanel weaponsButtons;
    private javax.swing.JPanel weaponsFields;
    private javax.swing.JPanel weaponsForm;
    private javax.swing.JScrollPane weaponsScroll;
    private javax.swing.JPanel weaponsTab;
    private javax.swing.JTable weaponsTable;
    // End of variables declaration//GEN-END:variables
}
