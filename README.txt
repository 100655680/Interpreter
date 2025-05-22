*** Get Latest Version from Git Hub

git stash save "Backup before pulling"
git pull

*** Compille latest version
*** Remove .class files
Get-ChildItem -Path . -Recurse -Filter *.class | Remove-Item -Force

*** compile new version

javac *.java

*** Run test.txt script

java Interpreter test.txt


** There are multiple tests in my script test.txt ?

*** GUI version 2 (includes Drop and Drag txt file)

java GUI2

*** Opens GUI where I can type or import txt file
