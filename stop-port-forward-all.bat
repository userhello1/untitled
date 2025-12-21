@echo off
echo Arret des port-forwards...
taskkill /FI "WINDOWTITLE eq Discovery Service*" /F
taskkill /FI "WINDOWTITLE eq Customer Service*" /F
taskkill /FI "WINDOWTITLE eq Billing Service*" /F
taskkill /FI "WINDOWTITLE eq Inventory Service*" /F
echo Port-forwards arretes !