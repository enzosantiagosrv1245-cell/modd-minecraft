#!/bin/bash
echo "🔧 CORRIGINDO PROBLEMA DO GIT"
echo ""

# Remover arquivo grande do git
echo "Removendo gradle.zip do repositório..."
git rm --cached gradle.zip
rm gradle.zip

# Criar .gitignore para evitar arquivos desnecessários
echo "Criando .gitignore..."
cat > .gitignore << 'EOF'
# Gradle
.gradle/
build/
gradle.zip
gradle-*/

# IDE
.idea/
.vscode/
*.iml
*.iws
*.ipr

# OS
.DS_Store
Thumbs.db

# Minecraft
run/
logs/
crash-reports/

# Compiled
*.class
*.jar
!gradle/wrapper/gradle-wrapper.jar
EOF

# Commit das correções
echo "Fazendo commit das correções..."
git add .gitignore
git add compile-mod.sh
git commit -m "Fix: Remove gradle.zip e add .gitignore"

echo "✅ Problema corrigido!"
echo ""
echo "Agora execute:"
echo "git push"