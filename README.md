# quora
The question answer website project


#Working with the Project

##Checking out a branch
>mkdir quora
>cd quora
>>git init
>git pull https://github.com/imnoor/quora.git
>git remote add origin https://github.com/imnoor/quora.git
>git fetch
>git checkout questions ( assuming questions is the name of the branch you would be working on)

##Fixing Issues with generated code on intelliJ

https://stackoverflow.com/questions/5170620/unable-to-use-intellij-with-a-generated-sources-folder

Solution # 1
Project Structure → Modules → Click the generated-sources folder and make it a sources folder.

Solution # 2
Right click project folder
Select Maven
Select Generate Sources And Update Folders
