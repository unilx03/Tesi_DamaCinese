JAVAC = javac
JAVA = java
MAIN = ChineseCheckers

all: $(MAIN).class

$(MAIN).class: $(MAIN).java
	$(JAVAC) $(MAIN).java

run: all
	nohup sh -c ' \
		$(JAVA) -Xmx1G $(MAIN) 2 1 20 0 > results_2_1_0base.log 2>&1 && \
		$(JAVA) -Xmx2G $(MAIN) 2 3 12 0 > results_2_3_0base.log 2>&1 && \
		\
		$(JAVA) -Xmx1G $(MAIN) 3 1 21 0 paranoid > results_3_1_0base_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 4 1 20 0 paranoid > results_4_1_0base_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 6 1 18 0 paranoid > results_6_1_0base_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 3 1 21 0 maxn > results_3_1_0base_maxn.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 4 1 20 0 maxn > results_4_1_0base_maxn.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 6 1 18 0 maxn > results_6_1_0base_maxn.log 2>&1 && \
		\
		$(JAVA) -Xmx2G $(MAIN) 3 3 9 0 paranoid > results_3_3_0base_paranoid.log 2>&1 && \
		$(JAVA) -Xmx2G $(MAIN) 4 3 8 0 paranoid > results_4_3_0base_paranoid.log 2>&1 && \
		$(JAVA) -Xmx2G $(MAIN) 6 3 6 0 paranoid > results_6_3_0base_paranoid.log 2>&1 && \
		$(JAVA) -Xmx2G $(MAIN) 3 3 9 0 maxn > results_3_3_0base_maxn.log 2>&1 && \
		$(JAVA) -Xmx2G $(MAIN) 4 3 8 0 maxn > results_4_3_0base_maxn.log 2>&1 && \
		$(JAVA) -Xmx2G $(MAIN) 6 3 6 0 maxn > results_6_3_0base_maxn.log 2>&1 && \
		\
		$(JAVA) -Xmx4G $(MAIN) 2 6 5 0 > results_2_6_0base.log 2>&1 && \
		\
		$(JAVA) -Xmx1G $(MAIN) 2 1 20 1 > results_2_1_1tables.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 2 3 20 1 > results_2_3_1tables.log 2>&1 && \
		\
		$(JAVA) -Xmx1G $(MAIN) 3 1 21 1 paranoid > results_3_1_1tables_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 4 1 20 1 paranoid > results_4_1_1tables_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 6 1 18 1 paranoid > results_6_1_1tables_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 3 1 21 1 maxn > results_3_1_1tables_maxn.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 4 1 20 1 maxn > results_4_1_1tables_maxn.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 6 1 18 1 maxn > results_6_1_1tables_maxn.log 2>&1 && \
		\
		$(JAVA) -Xmx4G $(MAIN) 3 3 12 1 paranoid > results_3_3_1tables_paranoid.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 4 3 12 1 paranoid > results_4_3_1tables_paranoid.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 6 3 12 1 paranoid > results_6_3_1tables_paranoid.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 3 3 12 1 maxn > results_3_3_1tables_maxn.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 4 3 12 1 maxn > results_4_3_1tables_maxn.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 6 3 12 1 maxn > results_6_3_1tables_maxn.log 2>&1 && \
		\
		$(JAVA) -Xmx4G $(MAIN) 2 6 5 1 > results_2_6_1tables.log 2>&1 && \
		\
		$(JAVA) -Xmx1G $(MAIN) 2 1 20 2 > results_2_1_2specialRules.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 2 3 20 2 > results_2_3_2specialRules.log 2>&1 && \
		\
		$(JAVA) -Xmx1G $(MAIN) 3 1 21 2 paranoid > results_3_1_2specialRules_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 4 1 20 2 paranoid > results_4_1_2specialRules_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 6 1 18 2 paranoid > results_6_1_2specialRules_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 3 1 21 2 maxn > results_3_1_2specialRules_maxn.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 4 1 20 2 maxn > results_4_1_2specialRules_maxn.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 6 1 18 2 maxn > results_6_1_2specialRules_maxn.log 2>&1 && \
		\
		$(JAVA) -Xmx4G $(MAIN) 3 3 12 2 paranoid > results_3_3_2specialRules_paranoid.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 4 3 12 2 paranoid > results_4_3_2specialRules_paranoid.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 6 3 12 2 paranoid > results_6_3_2specialRules_paranoid.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 3 3 12 2 maxn > results_3_3_2specialRules_maxn.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 4 3 12 2 maxn > results_4_3_2specialRules_maxn.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 6 3 12 2 maxn > results_6_3_2specialRules_maxn.log 2>&1 && \
		\
		$(JAVA) -Xmx4G $(MAIN) 2 6 5 2 > results_2_6_2specialRules.log 2>&1 && \
		\
		$(JAVA) -Xmx1G $(MAIN) 2 1 20 3 > results_2_1_3moveOrdering.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 2 3 20 3 > results_2_3_3moveOrdering.log 2>&1 && \
		\
		$(JAVA) -Xmx1G $(MAIN) 3 1 21 3 paranoid > results_3_1_3moveOrdering_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 4 1 20 3 paranoid > results_4_1_3moveOrdering_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 6 1 18 3 paranoid > results_6_1_3moveOrdering_paranoid.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 3 1 21 3 maxn > results_3_1_3moveOrdering_maxn.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 4 1 20 3 maxn > results_4_1_3moveOrdering_maxn.log 2>&1 && \
		$(JAVA) -Xmx1G $(MAIN) 6 1 18 3 maxn > results_6_1_3moveOrdering_maxn.log 2>&1 && \
		\
		$(JAVA) -Xmx4G $(MAIN) 3 3 12 3 paranoid > results_3_3_3moveOrdering_paranoid.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 4 3 12 3 paranoid > results_4_3_3moveOrdering_paranoid.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 6 3 12 3 paranoid > results_6_3_3moveOrdering_paranoid.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 3 3 12 3 maxn > results_3_3_3moveOrdering_maxn.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 4 3 12 3 maxn > results_4_3_3moveOrdering_maxn.log 2>&1 && \
		$(JAVA) -Xmx4G $(MAIN) 6 3 12 3 maxn > results_6_3_3moveOrdering_maxn.log 2>&1 && \
		\
		$(JAVA) -Xmx4G $(MAIN) 2 6 5 3 > results_2_6_3moveOrdering.log 2>&1 \
	' &

clean:
	rm -f *.class *.log nohup.out