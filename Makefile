
all: variants out/pSevaTemplate.xml

parts.xml: data/parts.ttl
	rapper -i turtle -o rdfxml data/parts.ttl > parts.xml

CreateSevaTemplate.class: CreateSevaTemplate.java
	@javac -cp lib/libSBOLj-2.1.2-SNAPSHOT-withDependencies.jar CreateSevaTemplate.java

out/pSevaTemplate.xml: CreateSevaTemplate.class
	@echo Generating pSeva template using libSBOLj...
	@java -cp lib/libSBOLj-2.1.2-SNAPSHOT-withDependencies.jar:. CreateSevaTemplate

variants: parts.xml
	@echo Creating variants using create-variants.js...
	@node create-variants

.PHONY: all


