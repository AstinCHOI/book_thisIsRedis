# 5장. 레디스 클라이언트
Python으로 구현하다가 Python에서 Java 변형이 조금 힘들고, 책 이해하는 시간에 방해가 된다고 판단하여 그 이후의 장들은 Java로 구현함.


# 8장. 확장과 분산 기법

https://redis.io/topics/cluster-tutorial  
https://redis.io/topics/partitioning  

## 1. 단일 복제 (마스터 - 슬레이브)

### 마스터 노드 시작
```bash
$ ~/redis-4.0.9/src/redis-server master.conf >> master.log &
```

### 마스터 노드 복제 상태 확인 1
```bash
$ ~/redis-4.0.9/src/redis-cli -p 6300
127.0.0.1:6300> info replication
# Replication
role:master
connected_slaves:0
master_replid:72237f06cce47b2e8ad6d4fbcaedb795aa78c4d4
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:0
second_repl_offset:-1
repl_backlog_active:0
repl_backlog_size:1048576
repl_backlog_first_byte_offset:0
repl_backlog_histlen:0
```

### 슬레이브 노드 시작
```bash
$ ~/redis-4.0.9/src/redis-server slave.conf >> slave.log &
```

### 마스터 노드 복제 상태 확인 2
```bash
$ ~/redis-4.0.9/src/redis-cli -p 6300
127.0.0.1:6300> info replication
# Replication
role:master
connected_slaves:1
slave0:ip=127.0.0.1,port=6301,state=online,offset=14,lag=1
master_replid:1376c91d7610e8e1c9c60def35d6ce5f1baad4e5
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:28
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:28
```

### 슬레이브 노드 복제 상태 확인
```bash
$ ~/redis-4.0.9/src/redis-cli -p 6301
127.0.0.1:6301> info replication
# Replication
role:slave
master_host:0.0.0.0
master_port:6300
master_link_status:up
master_last_io_seconds_ago:7
master_sync_in_progress:0
slave_repl_offset:196
slave_priority:100
slave_read_only:0
connected_slaves:0
master_replid:1376c91d7610e8e1c9c60def35d6ce5f1baad4e5
master_replid2:0000000000000000000000000000000000000000
master_repl_offset:196
second_repl_offset:-1
repl_backlog_active:1
repl_backlog_size:1048576
repl_backlog_first_byte_offset:1
repl_backlog_histlen:196
```

## 2. 해시 기반 샤딩

주의 - 아래 구현은 단순 해시 기반 샤딩이다. 샤딩의 마스터(쓰기)와 슬레이브(읽기)를 구분하지 않는다.

### 레디스 샤드 실행
```bash
$ ~/redis-4.0.9/src/redis-server 6380.conf >> 6380.log &
$ ~/redis-4.0.9/src/redis-server 6381.conf >> 6381.log &
```

### 샤드 노드 1 확인
```bash
$ ~/redis-4.0.9/src/redis-cli -p 6380
127.0.0.1:6380> keys *
  1) "Sharding Test-143"
  2) "Sharding Test-400"
  3) "Sharding Test-376"
  4) "Sharding Test-286"
  5) "Sharding Test-265"
  ...
```

### 샤드 노드 2 확인
```bash
$ ~/redis-4.0.9/src/redis-cli -p 6381
127.0.0.1:6381> keys *
  1) "Sharding Test-90"
  2) "Sharding Test-370"
  3) "Sharding Test-345"
  4) "Sharding Test-98"
  5) "Sharding Test-364"
  ...
```

# 9장. 레디스 운영시 고려사항

```bash
$ ~/redis-4.0.9/src/redis-benchmark -l --csv
```

CPU 사용률을 쉽게 확인하기 위해서 htop를 설치한다.  
```bash
$ brew install htop
```

레디스 사용할때, redis.conf에서 maxmemory를 현재 사용하고 있는 컴퓨터 메모리에 맞춰서 잘 사용한다. 또한, 운영체제의 Swap 영역도 잘 세팅한다. 아래는 레드헷에서 권장하는 설정 가이드다.  

