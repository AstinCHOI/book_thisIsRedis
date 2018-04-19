# 
# Usage
# $ cat redis_data_command.txt | redis-cli --pipe
# $ redis-cli flushdb
# $ cat redis_data_protocol.txt | redis-cli --pipe

TOTAL_NUMBER_OF_COMMAND = 10000000


def makeDataFileAsCommand(filename):
    with open(filename, 'wt') as f:
        for i in range(TOTAL_NUMBER_OF_COMMAND):
            incr = str(100000000 + i)
            key = "key" + incr
            value = "data" + incr
            f.write("set {} {}\r\n".format(key, value))
    

def makeDataFileAsProtocol(filename):
    with open(filename, 'wt') as f:
        for i in range(TOTAL_NUMBER_OF_COMMAND):
            incr = str(100000000 + i)
            key = "key" + incr
            value = "data" + incr
            f.write("*3\r\n")
            f.write("$3\r\n")
            f.write("set\r\n")
            f.write("${}\r\n".format(len(key)))
            f.write(key + "\r\n")
            f.write("${}\r\n".format(len(value)))
            f.write(value + "\r\n")


if __name__ == "__main__":
    makeDataFileAsCommand('redis_data_command.txt')
    makeDataFileAsProtocol('redis_data_protocol.txt')