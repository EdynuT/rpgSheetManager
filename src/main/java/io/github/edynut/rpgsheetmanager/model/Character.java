package io.github.edynut.rpgsheetmanager.model;

public class Character {
    private String nome;
    private StatusBase statusBase = new StatusBase();
    private StatusSecundario statusSecundarios = new StatusSecundario();
    private java.util.Map<String, Pericia> pericias = new java.util.LinkedHashMap<>();
    private Item[] itens = new Item[0];
    private Armamento[] armamentos = new Armamento[0];
    private Municao[] municoes = new Municao[0];
    private java.util.Map<String, DefesaPeca> defesas = new java.util.LinkedHashMap<>();
    private String[] tiposDanoDefesa = new String[0];
    private HabilidadePassiva[] habilidadesPassivas = new HabilidadePassiva[0];
    private HabilidadeAtiva[] habilidadesAtivas = new HabilidadeAtiva[0];
    private Magia[] magias = new Magia[0];
    private Magia[] cancoes = new Magia[0];   // mesma estrutura, categoria diferente
    private Magia[] alquimias = new Magia[0]; // idem
    private Debuff[] debuffs = new Debuff[0];
    private Pet[] pets = new Pet[0];
    private Favorito[] favoritos = new Favorito[0];
    private Pacto[] pactos = new Pacto[0];
    private Trauma[] traumas = new Trauma[0];

    public Character() {}

