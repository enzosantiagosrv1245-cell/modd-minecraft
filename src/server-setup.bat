@echo off
echo ====================================
echo   LAWYER MOD - SERVIDOR MINECRAFT
echo ====================================
echo.

:: Criar diretório do servidor
if not exist "server" mkdir server
cd server

:: Baixar Forge Server (versão 1.18.2)
echo Baixando Minecraft Forge Server...
if not exist "forge-1.18.2-40.2.0-installer.jar" (
    echo Por favor, baixe o Forge 1.18.2-40.2.0 installer de:
    echo https://files.minecraftforge.net/net/minecraftforge/forge/index_1.18.2.html
    echo E coloque o arquivo nesta pasta como: forge-1.18.2-40.2.0-installer.jar
    pause
)

:: Instalar Forge
if exist "forge-1.18.2-40.2.0-installer.jar" (
    echo Instalando Forge...
    java -jar forge-1.18.2-40.2.0-installer.jar --installServer
)

:: Criar pasta mods
if not exist "mods" mkdir mods

:: Copiar mod para pasta mods (após compilar)
echo.
echo INSTRUÇÕES PARA FINALIZAR O SETUP:
echo.
echo 1. Compile o mod usando: gradlew build
echo 2. Copie o arquivo .jar gerado de build/libs/ para server/mods/
echo 3. Aceite o EULA editando eula.txt e mudando eula=false para eula=true
echo 4. Execute start-server.bat para iniciar o servidor
echo.

:: Criar script de inicialização do servidor
echo @echo off > start-server.bat
echo echo Iniciando servidor Minecraft com LawyerMod... >> start-server.bat
echo java -Xmx2G -Xms2G -jar forge-1.18.2-40.2.0.jar >> start-server.bat
echo pause >> start-server.bat

:: Criar arquivo de propriedades do servidor
echo #Minecraft server properties > server.properties
echo server-port=25565 >> server.properties
echo gamemode=survival >> server.properties
echo difficulty=normal >> server.properties
echo spawn-protection=0 >> server.properties
echo max-players=20 >> server.properties
echo online-mode=false >> server.properties
echo pvp=true >> server.properties
echo level-name=world >> server.properties
echo motd=Servidor com LawyerMod - Sistema de Advogados! >> server.properties

echo.
echo ====================================
echo   SETUP CONCLUÍDO!
echo ====================================
echo.
echo Próximos passos:
echo 1. Compile o mod: gradlew build
echo 2. Copie o .jar para server/mods/
echo 3. Aceite o EULA em eula.txt
echo 4. Execute start-server.bat
echo.
pause