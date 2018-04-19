import time

import redis


TOTAL_NUMBER_OF_COMMAND = 10000000

r = redis.StrictRedis(host='localhost', port=6379, db=0)
start = int(time.time())
pipe = r.pipeline()
for i in range(TOTAL_NUMBER_OF_COMMAND):
    key = value = "key" + str(100000000 + i)
    pipe.set(key, value)
pipe.execute()
elapsed = int(time.time()) - start
print("requests per second : {}".format(TOTAL_NUMBER_OF_COMMAND / elapsed))
print("time : {}s".format(elapsed))
