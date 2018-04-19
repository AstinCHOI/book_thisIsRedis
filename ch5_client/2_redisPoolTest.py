import redis

pool = redis.BlockingConnectionPool(host='localhost', port=6379, db=0, 
            max_connections=20, decode_responses=True)
r = redis.Redis(connection_pool=pool)
r.hset("info:astin", "name", "astin")
r.hset("info:astin", "birth", "1985-12-12")

r1 = redis.Redis(connection_pool=pool)
result = r1.hgetall("info:astin")
print(result)
print(result["name"], result["birth"])

pool.disconnect()
