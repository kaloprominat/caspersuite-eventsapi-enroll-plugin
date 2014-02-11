#	This makefile compiles java class and assemble it to jar-archive

prep-dist-folder:
	mkdir -p ./dist/class
	mkdir -p ./dist/plugin

compile-plugin: prep-dist-folder
	javac -d ./dist/class -cp ./JAMFEventNotificationMonitor.jar ./src/com/jnuc/catchmobiledeviceenroll/CatchMobileDeviceEnroll.java

prep-tmp:
	mkdir -p ./tmp

plugin: compile-plugin prep-tmp
	cp -r ./META-INF ./tmp/
	cp -r ./dist/class/com ./tmp/
	jar cvf ./dist/plugin/CatchMobileDeviceEnroll.jar -C tmp/ .
	rm -rf ./tmp

clean:
	rm -rf ./tmp
	rm -rf ./dist
