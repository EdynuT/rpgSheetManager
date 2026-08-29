package src.model;

public class PlayerCharacter {
    private Integer id;
    private Integer level;
    private String name;
    private Integer age;
    private String race;
    private String characterClass;
    private String subclass;
    private String origin;
    private String language;
    // Stats
    private int baseHealth;
    private int baseMana;
    private int baseStamina;
    private int baseSanity;
    private int currentHealth;
    private int currentMana;
    private int currentStamina;
    private int currentSanity;
    
    // Empty constructor required for instantiation via DAO
    public PlayerCharacter() {
        
    }
    
    public PlayerCharacter(Integer id, Integer level, String name, Integer age, String race, String characterClass, String subclass,
            String origin, String language, int baseHealth, int baseMana, int baseStamina, int baseSanity,
            int currentHealth, int currentMana, int currentStamina, int currentSanity) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.age = age;
        this.race = race;
        this.characterClass = characterClass;
        this.subclass = subclass;
        this.origin = origin;
        this.language = language;
        this.baseHealth = baseHealth;
        this.baseMana = baseMana;
        this.baseStamina = baseStamina;
        this.baseSanity = baseSanity;
        this.currentHealth = currentHealth;
        this.currentMana = currentMana;
        this.currentStamina = currentStamina;
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
   
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public int getCurrentHealth() {
        return currentHealth; 
    }
    
    public int getBaseHealth() {
        return baseHealth; 
    }

    public void setBaseHealth(int baseHealth) {
        this.baseHealth = baseHealth; 
    }

    public int getBaseMana() {
        return baseMana; 
    }

    public void setBaseMana(int baseMana) {
        this.baseMana = baseMana; 
    }

    public int getBaseStamina() {
        return baseStamina; 
    }

    public void setBaseStamina(int baseStamina) {
        this.baseStamina = baseStamina; 
    }

    public int getBaseSanity() {
        return baseSanity; 
    }

    public void setBaseSanity(int baseSanity) {
        this.baseSanity = baseSanity; 
    }

    public int getCurrentMana() {
        return currentMana; 
    }

    public void setCurrentMana(int currentMana) {
        this.currentMana = currentMana; 
    }

    public int getCurrentStamina() {
        return currentStamina; 
    }

    public void setCurrentStamina(int currentStamina) {
        this.currentStamina = currentStamina; 
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
