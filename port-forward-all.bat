@echo off
echo Demarrage des port-forwards...

start "Discovery Service" kubectl port-forward svc/discovery-service 8761:8761 -n untitled
start "Customer Service" kubectl port-forward svc/customer-service 8081:8081 -n untitled
start "Billing Service" kubectl port-forward svc/billing-service 8083:8083 -n untitled
start "Inventory Service" kubectl port-forward svc/inventory-service 8082:8082 -n untitled

echo.
echo Port-forwards demarres !
echo Discovery:  http://localhost:8761
echo Customer:   http://localhost:8081
echo Billing:    http://localhost:8083
echo Inventory:  http://localhost:8082
echo Gateway:    http://localhost:8888 (deja accessible)
echo.
echo Pour arreter, fermez les fenetres ou utilisez: stop-port-forward-all.bat