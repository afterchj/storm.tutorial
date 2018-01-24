# Storm & Trident快速入门

## 目录
1. Storm产生的背景和用途
2. Storm是啥，有啥特性
3. Storm框架运行的逻辑结构
4. Storm框架运营的物理结构
5. 搭建Storm的基础设施
6. 编写和运行一个简单的Storm程序
7. Storm高级特性
8. 为啥要有Trident
9. Trident和Storm的关系
10. Trident运行的逻辑和物理结构
11. 编写和运行一个简单的Trident程序
12. Storm/Trident和各个持久层
13. 在本机调试Storm/Trident


## 1. Storm产生的背景和用途
  互联网尤其是移动互联网的井喷发展，使得整个人类社会的信息化、数字化的程度正在以前所未有的速度在提高，随之而来数字化数据也在爆发式的产生。举个例子，人的社会生活的方方面面，在90年代之前往往是以纸质证件和票据为依据。发工资有纸质的工资条，买东西用纸质钞票付款，出行需要介绍信，打折扣需要纸质优惠券，凡此种种不一而足。如今我们的日常生活，可以不带钱包出门，买东西刷支付宝，住宿用团购（甚至身份登记也是用身份证上的电子芯片），打的用APP，即便是大件商品也可以网购。似乎我们的生活已经与那些纸质票据没有太大关系了，互联网和APP能为我们搞定一切。  
  然而，如果我们仔细审视网络云端的另一头，看看是什么东西under the hood，会发现许多似曾相识的事物。银行、证券系统的服务器里运行的程序，其工作对象仍然是那些表格和报表，只不过换了个数字化的外衣。HRM系统管理的仍然是那些人事档案，同样也是换了个数字化的外衣。既然数据和信息从纸张迁移到了数字计算机上，那么基于原来这些纸质数据的计算工作，自然也转移到了计算机上。因此产生了CMS，ERP，HRM，GM等各种各样的所谓“系统”。  
  有经验的程序员往往会为不断的做重复的事情所烦恼。比如，上个月接了个企业网站的单子，是SSH的系统，这个月接了个电商平台的单子，又是SSH。日复一日的在做几乎相同的事情，不但效率低下，还容易出错。所以，到底发生了什么？回想纸质年代，我们需要关心纸质单证是怎么被生产出来的吗？我们需要关心笔是怎么生产出来的吗？我们需要知道存放单据的架子是怎么生成的吗？答案都是否定的，我们只需要知道每个数字是如何计算的，结果对不对；每条信息是如何产生、传达和验证的，有这些就可以了。更深入的说，就是我们只关心数据的计算（包含传递）的过程，而不用关心它是如何被计算的。但在数字信息时代的初期，业务开发人员需要不断的重复去发明轮子。他们一遍又一遍的写着结构类似的代码，做着逻辑类似的计算工作，并且每次开发都要为快速处理大量的数据伤脑筋。  
  有这样的现实问题，就会有人去想办法解决。Apache Storm就是在这大背景下产生的，一种用于做分布式的实时计算系统。Storm既解决了提供计算模型的问题，为开发人员提供了一个清晰的数据流框架，又能够稳定可靠的处理海量数据。因此，可有效避免写重复代码，让开发人员把精力更多的集中在业务逻辑本身。  
  
## 2. Storm是啥，有啥特性
  简单来讲，Storm就是一个数据流及其计算过程的模型，并且该模型可以持久运行在多台不同的主机上。用Storm可规定数据计算的步骤，源源不断的从数据原取出数据，计算，写入持久层。因此，Storm官网上用了一个形象的图片说明了此模型：
  ![Alt storm](http://storm.apache.org/images/storm-flow.png)
  
## 3. 
  
  
  

