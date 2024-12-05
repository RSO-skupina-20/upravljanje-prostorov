# Upravljanje prostorov
Mikrostoritev za upravljanje prostorov bo vsebovala funkcionalnosti za dodajanje, urejanje, brisanje
in prikaz razpoložljivih prostorov za dogodke. Preko nje bodo lastniki prostorov upravljali prostore,
uporabnikom pa bo omogočeno pregledovanje razpoložljivih prostorov, namenjenim za organizacijo
dogodka.

Funkcionalnosti aplikacije: 
- Vnesi prostor: Lastniki prostorov lahko vnesejo nov prostor za rezervacijo
- Uredi prostor: Lastniki prostorov lahko uredijo obstoječi prostor
- Izbriši prostor: Lastniki prostorov lahko izbrišejo prostor, ki ni več na voljo
- Pregled prostorov: Vsi lahko pridobijo podatke o prostorih

# 1. Dodajanje .env datoteke
- Dodati je potrebno DB_URL, DB_USER, DB_PASSWORD in JWT_SECRET
- Prvi trije podatki morajo ustrezati glede na naslednji korak, kjer ustvarimo bazo

## 2. Zagon API-ja upravljanje-prostorov v Docker okolju
- Ustvarjanje podatkovne baze (za vse mikrostorive enako) `docker run --name najem-prostorov-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=najem-prostorov -p 5434:5432 -d postgres`
- Ustvarjanje jar datoteke: `mvn clean package`
- Ustvarjanje Docker slike: `docker build -t upravljanje-prostorov-api .`
- Zagon Docker kontejnerja: `docker run --env-file .env -p 8080:8080 upravljanje-prostorov-api `

## 3. Api dokumentacija
Api dokumentacija je dostopna na naslovu: `http://localhost:8080/api-specs/ui`

## 4. Api metode
- GET v1/prostori  - Pridobitev vse prostorov
- POST v1/prostori - Dodajanje novega prostora (potreben jwt žeton)
- GET v1/prostori/{id} - Pridobi prostor glede na id
- PUT v1/prostori/{id} - Posodobi prostor glede na id (potreben jwt žeton)
- DELETE v1/prostori/{id} - Izbriši prostor (potreben jwt žeton)
