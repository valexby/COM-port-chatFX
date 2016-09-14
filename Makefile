build:
	g++ -Wall -fPIC -I /usr/lib/jvm/java-8-oracle/include -I /usr/lib/jvm/java-8-oracle/include/linux -c c_src/com_helper.cpp
	g++ -I /usr/lib/jvm/java-8-oracle/include -I /usr/lib/jvm/java-8-oracle/include/linux -shared -Wl,-soname,libcom_helper.so \-o c_out/libcom_helper.so com_helper.o -lc
	rm com_helper.o