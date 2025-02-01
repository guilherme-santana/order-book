# Usa uma imagem do JDK como base
FROM openjdk:17-slim

# Define o diretório de trabalho no container
WORKDIR /app

# Copiar o wrapper do Gradle e os arquivos do projeto
COPY gradlew .
COPY gradle ./gradle
COPY . .

# Tornar o wrapper executável
RUN chmod +x gradlew

# Copia os arquivos do projeto para dentro do container
COPY . /app

# Copia o script wait-for-it.sh para a raiz do container
COPY wait-for-it.sh /wait-for-it.sh

# FORÇA a permissão de execução
RUN chmod 777 /wait-for-it.sh

# INSTALA O NETCAT (nc) PARA O wait-for-it.sh FUNCIONAR
RUN apt-get update && apt-get install -y netcat

# Instalar dependências para build
RUN apt-get update && apt-get install -y gradle

# Executar o build do Gradle
RUN ./gradlew build

# Copia o arquivo JAR gerado pelo Gradle para dentro do container
COPY build/libs/*.jar app.jar

# Define o comando padrão
CMD ["java", "-jar", "app.jar", "./gradlew", "bootRun"]
