package model;

public class PlayerCharacter {
    protected Integer id;
    protected Integer level;
    protected String name;
    protected Integer age;
    protected String race;
    protected String characterClass;
    protected String subclass;
    protected String origin;
    protected String language;
    // Stats
    protected int baseHealth;
    protected int currentHealth;
    protected int baseMana;
    protected int currentMana;
    protected int baseStamina;
    protected int currentStamina;
    protected int baseSanity;
    protected int currentSanity;
    
    // Empty constructor required for instantiation via DAO
    public PlayerCharacter() {
        
    }
    
    public PlayerCharacter(Integer id, Integer level, String name, Integer age, String race, String characterClass, String subclass,
            String origin, String language, int baseHealth, int currentHealth, int baseMana, int currentMana,
            int baseStamina, int currentStamina, int baseSanity, int currentSanity) {
        this.id = id;
        this.level = level;
        if (name != null && !name.trim().isEmpty()) {
                this.name = name;
        } else {
            throw new IllegalArgumentException("Character name must not be null!");
        }
        this.age = age;
        this.race = race;
        this.characterClass = characterClass;
        this.subclass = subclass;
        this.origin = origin;
        this.language = language;
        this.baseHealth = baseHealth;
        this.currentHealth = currentHealth;
        this.baseMana = baseMana;
        this.currentMana = currentMana;
        this.baseStamina = baseStamina;
        this.currentStamina = currentStamina;
        this.baseSanity = baseSanity;
        this.currentSanity = currentSanity;
    }

    // Getters and Setters with strict encapsulation
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getLevel() {
        return level;
    }
    
    public void setLevel(Integer level) {
        this.level = level;
    }
    
    public String getName() {
        return name; 
    }
    
    public void setName(String name) {
        this.name = name; 
    }
    
    public Integer getAge() {
        return age; 
    }

    public void setAge(Integer age) {
        this.age = age; 
    }
   
    public int getBaseHealth() {
        return baseHealth;
    }

    public void setBaseHealth(int baseHealth) {
        this.baseHealth = baseHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getBaseMana() {
        return baseMana;
    }

    public void setBaseMana(int baseMana) {
        this.baseMana = baseMana;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int currentMana) {
        this.currentMana = currentMana;
    }

    public int getBaseStamina() {
        return baseStamina;
    }

    public void setBaseStamina(int baseStamina) {
        this.baseStamina = baseStamina;
    }

    public int getCurrentStamina() {
        return currentStamina;
    }

    public void setCurrentStamina(int currentStamina) {
        this.currentStamina = currentStamina;
    }

    public int getBaseSanity() {
        return baseSanity;
    }

    public void setBaseSanity(int baseSanity) {
        this.baseSanity = baseSanity;
    }

    public int getCurrentSanity() {
        return currentSanity;
    }

    public void setCurrentSanity(int currentSanity) {
        this.currentSanity = currentSanity;
    }


    public String getRace() {
        return race; 
    }

    public void setRace(String race) {
        this.race = race; 
    }

    public String getCharacterClass() {
        return characterClass; 
    }

    public void setCharacterClass(String characterClass) {
        this.characterClass = characterClass; 
    }

    public String getSubclass() {
        return subclass; 
    }

    public void setSubclass(String subclass) {
        this.subclass = subclass; 
    }

    public String getOrigin() {
        return origin; 
    }

    public void setOrigin(String origin) {
        this.origin = origin; 
    }

    public String getLanguage() {
        return language; 
    }

    public void setLanguage(String language) {
        this.language = language; 
    }

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", race='" + race + '\'' +
                ", characterClass='" + characterClass + '\'' +
                ", subclass='" + subclass + '\'' +
                ", origin='" + origin + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
