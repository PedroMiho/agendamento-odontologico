# Essa etapa prepara o ambiente que será usado para compilar seu projeto e gerar o arquivo .jar.
# Comece com uma imagem que já tem Maven e Java 21 instalados e chame essa etapa de build
FROM maven:3.9-eclipse-temurin-21 AS build

#Indica em qual pasta os arquivos serão copiados e salvos
WORKDIR /app


# Porque o Maven precisa ler o pom.xml para saber como construir seu projeto.
# Esse arquivo informa, por exemplo:
    # Quais dependências baixar, como Spring Boot e o driver MySQL.
    # Qual versão do Java o projeto utiliza.
    # Quais plugins usar para gerar o .jar.
COPY pom.xml .

# Essa etapa baixa antecipadamente as dependências e os plugins necessários para construir o projeto, usando as informações do pom.xml.
RUN mvn -B dependency:go-offline

# Copia o código do projeto para /app/src dentro da imagem.
COPY src ./src
# Limpa os resultados anteriores, compila o projeto e gera o .jar em /app/target, sem executar os testes.
RUN mvn -B clean package -DskipTests


# Inicia uma nova etapa, usando uma imagem com Java 21 para executar o .jar. Ela não precisa do Maven, porque o projeto já foi compilado na etapa anterior.
FROM eclipse-temurin:21-jre
# Define /app como a pasta de trabalho dessa nova etapa.
WORKDIR /app

# Copia o .jar gerado na etapa build para a etapa atual, renomeando-o para app.jar. Como o WORKDIR é /app, ele fica em /app/app.jar.
COPY --from=build /app/target/*.jar app.jar

# Documenta que a aplicação utilizará a porta 10000. Não abre a porta sozinho; o Spring precisa escutar nela.
EXPOSE 10000

# Define o comando que inicia a aplicação quando o contêiner é executado, equivalente a:
ENTRYPOINT ["java", "-jar", "/app/app.jar"]