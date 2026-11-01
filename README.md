# 🎲 D20 Chaos — Minecraft Mod

![Minecraft Version](https://img.shields.io/badge/Minecraft-1.21.1-brightgreen.svg)
![Modloader](https://img.shields.io/badge/NeoForge-21.1.256-orange.svg)
![Java Version](https://img.shields.io/badge/Java-21-blue.svg)
![Status](https://img.shields.io/badge/Status-Ativo-success.svg)

> **O mundo do Minecraft transformado em um cassino de sobrevivência imprevisível.**  
> Cada ação cotidiana — andar, minerar, construir, combater ou fabricar — está à mercê do Destino.

---

## 📖 Visão Geral

O **D20 Chaos** é um mod desenvolvido para **Minecraft 1.21.1 (NeoForge)** que introduz um sistema de risco, caos e recompensa baseado em rolagens de dados D20. O mod opera em uma **arquitetura de dupla camada**:

1. **Camada Macro (A Decisão Geral - Ciclo de 20 Minutos):**
   - A cada 20 minutos (24.000 ticks), o dado principal gira na tela com som estrondoso e define o **Tier Global** que dita o "humor do mundo".
   - Configurável em tempo real no jogo via `/d20 timer <segundos>`.

2. **Camada Micro (A Rolagem das Ações - 4% de Gatilho):**
   - Sempre que o jogador realiza uma ação essencial (andar 1 bloco, quebrar bloco, colocar bloco, golpear mob ou fabricar item), o D20 gira sutilmente na interface.
   - Há uma chance matemática de **4% (0.04f)** de acionar um **evento determinístico** da classe correspondente ao Tier ativo.
   - Quando o gatilho de 4% é acionado, o D20 trava o número por 3 segundos com pulso visual, som estelar, explosão radial de partículas e um badge flutuante `★ GATILHO`.

---

## 🎨 Interface & Arte Pixel Art Autêntica

O HUD do D20 Chaos foi desenhado para máxima legibilidade e imersão sem interferir na jogabilidade vanilla:

- **Cards no Canto Superior Esquerdo:**
  - Painel com o nome do Tier ativo (`MODO DEUS`, `MALDIÇÃO MORTAL`, etc.), temporizador regressivo (`02:45`), nível de Caos e taxa de gatilho.
  - 5 cards delimitados com alinhamento perfeito sem sobreposição de texto: `Mineração`, `Locomoção`, `Construção`, `Combate` e `Fabricação`.
  - Card dedicado exibindo o último evento ativado.

- **O Dado D20 Pixel-Perfect (32x32 Nativo 1:1):**
  - Posicionado elegantemente acima da barra de corações e fome (`screenHeight - 78`).
  - Amostragem nativa 1:1 em 32x32: **zero interpolação, zero halos ou bordas borradas**.
  - **Micro-Fonte Pixel Art Dedicada (3x5):** Os números de 1 a 20 são estampados no centro exato da face triangular invertida com relevo e sombra, usando a paleta original lilás estelar (`#BEAAC3`) e sombra profunda (`#110419`).
  - **Partículas Celestiais Vivas:** Cruzes de 5 pixels (`+`), quadradinhos 2x2 e pontos estelares que orbitam e respiram organicamente ao redor do dado, com explosão radial em 8 direções no momento do gatilho.

---

## ⚡ As 5 Faixas de Decisão (Tiers) e Módulos Ativos

| Tier | Faixa D20 | Nome do Tier | Cor | Filosofia & Eventos Ativos |
| :---: | :---: | :--- | :---: | :--- |
| **Tier 1** | **1 a 3** | **Maldição Mortal** | 🔴 Carmesim | **Risco Extremo:**<br>• `InescapableAnvilTrap` (Locomoção): Trava total de pulo e queda de bigorna mortal acelerada que se estilhaça no impacto.<br>• `AbyssalNestTrap` (Mineração): Erupção de Vex e Aranha da Caverna hostil atacando o minerador.<br>• `UnstableGravityTrap` (Construção): O bloco perde a sustentação e despenca no ar como entidade física.<br>• `ReverseThornsTrap` (Combate): Reflete 50% de todo dano causado de volta contra o jogador.<br>• `ScrapCraftTrap` (Fabricação): Sucateia materiais com nuvem tóxica sem gerar o item. |
| **Tier 2** | **4 a 9** | **Sabotagem e Azar** | 🟠 Laranja | **Penalidades & Sabotagem:**<br>• `StumbleDebuff` (Locomoção): Tropeço físico involuntário e Lentidão I.<br>• `ToxicGasDebuff` (Mineração): Rompimento de gás subterrâneo com Náusea I e Veneno I.<br>• `BrittleBlockDebuff` (Construção): O bloco esfarela ao ser colocado sem dropar o item.<br>• `LoweredGuardDebuff` (Combate): Desgaste severo na arma e Fraqueza I.<br>• `ToolWearDebuff` (Fabricação): Lasca ferramentas ou armaduras equipadas. |
| **Tier 3** | **10 a 11** | **Zona Neutra** | ⚪ Prata | **Equilíbrio Vanilla:**<br>• `SereneBreezeNeutral`: Brisa cósmica serena com partículas de End Rod e sino suave de ametista. Zero punições e zero bônus. |
| **Tier 4** | **12 a 17** | **Ventos Favoráveis** | 🟢 Esmeralda | **Bênçãos & Abundância:**<br>• `MomentumBuff` (Locomoção): Rajadas de vento com Velocidade II e Salto II.<br>• `RichVeinBuff` (Mineração): Pressa II (Haste II) e minérios duplicados.<br>• `ArchitectBuff` (Construção): Reembolso imediato do bloco colocado + Resistência I.<br>• `HunterBuff` (Combate): Golpe vampírico que cura 2 corações inteiros e concede Força II.<br>• `ReinforcedCraftBuff` (Fabricação): Reparação na ferramenta principal + 40 XP. |
| **Tier 5** | **18 a 20** | **Modo Deus (Divino)** | 🟡 Dourado/Roxo | **Supremacia Cósmica:**<br>• `HermesStepsBuff` (Locomoção): Velocidade III, Salto III e congelamento de superfícies.<br>• `MidasTransmutationBuff` (Mineração): Transmuta pedra e ardósia em blocos maciços de Ouro e Diamante.<br>• `FreeBlockBuff` (Construção): Chance de duplicar blocos no inventário.<br>• `ThorExecutionBuff` (Combate): Raio divino fulminante e queda de cabeça do monstro.<br>• `MasterpieceCraftBuff` (Fabricação): Encantamento na peça recém-criada e chuva de 100 XP. |

---

## 🛡️ Campo de Força Cinético (`ForcefieldManager`)

Para garantir que armadilhas caóticas e explosões nunca destruam as bases e construções dos jogadores, o mod conta com o **Domo Cinético de Campo de Força**:
- Sela a área perigosa dinamicamente ao redor do jogador sem alterar um único bloco do mundo.
- Repele com física elástica quem tentar cruzar a borda.
- **Proteção Anti-Griefing Total (`clearToBlow`):** Cancela o dano destrutivo a blocos em explosões internas, mantendo baús, fiações de redstone e construções intactas.

---

## 💻 Comandos no Jogo

Todos os comandos são acessíveis no chat do Minecraft:

| Comando | Descrição |
| :--- | :--- |
| `/d20 timer <segundos>` | Altera o tempo restante do ciclo macro atual (ex: `/d20 timer 30`). |
| `/d20 cycle <segundos>` | Define a duração padrão de cada ciclo (padrão: 1200s = 20 min). |
| `/d20 nextCycle` | Força a rotação imediata para a próxima rolagem do D20 Macro. |
| `/d20 setTier <1-20>` | Força diretamente o resultado da Decisão Geral (1 a 20). |
| `/d20 chance <0.0-1.0>` | Altera a taxa de disparo micro das ações (ex: `/d20 chance 1.0` para 100% de teste). |
| `/d20 trigger <id_evento>` | Dispara manualmente qualquer módulo registrado pelo seu ID. |
| `/d20 list` | Exibe no chat a lista de todos os módulos registrados e suas categorias. |
| `/d20 status` | Exibe o status completo do jogador (Tier, Caos, Tempo, etc.). |

---

## 🛠️ Como Compilar e Executar

### Pré-requisitos
- **Java 21** instalado.
- **Python 3.10+** (utilizado pelo pipeline de compilação automatizada).
- Minecraft 1.21.1 com NeoForge 21.1.256.

### Compilação
Clone o repositório e execute o script de build:
```bash
git clone https://github.com/matteushr/d20-chaos.git
cd d20-chaos
python build.py
```
O script compilará todas as classes com codificação UTF-8, empacotará os recursos e gerará o `.jar` pronto para uso em `build/d20chaos-1.0.0.jar`.

---

## 🤝 Como Contribuir & Abrir Issues

Adoramos receber feedback, relatos de bugs e novas ideias de eventos da comunidade! Veja como participar:

### 1. Reportando um Bug 🐛
Se você encontrou algum comportamento inesperado, erro visual ou crash:
1. Acesse a aba **[Issues](https://github.com/matteushr/d20-chaos/issues)** do repositório.
2. Clique em **New Issue** e selecione o modelo **🐛 Relato de Bug / Erro**.
3. Preencha os campos solicitados:
   - **Passos para reproduzir** (qual ação realizou, qual Tier estava ativo).
   - **Comportamento observado vs esperado**.
   - **Logs do jogo:** Trechos do `latest.log` ou `crash-reports/` contendo as mensagens `[D20]`.
   - Se possível, anexe capturas de tela.

### 2. Sugerindo um Novo Evento ou Calamidade 🎲
Quer ver uma armadilha nova, um monstro especial ou uma bênção divina no mod?
1. Abra uma issue usando o modelo **🎲 Sugestão de Novo Evento / Calamidade**.
2. Indique o **Tier alvo** (Tier 1 a 5) e a **Categoria da ação** (`MINING`, `MOVEMENT`, `BLOCK_PLACE`, `COMBAT_EXPLORE`, `CRAFTING`).
3. Descreva a mecânica e a experiência que o evento deve proporcionar!

### 3. Enviando um Pull Request (PR) 🚀
1. Faça um Fork do repositório.
2. Crie uma branch para sua funcionalidade: `git checkout -b feature/minha-calamidade`.
3. Garanta que o código segue o padrão modular com o contrato `ID20ActionEvent`.
4. Compile com `python build.py` e verifique se não há erros de compilação.
5. Envie o commit e abra o Pull Request detalhando as alterações.

---

## 📜 Licença

Distribuído sob a licença MIT. Consulte `LICENSE` para mais informações.
