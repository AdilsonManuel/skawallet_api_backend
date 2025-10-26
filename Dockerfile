# -------------------------------------------------------------
# ETAPA 1: CONSTRUÇÃO (BUILDER) - Usa JDK para compilar
# -------------------------------------------------------------
FROM eclipse-temurin:17-jdk-jammy AS builder

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia os arquivos necessários para o build
# Otimização do Docker: Copiar pom.xml primeiro para cachear as dependências
COPY pom.xml .
# Baixa as dependências
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src ./src

# Executa o build final. O JAR será criado em /app/target/
RUN mvn clean install -DskipTests

# -------------------------------------------------------------
# ETAPA 2: IMAGEM FINAL (PRODUÇÃO) - Usa apenas o JRE para ser leve
# -------------------------------------------------------------
FROM eclipse-temurin:17-jre-jammy

# Define o diretório de trabalho no contêiner final
WORKDIR /app

# Expor a porta que a aplicação Spring Boot usará
EXPOSE 8080

# === PONTO CRÍTICO DA CÓPIA DO JAR ===
# Copia o JAR do estágio de construção usando um coringa (*).
COPY --from=builder /app/target/*.jar /app/app.jar

# Define o ponto de entrada para rodar a aplicação Java
ENTRYPOINT ["java", "-jar", "app.jar"]
