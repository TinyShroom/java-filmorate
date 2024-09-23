# java-filmorate
Simple REST application, written on Java using Spring Boot.
Allows the user to add, retrieve and delete users and films from database using POST, PUT, GET and DELETE requests.

## Database structure

![Database structure in the form of an ER diagram](filmorate.png)
## Реализованные эндпоинты
1. ***Director***   
   `GET /directors` Get list of all directors  
   `GET /directors/{id}` Get director by id  
   `POST /directors` Create director  
   `PUT /directors`  Change director info  
   `DELETE /directors/{id}` Delete director
2. ***Getting most popular films by genre and year***   
   `GET /films/popular?count={limit}&genreId={genreId}&year={year}`
3. ***Get users feed***  
   `GET /users/{id}/feed`
4. ***Removing movies and users***  
   `DELETE /users/{userId}` Delete user by `id`  
   `DELETE /films/{filmId}` Delete film by `id`
5. ***Recommendations***  
   `GET /users/{id}/recommendations` Recommendations for user
6. ***Search***  
   `GET /fimls/search?query={query}&by={by}` Returns a list of movies sorted by popularity  
   Parameters:  
   ```query``` — text to search  
   ```by``` — may be `director` (search by director), `title` (search by title), or both values separated by commas when
   searching by director and title at the same time.
7. ***Reviews***  
   `POST /reviews` Create review  
   `PUT /reviews` Update review  
   `DELETE /reviews/{id}` Delete review by `id`  
   `GET /reviews/{id}` Get review by `id`  
   `GET /reviews?filmId={filmId}&count={count}` Get all review by film `id`  
   `PUT /reviews/{id}/like/{userId}` Like the review  
   `PUT /reviews/{id}/dislike/{userId}` Dislike the review  
   `DELETE /reviews/{id}/like/{userId}` Remove like from review  
   `DELETE /reviews/{id}/dislike/{userId}` Remove dislike from review
8. ***Common films***  
   `GET /films/common?userId={userId}&friendId={friendId}` Returns movies sorted by popularity  
   Parameters:  
   `userId` — `id` of user requesting the information  
   `friendId` — `id` of friend of user to compare list of films