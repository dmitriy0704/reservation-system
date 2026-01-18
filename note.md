# Полезные заметки по проекту


### Запуск контейнера
```shell
docker run --name my-postgres -e POSTGRES_PASSWORD=root -p 5433:5432 -d postgres:latest
```

### SQL для ДБ

```sql
-- Создание таблицы 
CREATE TABLE "user" (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INTEGER
);

-- Вставка тестовых данных
INSERT INTO "user" (name, age) VALUES
                                   ('Alice', 25),
                                   ('Bob', 30),
                                   ('Charlie', 22);
```