| Memory | SWAP |
|--------|------|
| 2GB | 4GB |
| 2-8GB | x2GB |
| 8-64GB | x1/2GB |
| 64GB | 4GB |

그리고, 네트워크 병목 현상도 잘 고려해야하는데, 네트워크 허브를 복제를 위한것과 레디스 읽기/쓰기를 위한 것을 따로 분리해두면 좋다(대역폭을 고려해서..).  

스냅샷과 AOF(Append Only File)을 위해서(fork 함수와 COW - Copy On Write 사용), OS의 /etc/sysctl.conf 에서 vm.overcommit_memory=1 옵션을 고려해야한다.  
* 0 : 요청된 물리 메모리 만큼의 공간이 있어야함  
* 1 : 메모리에 요청된 공간이 없더라도 SWAP 영역에 존재할 때 가능  
* 2 : 사용중인 메모리가 'SWAP 공간' + vm.overcommit_ratio * 물리 메모리 크기 이내 일 때 가능  

레디스 메모리 영역 지정할 때 보통 물리 메모리의 60% 혹은 50%의 이하로 잡는다(OS가 운영하는 메모리도 고려해야하고, 데이터 읽기 및 쓰기 비중도 고려한다).  


# 10장. 레디스 튜닝

redis.conf 파일은 레디스가 구동될 때 설정되는 파일이고, 'config set' 명령은 구동 중 사용하는 명령이다. 구동 중 명령을 사용하더라도, 다시 레디스가 구동되면 redis.conf의 설정을 따른다.  

파일 디스크립터 개수 확인 (Redis의 maxclients 설정과 OS의 /etc/security/limits.conf 설정)    
```bash
$ ulimit -n
```

레디스 설정 확인  
```bash
redis> config get *
```

스냅샷  
dump.rdb 파일이 생성된다. 리눅스의 fork() 함수에 의해서 동작한다. COW란 동일한 메모리 영역을 공유하는 두 개의 프로세스 중에서 어느 하나가 메모리의 특정 영역을 변경하면 변경된 내용만 다른 메모리 영역에 복사하는 것을 의미한다. 그래서, 스냅샷을 하면 전체 메모리는 자식 프로세스의 메모리 크기 + 부모 프로세스가 변경한 페이지 개수 * (4KB - 시스템 메모리 크기)이다. 그래서, fork() 함수가 호출되어 자식 프로세스가 종료되기 전에 모든 페이지가 변경된다면 메모리를 최대 2배 사용하게 된다.  

AOF  
AOF(appendonly)는 데이터의 영구저장을 위해서 .aof 파일을 저장한다. 저장 형식은 5장에서 본 레디스 프로토콜의 형식이다. 또한, 스냅샷보다 더 많은 디스크 용량을 차지하는 대신, 더 나은 데이터 정합성을 보장한다. 다만, 모든 명령어를 일일이 파일에 기록하므로 스냅샷에 비해서 더 느린 응답시간을 제공한다.


# 11장. 루아 스크립트

```bash
127.0.0.1:6379> eval "local sum = ARGV[1] + ARGV[2] local result = redis.call('set', KEYS[1], sum) return result" 1 test:key 320 240
OK
127.0.0.1:6379> get test:key
"560"
127.0.0.1:6379>
127.0.0.1:6379> script load "local sum = ARGV[1] + ARGV[2] local result = redis.call('set', KEYS[1], sum) return result"
"65d3571d5642a15da3c5c4c36ec217b9cb1cdc92"
127.0.0.1:6379> evalsha 65d3571d5642a15da3c5c4c36ec217b9cb1cdc92 1 test:key 1 2
OK
127.0.0.1:6379> get test:key
"3"
```

아래와 같은 스크립트 조심할 것..  
```bash
127.0.0.1:6379> eval "while true do if true then local a = 0 end end" 0

# 다른 터미널에서 확인
127.0.0.1:6379> info
BUSY Redis is busy running a script. You can only call SCRIPT KILL or SHUTDOWN NOSAVE.
127.0.0.1:6379> script kill # or shutdown nosave

```