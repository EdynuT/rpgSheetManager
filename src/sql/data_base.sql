CREATE TYPE item_category AS ENUM ('WEAPON', 'ARMOR', 'AMMUNITION', 'GENERAL');
CREATE TYPE ability_category AS ENUM ('ACTIVE', 'PASSIVE', 'MAGIC')

CREATE TABLE player_character (
    id SERIAL PRIMARY KEY,
    level INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    race VARCHAR(50) NOT NULL,
    character_class VARCHAR(50) NOT NULL,
    subclass VARCHAR(50),
    origin VARCHAR(50) NOT NULL,
    languages TEXT,
    base_health INT NOT NULL,
    base_mana INT NOT NULL,
    base_stamina INT NOT NULL,
    base_sanity INT NOT NULL,
    current_health INT NOT NULL,
    current_mana INT NOT NULL,
    current_stamina INT NOT NULL,
    current_sanity INT NOT NULL
);

CREATE TABLE ability (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    action_type VARCHAR(20) NOT NULL,
    cost_type VARCHAR(20) NOT NULL,
    cost_value INT NOT NULL DEFAULT 0,
    description TEXT
);

CREATE TABLE item (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    weight DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    category item_category NOT NULL
);

CREATE TABLE ability (
    id SERIAL PRIMARY KEY,
    character_id INT NOT NULL REFERENCES player_character(id) ON DELETE CASCADE,
    ability_name VARCHAR(50) NOT NULL,
    score INT NOT NULL DEFAULT 0,
    UNIQUE (character_id, ability_name)
);

CREATE TABLE secondary_bond (
    id SERIAL PRIMARY KEY,
    character_id INT NOT NULL REFERENCES player_character(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    description TEXT,
    is_active BOOL DEFAULT true
);

CREATE TABLE character_ability (
    character_id INT NOT NULL REFERENCES player_character(id) ON DELETE CASCADE,
    ability_id INT NOT NULL REFERENCES ability(id) ON DELETE CASCADE,
    is_toggled BOOL NOT NULL DEFAULT false,
    PRIMARY KEY (character_id, ability_id)
);

CREATE TABLE weapon (
    item_id INT PRIMARY KEY REFERENCES item(id) ON DELETE CASCADE,
    scaling_attribute VARCHAR(20) NOT NULL,
    damage_dice TEXT,
    flat_damage INT DEFAULT 0,
    critical_range INT NOT NULL DEFAULT 20,
    critical_multiplier INT NOT NULL DEFAULT 2
);

CREATE TABLE armor (
    item_id INT PRIMARY KEY REFERENCES item(id) ON DELETE CASCADE,
    physical_ac INT NOT NULL DEFAULT 0,
    elemental_ac INT NOT NULL DEFAULT 0
);

CREATE TABLE ammunition (
    item_id INT PRIMARY KEY REFERENCES item(id) ON DELETE CASCADE,
    modification_target VARCHAR(50) NOT NULL,
    modifier_value INT NOT NULL,
    condition TEXT
);

CREATE TABLE inventory (
    id SERIAL PRIMARY KEY,
    character_id INT NOT NULL REFERENCES player_character(id) ON DELETE CASCADE,
    item_id INT NOT NULL REFERENCES item(id) ON DELETE CASCADE,
    quantity INT NOT NULL DEFAULT 1 CHECK (quantity > 0),
    is_equipped BOOL NOT NULL DEFAULT false,
    UNIQUE (character_id, item_id, is_equipped)
);
