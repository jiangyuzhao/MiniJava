先用正则表达式扫描、找到最大的一个TEMPNUM，以后要再用到新的临时TEMPNUM时，就从这里开始计数。
然后用Pigelet2SpigletVisitor扫描。
因为splglet的语法很严格，在piglet里面可以用Exp的地方，在spiglet中大多数都只能用TEMP。
所以遇到任何Exp，都先_ret.appendCode("MOVE TEMPNUM Exp")，
把它保存到一个新的临时TEMP里面，并且记录下这个TEMPNUM；
以后需要用到这个代码段的时候，直接用这个TEMPNUM就行了。

不过，还是有一部分情况，可以直接用Exp或SimpleExp，不必用TEMP。
如果所有地方都写成TEMP，未免太过冗余。
所以遇到任何Exp，除了把它保存到一个新的临时TEMP里面，也保存一份它的Exp版本。
如果这个Exp还属于SimpleExp，还保存一份它的SimpleExp版本。
1）如果在需要用到这个代码段的时候，可以直接用Exp或SimpleExp的版本，
那就直接用，不需要_ret.appendCode("MOVE TEMPNUM Exp")了。
同时把这个TEMPNUM释放、回收重用。
2）如果在需要用到这个代码段的时候，发现它就是类似L2的Label而不是函数名label、
或者它是字面值：那么更是可以直接回收它的TEMPNUM了。

当然，当exp中涉及到其他的TEMP时，要小心代码添加的顺序。
例如piglet里可以写：Operator Exp1 Exp2
在spiglet里只能写成：Operator TEMP SimpleExp

exp2.getCode == "MOVE TEMPNUM2 Exp1 \n MOVE TEMPNUM3 Exp2 \n OP TEMPNUM2 TEMPNUM3"
于是exp2.getExp() == "MOVE TEMPNUM2 Exp1 \n OP TEMPNUM2 Exp2";
生成的代码顺序不可以是"MOVE TEMPNUM1 MOVE TEMPNUM2 Exp1 \n OP TEMPNUM2 Exp2"
需要是"MOVE TEMPNUM2 Exp1 \n MOVE TEMPNUM1 OP TEMPNUM2 Exp2"
（其实，这样只能节省一两个TEMPNUM。）

其实，最好的方法，是用两个泛型参数的Visitor。
第二个参数用于指示，返回值必须是Temp，还是可以是SimpleExp或者Exp。
这样就不需要先分配TEMP、再回收不必要的TEMP，这样多此一举了。
