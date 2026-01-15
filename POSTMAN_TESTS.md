# Postman tests — Event Management Platform

Ce document décrit les endpoints et les requêtes à importer/exécuter avec Postman pour tester les services locaux démarrés via `docker-compose`.

Pré-requis
- Démarrer les services:

```bash
docker-compose up --build -d
```

- Endpoints locaux (par défaut):
  - API Gateway : http://localhost:8080
  - Event Service : http://localhost:8081
  - Booking Service : http://localhost:8082

Notes : si vous utilisez l'API Gateway, les routes sont proxifiées vers les microservices. Les exemples ci-dessous utilisent directement les services.

1) Event Service
-----------------

Base URL: `http://localhost:8081` (ou `http://localhost:8080/events` via gateway)

- Créer un événement (POST /events)

Request:
- Method: POST
- URL: `http://localhost:8081/events`
- Headers: `Content-Type: application/json`
- Body (raw JSON):

```json
{
  "name": "Concert Rock",
  "location": "Salle A",
  "availableSeats": 200,
  "category": "MUSIC",
  "description": "Concert de rock",
  "date": "2026-03-10T20:00:00"
}
```

- Récupérer tous les événements (GET /events)

Request: GET `http://localhost:8081/events`

- Récupérer un événement (GET /events/{id})

Request: GET `http://localhost:8081/events/1`

- Rechercher (GET /events/search?keyword=rock)

Request: GET `http://localhost:8081/events/search?keyword=rock`

- Filtrer par catégorie (GET /events/category/MUSIC)

Request: GET `http://localhost:8081/events/category/MUSIC`

- Réserver des places (appel interne) (POST /events/{id}/reserve?numberOfSeats=2)

Request: POST `http://localhost:8081/events/1/reserve?numberOfSeats=2`

- Libérer des places (POST /events/{id}/release?numberOfSeats=2)

Request: POST `http://localhost:8081/events/1/release?numberOfSeats=2`

2) Booking Service
-------------------

Base URL: `http://localhost:8082` (ou via gateway `http://localhost:8080/bookings`)

- Créer une réservation (POST /bookings)

Request:
- Method: POST
- URL: `http://localhost:8082/bookings`
- Headers: `Content-Type: application/json`
- Body (raw JSON):

```json
{
  "eventId": 1,
  "userId": 42,
  "numberOfTickets": 2,
  "totalPrice": 120.00,
  "userEmail": "user@example.com",
  "eventName": "Concert Rock",
  "eventDate": "2026-03-10T20:00:00",
  "notes": "Place proche de la scène"
}
```

Expected behaviour:
- The Booking Service calls Event Service `/events/{id}/reserve` to reserve seats.
- If Event Service confirms, booking is persisted and returns `201 Created` with booking payload.

- Récupérer toutes les réservations (GET /bookings)

Request: GET `http://localhost:8082/bookings`

- Récupérer une réservation (GET /bookings/{id})

Request: GET `http://localhost:8082/bookings/1`

- Annuler une réservation (POST /bookings/{id}/cancel)

Request: POST `http://localhost:8082/bookings/1/cancel`

Expected behaviour:
- Booking Service appelle Event Service `/events/{id}/release` pour libérer les places, met à jour le statut.

3) API Gateway (optionnel)
-------------------------

Si vous préférez utiliser l'API Gateway comme point d'entrée unifié :

- Liste des événements via gateway : `GET http://localhost:8080/events`
- Créer réservation via gateway : `POST http://localhost:8080/bookings`

4) Tests health
---------------

- Event Service health: `GET http://localhost:8081/events/health`
- Booking Service health: `GET http://localhost:8082/bookings/health`
- Eureka: `GET http://localhost:8761/`
- Config Server: `GET http://localhost:8888/actuator/health`

5) Postman collection (manually create)
--------------------------------------

Import steps:
1. Créez une nouvelle collection `EventManagementPlatform`.
2. Ajoutez les requêtes listées ci-dessus dans l'ordre : Event create, Event list, Booking create, Booking list, Booking cancel.
3. Pour les tests automatisés, ajoutez un test Postman JS qui vérifie `pm.response.code === 201` après la création.

Exemple de script test Postman (après création booking):

```javascript
pm.test("Status is 201", function () {
    pm.response.to.have.status(201);
});

const body = pm.response.json();
pm.environment.set("lastBookingId", body.id);
```

6) Dépannage
-------------

- Si la création de réservation retourne `409` ou `CONFLICT` : vérifier que l'Event Service a bien assez de places (GET /events/{id}) et que le service Event est joignable.
- Vérifiez les logs Docker: `docker logs booking-service` ou `docker-compose logs -f booking-service`.

7) Remarques finales
--------------------

- Le Booking Service dans ce repo a été complété avec un contrôleur REST, un service métier, un repository JPA et un client REST basique vers l'Event Service (`RestEventServiceClient`).
- Variable d'environnement utile : `EVENT_SERVICE_URL` (par défaut `http://event-service:8080`). Quand vous appelez directement `http://localhost:8081`, le client interne utilisera le nom du conteneur.

---
