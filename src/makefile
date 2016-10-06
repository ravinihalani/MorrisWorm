JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
	MorrisMain.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
