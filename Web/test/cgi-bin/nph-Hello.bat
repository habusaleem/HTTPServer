@echo off
echo HTTP/1.0 200 OK
echo Content-Type: text/html
echo ^<HTML^>
echo.
echo ^<HEAD^>
echo ^<TITLE^>Test^</TITLE^>
echo ^</HEAD^>

echo ^<BODY^>
echo ^<p^>
echo Environment variables:
set
echo </p^>
echo ^</BODY^>
echo ^</HTML^>