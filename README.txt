*** Get Latest Version from Git Hub

git stash save "Backup before pulling"
git pull

*** Compille latest version
	*Remove .class files
Get-ChildItem -Path . -Recurse -Filter *.class | Remove-Item -Force
	*compile new version

javac *.java

*** Run test.txt script

java Interpreter test.txt

** There are multiple tests in my script - but insturctions suggest need at least 5 test scripts, so may be best to split them out?