    public Character(String nome) {
        this.nome = nome;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public StatusBase getStatusBase() { return statusBase; }
    public void setStatusBase(StatusBase statusBase) { this.statusBase = statusBase; }

    public StatusSecundario getStatusSecundarios() { return statusSecundarios; }
    public void setStatusSecundarios(StatusSecundario statusSecundarios) { this.statusSecundarios = statusSecundarios; }

    public java.util.Map<String, Pericia> getPericias() { return pericias; }
    public void setPericias(java.util.Map<String, Pericia> pericias) { this.pericias = pericias; }

    public Item[] getItens() { return itens; }
    public void setItens(Item[] itens) { this.itens = itens; }

    public Armamento[] getArmamentos() { return armamentos; }
    public void setArmamentos(Armamento[] armamentos) { this.armamentos = armamentos; }

    public Municao[] getMunicoes() { return municoes; }
    public void setMunicoes(Municao[] municoes) { this.municoes = municoes; }

    public java.util.Map<String, DefesaPeca> getDefesas() { return defesas; }
    public void setDefesas(java.util.Map<String, DefesaPeca> defesas) { this.defesas = defesas; }

    public String[] getTiposDanoDefesa() { return tiposDanoDefesa; }
    public void setTiposDanoDefesa(String[] tiposDanoDefesa) { this.tiposDanoDefesa = tiposDanoDefesa; }

    public HabilidadePassiva[] getHabilidadesPassivas() { return habilidadesPassivas; }
    public void setHabilidadesPassivas(HabilidadePassiva[] habilidadesPassivas) { this.habilidadesPassivas = habilidadesPassivas; }

    public HabilidadeAtiva[] getHabilidadesAtivas() { return habilidadesAtivas; }
    public void setHabilidadesAtivas(HabilidadeAtiva[] habilidadesAtivas) { this.habilidadesAtivas = habilidadesAtivas; }

    public Magia[] getMagias() { return magias; }
    public void setMagias(Magia[] magias) { this.magias = magias; }

    public Magia[] getCancoes() { return cancoes; }
    public void setCancoes(Magia[] cancoes) { this.cancoes = cancoes; }

    public Magia[] getAlquimias() { return alquimias; }
    public void setAlquimias(Magia[] alquimias) { this.alquimias = alquimias; }

    public Debuff[] getDebuffs() { return debuffs; }
    public void setDebuffs(Debuff[] debuffs) { this.debuffs = debuffs; }

    public Pet[] getPets() { return pets; }
    public void setPets(Pet[] pets) { this.pets = pets; }

    public Favorito[] getFavoritos() { return favoritos; }
    public void setFavoritos(Favorito[] favoritos) { this.favoritos = favoritos; }

    public Pacto[] getPactos() { return pactos; }
    public void setPactos(Pacto[] pactos) { this.pactos = pactos; }

    public Trauma[] getTraumas() { return traumas; }
    public void setTraumas(Trauma[] traumas) { this.traumas = traumas; }

    // ------------------------------------------------------------------
    // StatusBase
    // ------------------------------------------------------------------

    public static class StatusBase {
        private int nivel = 1;
        private String raca = "";
        private String classe = "";
        private String subclasse = "";
        private String origem = "";
        private ResourceStat vida = new ResourceStat();
        private ResourceStat mana = new ResourceStat();
        private ResourceStat esforco = new ResourceStat();
        private ResourceStat sanidade = new ResourceStat();

        public StatusBase() {} 

        public int getNivel() { return nivel; }
        public void setNivel(int nivel) { this.nivel = nivel; }

        public String getRaca() { return raca; }
        public void setRaca(String raca) { this.raca = raca; }

        public String getClasse() { return classe; }
        public void setClasse(String classe) { this.classe = classe; }

        public String getSubclasse() { return subclasse; }
        public void setSubclasse(String subclasse) { this.subclasse = subclasse; }

        public String getOrigem() { return origem; }
        public void setOrigem(String origem) { this.origem = origem; }

        public ResourceStat getVida() { return vida; }
        public void setVida(ResourceStat vida) { this.vida = vida; }

        public ResourceStat getMana() { return mana; }
        public void setMana(ResourceStat mana) { this.mana = mana; }

        public ResourceStat getEsforco() { return esforco; }
        public void setEsforco(ResourceStat esforco) { this.esforco = esforco; }

        public ResourceStat getSanidade() { return sanidade; }
        public void setSanidade(ResourceStat sanidade) { this.sanidade = sanidade; }
    }

    // ------------------------------------------------------------------
    // StatusSecundarios
    // ------------------------------------------------------------------

    public static class StatusSecundario {
        private int esquiva = 0;
        private int ataque = 0;
        private int bloqueio = 0;
        private int movimento = 0;
        private int caNatural = 0;
        private int caMagias = 0;
        private double pesoEquipamento = 0.0;
        private double pesoLimite = 0.0;

        public StatusSecundario() {}

        public int getEsquiva() { return esquiva; }
        public void setEsquiva(int esquiva) { this.esquiva = esquiva; }

        public int getAtaque() { return ataque; }
        public void setAtaque(int ataque) { this.ataque = ataque; }

        public int getBloqueio() { return bloqueio; }
        public void setBloqueio(int bloqueio) { this.bloqueio = bloqueio; }

        public int getMovimento() { return movimento; }
        public void setMovimento(int movimento) { this.movimento = movimento; }

        public int getCaNatural() { return caNatural; }
        public void setCaNatural(int caNatural) { this.caNatural = caNatural; }

        public int getCaMagias() { return caMagias; }
        public void setCaMagias(int caMagias) { this.caMagias = caMagias; }

        public double getPesoEquipamento() { return pesoEquipamento; }
        public void setPesoEquipamento(double pesoEquipamento) { this.pesoEquipamento = pesoEquipamento; }

        public double getPesoLimite() { return pesoLimite; }
        public void setPesoLimite(double pesoLimite) { this.pesoLimite = pesoLimite; }
    }

    // ------------------------------------------------------------------
    // Pericias 
    // ------------------------------------------------------------------

    public static class Pericia {
        int valor = 0;
        int modificador = 0;
        String[] buffsDebuffs = new String[0];

        public Pericia() {}

        public int getValor() { return valor; }
        public void setValor(int valor) { this.valor = valor; }

        public String[] getBuffsDebuffs() { return buffsDebuffs; }
        public void setBuffsDebuffs(String[] buffsDebuffs) { this.buffsDebuffs = buffsDebuffs; }

        public int getModificador() { return modificador; }
        public void setModificador(int modificador) { this.modificador = modificador; }
    }

    // ------------------------------------------------------------------
    // Itens
    // ------------------------------------------------------------------

    public static class Item {
    private String id = java.util.UUID.randomUUID().toString();
    private String nome = "";
    private String descricao = "";
    private String categoria = "Geral";
    private String efeito = "";
    private boolean equipado = false;
    private double peso = 0.0;
    private int quantidade = 1;

    public Item() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nomeItem) { this.nome = nomeItem; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getEfeito() { return efeito; }
    public void setEfeito(String efeito) { this.efeito = efeito; }

    public boolean isEquipado() { return equipado; }
    public void setEquipado(boolean equipado) { this.equipado = equipado; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    }

    // ------------------------------------------------------------------
    // Armamentos
    // ------------------------------------------------------------------

    public static class Armamento {
        private String id = java.util.UUID.randomUUID().toString();
        private String nome = "";
        private String descricao = "";
        private String danoNormal = "";
        private String danoCritico = "";
        private boolean equipado = false;
        private double peso = 0.0;
        private HabilidadePassiva[] habilidadesPassivas = new HabilidadePassiva[0];
        private HabilidadeAtiva[] habilidadesAtivas = new HabilidadeAtiva[0];

        public Armamento() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nomeArmamento) { this.nome = nomeArmamento; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public String getDanoNormal() { return danoNormal; }
        public void setDanoNormal(String danoNormal) { this.danoNormal = danoNormal; }

        public String getDanoCritico() { return danoCritico; }
        public void setDanoCritico(String danoCritico) { this.danoCritico = danoCritico; }

        public boolean isEquipado() { return equipado; }
        public void setEquipado(boolean equipado) { this.equipado = equipado; }

        public double getPeso() { return peso; }
        public void setPeso(double peso) { this.peso = peso; }

        public HabilidadePassiva[] getHabilidadesPassivas() { return habilidadesPassivas; }
        public void setHabilidadesPassivas(HabilidadePassiva[] habilidadesPassivas) { this.habilidadesPassivas = habilidadesPassivas; }

        public HabilidadeAtiva[] getHabilidadesAtivas() { return habilidadesAtivas; }
        public void setHabilidadesAtivas(HabilidadeAtiva[] habilidadesAtivas) { this.habilidadesAtivas = habilidadesAtivas; }
    }

    // ------------------------------------------------------------------
    // Munições
    // ------------------------------------------------------------------

    public static class Municao {
        private String id = java.util.UUID.randomUUID().toString();
        private String nome = "";
        private String tipo = "";
        private int quantidade = 0;
        private String descricao = "";
        private String dano = "";
        private double peso = 0.0;

        public Municao() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nomeMunicao) { this.nome = nomeMunicao; }

        public String getTipo() { return tipo; }
        public void setTipo(String tipo) { this.tipo = tipo; }

        public int getQuantidade() { return quantidade; }
        public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public String getDano() { return dano; }
        public void setDano(String dano) { this.dano = dano; }

        public double getPeso() { return peso; }
        public void setPeso(double peso) { this.peso = peso; }
    }
    
    // ------------------------------------------------------------------
    // Defesas
    // ------------------------------------------------------------------

    public static class DefesaPeca {
        private String nome = "";
        private java.util.Map<String, Integer> caTipos = new java.util.LinkedHashMap<>();
        private double peso = 0.0;

        public DefesaPeca() {}

        public String getNome() { return nome; }
        public void setNome(String nomeArmadura) { this.nome = nomeArmadura; }

        public java.util.Map<String, Integer> getCaTipos() { return caTipos; }
        public void setCaTipos(java.util.Map<String, Integer> caTipos) { this.caTipos = caTipos; }

        public double getPeso() { return peso; }
        public void setPeso(double peso) { this.peso = peso; }
    }

    // ------------------------------------------------------------------
    // Habilidades
    // ------------------------------------------------------------------

    public static class HabilidadePassiva {
        private String id = java.util.UUID.randomUUID().toString();
        private String nome = "";
        private String descricao = "";
        private String efeito = "";

        public HabilidadePassiva() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nomeHabilidadePassiva) { this.nome = nomeHabilidadePassiva; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public String getEfeito() { return efeito; }
        public void setEfeito(String efeito) { this.efeito = efeito; }
    }

    public static class HabilidadeAtiva {
        private String id = java.util.UUID.randomUUID().toString();
        private String nome = "";
        private String descricao = "";
        private String efeito = "";
        private String acaoTipo = "Normal";
        private String custoTipo = "Esforço";
        private int custoValor = 0;

        public HabilidadeAtiva() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nomeHabilidadeAtiva) { this.nome = nomeHabilidadeAtiva; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public String getEfeito() { return efeito; }
        public void setEfeito(String efeito) { this.efeito = efeito; }

        public String getAcaoTipo() { return acaoTipo; }
        public void setAcaoTipo(String acaoTipo) { this.acaoTipo = acaoTipo; }

        public String getCustoTipo() { return custoTipo; }
        public void setCustoTipo(String custoTipo) { this.custoTipo = custoTipo; }

        public int getCustoValor() { return custoValor; }
        public void setCustoValor(int custoValor) { this.custoValor = custoValor; }
    }

    // ------------------------------------------------------------------
    // Magias
    // ------------------------------------------------------------------

    public static class Magia {
        private String id = java.util.UUID.randomUUID().toString();
        private String categoria = "Magia";
        private String nome = "";
        private String descricao = "";
        private String danoNormal = "";
        private String danoCritico = "";
        private String efeito = "";
        private String acaoTipo = "Normal";
        private String custoTipo = "Mana";
        private int custoValor = 0;

        public Magia() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getCategoria() { return categoria; }
        public void setCategoria(String categoria) { this.categoria = categoria; }

        public String getNome() { return nome; }
        public void setNome(String nomeMagia) { this.nome = nomeMagia; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public String getDanoNormal() { return danoNormal; }
        public void setDanoNormal(String danoNormal) { this.danoNormal = danoNormal; }

        public String getDanoCritico() { return danoCritico; }
        public void setDanoCritico(String danoCritico) { this.danoCritico = danoCritico; }
        
        public String getEfeito() { return efeito; }
        public void setEfeito(String efeito) { this.efeito = efeito; }
        
        public String getAcaoTipo() { return acaoTipo; }
        public void setAcaoTipo(String acaoTipo) { this.acaoTipo = acaoTipo; }

        public String getCustoTipo() { return custoTipo; }
        public void setCustoTipo(String custoTipo) { this.custoTipo = custoTipo; }

        public int getCustoValor() { return custoValor; }
        public void setCustoValor(int custoValor) { this.custoValor = custoValor; }
    }

    // Sistematicamente isso n tem diferença alguma de magia senão pela nomeação, então nem sei se compensa criar uma classe separada para isso, o mesmo acontece com alquimias
    //public static class Cancao {
    //    
    //}

    // ------------------------------------------------------------------
    //  Buffs, Debuffs, Pets, Favoritos, Pactos, Traumas
    // ------------------------------------------------------------------

    public static class Buff {
        private String id = java.util.UUID.randomUUID().toString();
        private String nome = "";
        private String descricao = "";
        private int duracao = 0;
        private String efeito = "";

        public Buff() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nomeBuff) { this.nome = nomeBuff; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public int getDuracao() { return duracao; }
        public void setDuracao(int duracao) { this.duracao = duracao; }

        public String getEfeito() { return efeito; }
        public void setEfeito(String efeito) { this.efeito = efeito; }
    }

    public static class Debuff {
        private String id = java.util.UUID.randomUUID().toString();
        private String nome = "";
        private String descricao = "";
        private int duracao = 0;
        private String efeito = "";

        public Debuff() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nomeDebuff) { this.nome = nomeDebuff; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public int getDuracao() { return duracao; }
        public void setDuracao(int duracao) { this.duracao = duracao; }

        public String getEfeito() { return efeito; }
        public void setEfeito(String efeito) { this.efeito = efeito; }
    }

    public static class Pet {
        private String id = java.util.UUID.randomUUID().toString();
        private String nome = "";
        private String descricao = "";

        public Pet() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nomePet) { this.nome = nomePet; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }

    public static class Favorito {
        private String id = java.util.UUID.randomUUID().toString();
        private String nome = "";
        private String descricao = "";

        public Favorito() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nomeFavorito) { this.nome = nomeFavorito; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }
    }

    public static class Pacto {
        private String id = java.util.UUID.randomUUID().toString();
        private String nome = "";
        private String descricao = "";
        private Buff[] buffs = new Buff[0];
        private Debuff[] debuffs = new Debuff[0];

        public Pacto() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nomePacto) { this.nome = nomePacto; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public Buff[] getBuffs() { return buffs; }
        public void setBuffs(Buff[] buffs) { this.buffs = buffs; }

        public Debuff[] getDebuffs() { return debuffs; }
        public void setDebuffs(Debuff[] debuffs) { this.debuffs = debuffs; }
    }

    public static class Trauma {
        private String id = java.util.UUID.randomUUID().toString();
        private String nome = "";
        private String descricao = "";
        private Debuff[] debuffs = new Debuff[0];
        private Buff[] buffs = new Buff[0];
        private boolean ativo = false;

        public Trauma() {}

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getNome() { return nome; }
        public void setNome(String nomeTrauma) { this.nome = nomeTrauma; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public Debuff[] getDebuffs() { return debuffs; }
        public void setDebuffs(Debuff[] debuffs) { this.debuffs = debuffs; }

        public Buff[] getBuffs() { return buffs; }
        public void setBuffs(Buff[] buffs) { this.buffs = buffs; }

        public boolean isAtivo() { return ativo; }
        public void setAtivo(boolean ativo) { this.ativo = ativo; }
    }
}
