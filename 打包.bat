@echo off
set DaysAgo=1
set Today=%date:~0,4%%date:~5,2%%date:~8,2%

E:
cd E:\workspace\eFiling
jar -cvf eFiling%Today%.jar @list.txt > log.log
echo ==============================================
if not errorlevel   1  echo ==           恭喜您，  打包成功！       ==
if errorlevel   1 echo ===        打包失败，请检查！        ===
echo ==============================================

echo ********************************Edited by ChenMinghui**************************
pause