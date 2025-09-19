#!/bin/bash
echo "====================================="
echo "    COMPILANDO LAWYERMOD (LINUX)"
echo "====================================="
echo ""

# Verificar Java
echo "[1/5] Verificando Java..."
if java -version 2>&1 >/dev/null; then
    echo "✓ Java OK"
else
    echo "❌ ERRO: Java não encontrado!"
    echo "Instale Java 17:"
    echo "sudo apt update && sudo apt install openjdk-17-jdk"
    exit 1
fi
echo ""

# Verificar estrutura
echo "[2/5] Verificando estrutura..."
if [ ! -f "src/main/java/com/yourname/lawyermod/LawyerMod.java" ]; then
    echo "❌ ERRO: LawyerMod.java não encontrado!"
    echo "Estrutura atual:"
    find . -name "*.java" 2>/dev/null || echo "Nenhum arquivo Java encontrado"
    exit 1
fi
echo "✓ Estrutura OK"
echo ""

# Limpar builds anteriores
echo "[3/5] Limpando builds anteriores..."
rm -rf build/
echo "✓ Limpeza OK"
echo ""

# Dar permissão ao gradlew
if [ -f "gradlew" ]; then
    chmod +x gradlew
fi

# Compilar mod
echo "[4/5] Compilando mod..."
if [ -f "gradlew" ]; then
    ./gradlew build
elif [ -f "gradlew.bat" ]; then
    echo "❌ Encontrado gradlew.bat mas estamos no Linux!"
    echo "Tentando usar gradle diretamente..."
    gradle build
else
    echo "Gradle wrapper não encontrado, usando gradle diretamente..."
    gradle build
fi

if [ $? -ne 0 ]; then
    echo "❌ ERRO na compilação!"
    echo "Verifique os logs acima."
    exit 1
fi
echo "✓ Compilação OK"
echo ""

# Verificar arquivo final
echo "[5/5] Verificando arquivo final..."
if ls build/libs/*.jar 1> /dev/null 2>&1; then
    echo "✅ SUCESSO! Mod compilado!"
    echo ""
    echo "Arquivo .jar criado em: build/libs/"
    ls -la build/libs/*.jar
    echo ""
    echo "PRÓXIMOS PASSOS:"
    echo "1. Copie o .jar para pasta mods/ do servidor"
    echo "2. Inicie servidor Minecraft Forge"
    echo "3. Use /givefunctionpaper para testar"
else
    echo "❌ ERRO: Arquivo .jar não foi criado!"
    exit 1
fi

echo ""
echo "====================================="
echo "    COMPILAÇÃO CONCLUÍDA!"
echo "====================================="