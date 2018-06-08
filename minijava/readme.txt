读入minijava源代码
root.accept(new SymbolTableVisitor(), MClassList.instance); 先建立符号表

MClassList.instance.completeClass(); 对每一个class进行completeClass
MClassList.instance.allocTemp(20);
	总temp表：对每一个class进行allocTemp，currentTemp从20开始累加，因为函数参数至多有20个。那么，就要检查参数个数大于19个

最后用MinijavaToPigletVisitor()进行遍历，返回代码。
————————————————————————————————————————————
mClass的completeClass： 	没有父类：结束
	父类completeClass
	父类每个方法如果在本类没有重写：加入自己的方法
	父类每个变量如果在本类没有覆盖：加入自己的变量
然而，为了offset只设置一次、要设置一个flag，确定顺序是先执行父类、再执行子类，避免重复操作。
比如，先遍历到C，C父类是B，B父类是A（顶层）
A结束
B中加入A独有的方法、变量 = B并A
C中加入B独有的方法、变量 = C并B = C并B并A
再遍历到B，则直接返回。

mClass的allocTemp：此时每个类已经独立。返回值是为了保持currentTemp的连续性
	Class变量表：每个变量setOffset；offset从4开始累加
	Class方法表：每个方法setOffset；setPigletName：本类名+方法名；offset从0开始累加
	每个方法内部，也进行allocTemp。返回值是为了保持currentTemp的连续性
	mMethod的allocTemp：
		Method参数temp表：对每个参数，setTemp；Temp从1开始累加
		（类的offset表会占temp的一个位置，就是temp 0）
		对每个变量，setTemp；返回值是为了保持currentTemp的连续性
		最后的总temp表：每个类中的每个方法中的每个变量，依次加入temp表。
		之后可以随时getNextTempNum，把要用的临时变量放入总temp表

总的来说，有这么几张表：
类
	方法：Class方法表，使用offset找到它的名字：x_x
		参数：Method参数temp表，1～19
		变量：总temp表，>=20
	变量：Class变量表；temp为0，用temp 0（实例表）+offset找到它的名字：TEMP x。

————————————————————————————————————————
如何区分开传引用和传值：
如果参数是一个类，要修改参数里的字段，则必然要使用类的方法而不能直接修改。而使用类的方法进行修改，就一定要找到这个类的符号表。于是修改的结果就能保留在这个实例中。这就相当于传引用。如果参数是一个基本类型变量，那么直接修改它，函数结束后参数被销毁，对原变量没有影响。这就相当于传值。

————————————————————————————————————————
如何实现多态：
每个类对同名变量，都加入varset、彼此之间用pigletname来区分。
对于同名方法，则只保存子类的加入methodset。
也因此，变量的offset之间不会出现gap；方法的offset之间会有gap。
在setoffset时要保持一致，同时注意不要重复设置！
还好，minijava里不允许在B中写A.setAge()或B.setAge()，只能写setAge()或this.setAge()。而且不能写this.age（字段） = age（方法参数），因为this.后只能是一个方法而不是字段。如果写成age = a这样的东西，不能重名，于是根本没有引起混淆的机会。如果写成age = age这样，字段age并没有被修改，实验结果也是正确的。

以下几种多态的情况，实验都是对的。
Animal a = new Dog();
0、a的类型，在编译时为Animal，在运行时为Dog
在类中调用同名字段，编译时确定（这里是Animal的字段）
但是，不通过类的方法而要访问该类的字段，在minijava中无法实现
相当于所有的字段都是protected的。

1、子类和父类的同名方法：覆盖。
在子类中调用同名方法，运行时确定（这里是Dog的方法）
同名方法中修改同名字段，也是运行时确定（这里是Dog的字段）

2、在子类中调用父类独有的方法：Animal
此时修改同名字段，修改的是Animal