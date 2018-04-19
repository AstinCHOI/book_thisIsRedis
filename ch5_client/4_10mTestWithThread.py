import time
import threading

import redis


TOTAL_OP = 10000000
THREAD = 5

def redis_thread(pool, idx):
    r = redis.Redis(connection_pool=pool)

    for i in range(TOTAL_OP):
        if i % THREAD == idx:
            key = value = "key" + str(100000000 + i)
            r.set(key, value)
    

pool = redis.BlockingConnectionPool(host='localhost', port=6379, db=0,
            max_connections=500, decode_responses=True)

threads = []
start = int(time.time())
for i in range(THREAD):
    t = threading.Thread(target=redis_thread, args=(pool, i))
    threads.append(t)
    t.start()

for t in threads:
    t.join()

pool.disconnect()
elapsed = int(time.time()) - start
print("requests per second : {}".format(TOTAL_OP / elapsed))
print("time : {}s".format(elapsed))
