# Etapa 1: Construção do aplicativo (build stage)
FROM maven:latest AS builder

# Diretório de trabalho
WORKDIR /app

# Copiar os arquivos de configuração do Maven
COPY pom.xml .

# Copiar o código fonte
COPY src ./src

# Construir o JAR
RUN mvn clean install -DskipTests

# Etapa 2: Imagem final do aplicativo
FROM openjdk:17-jdk-slim

# Diretório de trabalho no contêiner final
WORKDIR /app

# Expor a porta do aplicativo
EXPOSE 8080

# Copiar apenas o arquivo JAR gerado da etapa de construção
COPY --from=builder /app/target/skawallet-01.jar /app/app.jar

# Definir o ponto de entrada para rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]