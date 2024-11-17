# upravljanje-prostorov


## 1. Zagon API-ja upravljanje-prostorov v lokalnem okolju 
- Ustvarjanje podatkovne baze: `docker run --name upravljanje-prostorov-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=prostori -p 5433:5432 -d postgres`
- Ustvarjanje jar datoteke: `mvn clean package`
- Zagon API-ja: `java -jar target/upravljanje-prostorov-0.0.1-SNAPSHOT.jar`

## 2. Zagon API-ja upravljanje-prostorov v Docker okolju
- Ustvarjanje podatkovne baze: `docker run --name upravljanje-prostorov-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=prostori -p 5433:5432 -d postgres`
- Ustvarjanje jar datoteke: `mvn clean package`
- Ustvarjanje Docker slike: `docker build -t upravljanje-prostorov .`
- Zagon Docker kontejnerja: `docker run -p 8080:8080 upravljanje-prostorov-api `

## 3. Api dokumentacija
Api dokumentacija je dostopna na naslovu: `http://localhost:8080/api-specs/ui`