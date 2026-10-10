/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edynu.rpgSheetManager.gui;

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
import com.edynu.rpgSheetManager.model.Ability;
import com.edynu.rpgSheetManager.model.AbilityCategory;
import com.edynu.rpgSheetManager.model.ActionType;
import com.edynu.rpgSheetManager.model.Amunition;
import com.edynu.rpgSheetManager.model.Armor;
import com.edynu.rpgSheetManager.model.CharacterAbility;
import com.edynu.rpgSheetManager.model.CostType;
import com.edynu.rpgSheetManager.model.Inventory;
import com.edynu.rpgSheetManager.model.Item;
import com.edynu.rpgSheetManager.model.ItemCategory;
import com.edynu.rpgSheetManager.model.PlayerCharacter;
import com.edynu.rpgSheetManager.model.SecondBound;
import com.edynu.rpgSheetManager.model.Skill;
import com.edynu.rpgSheetManager.model.Weapon;
import com.edynu.rpgSheetManager.service.MainWindowService;

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
        cmbCategory.setModel(new DefaultComboBoxModel<>(AbilityCategory.values()));
        cmbActionType.setModel(new DefaultComboBoxModel<>(ActionType.values()));
        cmbCostType.setModel(new DefaultComboBoxModel<>(CostType.values()));
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
        loadBonds();
        loadAbilities();
        loadInventory();
    }

    private void clearCharacter() {
        selected = null;
        lstCharacter.clearSelection();
        clear(txtName, txtLevel, txtAge, txtRace, txtClass, txtSubclass, txtOrigin, txtLanguages,
                txtHealth, txtBaseHealth, txtMana, txtBaseMana, txtStamina, txtBaseStamina, txtSanity, txtBaseSanity);
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
        DefaultTableModel model = (DefaultTableModel) tblBonds.getModel();
        model.setRowCount(0);
        for (SecondBound bond : bonds) {
            model.addRow(new Object[] {bond.getBoundName(), bond.getValue()});
        }
        clearBondForm();
    }

    private void clearBondForm() {
        tblBonds.clearSelection();
        clear(txtBondName, txtBondValue);
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
        DefaultTableModel model = (DefaultTableModel) tblAbilities.getModel();
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
        tblAbilities.clearSelection();
        clear(txtAbilityName, txtCost, txtAbilityDescription);
        chkActive.setSelected(false);
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
        DefaultTableModel model = (DefaultTableModel) tblInventory.getModel();
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
        tblInventory.clearSelection();
        cmbItem.setSelectedIndex(-1);
        clear(txtQuantity);
        chkEquipped.setSelected(false);
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
        for (Amunition ammo : ammunition) {
            model.addRow(new Object[] {ammo.getName(), ammo.getDamageDice(), ammo.getWeight()});
        }

        cmbItem.removeAllItems();
        for (Item item : allItems) {
            cmbItem.addItem(item.getName());
        }
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
                txtWeaponMultiplier, txtWeaponDescription);
    }

    private void clearArmorForm() {
        tblArmor.clearSelection();
        clear(txtArmorName, txtArmorWeight, txtPhysicalAc, txtElementalAc, txtArmorDescription);
    }

    private void clearAmmoForm() {
        tblAmmunition.clearSelection();
        clear(txtAmmoName, txtAmmoWeight, txtAmmoDamage, txtAmmoDescription);
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
        pnlSkillsButtons = new javax.swing.JPanel();
        btnNewSkill = new javax.swing.JButton();
        btnSaveSkill = new javax.swing.JButton();
        btnDeleteSkill = new javax.swing.JButton();
        pnlBondsTab = new javax.swing.JPanel();
        scrBonds = new javax.swing.JScrollPane();
        tblBonds = new javax.swing.JTable();
        pnlBondsForm = new javax.swing.JPanel();
        pnlBondsFields = new javax.swing.JPanel();
        lblBondName = new javax.swing.JLabel();
        txtBondName = new javax.swing.JTextField();
        lblBondValue = new javax.swing.JLabel();
        txtBondValue = new javax.swing.JTextField();
        pnlBondsButtons = new javax.swing.JPanel();
        btnNewBond = new javax.swing.JButton();
        btnSaveBond = new javax.swing.JButton();
        btnDeleteBond = new javax.swing.JButton();
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
        lblActive = new javax.swing.JLabel();
        chkActive = new javax.swing.JCheckBox();
        pnlAbilitiesButtons = new javax.swing.JPanel();
        btnNewAbility = new javax.swing.JButton();
        btnSaveAbility = new javax.swing.JButton();
        btnDeleteAbility = new javax.swing.JButton();
        btnLearn = new javax.swing.JButton();
        btnForget = new javax.swing.JButton();
        pnlInventoryTab = new javax.swing.JPanel();
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
        btnNewEntry = new javax.swing.JButton();
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
        pnlWeaponsButtons = new javax.swing.JPanel();
        btnNewWeapon = new javax.swing.JButton();
        btnSaveWeapon = new javax.swing.JButton();
        btnDeleteWeapon = new javax.swing.JButton();
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
        pnlAmmunitionButtons = new javax.swing.JPanel();
        btnNewAmmo = new javax.swing.JButton();
        btnSaveAmmo = new javax.swing.JButton();
        btnDeleteAmmo = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RPG Sheet Manager");
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

        lblSearch.setText("Character name");
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

        lblName.setText("Name");
        pnlCharacterFields.add(lblName);

        txtName.setColumns(12);
        pnlCharacterFields.add(txtName);

        lblLevel.setText("Level");
        pnlCharacterFields.add(lblLevel);
        pnlCharacterFields.add(txtLevel);

        lblAge.setText("Age");
        pnlCharacterFields.add(lblAge);
        pnlCharacterFields.add(txtAge);

        lblRace.setText("Race");
        pnlCharacterFields.add(lblRace);
        pnlCharacterFields.add(txtRace);

        lblClass.setText("Class");
        pnlCharacterFields.add(lblClass);
        pnlCharacterFields.add(txtClass);

        lblSubclass.setText("Subclass");
        pnlCharacterFields.add(lblSubclass);
        pnlCharacterFields.add(txtSubclass);

        lblOrigin.setText("Origin");
        pnlCharacterFields.add(lblOrigin);
        pnlCharacterFields.add(txtOrigin);

        lblLanguages.setText("Languages");
        pnlCharacterFields.add(lblLanguages);
        pnlCharacterFields.add(txtLanguages);

        lblHealth.setText("Health");
        pnlCharacterFields.add(lblHealth);
        pnlCharacterFields.add(txtHealth);

        lblBaseHealth.setText("Base health");
        pnlCharacterFields.add(lblBaseHealth);
        pnlCharacterFields.add(txtBaseHealth);

        lblMana.setText("Mana");
        pnlCharacterFields.add(lblMana);
        pnlCharacterFields.add(txtMana);

        lblBaseMana.setText("Base mana");
        pnlCharacterFields.add(lblBaseMana);
        pnlCharacterFields.add(txtBaseMana);

        lblStamina.setText("Stamina");
        pnlCharacterFields.add(lblStamina);
        pnlCharacterFields.add(txtStamina);

        lblBaseStamina.setText("Base stamina");
        pnlCharacterFields.add(lblBaseStamina);
        pnlCharacterFields.add(txtBaseStamina);

        lblSanity.setText("Sanity");
        pnlCharacterFields.add(lblSanity);
        pnlCharacterFields.add(txtSanity);

        lblBaseSanity.setText("Base sanity");
        pnlCharacterFields.add(lblBaseSanity);
        pnlCharacterFields.add(txtBaseSanity);

        pnlCharacterTab.add(pnlCharacterFields, java.awt.BorderLayout.PAGE_START);

        pnlCharacterButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewCharacter.setText("New");
        btnNewCharacter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCharacterButtonActionPerformed(evt);
            }
        });
        pnlCharacterButtons.add(btnNewCharacter);

        btnSaveCharacter.setText("Save");
        btnSaveCharacter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCharacterButtonActionPerformed(evt);
            }
        });
        pnlCharacterButtons.add(btnSaveCharacter);

        btnDeleteCharacter.setText("Delete");
        btnDeleteCharacter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCharacterButtonActionPerformed(evt);
            }
        });
        pnlCharacterButtons.add(btnDeleteCharacter);

        btnChangePhoto.setText("Change photo");
        btnChangePhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePhotoButtonActionPerformed(evt);
            }
        });
        pnlCharacterButtons.add(btnChangePhoto);

        pnlCharacterTab.add(pnlCharacterButtons, java.awt.BorderLayout.CENTER);

        tabMain.addTab("Character", pnlCharacterTab);

        pnlSkillsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlSkillsTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblSkills.setModel(new javax.swing.table.DefaultTableModel(
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
        tblSkills.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                skillsTableMouseClicked(evt);
            }
        });
        scrSkills.setViewportView(tblSkills);

        pnlSkillsTab.add(scrSkills, java.awt.BorderLayout.CENTER);

        pnlSkillsForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlSkillsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblSkillName.setText("Skill");
        pnlSkillsFields.add(lblSkillName);

        txtSkillName.setColumns(12);
        pnlSkillsFields.add(txtSkillName);

        lblSkillValue.setText("Value");
        pnlSkillsFields.add(lblSkillValue);
        pnlSkillsFields.add(txtSkillValue);

        pnlSkillsForm.add(pnlSkillsFields, java.awt.BorderLayout.PAGE_START);

        pnlSkillsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewSkill.setText("New");
        btnNewSkill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSkillButtonActionPerformed(evt);
            }
        });
        pnlSkillsButtons.add(btnNewSkill);

        btnSaveSkill.setText("Save");
        btnSaveSkill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSkillButtonActionPerformed(evt);
            }
        });
        pnlSkillsButtons.add(btnSaveSkill);

        btnDeleteSkill.setText("Delete");
        btnDeleteSkill.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSkillButtonActionPerformed(evt);
            }
        });
        pnlSkillsButtons.add(btnDeleteSkill);

        pnlSkillsForm.add(pnlSkillsButtons, java.awt.BorderLayout.CENTER);

        pnlSkillsTab.add(pnlSkillsForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Skills", pnlSkillsTab);

        pnlBondsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlBondsTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblBonds.setModel(new javax.swing.table.DefaultTableModel(
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
        tblBonds.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bondsTableMouseClicked(evt);
            }
        });
        scrBonds.setViewportView(tblBonds);

        pnlBondsTab.add(scrBonds, java.awt.BorderLayout.CENTER);

        pnlBondsForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlBondsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblBondName.setText("Bond");
        pnlBondsFields.add(lblBondName);

        txtBondName.setColumns(12);
        pnlBondsFields.add(txtBondName);

        lblBondValue.setText("Value");
        pnlBondsFields.add(lblBondValue);
        pnlBondsFields.add(txtBondValue);

        pnlBondsForm.add(pnlBondsFields, java.awt.BorderLayout.PAGE_START);

        pnlBondsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewBond.setText("New");
        btnNewBond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBondButtonActionPerformed(evt);
            }
        });
        pnlBondsButtons.add(btnNewBond);

        btnSaveBond.setText("Save");
        btnSaveBond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBondButtonActionPerformed(evt);
            }
        });
        pnlBondsButtons.add(btnSaveBond);

        btnDeleteBond.setText("Delete");
        btnDeleteBond.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBondButtonActionPerformed(evt);
            }
        });
        pnlBondsButtons.add(btnDeleteBond);

        pnlBondsForm.add(pnlBondsButtons, java.awt.BorderLayout.CENTER);

        pnlBondsTab.add(pnlBondsForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Bonds", pnlBondsTab);

        pnlAbilitiesTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlAbilitiesTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblAbilities.setModel(new javax.swing.table.DefaultTableModel(
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
        tblAbilities.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                abilitiesTableMouseClicked(evt);
            }
        });
        scrAbilities.setViewportView(tblAbilities);

        pnlAbilitiesTab.add(scrAbilities, java.awt.BorderLayout.CENTER);

        pnlAbilitiesForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlAbilitiesFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblAbilityName.setText("Name");
        pnlAbilitiesFields.add(lblAbilityName);

        txtAbilityName.setColumns(12);
        pnlAbilitiesFields.add(txtAbilityName);

        lblCategory.setText("Category");
        pnlAbilitiesFields.add(lblCategory);
        pnlAbilitiesFields.add(cmbCategory);

        lblActionType.setText("Action");
        pnlAbilitiesFields.add(lblActionType);
        pnlAbilitiesFields.add(cmbActionType);

        lblCostType.setText("Cost type");
        pnlAbilitiesFields.add(lblCostType);
        pnlAbilitiesFields.add(cmbCostType);

        lblCost.setText("Cost");
        pnlAbilitiesFields.add(lblCost);
        pnlAbilitiesFields.add(txtCost);

        lblAbilityDescription.setText("Description");
        pnlAbilitiesFields.add(lblAbilityDescription);
        pnlAbilitiesFields.add(txtAbilityDescription);

        lblActive.setText("Active");
        pnlAbilitiesFields.add(lblActive);
        pnlAbilitiesFields.add(chkActive);

        pnlAbilitiesForm.add(pnlAbilitiesFields, java.awt.BorderLayout.PAGE_START);

        pnlAbilitiesButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewAbility.setText("New");
        btnNewAbility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAbilityButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnNewAbility);

        btnSaveAbility.setText("Save");
        btnSaveAbility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAbilityButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnSaveAbility);

        btnDeleteAbility.setText("Delete");
        btnDeleteAbility.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAbilityButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnDeleteAbility);

        btnLearn.setText("Learn");
        btnLearn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                learnButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnLearn);

        btnForget.setText("Forget");
        btnForget.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forgetButtonActionPerformed(evt);
            }
        });
        pnlAbilitiesButtons.add(btnForget);

        pnlAbilitiesForm.add(pnlAbilitiesButtons, java.awt.BorderLayout.CENTER);

        pnlAbilitiesTab.add(pnlAbilitiesForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Abilities", pnlAbilitiesTab);

        pnlInventoryTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlInventoryTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblInventory.setModel(new javax.swing.table.DefaultTableModel(
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

        lblQuantity.setText("Quantity");
        pnlInventoryFields.add(lblQuantity);
        pnlInventoryFields.add(txtQuantity);

        lblEquipped.setText("Equipped");
        pnlInventoryFields.add(lblEquipped);
        pnlInventoryFields.add(chkEquipped);

        pnlInventoryForm.add(pnlInventoryFields, java.awt.BorderLayout.PAGE_START);

        pnlInventoryButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewEntry.setText("New");
        btnNewEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newEntryButtonActionPerformed(evt);
            }
        });
        pnlInventoryButtons.add(btnNewEntry);

        btnSaveEntry.setText("Save");
        btnSaveEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEntryButtonActionPerformed(evt);
            }
        });
        pnlInventoryButtons.add(btnSaveEntry);

        btnDeleteEntry.setText("Delete");
        btnDeleteEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEntryButtonActionPerformed(evt);
            }
        });
        pnlInventoryButtons.add(btnDeleteEntry);

        pnlInventoryForm.add(pnlInventoryButtons, java.awt.BorderLayout.CENTER);

        pnlInventoryTab.add(pnlInventoryForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Inventory", pnlInventoryTab);

        pnlItemsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlItemsTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblItems.setModel(new javax.swing.table.DefaultTableModel(
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
        tblItems.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                itemsTableMouseClicked(evt);
            }
        });
        scrItems.setViewportView(tblItems);

        pnlItemsTab.add(scrItems, java.awt.BorderLayout.CENTER);

        pnlItemsForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlItemsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblItemName.setText("Name");
        pnlItemsFields.add(lblItemName);

        txtItemName.setColumns(12);
        pnlItemsFields.add(txtItemName);

        lblItemWeight.setText("Weight");
        pnlItemsFields.add(lblItemWeight);
        pnlItemsFields.add(txtItemWeight);

        lblItemDescription.setText("Description");
        pnlItemsFields.add(lblItemDescription);
        pnlItemsFields.add(txtItemDescription);

        pnlItemsForm.add(pnlItemsFields, java.awt.BorderLayout.PAGE_START);

        pnlItemsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewItem.setText("New");
        btnNewItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newItemButtonActionPerformed(evt);
            }
        });
        pnlItemsButtons.add(btnNewItem);

        btnSaveItem.setText("Save");
        btnSaveItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveItemButtonActionPerformed(evt);
            }
        });
        pnlItemsButtons.add(btnSaveItem);

        btnDeleteItem.setText("Delete");
        btnDeleteItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteItemButtonActionPerformed(evt);
            }
        });
        pnlItemsButtons.add(btnDeleteItem);

        pnlItemsForm.add(pnlItemsButtons, java.awt.BorderLayout.CENTER);

        pnlItemsTab.add(pnlItemsForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Items", pnlItemsTab);

        pnlWeaponsTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlWeaponsTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblWeapons.setModel(new javax.swing.table.DefaultTableModel(
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
        tblWeapons.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                weaponsTableMouseClicked(evt);
            }
        });
        scrWeapons.setViewportView(tblWeapons);

        pnlWeaponsTab.add(scrWeapons, java.awt.BorderLayout.CENTER);

        pnlWeaponsForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlWeaponsFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblWeaponName.setText("Name");
        pnlWeaponsFields.add(lblWeaponName);

        txtWeaponName.setColumns(12);
        pnlWeaponsFields.add(txtWeaponName);

        lblWeaponWeight.setText("Weight");
        pnlWeaponsFields.add(lblWeaponWeight);
        pnlWeaponsFields.add(txtWeaponWeight);

        lblWeaponAttribute.setText("Attribute");
        pnlWeaponsFields.add(lblWeaponAttribute);
        pnlWeaponsFields.add(txtWeaponAttribute);

        lblWeaponDamage.setText("Damage");
        pnlWeaponsFields.add(lblWeaponDamage);
        pnlWeaponsFields.add(txtWeaponDamage);

        lblWeaponRange.setText("Critical range");
        pnlWeaponsFields.add(lblWeaponRange);
        pnlWeaponsFields.add(txtWeaponRange);

        lblWeaponMultiplier.setText("Critical multiplier");
        pnlWeaponsFields.add(lblWeaponMultiplier);
        pnlWeaponsFields.add(txtWeaponMultiplier);

        lblWeaponDescription.setText("Description");
        pnlWeaponsFields.add(lblWeaponDescription);
        pnlWeaponsFields.add(txtWeaponDescription);

        pnlWeaponsForm.add(pnlWeaponsFields, java.awt.BorderLayout.PAGE_START);

        pnlWeaponsButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewWeapon.setText("New");
        btnNewWeapon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newWeaponButtonActionPerformed(evt);
            }
        });
        pnlWeaponsButtons.add(btnNewWeapon);

        btnSaveWeapon.setText("Save");
        btnSaveWeapon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveWeaponButtonActionPerformed(evt);
            }
        });
        pnlWeaponsButtons.add(btnSaveWeapon);

        btnDeleteWeapon.setText("Delete");
        btnDeleteWeapon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteWeaponButtonActionPerformed(evt);
            }
        });
        pnlWeaponsButtons.add(btnDeleteWeapon);

        pnlWeaponsForm.add(pnlWeaponsButtons, java.awt.BorderLayout.CENTER);

        pnlWeaponsTab.add(pnlWeaponsForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Weapons", pnlWeaponsTab);

        pnlArmorTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlArmorTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblArmor.setModel(new javax.swing.table.DefaultTableModel(
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
        tblArmor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                armorTableMouseClicked(evt);
            }
        });
        scrArmor.setViewportView(tblArmor);

        pnlArmorTab.add(scrArmor, java.awt.BorderLayout.CENTER);

        pnlArmorForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlArmorFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblArmorName.setText("Name");
        pnlArmorFields.add(lblArmorName);

        txtArmorName.setColumns(12);
        pnlArmorFields.add(txtArmorName);

        lblArmorWeight.setText("Weight");
        pnlArmorFields.add(lblArmorWeight);
        pnlArmorFields.add(txtArmorWeight);

        lblPhysicalAc.setText("Physical AC");
        pnlArmorFields.add(lblPhysicalAc);
        pnlArmorFields.add(txtPhysicalAc);

        lblElementalAc.setText("Elemental AC");
        pnlArmorFields.add(lblElementalAc);
        pnlArmorFields.add(txtElementalAc);

        lblArmorDescription.setText("Description");
        pnlArmorFields.add(lblArmorDescription);
        pnlArmorFields.add(txtArmorDescription);

        pnlArmorForm.add(pnlArmorFields, java.awt.BorderLayout.PAGE_START);

        pnlArmorButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewArmor.setText("New");
        btnNewArmor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newArmorButtonActionPerformed(evt);
            }
        });
        pnlArmorButtons.add(btnNewArmor);

        btnSaveArmor.setText("Save");
        btnSaveArmor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveArmorButtonActionPerformed(evt);
            }
        });
        pnlArmorButtons.add(btnSaveArmor);

        btnDeleteArmor.setText("Delete");
        btnDeleteArmor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteArmorButtonActionPerformed(evt);
            }
        });
        pnlArmorButtons.add(btnDeleteArmor);

        pnlArmorForm.add(pnlArmorButtons, java.awt.BorderLayout.CENTER);

        pnlArmorTab.add(pnlArmorForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Armor", pnlArmorTab);

        pnlAmmunitionTab.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlAmmunitionTab.setLayout(new java.awt.BorderLayout(10, 10));

        tblAmmunition.setModel(new javax.swing.table.DefaultTableModel(
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
        tblAmmunition.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ammunitionTableMouseClicked(evt);
            }
        });
        scrAmmunition.setViewportView(tblAmmunition);

        pnlAmmunitionTab.add(scrAmmunition, java.awt.BorderLayout.CENTER);

        pnlAmmunitionForm.setLayout(new java.awt.BorderLayout(0, 10));

        pnlAmmunitionFields.setLayout(new java.awt.GridLayout(0, 2, 5, 5));

        lblAmmoName.setText("Name");
        pnlAmmunitionFields.add(lblAmmoName);

        txtAmmoName.setColumns(12);
        pnlAmmunitionFields.add(txtAmmoName);

        lblAmmoWeight.setText("Weight");
        pnlAmmunitionFields.add(lblAmmoWeight);
        pnlAmmunitionFields.add(txtAmmoWeight);

        lblAmmoDamage.setText("Damage");
        pnlAmmunitionFields.add(lblAmmoDamage);
        pnlAmmunitionFields.add(txtAmmoDamage);

        lblAmmoDescription.setText("Description");
        pnlAmmunitionFields.add(lblAmmoDescription);
        pnlAmmunitionFields.add(txtAmmoDescription);

        pnlAmmunitionForm.add(pnlAmmunitionFields, java.awt.BorderLayout.PAGE_START);

        pnlAmmunitionButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnNewAmmo.setText("New");
        btnNewAmmo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAmmoButtonActionPerformed(evt);
            }
        });
        pnlAmmunitionButtons.add(btnNewAmmo);

        btnSaveAmmo.setText("Save");
        btnSaveAmmo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAmmoButtonActionPerformed(evt);
            }
        });
        pnlAmmunitionButtons.add(btnSaveAmmo);

        btnDeleteAmmo.setText("Delete");
        btnDeleteAmmo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAmmoButtonActionPerformed(evt);
            }
        });
        pnlAmmunitionButtons.add(btnDeleteAmmo);

        pnlAmmunitionForm.add(pnlAmmunitionButtons, java.awt.BorderLayout.CENTER);

        pnlAmmunitionTab.add(pnlAmmunitionForm, java.awt.BorderLayout.LINE_END);

        tabMain.addTab("Ammunition", pnlAmmunitionTab);

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
                    number(txtLevel, "Level"), txtName.getText(), number(txtAge, "Age"), txtRace.getText(),
                    txtClass.getText(), txtSubclass.getText(), txtOrigin.getText(), txtLanguages.getText(),
                    number(txtBaseHealth, "Base health"), number(txtHealth, "Health"),
                    number(txtBaseMana, "Base mana"), number(txtMana, "Mana"),
                    number(txtBaseStamina, "Base stamina"), number(txtStamina, "Stamina"),
                    number(txtBaseSanity, "Base sanity"), number(txtSanity, "Sanity"));
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
            showMessage("Select a character first.");
            return;
        }
        try {
            int row = tblSkills.getSelectedRow();
            Integer id = row >= 0 ? skills.get(row).getId() : null;
            service.saveSkill(new Skill(id, selected.getId(), txtSkillName.getText(), number(txtSkillValue, "Value")));
            loadSkills();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveSkillButtonActionPerformed

    private void deleteSkillButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSkillButtonActionPerformed
        int row = tblSkills.getSelectedRow();
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
        int row = tblBonds.getSelectedRow();
        if (row < 0) {
            return;
        }
        SecondBound bond = bonds.get(row);
        txtBondName.setText(bond.getBoundName());
        txtBondValue.setText(String.valueOf(bond.getValue()));
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
            int row = tblBonds.getSelectedRow();
            Integer id = row >= 0 ? bonds.get(row).getId() : null;
            service.saveBond(new SecondBound(id, selected.getId(), txtBondName.getText(), number(txtBondValue, "Value")));
            loadBonds();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveBondButtonActionPerformed

    private void deleteBondButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBondButtonActionPerformed
        int row = tblBonds.getSelectedRow();
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
        CharacterAbility link = findLearned(ability.getAbilityId());
        chkActive.setSelected(link != null && link.isToggled());
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
                    number(txtCost, "Cost"), txtAbilityDescription.getText()));
            loadAbilities();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveAbilityButtonActionPerformed

    private void deleteAbilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAbilityButtonActionPerformed
        int row = tblAbilities.getSelectedRow();
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
        int row = tblAbilities.getSelectedRow();
        if (selected == null || row < 0) {
            showMessage("Select a character and click an ability in the table first.");
            return;
        }
        try {
            service.learnAbility(selected.getId(), abilities.get(row).getAbilityId(), chkActive.isSelected());
            loadAbilities();
        } catch (SQLException e) {
            showError(e);
        }
    }//GEN-LAST:event_learnButtonActionPerformed

    private void forgetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forgetButtonActionPerformed
        int row = tblAbilities.getSelectedRow();
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
        int row = tblInventory.getSelectedRow();
        if (row < 0) {
            return;
        }
        Inventory entry = inventory.get(row);
        cmbItem.setSelectedIndex(allItems.indexOf(findItem(entry.getItemId())));
        txtQuantity.setText(String.valueOf(entry.getQuantity()));
        chkEquipped.setSelected(entry.isEquipped());
    }//GEN-LAST:event_inventoryTableMouseClicked

    private void newEntryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newEntryButtonActionPerformed
        clearInventoryForm();
    }//GEN-LAST:event_newEntryButtonActionPerformed

    private void saveEntryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEntryButtonActionPerformed
        if (selected == null || cmbItem.getSelectedIndex() < 0) {
            showMessage("Select a character and choose an item first.");
            return;
        }
        try {
            int row = tblInventory.getSelectedRow();
            Integer id = row >= 0 ? inventory.get(row).getId() : null;
            int itemId = allItems.get(cmbItem.getSelectedIndex()).getId();
            service.saveInventory(new Inventory(id, selected.getId(), itemId, number(txtQuantity, "Quantity"), chkEquipped.isSelected()));
            loadInventory();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveEntryButtonActionPerformed

    private void deleteEntryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEntryButtonActionPerformed
        int row = tblInventory.getSelectedRow();
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
                    decimal(txtItemWeight, "Weight"), ItemCategory.GENERAL));
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveItemButtonActionPerformed

    private void deleteItemButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteItemButtonActionPerformed
        int row = tblItems.getSelectedRow();
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
                    decimal(txtWeaponWeight, "Weight"), ItemCategory.WEAPON, txtWeaponAttribute.getText(),
                    txtWeaponDamage.getText(), number(txtWeaponRange, "Critical range"),
                    number(txtWeaponMultiplier, "Critical multiplier")));
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveWeaponButtonActionPerformed

    private void deleteWeaponButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteWeaponButtonActionPerformed
        int row = tblWeapons.getSelectedRow();
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
                    decimal(txtArmorWeight, "Weight"), number(txtPhysicalAc, "Physical AC"),
                    number(txtElementalAc, "Elemental AC")));
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveArmorButtonActionPerformed

    private void deleteArmorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteArmorButtonActionPerformed
        int row = tblArmor.getSelectedRow();
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
        int row = tblAmmunition.getSelectedRow();
        if (row < 0) {
            return;
        }
        Amunition ammo = ammunition.get(row);
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
            if (txtAmmoName.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Ammunition name must not be null");
            }
            int row = tblAmmunition.getSelectedRow();
            Amunition ammo = new Amunition();
            ammo.setId(row >= 0 ? ammunition.get(row).getId() : null);
            ammo.setName(txtAmmoName.getText());
            ammo.setWeight(decimal(txtAmmoWeight, "Weight"));
            ammo.setDamageDice(txtAmmoDamage.getText());
            ammo.setDescription(txtAmmoDescription.getText());
            ammo.setCategory(ItemCategory.AMMUNITION);
            service.saveAmmunition(ammo);
            loadItems();
        } catch (Exception e) {
            showError(e);
        }
    }//GEN-LAST:event_saveAmmoButtonActionPerformed

    private void deleteAmmoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAmmoButtonActionPerformed
        int row = tblAmmunition.getSelectedRow();
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
    private javax.swing.JButton btnChangePhoto;
    private javax.swing.JButton btnDeleteAbility;
    private javax.swing.JButton btnDeleteAmmo;
    private javax.swing.JButton btnDeleteArmor;
    private javax.swing.JButton btnDeleteBond;
    private javax.swing.JButton btnDeleteCharacter;
    private javax.swing.JButton btnDeleteEntry;
    private javax.swing.JButton btnDeleteItem;
    private javax.swing.JButton btnDeleteSkill;
    private javax.swing.JButton btnDeleteWeapon;
    private javax.swing.JButton btnForget;
    private javax.swing.JButton btnLearn;
    private javax.swing.JButton btnNewAbility;
    private javax.swing.JButton btnNewAmmo;
    private javax.swing.JButton btnNewArmor;
    private javax.swing.JButton btnNewBond;
    private javax.swing.JButton btnNewCharacter;
    private javax.swing.JButton btnNewEntry;
    private javax.swing.JButton btnNewItem;
    private javax.swing.JButton btnNewSkill;
    private javax.swing.JButton btnNewWeapon;
    private javax.swing.JButton btnSaveAbility;
    private javax.swing.JButton btnSaveAmmo;
    private javax.swing.JButton btnSaveArmor;
    private javax.swing.JButton btnSaveBond;
    private javax.swing.JButton btnSaveCharacter;
    private javax.swing.JButton btnSaveEntry;
    private javax.swing.JButton btnSaveItem;
    private javax.swing.JButton btnSaveSkill;
    private javax.swing.JButton btnSaveWeapon;
    private javax.swing.JCheckBox chkActive;
    private javax.swing.JCheckBox chkEquipped;
    private javax.swing.JComboBox<com.edynu.rpgSheetManager.model.ActionType> cmbActionType;
    private javax.swing.JComboBox<com.edynu.rpgSheetManager.model.AbilityCategory> cmbCategory;
    private javax.swing.JComboBox<com.edynu.rpgSheetManager.model.CostType> cmbCostType;
    private javax.swing.JComboBox<String> cmbItem;
    private javax.swing.JLabel lblAbilityDescription;
    private javax.swing.JLabel lblAbilityName;
    private javax.swing.JLabel lblActionType;
    private javax.swing.JLabel lblActive;
    private javax.swing.JLabel lblAge;
    private javax.swing.JLabel lblAmmoDamage;
    private javax.swing.JLabel lblAmmoDescription;
    private javax.swing.JLabel lblAmmoName;
    private javax.swing.JLabel lblAmmoWeight;
    private javax.swing.JLabel lblArmorDescription;
    private javax.swing.JLabel lblArmorName;
    private javax.swing.JLabel lblArmorWeight;
    private javax.swing.JLabel lblBaseHealth;
    private javax.swing.JLabel lblBaseMana;
    private javax.swing.JLabel lblBaseSanity;
    private javax.swing.JLabel lblBaseStamina;
    private javax.swing.JLabel lblBondName;
    private javax.swing.JLabel lblBondValue;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblClass;
    private javax.swing.JLabel lblCost;
    private javax.swing.JLabel lblCostType;
    private javax.swing.JLabel lblElementalAc;
    private javax.swing.JLabel lblEquipped;
    private javax.swing.JLabel lblHealth;
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
    private javax.swing.JLabel lblSkillName;
    private javax.swing.JLabel lblSkillValue;
    private javax.swing.JLabel lblStamina;
    private javax.swing.JLabel lblSubclass;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblWeaponAttribute;
    private javax.swing.JLabel lblWeaponDamage;
    private javax.swing.JLabel lblWeaponDescription;
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
    private javax.swing.JPanel pnlBondsButtons;
    private javax.swing.JPanel pnlBondsFields;
    private javax.swing.JPanel pnlBondsForm;
    private javax.swing.JPanel pnlBondsTab;
    private javax.swing.JPanel pnlCharacterButtons;
    private javax.swing.JPanel pnlCharacterFields;
    private javax.swing.JPanel pnlCharacterTab;
    private javax.swing.JPanel pnlInventoryButtons;
    private javax.swing.JPanel pnlInventoryFields;
    private javax.swing.JPanel pnlInventoryForm;
    private javax.swing.JPanel pnlInventoryTab;
    private javax.swing.JPanel pnlItemsButtons;
    private javax.swing.JPanel pnlItemsFields;
    private javax.swing.JPanel pnlItemsForm;
    private javax.swing.JPanel pnlItemsTab;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlSearch;
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
    private javax.swing.JScrollPane scrBonds;
    private javax.swing.JScrollPane scrCharacter;
    private javax.swing.JScrollPane scrInventory;
    private javax.swing.JScrollPane scrItems;
    private javax.swing.JScrollPane scrSkills;
    private javax.swing.JScrollPane scrWeapons;
    private javax.swing.JTabbedPane tabMain;
    private javax.swing.JTable tblAbilities;
    private javax.swing.JTable tblAmmunition;
    private javax.swing.JTable tblArmor;
    private javax.swing.JTable tblBonds;
    private javax.swing.JTable tblInventory;
    private javax.swing.JTable tblItems;
    private javax.swing.JTable tblSkills;
    private javax.swing.JTable tblWeapons;
    private javax.swing.JTextField txtAbilityDescription;
    private javax.swing.JTextField txtAbilityName;
    private javax.swing.JTextField txtAge;
    private javax.swing.JTextField txtAmmoDamage;
    private javax.swing.JTextField txtAmmoDescription;
    private javax.swing.JTextField txtAmmoName;
    private javax.swing.JTextField txtAmmoWeight;
    private javax.swing.JTextField txtArmorDescription;
    private javax.swing.JTextField txtArmorName;
    private javax.swing.JTextField txtArmorWeight;
    private javax.swing.JTextField txtBaseHealth;
    private javax.swing.JTextField txtBaseMana;
    private javax.swing.JTextField txtBaseSanity;
    private javax.swing.JTextField txtBaseStamina;
    private javax.swing.JTextField txtBondName;
    private javax.swing.JTextField txtBondValue;
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
    private javax.swing.JTextField txtSkillName;
    private javax.swing.JTextField txtSkillValue;
    private javax.swing.JTextField txtStamina;
    private javax.swing.JTextField txtSubclass;
    private javax.swing.JTextField txtWeaponAttribute;
    private javax.swing.JTextField txtWeaponDamage;
    private javax.swing.JTextField txtWeaponDescription;
    private javax.swing.JTextField txtWeaponMultiplier;
    private javax.swing.JTextField txtWeaponName;
    private javax.swing.JTextField txtWeaponRange;
    private javax.swing.JTextField txtWeaponWeight;
    // End of variables declaration//GEN-END:variables
}
