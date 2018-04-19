import time

import redis


TOTAL_OP = 10000000

pool = redis.ConnectionPool(host='localhost', port=6379,
        decode_responses=True)
r = redis.Redis(connection_pool=pool)
start = int(time.time())
for i in range(TOTAL_OP):
    key = value = "key" + str(100000000 + i)
    r.set(key, value)

elapsed = int(time.time()) - start
print("requests per second : {}".format(TOTAL_OP / elapsed))
print("time : {}s".format(elapsed))
pool.disconnect()
