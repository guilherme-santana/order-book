# Usa uma imagem do JDK como base
FROM openjdk:21-slim

# Define o diretório de trabalho no container
WORKDIR /app

# Copiar o wrapper do Gradle e os arquivos do projeto
COPY gradlew .
COPY gradle ./gradle
COPY . .

# Tornar o wrapper executável
RUN chmod +x gradlew

# Copia o script wait-for-it.sh para a raiz do container e garante permissões
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Instala o netcat e o gradle em um único comando para evitar problemas de cache
RUN apt-get update && \
    apt-get install -y --no-install-recommends netcat gradle && \
    rm -rf /var/lib/apt/lists/*

# Executar o build do Gradle
RUN ./gradlew build

# Copia o arquivo JAR gerado pelo Gradle para dentro do container
COPY build/libs/*.jar app.jar

# Define o comando padrão
CMD ["java", "-jar", "app.jar"]
