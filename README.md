本项目使用Java语言，在特定场景下实现了Basic——Paxos的基本功能，具备一定的容错能力。
# 场景

M1：proposer， 能及时回复

M2：proposer，有时候全部能回复，大部分时候只选择一个回复或者不回复

M3：proposer，不及时，但也不晚，但有时候无法回复

M4-9：acceptor，响应时间会有所不同



# 运行
使用java1.8

编译器：idea



## 方法1

直接使用idea，运行 `vote.class` 的 `main` 方法



## 方法2

使用java编译

```shell
$ cd /yourPath/paxos/src
# 编译
$ javac Vote.java -d ../out
# 去到存放编译后的class文件的目录下
$ cd ../out
# 运行
$ java Vote
```



# 代码实现

## Message

封装了不同的消息体

**proposer**

1. prepare < proposalNum >
2. accept < proposalNum, proposalValue >



**acceptor**

3. promise < promiseNum(, value) >
4. accepted < proposalNum, proposalValue  >



## Common

通用数据

* ip, port



## Socket

* 封装的类用于通信使用



**client** -> proposer

**server** -> acceptor

* Queue< Socket > sockets: 消息队列



## Proposer

* prepare

  * 向超过一半的acceptor发送prepare请求

* accept

  


## Acceptor

* 作为 socket server，一直监听 client 的消息
* 使用函数 `handleMessage`  一次处理一条从 proposer 发出的请求



* promise
* acceptted













