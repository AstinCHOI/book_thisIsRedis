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