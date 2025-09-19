# LawyerMod - Mod de Advogado para Minecraft

Este mod adiciona um sistema de advogados ao Minecraft, permitindo que jogadores assumam a função de advogado e julguem outros jogadores.

## 🎯 Recursos

- **Papel de Função**: Item usável que permite escolher profissões (PERMANENTE até remoção por admin)
- **Sistema de Advogado**: Função especial com poderes de julgamento
- **Sistema de Anjo**: Função celestial com imortalidade e poderes divinos
- **Sistema de Missões**: Missões personalizadas por função com barra de progresso
- **Sistema de Level**: Progresso de nível 1 para nível 2 com missões mais difíceis
- **Comandos Especiais por Função**

### 🛡️ **Função: ANJO**
**Benefícios:**
- **Imortalidade**: Não pode morrer (cancela morte automaticamente)
- **Voo Angelical**: Pode voar usando comando `/fly`
- **Cura Divina**: Cura instantânea com `/heal`
- **Benção**: Pode curar outros jogadores com `/bless <jogador>`
- **Missão**: Coletar 15 madeiras (Nível 1) / 30 madeiras (Nível 2)

### ⚖️ **Função: ADVOGADO**
**Poderes:**
- `/julgar <jogador>` - Julga um jogador (teletransporta e impede movimento)
- `/liberto` - Liberta o julgado (remove metade da vida)
- `/condenado` - Condena o julgado (teletransporta para local aleatório)
- **Missão**: Coletar 10 pedras (Nível 1) / 20 pedras (Nível 2)

### 🎯 **Sistema de Missões**
- **HUD Visual**: Aparece no canto superior direito com nome, descrição e barra de progresso
- **Progresso Automático**: Detecta automaticamente itens coletados
- **Level Up**: Mensagem especial quando sobe de nível
- **Duas Dificuldades**: Nível 1 (básico) e Nível 2 (avançado)

## 📋 Pré-requisitos

- Java 17 ou superior
- Minecraft 1.18.2
- Minecraft Forge 40.2.0+

## 🔧 Instalação e Setup

### 1. Compilar o Mod

```bash
# Clone ou baixe os arquivos do mod
# Navegue até a pasta do projeto
./gradlew build
```

### 2. Setup do Servidor

1. Execute o script `server-setup.bat` (Windows) ou crie manualmente:
   ```bash
   mkdir server
   cd server
   ```

2. Baixe o Minecraft Forge 1.18.2-40.2.0 installer de: https://files.minecraftforge.net/net/minecraftforge/forge/index_1.18.2.html

3. Instale o Forge:
   ```bash
   java -jar forge-1.18.2-40.2.0-installer.jar --installServer
   ```

4. Copie o arquivo `.jar` compilado do mod da pasta `build/libs/` para `server/mods/`

5. Aceite o EULA editando `eula.txt` e mudando `eula=false` para `eula=true`

6. Inicie o servidor:
   ```bash
   java -Xmx2G -Xms2G -jar forge-1.18.2-40.2.0.jar
   ```

## 🎮 Como Usar

### Para Administradores

1. **Dar Papel de Função**: Use o comando `/givefunctionpaper <jogador>` para dar o papel de função a um jogador

### Para Jogadores

1. **Obter Função**: Clique com o botão direito no "Papel de Função" no inventário
2. **Selecionar Advogado**: Digite `/selectfunction lawyer` para se tornar advogado
3. **Usar Poderes de Advogado**:
   - `/julgar <nome_jogador>` - Teletransporta o jogador para sua frente e impede que se mova
   - `/liberto` - Liberta o jogador julgado, mas ele perde metade da vida
   - `/condenado` - Teletransporta o jogador julgado para um local aleatório

## 📝 Comandos Disponíveis

### **Comandos de Admin:**
| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/givefunctionpaper <jogador>` | Dá um papel de função ao jogador | Admin (OP) |
| `/removefunction <jogador>` | Remove função do jogador | Admin (OP) |

### **Comandos Gerais:**
| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/selectfunction lawyer` | Seleciona a função de advogado | Jogador com papel |
| `/selectfunction angel` | Seleciona a função de anjo | Jogador com papel |
| `/mission` | Mostra status da missão atual | Qualquer jogador |

### **Comandos de Advogado:**
| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/julgar <jogador>` | Julga um jogador | Advogado |
| `/liberto` | Liberta o jogador julgado | Advogado |
| `/condenado` | Condena o jogador julgado | Advogado |

### **Comandos de Anjo:**
| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/heal` | Cura completamente a si mesmo | Anjo |
| `/fly` | Ativa/desativa voo angelical | Anjo |
| `/bless <jogador>` | Cura e abençoa outro jogador | Anjo |

## 🏗️ Estrutura do Projeto

```
LawyerMod/
├── src/main/java/com/yourname/lawyermod/
│   ├── LawyerMod.java              # Classe principal do mod
│   ├── ModItems.java               # Registro de itens
│   ├── FunctionPaperItem.java      # Item de papel de função
│   ├── PlayerDataManager.java      # Gerenciamento de dados dos jogadores
│   ├── ModCommands.java            # Sistema de comandos
│   └── LawyerEventHandler.java     # Manipulador de eventos
├── src/main/resources/
│   └── META-INF/
│       └── mods.toml               # Configuração do mod
├── build.gradle                    # Configuração de build
├── server-setup.bat               # Script de setup do servidor
└── README.md                      # Este arquivo
```

## 🔧 Personalização

### Adicionar Novas Funções

1. Modifique `FunctionPaperItem.java` para adicionar novas opções na GUI
2. Adicione novos comandos em `ModCommands.java`
3. Implemente a lógica da nova função em `PlayerDataManager.java`

### Modificar Poderes do Advogado

- **Localização da Condenação**: Modifique o método `condemnPlayer()` em `ModCommands.java`
- **Penalidade da Libertação**: Altere a linha `judgedPlayer.setHealth(currentHealth / 2);`
- **Duração do Julgamento**: Adicione sistema de tempo em `PlayerDataManager.java`

## 🐛 Problemas Conhecidos

- O jogador julgado pode ainda usar comandos de teletransporte
- Não há persistência de dados entre reinicializações do servidor
- Interface gráfica simula tela preta através do chat

## 🚀 Melhorias Futuras

- [ ] GUI visual real para seleção de funções
- [ ] Sistema de prisão com coordenadas fixas
- [ ] Mais funções (Juiz, Policial, etc.)
- [ ] Sistema de tempo limite para julgamentos
- [ ] Persistência de dados em arquivo
- [ ] Sistema de recursos/apelação

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📞 Suporte

Se encontrar problemas ou tiver sugestões:
1. Verifique se seguiu todas as instruções de instalação
2. Confirme que está usando as versões corretas do Java, Minecraft e Forge
3. Abra uma issue no repositório com detalhes do problema

---

**Nota**: Este mod foi testado com Minecraft 1.18.2 e Forge 40.2.0. Compatibilidade com outras versões não é garantida.